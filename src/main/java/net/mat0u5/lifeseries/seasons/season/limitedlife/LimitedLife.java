package net.mat0u5.lifeseries.seasons.season.limitedlife;

import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.config.DefaultConfigValues;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.seasons.boogeyman.BoogeymanManager;
import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.thirdlife.ThirdLife;
import net.mat0u5.lifeseries.seasons.secretsociety.SecretSociety;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.enums.PacketNames;
import net.mat0u5.lifeseries.utils.enums.SessionTimerStates;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.player.ScoreboardUtils;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.TraderLlamaEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradedItem;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldView;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestTypes;

import java.util.Collection;
import java.util.Optional;
import java.util.Random;

import static net.mat0u5.lifeseries.Main.*;
import static net.mat0u5.lifeseries.seasons.other.WatcherManager.isWatcher;

public class LimitedLife extends Season {
    public static final String COMMANDS_ADMIN_TEXT = "/lifeseries, /session, /claimkill, /lives, /boogeyman";
    public static final String COMMANDS_TEXT = "/claimkill, /lives";

    private boolean SHOW_DEATH_TITLE = true;
    public static int DEATH_NORMAL = -3600;
    private int DEATH_BOOGEYMAN = -7200;
    private int KILL_NORMAL = 1800;
    private int KILL_BOOGEYMAN = 3600;
    public static boolean TICK_OFFLINE_PLAYERS = false;
    private final DefaultConfigValues config = new DefaultConfigValues();
    private final Random rnd = new Random();
    private int checkCooldown = 0;


    @Override
    public Seasons getSeason() {
        return Seasons.LIMITED_LIFE;
    }

    @Override
    public ConfigManager createConfig() {
        return new LimitedLifeConfig();
    }

    @Override
    public BoogeymanManager createBoogeymanManager() {
        return new LimitedLifeBoogeymanManager();
    }

    @Override
    public SecretSociety createSecretSociety() {
        return new LimitedLifeSecretSociety();
    }

    @Override
    public LivesManager createLivesManager() {
        return new LimitedLifeLivesManager();
    }

    @Override
    public String getAdminCommands() {
        return COMMANDS_ADMIN_TEXT;
    }

    @Override
    public String getNonAdminCommands() {
        return COMMANDS_TEXT;
    }

    public void displayTimers(MinecraftServer server) {
        String message = "";
        if (currentSession.statusNotStarted()) {
            message = "Session has not started";
        }
        else if (currentSession.statusStarted()) {
            message = currentSession.getRemainingTimeStr();
        }
        else if (currentSession.statusPaused()) {
            message = "Session has been paused";
        }
        else if (currentSession.statusFinished()) {
            message = "Session has ended";
        }

        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {

            if (NetworkHandlerServer.wasHandshakeSuccessful(player)) {
                long timestamp = SessionTimerStates.OFF.getValue();
                if (currentSession.statusNotStarted()) timestamp = SessionTimerStates.NOT_STARTED.getValue();
                else if (currentSession.statusPaused()) timestamp = SessionTimerStates.PAUSED.getValue();
                else if (currentSession.statusFinished()) timestamp = SessionTimerStates.ENDED.getValue();
                else if (currentSession.sessionLength != null) {
                    long remainingMillis = (currentSession.sessionLength - (int) currentSession.passedTime) * 50;
                    timestamp = System.currentTimeMillis() + remainingMillis;
                }
                if (timestamp != SessionTimerStates.OFF.getValue()) {
                    NetworkHandlerServer.sendLongPacket(player, PacketNames.SESSION_TIMER, timestamp);
                }

                if (livesManager.hasAssignedLives(player) && livesManager.getPlayerLives(player) != null) {
                    long playerLives;
                    if (livesManager.isAlive(player)) {
                        Integer playerLivesInt = livesManager.getPlayerLives(player);
                        playerLives = playerLivesInt == null ? -1 : playerLivesInt;
                    }
                    else {
                        playerLives = -1;
                    }
                    String livesColor = livesManager.getColorForLives(player).toString();
                    NetworkHandlerServer.sendLongPacket(player, PacketNames.fromName(PacketNames.LIMITED_LIFE_TIMER.getName()+livesColor), playerLives);
                }
            }
            else {
                MutableText fullMessage = Text.empty();
                if (currentSession.displayTimer.contains(player.getUuid())) {
                    fullMessage.append(Text.literal(message).formatted(Formatting.GRAY));
                }
                if (livesManager.hasAssignedLives(player)) {
                    if (!fullMessage.getString().isEmpty()) fullMessage.append(Text.of("  |  "));
                    fullMessage.append(livesManager.getFormattedLives(player));
                }
                player.sendMessage(fullMessage, true);
            }
        }
    }

    private int secondCounter = 0;
    @Override
    public void tickSessionOn(MinecraftServer server) {
        super.tickSessionOn(server);
        if (!currentSession.statusStarted()) return;

        secondCounter--;
        if (secondCounter <= 0) {
            secondCounter = 20;
            livesManager.getAlivePlayers().forEach(livesManager::removePlayerLife);

            if (TICK_OFFLINE_PLAYERS) {
                Collection<ScoreboardEntry> entries = ScoreboardUtils.getScores(LivesManager.SCOREBOARD_NAME);
                for (ScoreboardEntry entry : entries) {
                    if (entry.value() <= 0) continue;
                    if (PlayerUtils.getPlayer(entry.owner()) != null) continue;
                    ScoreboardUtils.setScore(ScoreHolder.fromName(entry.owner()), LivesManager.SCOREBOARD_NAME, entry.value() - 1);
                }
            }
        }
    }

    @Override
    public void onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
        SessionTranscript.onPlayerDeath(player, source);
        if (source != null) {
            if (source.getAttacker() instanceof ServerPlayerEntity serverAttacker) {
                if (player != source.getAttacker()) {
                    onPlayerKilledByPlayer(player, serverAttacker);
                    return;
                }
            }
        }
        if (player.getPrimeAdversary() != null) {
            if (player.getPrimeAdversary() instanceof ServerPlayerEntity serverAdversary) {
                if (player != player.getPrimeAdversary()) {
                    onPlayerKilledByPlayer(player, serverAdversary);
                    return;
                }
            }
        }
        onPlayerDiedNaturally(player);
        if (livesManager.canChangeLivesNaturally(player)) {
            livesManager.addToPlayerLives(player, DEATH_NORMAL);
            if (livesManager.isAlive(player)) {
                PlayerUtils.sendTitle(player, Text.literal(OtherUtils.formatSecondsToReadable(DEATH_NORMAL)).formatted(Formatting.RED), 20, 80, 20);
            }
        }
    }

    @Override
    public void onClaimKill(ServerPlayerEntity killer, ServerPlayerEntity victim) {
        boolean wasAllowedToAttack = isAllowedToAttack(killer, victim, false);
        boolean wasBoogeyCure = boogeymanManager.isBoogeymanThatCanBeCured(killer, victim);
        super.onClaimKill(killer, victim);

        if (!wasBoogeyCure) {
            if (wasAllowedToAttack && livesManager.canChangeLivesNaturally()) {
                livesManager.addToPlayerLives(killer, KILL_NORMAL);
                PlayerUtils.sendTitle(killer, Text.literal(OtherUtils.formatSecondsToReadable(KILL_NORMAL)).formatted(Formatting.GREEN), 20, 80, 20);
            }
        }
        else if (livesManager.canChangeLivesNaturally()) {
            //Victim was killed by boogeyman - remove 2 hours from victim and add 1 hour to boogey

            boolean wasAlive = false;

            String msgVictim = OtherUtils.formatSecondsToReadable(DEATH_BOOGEYMAN-DEATH_NORMAL);
            String msgKiller = OtherUtils.formatSecondsToReadable(KILL_BOOGEYMAN);

            if (livesManager.isAlive(victim)) {
                livesManager.addToPlayerLives(victim, DEATH_BOOGEYMAN-DEATH_NORMAL);
                wasAlive = true;
            }
            livesManager.addToPlayerLives(killer, KILL_BOOGEYMAN);
            if (livesManager.isAlive(victim)) {
                PlayerUtils.sendTitle(killer, Text.literal(msgKiller).formatted(Formatting.GREEN), 20, 80, 20);
                PlayerUtils.sendTitle(victim, Text.literal(msgVictim).formatted(Formatting.RED), 20, 80, 20);
            }
            else if (wasAlive && SHOW_DEATH_TITLE) {
                PlayerUtils.sendTitleWithSubtitle(killer,
                        Text.literal(msgKiller).formatted(Formatting.GREEN),
                        livesManager.getDeathMessage(victim),
                        20, 80, 20);
            }
            else {
                PlayerUtils.sendTitle(killer, Text.literal(msgKiller).formatted(Formatting.GREEN), 20, 80, 20);
            }
        }
    }

    @Override
    public void onPlayerKilledByPlayer(ServerPlayerEntity victim, ServerPlayerEntity killer) {
        boolean wasAllowedToAttack = isAllowedToAttack(killer, victim, false);
        boolean wasBoogeyCure = boogeymanManager.isBoogeymanThatCanBeCured(killer, victim);
        super.onPlayerKilledByPlayer(victim, killer);

        if (!wasBoogeyCure && livesManager.canChangeLivesNaturally()) {
            boolean wasFinalKill = livesManager.isAlive(victim) || !SHOW_DEATH_TITLE;
            if (wasAllowedToAttack) {
                String msgKiller = OtherUtils.formatSecondsToReadable(KILL_NORMAL);
                livesManager.addToPlayerLives(killer, KILL_NORMAL);
                if (wasFinalKill) {
                    PlayerUtils.sendTitle(killer, Text.literal(msgKiller).formatted(Formatting.GREEN), 20, 80, 20);
                }
                else {
                    PlayerUtils.sendTitleWithSubtitle(killer,
                            Text.literal(msgKiller).formatted(Formatting.GREEN),
                            livesManager.getDeathMessage(victim),
                            20, 80, 20);
                }
            }
            String msgVictim = OtherUtils.formatSecondsToReadable(DEATH_NORMAL);
            livesManager.addToPlayerLives(victim, DEATH_NORMAL);
            if (wasFinalKill) {
                PlayerUtils.sendTitle(victim, Text.literal(msgVictim).formatted(Formatting.RED), 20, 80, 20);
            }
        }
        else if (livesManager.canChangeLivesNaturally()) {

            //Victim was killed by boogeyman - remove 2 hours from victim and add 1 hour to boogey
            String msgVictim = OtherUtils.formatSecondsToReadable(DEATH_BOOGEYMAN);
            String msgKiller = OtherUtils.formatSecondsToReadable(KILL_BOOGEYMAN);
            livesManager.addToPlayerLives(victim, DEATH_BOOGEYMAN);
            livesManager.addToPlayerLives(killer, KILL_BOOGEYMAN);

            if (livesManager.isAlive(victim) || !SHOW_DEATH_TITLE) {
                PlayerUtils.sendTitle(victim, Text.literal(msgVictim).formatted(Formatting.RED), 20, 80, 20);
                PlayerUtils.sendTitleWithSubtitle(killer,Text.of("§aYou are cured!"), Text.literal(msgKiller).formatted(Formatting.GREEN), 20, 80, 20);
            }
            else {
                PlayerUtils.sendTitleWithSubtitle(killer,Text.of("§aYou are cured, "+msgKiller),
                        livesManager.getDeathMessage(victim)
                        , 20, 80, 20);
            }
        }
    }

    @Override
    public boolean isAllowedToAttack(ServerPlayerEntity attacker, ServerPlayerEntity victim, boolean allowSelfDefense) {
        if (livesManager.isOnSpecificLives(attacker, 2, false) && livesManager.isOnAtLeastLives(victim, 3, false)) return true;
        return super.isAllowedToAttack(attacker, victim, allowSelfDefense);
    }

    @Override
    public void reload() {
        super.reload();
        if (!(seasonConfig instanceof LimitedLifeConfig config)) return;
        LimitedLifeLivesManager.DEFAULT_TIME = LimitedLifeConfig.TIME_DEFAULT.get(config);
        LimitedLifeLivesManager.YELLOW_TIME = LimitedLifeConfig.TIME_YELLOW.get(config);
        LimitedLifeLivesManager.RED_TIME = LimitedLifeConfig.TIME_RED.get(config);
        DEATH_NORMAL = LimitedLifeConfig.TIME_DEATH.get(config);
        DEATH_BOOGEYMAN = LimitedLifeConfig.TIME_DEATH_BOOGEYMAN.get(config);
        KILL_NORMAL = LimitedLifeConfig.TIME_KILL.get(config);
        KILL_BOOGEYMAN = LimitedLifeConfig.TIME_KILL_BOOGEYMAN.get(config);
        TICK_OFFLINE_PLAYERS = LimitedLifeConfig.TICK_OFFLINE_PLAYERS.get(config);
        LimitedLifeLivesManager.BROADCAST_COLOR_CHANGES = LimitedLifeConfig.BROADCAST_COLOR_CHANGES.get(config);
    }

    @Override
    public Integer getDefaultLives() {
        return LimitedLifeLivesManager.DEFAULT_TIME;
    }

    public static Integer getNextLivesColorLives(Integer currentLives) {
        if (currentLives == null) return null;

        if (currentLives > LimitedLifeLivesManager.DEFAULT_TIME) return LimitedLifeLivesManager.DEFAULT_TIME;
        else if (currentLives > LimitedLifeLivesManager.YELLOW_TIME) return LimitedLifeLivesManager.YELLOW_TIME;
        else if (currentLives > LimitedLifeLivesManager.RED_TIME) return LimitedLifeLivesManager.RED_TIME;
        return 0;
    }
    @Override
    public void tick(MinecraftServer server) {
        super.tick(server);
        checkCooldown--;

        if (!config.SIMPLE_LIFE.get(createConfig())) return;

        if (checkCooldown <= 0) {
            checkCooldown = 1200; //1 Minute
            ServerWorld world = server.getOverworld();
            if (world == null) return;
            int traderCount = 0;
            for (Entity entity : world.iterateEntities()) {
                if (entity instanceof WanderingTraderEntity) {
                    traderCount++;
                }
            }
            int maxTraders = config.TRADERS_MAX_AMOUNT.get(createConfig());

            if (traderCount == 0) checkCooldown = 1200; // 1 minute
            if (traderCount >= 1) checkCooldown = 1800; // 1.5 minutes
            if (traderCount >= maxTraders) return;

            for (int i = 0; i < 5; i++) {
                if (trySpawnTrader(world)) {
                    break;
                }
            }
        }
    }

    public boolean trySpawnTrader(ServerWorld world) {
        PlayerEntity player = world.getRandomAlivePlayer();
        if (player == null) return true;

        BlockPos playerPos = player.getBlockPos();
        PointOfInterestStorage poiStorage = world.getPointOfInterestStorage();

        Optional<BlockPos> optionalPos = poiStorage.getPosition(
                poiType -> poiType.matchesKey(PointOfInterestTypes.MEETING),
                pos -> true,
                playerPos,
                64,
                PointOfInterestStorage.OccupationStatus.ANY
        );

        BlockPos spawnCenter = optionalPos.orElse(playerPos);
        BlockPos spawnPos = this.getNearbySpawnPos(world, spawnCenter, 64);

        if (spawnPos != null && this.doesNotSuffocateAt(world, spawnPos)) {
            WanderingTraderEntity trader = (WanderingTraderEntity) EntityType.WANDERING_TRADER.spawn(world, spawnPos, SpawnReason.EVENT);
            if (trader != null) {

                // Spawn llamas
                for (int j = 0; j < 2; j++) spawnLlama(world, trader, 4);

                trader.setDespawnDelay(12000);

                TradeOfferList offers = trader.getOffers();
                offers.clear();

                // Choose between Simple Life or Complex Life trades
                if (config.COMPLEX_LIFE_TRADES.get(createConfig())) {
                    // Complex Life trades
                    offers.add(new TradeOffer(new TradedItem(Items.DIRT, 32), Optional.empty(), Items.OAK_SAPLING.getDefaultStack(), 0, 999999, 0, 0, 0));
                    offers.add(new TradeOffer(new TradedItem(Items.DIRT, 5), Optional.empty(), Items.BONE.getDefaultStack(), 0, 999999, 0, 0, 0));
                    offers.add(new TradeOffer(new TradedItem(Items.DIRT, 5), Optional.empty(), Items.SAND.getDefaultStack(), 0, 999999, 0, 0, 0));

                    offers.add(new TradeOffer(new TradedItem(Items.SAND, 10), Optional.empty(), Items.SUGAR_CANE.getDefaultStack(), 0, 999999, 0, 0, 0));

                    offers.add(new TradeOffer(new TradedItem(Items.OAK_PLANKS, 2), Optional.empty(), Items.COBBLESTONE.getDefaultStack(), 0, 999999, 0, 0, 0));
                    offers.add(new TradeOffer(new TradedItem(Items.OAK_PLANKS, 40), Optional.empty(), Items.WATER_BUCKET.getDefaultStack(), 0, 999999, 0, 0, 0));
                    offers.add(new TradeOffer(new TradedItem(Items.OAK_PLANKS, 40), Optional.empty(), Items.LAVA_BUCKET.getDefaultStack(), 0, 999999, 0, 0, 0));
                    offers.add(new TradeOffer(new TradedItem(Items.OAK_PLANKS, 32), Optional.empty(), Items.COW_SPAWN_EGG.getDefaultStack(), 0, 999999, 0, 0, 0));

                    offers.add(new TradeOffer(new TradedItem(Items.COBBLESTONE, 10), Optional.empty(), Items.IRON_INGOT.getDefaultStack(), 0, 999999, 0, 0, 0));
                    offers.add(new TradeOffer(new TradedItem(Items.COBBLESTONE, 10), Optional.empty(), Items.GOLD_INGOT.getDefaultStack(), 0, 999999, 0, 0, 0));
                    offers.add(new TradeOffer(new TradedItem(Items.COBBLESTONE, 1), Optional.empty(), Items.REDSTONE.getDefaultStack(), 0, 999999, 0, 0, 0));
                    offers.add(new TradeOffer(new TradedItem(Items.COBBLESTONE, 1), Optional.empty(), Items.GRAVEL.getDefaultStack(), 0, 999999, 0, 0, 0));

                    offers.add(new TradeOffer(new TradedItem(Items.IRON_INGOT, 20), Optional.empty(), Items.DIAMOND.getDefaultStack(), 0, 999999, 0, 0, 0));
                    offers.add(new TradeOffer(new TradedItem(Items.IRON_INGOT, 40), Optional.empty(), Items.TRIDENT.getDefaultStack(), 0, 999999, 0, 0, 0));
                    offers.add(new TradeOffer(new TradedItem(Items.IRON_INGOT, 16), Optional.empty(), Items.WOLF_SPAWN_EGG.getDefaultStack(), 0, 999999, 0, 0, 0));
                    offers.add(new TradeOffer(new TradedItem(Items.IRON_INGOT, 40), Optional.empty(), Items.TRIDENT.getDefaultStack(), 0, 999999, 0, 0, 0));

                    offers.add(new TradeOffer(new TradedItem(Items.GOLD_INGOT, 5), Optional.empty(), Items.LAPIS.getDefaultStack(), 0, 999999, 0, 0, 0));

                    int rand = rnd.nextInt(2);
                    if (rand == 0) offers.add(new TradeOffer(new TradedItem(Items.DIAMOND, 5), Optional.empty(), Items.NETHERITE_SCRAP.getDefaultStack(), 0, 999999, 0, 0, 0));
                    if (rand == 1) offers.add(new TradeOffer(new TradedItem(Items.DIAMOND, 10), Optional.empty(), Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE.getDefaultStack(), 0, 999999, 0, 0, 0));
                    offers.add(new TradeOffer(new TradedItem(Items.DIAMOND, 3), Optional.empty(), Items.CREEPER_SPAWN_EGG.getDefaultStack(), 0, 999999, 0, 0, 0));
                    offers.add(new TradeOffer(new TradedItem(Items.DIAMOND, 16), Optional.empty(), Items.END_CRYSTAL.getDefaultStack(), 0, 999999, 0, 0, 0));

                } else {
                    // Default Simple Life trades
                    offers.add(new TradeOffer(new TradedItem(Items.DIRT, 5), Optional.empty(), Items.IRON_INGOT.getDefaultStack(), 0, 999999, 0, 0, 0));
                    offers.add(new TradeOffer(new TradedItem(Items.DIRT, 40), Optional.empty(), Items.WATER_BUCKET.getDefaultStack(), 0, 999999, 0, 0, 0));
                    offers.add(new TradeOffer(new TradedItem(Items.DIRT, 40), Optional.empty(), Items.LAVA_BUCKET.getDefaultStack(), 0, 999999, 0, 0, 0));
                    offers.add(new TradeOffer(new TradedItem(Items.DIRT, 1), Optional.empty(), Items.SAND.getDefaultStack(), 0, 999999, 0, 0, 0));
                    offers.add(new TradeOffer(new TradedItem(Items.DIRT, 1), Optional.empty(), Items.GRAVEL.getDefaultStack(), 0, 999999, 0, 0, 0));
                    offers.add(new TradeOffer(new TradedItem(Items.DIRT, 5), Optional.empty(), Items.GOLD_INGOT.getDefaultStack(), 0, 999999, 0, 0, 0));
                    offers.add(new TradeOffer(new TradedItem(Items.DIRT, 1), Optional.empty(), Items.REDSTONE.getDefaultStack(), 0, 999999, 0, 0, 0));

                    int rand = rnd.nextInt(2);
                    if (rand == 0) offers.add(new TradeOffer(new TradedItem(Items.DIRT, 32), Optional.empty(), Items.OAK_SAPLING.getDefaultStack(), 0, 999999, 0, 0, 0));
                    if (rand == 1) offers.add(new TradeOffer(new TradedItem(Items.DIRT, 32), Optional.empty(), Items.SPRUCE_SAPLING.getDefaultStack(), 0, 999999, 0, 0, 0));
                }

                trader.setOffersFromServer(offers);
                return true;
            }
        }

        return false;
    }

    private void spawnLlama(ServerWorld world, WanderingTraderEntity trader, int range) {
        BlockPos pos = this.getNearbySpawnPos(world, trader.getBlockPos(), range);
        if (pos != null) {
            TraderLlamaEntity llama = (TraderLlamaEntity) EntityType.TRADER_LLAMA.spawn(world, pos, SpawnReason.EVENT);
            if (llama != null) llama.attachLeash(trader, true);
        }
    }

    private BlockPos getNearbySpawnPos(WorldView world, BlockPos pos, int range) {
        BlockPos finalPos = null;
        SpawnLocation spawnLocation = SpawnRestriction.getLocation(EntityType.WANDERING_TRADER);

        for (int i = 0; i < 10; i++) {
            int x = pos.getX() + rnd.nextInt(range * 2) - range;
            int z = pos.getZ() + rnd.nextInt(range * 2) - range;
            int y = world.getTopY(Heightmap.Type.WORLD_SURFACE, x, z);
            BlockPos candidate = new BlockPos(x, y, z);
            if (spawnLocation.isSpawnPositionOk(world, candidate, EntityType.WANDERING_TRADER)) {
                finalPos = candidate;
                break;
            }
        }

        return finalPos;
    }

    private boolean doesNotSuffocateAt(BlockView world, BlockPos pos) {
        for (BlockPos checkPos : BlockPos.iterate(pos, pos.add(1, 2, 1))) {
            if (!world.getBlockState(checkPos).getCollisionShape(world, checkPos).isEmpty()) return false;
        }
        return true;
    }
}
