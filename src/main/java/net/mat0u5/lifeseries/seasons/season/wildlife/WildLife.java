package net.mat0u5.lifeseries.seasons.season.wildlife;

import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.config.DefaultConfigValues;
import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.mat0u5.lifeseries.seasons.other.WatcherManager;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.thirdlife.ThirdLife;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.*;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.snails.Snails;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpower;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.*;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaWildcard;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.AttributeUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.player.ScoreboardUtils;
import net.mat0u5.lifeseries.utils.world.WorldUtils;
import net.minecraft.entity.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.passive.TraderLlamaEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradedItem;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldView;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestTypes;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.Random;
import static net.mat0u5.lifeseries.Main.currentSession;
import static net.mat0u5.lifeseries.Main.seasonConfig;
import static net.mat0u5.lifeseries.seasons.other.WatcherManager.isWatcher;
//? if >= 1.21.2
/*import net.minecraft.server.world.ServerWorld;*/

public class WildLife extends Season {
    public static final String COMMANDS_ADMIN_TEXT = "/lifeseries, /session, /claimkill, /lives, /wildcard, /superpower, /snail";
    public static final String COMMANDS_TEXT = "/claimkill, /lives, /snail";
    public static boolean KILLING_DARK_GREENS_GAINS_LIVES = true;
    public static boolean BROADCAST_LIFE_GAIN = true;
    private final DefaultConfigValues config = new DefaultConfigValues();
    private final Random rnd = new Random();
    private int checkCooldown = 0;

    @Override
    public Seasons getSeason() {
        return Seasons.WILD_LIFE;
    }

    @Override
    public ConfigManager createConfig() {
        Snails.loadConfig();
        return new WildLifeConfig();
    }

    @Override
    public String getAdminCommands() {
        return COMMANDS_ADMIN_TEXT;
    }

    @Override
    public String getNonAdminCommands() {
        return COMMANDS_TEXT;
    }

    @Override
    public void initialize() {
        super.initialize();
        Snails.loadConfig();
        Snails.loadSnailNames();
        TriviaBot.initializeItemSpawner();
    }

    @Override
    public void onPlayerJoin(ServerPlayerEntity player) {
        super.onPlayerJoin(player);
        WildcardManager.onPlayerJoin(player);
    }

    @Override
    public void onPlayerFinishJoining(ServerPlayerEntity player) {
        super.onPlayerFinishJoining(player);
        WildcardManager.onPlayerFinishJoining(player);
    }

    @Override
    public boolean isAllowedToAttack(ServerPlayerEntity attacker, ServerPlayerEntity victim, boolean allowSelfDefense) {
        if (Necromancy.isRessurectedPlayer(victim) || Necromancy.isRessurectedPlayer(attacker)) {
            return true;
        }
        if (livesManager.isOnSpecificLives(attacker, 2, false) && livesManager.isOnAtLeastLives(victim, 3, false)) {
            return true;
        }
        return super.isAllowedToAttack(attacker, victim, allowSelfDefense);
    }

    @Override
    public void onPlayerKilledByPlayer(ServerPlayerEntity victim, ServerPlayerEntity killer) {
        boolean wasAllowedToAttack = isAllowedToAttack(killer, victim, false);
        boolean wasBoogeyCure = boogeymanManager.isBoogeymanThatCanBeCured(killer, victim);
        super.onPlayerKilledByPlayer(victim, killer);
        if (livesManager.isOnAtLeastLives(victim, 4, false) && wasAllowedToAttack && !wasBoogeyCure) {
            if (Necromancy.isRessurectedPlayer(killer) && seasonConfig instanceof WildLifeConfig config) {
                if (WildLifeConfig.WILDCARD_SUPERPOWERS_ZOMBIES_REVIVE_BY_KILLING_DARK_GREEN.get(config)) {
                    Integer currentLives = livesManager.getPlayerLives(killer);
                    if (currentLives == null) currentLives = 0;
                    int lives = currentLives + 1;
                    if (lives <= 0) {
                        ScoreboardUtils.setScore(ScoreHolder.fromName(killer.getNameForScoreboard()), LivesManager.SCOREBOARD_NAME, lives);
                    }
                    else {
                        broadcastLifeGain(killer);
                        livesManager.addPlayerLife(killer);
                    }
                }
            }
            else {
                if (KILLING_DARK_GREENS_GAINS_LIVES) {
                    broadcastLifeGain(killer);
                    livesManager.addPlayerLife(killer);
                }
            }
        }
    }


    @Override
    public void onClaimKill(ServerPlayerEntity killer, ServerPlayerEntity victim) {
        super.onClaimKill(killer, victim);
        if (livesManager.isOnAtLeastLives(victim, 4, false) && KILLING_DARK_GREENS_GAINS_LIVES) {
            broadcastLifeGain(killer);
            livesManager.addPlayerLife(killer);
        }
    }

    public void broadcastLifeGain(ServerPlayerEntity player) {
        if (BROADCAST_LIFE_GAIN) {
            PlayerUtils.broadcastMessage(TextUtils.format("{}§7 gained a life for killing a §2dark green§7 player.", player));
        }
    }

    @Override
    public void tickSessionOn(MinecraftServer server) {
        // Call the base tick logic
        super.tickSessionOn(server);

        // Only spawn traders if SIMPLE_LIFE config is enabled
        if (!config.SIMPLE_LIFE.get(createConfig())) return;

        checkCooldown--;
        if (checkCooldown <= 0) {
            checkCooldown = 1200; // 1 minute

            ServerWorld world = server.getOverworld();
            if (world == null) return;

            // Try to spawn trader
            for (int i = 0; i < 5; i++) {
                if (trySpawnTrader(world)) break;
            }
        }

        // Custom session ticking logic
        WildcardManager.tickSessionOn();
    }

    @Override
    public void tick(MinecraftServer server) {
        super.tick(server);
        WildcardManager.tick();
    }

    @Override
    public boolean sessionStart() {
        super.sessionStart();
        WildcardManager.onSessionStart();
        return true;
    }

    @Override
    public void addSessionActions() {
        super.addSessionActions();
        WildcardManager.addSessionActions();
    }

    @Override
    public void sessionEnd() {
        WildcardManager.onSessionEnd();
        super.sessionEnd();
    }

    @Override
    public void reload() {
        super.reload();
        if (!(seasonConfig instanceof WildLifeConfig config)) return;
        Hunger.SWITCH_DELAY = 20 * WildLifeConfig.WILDCARD_HUNGER_RANDOMIZE_INTERVAL.get(config);
        Hunger.HUNGER_EFFECT_LEVEL = WildLifeConfig.WILDCARD_HUNGER_EFFECT_LEVEL.get(config);
        Hunger.NUTRITION_CHANCE = WildLifeConfig.WILDCARD_HUNGER_NUTRITION_CHANCE.get(config);
        Hunger.SATURATION_CHANCE = WildLifeConfig.WILDCARD_HUNGER_SATURATION_CHANCE.get(config);
        Hunger.EFFECT_CHANCE = WildLifeConfig.WILDCARD_HUNGER_EFFECT_CHANCE.get(config);
        Hunger.AVG_EFFECT_DURATION = WildLifeConfig.WILDCARD_HUNGER_AVG_EFFECT_DURATION.get(config);

        SizeShifting.MIN_SIZE = WildLifeConfig.WILDCARD_SIZESHIFTING_MIN_SIZE.get(config);
        SizeShifting.MAX_SIZE = WildLifeConfig.WILDCARD_SIZESHIFTING_MAX_SIZE.get(config);
        SizeShifting.SIZE_CHANGE_MULTIPLIER = WildLifeConfig.WILDCARD_SIZESHIFTING_SIZE_CHANGE_MULTIPLIER.get(config);
        SizeShifting.FIX_SIZECHANGING_BUGS = WildLifeConfig.WILDCARD_SIZESHIFTING_FIX_BUGS.get(config);


        Snail.GLOBAL_SPEED_MULTIPLIER = WildLifeConfig.WILDCARD_SNAILS_SPEED_MULTIPLIER.get(config);
        Snail.SHOULD_DROWN_PLAYER = WildLifeConfig.WILDCARD_SNAILS_DROWN_PLAYERS.get(config);

        TimeDilation.MIN_TICK_RATE = (float) (20.0 * WildLifeConfig.WILDCARD_TIMEDILATION_MIN_SPEED.get(config));
        TimeDilation.MAX_TICK_RATE = (float) (20.0 * WildLifeConfig.WILDCARD_TIMEDILATION_MAX_SPEED.get(config));
        TimeDilation.MIN_PLAYER_MSPT = (float) (50.0 / WildLifeConfig.WILDCARD_TIMEDILATION_PLAYER_MAX_SPEED.get(config));

        MobSwap.MAX_DELAY = 20 * WildLifeConfig.WILDCARD_MOBSWAP_START_SPAWN_DELAY.get(config);
        MobSwap.MIN_DELAY = 20 * WildLifeConfig.WILDCARD_MOBSWAP_END_SPAWN_DELAY.get(config);
        MobSwap.SPAWN_MOBS = WildLifeConfig.WILDCARD_MOBSWAP_SPAWN_MOBS.get(config);
        MobSwap.BOSS_CHANCE_MULTIPLIER = WildLifeConfig.WILDCARD_MOBSWAP_BOSS_CHANCE_MULTIPLIER.get(config);

        TriviaBot.CAN_START_RIDING = WildLifeConfig.WILDCARD_TRIVIA_BOTS_CAN_ENTER_BOATS.get(config);
        TriviaWildcard.TRIVIA_BOTS_PER_PLAYER = WildLifeConfig.WILDCARD_TRIVIA_BOTS_PER_PLAYER.get(config);
        TriviaBot.EASY_TIME = WildLifeConfig.WILDCARD_TRIVIA_SECONDS_EASY.get(config);
        TriviaBot.NORMAL_TIME = WildLifeConfig.WILDCARD_TRIVIA_SECONDS_NORMAL.get(config);
        TriviaBot.HARD_TIME = WildLifeConfig.WILDCARD_TRIVIA_SECONDS_HARD.get(config);
        WindCharge.MAX_MACE_DAMAGE = WildLifeConfig.WILDCARD_SUPERPOWERS_WINDCHARGE_MAX_MACE_DAMAGE.get(config);
        Superspeed.STEP_UP = WildLifeConfig.WILDCARD_SUPERPOWERS_SUPERSPEED_STEP.get(config);
        WildcardManager.ACTIVATE_WILDCARD_MINUTE = WildLifeConfig.ACTIVATE_WILDCARD_MINUTE.get(config);
        KILLING_DARK_GREENS_GAINS_LIVES = WildLifeConfig.KILLING_DARK_GREENS_GAINS_LIVES.get(config);
        BROADCAST_LIFE_GAIN = WildLifeConfig.BROADCAST_LIFE_GAIN.get(config);
        SuperpowersWildcard.WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME = WildLifeConfig.WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME.get(config);
        SuperpowersWildcard.setBlacklist(WildLifeConfig.WILDCARD_SUPERPOWERS_POWER_BLACKLIST.get(config));
        Callback.setBlacklist(WildLifeConfig.WILDCARD_CALLBACK_WILDCARDS_BLACKLIST.get(config));
        Callback.TURN_OFF = WildLifeConfig.WILDCARD_CALLBACK_TURN_OFF.get(config);

        AnimalDisguise.SHOW_ARMOR = WildLifeConfig.WILDCARD_SUPERPOWERS_ANIMALDISGUISE_ARMOR.get(config);
        AnimalDisguise.SHOW_HANDS = WildLifeConfig.WILDCARD_SUPERPOWERS_ANIMALDISGUISE_HANDS.get(config);

        Snails.loadConfig();
        Snails.loadSnailNames();
        Snails.reloadSnailNames();
        Snails.reloadSnailSkins();
        TriviaWildcard.reload();
    }

    @Override
    public void modifyEntityDrops(LivingEntity entity, DamageSource damageSource) {
        super.modifyEntityDrops(entity, damageSource);
        if (damageSource.getSource() instanceof PlayerEntity) {
            if (entity instanceof WardenEntity || entity instanceof WitherEntity || entity instanceof EnderDragonEntity) {
                //? if <= 1.21 {
                entity.dropStack(Items.TOTEM_OF_UNDYING.getDefaultStack());
                 //?} else {
                /*entity.dropStack((ServerWorld) WorldUtils.getEntityWorld(entity), Items.TOTEM_OF_UNDYING.getDefaultStack());
                *///?}
            }
        }
    }

    @Override
    public void onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
        if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.CREAKING)) {
            if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof Creaking creakingPower) {
                creakingPower.deactivate();
                reloadPlayerTeam(player);
            }
        }

        super.onPlayerDeath(player, source);

        TriviaBot.cursedGigantificationPlayers.remove(player.getUuid());
        TriviaBot.cursedHeartPlayers.remove(player.getUuid());
        AttributeUtils.resetMaxPlayerHealthIfNecessary(player);

        TriviaBot.cursedMoonJumpPlayers.remove(player.getUuid());
        AttributeUtils.resetPlayerJumpHeight(player);

        Superpower power = SuperpowersWildcard.getSuperpowerInstance(player);
        if (power != null) {
            power.deactivate();
        }
    }

    @Override
    public void onPlayerDisconnect(ServerPlayerEntity player) {
        super.onPlayerDisconnect(player);

        Superpower power = SuperpowersWildcard.getSuperpowerInstance(player);
        if (power != null) {
            power.deactivate();
        }
    }

    @Override
    public String getTeamForPlayer(ServerPlayerEntity player) {
        String team = super.getTeamForPlayer(player);

        if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.CREAKING)) {
            return "creaking_"+player.getNameForScoreboard();
        }

        return team;
    }

    @Override
    public void onPlayerDamage(ServerPlayerEntity player, DamageSource source, float amount, CallbackInfo ci) {
        if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.PLAYER_DISGUISE)) {
            if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof PlayerDisguise power) {
                power.onTakeDamage();
            }
        }
        if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.ANIMAL_DISGUISE)) {
            if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof AnimalDisguise power) {
                power.onTakeDamage();
            }
        }
        if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.INVISIBILITY)) {
            if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof Invisibility power) {
                power.onTakeDamage();
            }
        }
    }

    @Override
    public void onPrePlayerDamage(ServerPlayerEntity player, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getType() == player.getDamageSources().fall().getType() ||
            source.getType() == player.getDamageSources().stalagmite().getType() ||
            source.getType() == player.getDamageSources().flyIntoWall().getType()) {
            if (SuperpowersWildcard.hasActivePower(player, Superpowers.FLIGHT)) {
                if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof Flight power) {
                    if (power.isLaunchedUp) {
                        if (source.getType() != player.getDamageSources().flyIntoWall().getType()) power.isLaunchedUp = false;
                        cir.setReturnValue(false);
                        return;
                    }
                }
            }
            if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.TRIPLE_JUMP)) {
                if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof TripleJump power) {
                    if (power.isInAir) {
                        power.isInAir = false;
                        cir.setReturnValue(false);
                        return;
                    }
                }
            }
            if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.SUPER_PUNCH) && player.hasVehicle()) {
                if (player.getVehicle() instanceof ServerPlayerEntity) {
                    cir.setReturnValue(false);
                    return;
                }
            }
        }
    }

    @Override
    public void onRightClickEntity(ServerPlayerEntity player, World world, Hand hand, Entity entity, EntityHitResult hitResult) {
        if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.SUPER_PUNCH)) {
            if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof SuperPunch power) {
                power.tryRideEntity(entity);
            }
        }
    }

    @Override
    public void onAttackEntity(ServerPlayerEntity player, World world, Hand hand, Entity entity, EntityHitResult hitResult) {
        if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.INVISIBILITY)) {
            if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof Invisibility power) {
                power.onAttack();
            }
        }
    }

    @Override
    public void onUpdatedInventory(ServerPlayerEntity player) {
        super.onUpdatedInventory(player);
        Hunger.updateInventory(player);
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
