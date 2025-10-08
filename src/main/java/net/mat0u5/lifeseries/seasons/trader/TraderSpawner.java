package net.mat0u5.lifeseries.seasons.trader;

import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.config.DefaultConfigValues;
import net.mat0u5.lifeseries.seasons.season.thirdlife.ThirdLife;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.TraderLlamaEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradedItem;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldView;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestTypes;

import java.util.Optional;
import java.util.Random;

public class TraderSpawner extends ThirdLife {

    private final Random rnd = new Random();
    private int checkCooldown = 0;

    /** Always fetch latest config */
    private DefaultConfigValues config() {
        return ConfigManager.getConfig();
    }

    @Override
    public void tick(MinecraftServer server) {
        super.tick(server);

        // Debug: log active config
        logActiveConfig();

        if (!config().SIMPLE_LIFE.get(config())) return;

        if (--checkCooldown > 0) return;
        checkCooldown = 1200; // 1 minute

        ServerWorld world = server.getOverworld();
        if (world == null) return;

        for (int i = 0; i < 5; i++) {
            if (trySpawnTrader(world)) break;
        }
    }

    private boolean trySpawnTrader(ServerWorld world) {
        PlayerEntity player = world.getRandomAlivePlayer();
        if (player == null) return false;

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
        BlockPos spawnPos = getNearbySpawnPos(world, spawnCenter, 64);
        if (spawnPos == null) {
            log("No valid spawn position near " + player.getName().getString());
            return false;
        }

        if (!isTwoBlockHighAir(world, spawnPos)) {
            log("Spawn position blocked at " + spawnPos);
            return false;
        }

        WanderingTraderEntity trader = EntityType.WANDERING_TRADER.create(world);
        if (trader == null) {
            log("Failed to create trader entity.");
            return false;
        }

        trader.refreshPositionAndAngles(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                rnd.nextFloat() * 360f, 0f);

        if (!world.spawnEntity(trader)) {
            log("world.spawnEntity(trader) returned false at " + spawnPos);
            return false;
        }

        log("Spawned Wandering Trader at " + spawnPos);

        // Spawn llamas
        for (int j = 0; j < 2; j++) spawnLlama(world, trader, 4);

        trader.setDespawnDelay(12000);

        // Prepare trades dynamically based on current config
        TradeOfferList offers = new TradeOfferList();
        if (config().COMPLEX_LIFE_TRADES.get(config())) {
            addComplexLifeTrades(offers);
        } else {
            addSimpleLifeTrades(offers);
        }

        trader.setOffersFromServer(offers);
        return true;
    }

    private void spawnLlama(ServerWorld world, WanderingTraderEntity trader, int range) {
        BlockPos pos = getNearbySpawnPos(world, trader.getBlockPos(), range);
        if (pos == null) return;

        TraderLlamaEntity llama = EntityType.TRADER_LLAMA.create(world);
        if (llama == null) return;

        llama.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0f, 0f);
        if (world.spawnEntity(llama)) {
            llama.attachLeash(trader, true);
        }
    }

    private void addSimpleLifeTrades(TradeOfferList offers) {
        offers.add(new TradeOffer(new TradedItem(Items.DIRT, 5), Optional.empty(), Items.IRON_INGOT.getDefaultStack(), 0, 999999, 0, 0, 0));
        offers.add(new TradeOffer(new TradedItem(Items.DIRT, 40), Optional.empty(), Items.WATER_BUCKET.getDefaultStack(), 0, 999999, 0, 0, 0));
        offers.add(new TradeOffer(new TradedItem(Items.DIRT, 40), Optional.empty(), Items.LAVA_BUCKET.getDefaultStack(), 0, 999999, 0, 0, 0));
        offers.add(new TradeOffer(new TradedItem(Items.DIRT, 1), Optional.empty(), Items.SAND.getDefaultStack(), 0, 999999, 0, 0, 0));
        offers.add(new TradeOffer(new TradedItem(Items.DIRT, 1), Optional.empty(), Items.GRAVEL.getDefaultStack(), 0, 999999, 0, 0, 0));
        offers.add(new TradeOffer(new TradedItem(Items.DIRT, 5), Optional.empty(), Items.GOLD_INGOT.getDefaultStack(), 0, 999999, 0, 0, 0));
        offers.add(new TradeOffer(new TradedItem(Items.DIRT, 1), Optional.empty(), Items.REDSTONE.getDefaultStack(), 0, 999999, 0, 0, 0));

        int rand = rnd.nextInt(2);
        if (rand == 0)
            offers.add(new TradeOffer(new TradedItem(Items.DIRT, 32), Optional.empty(), Items.OAK_SAPLING.getDefaultStack(), 0, 999999, 0, 0, 0));
        else
            offers.add(new TradeOffer(new TradedItem(Items.DIRT, 32), Optional.empty(), Items.SPRUCE_SAPLING.getDefaultStack(), 0, 999999, 0, 0, 0));
    }

    private void addComplexLifeTrades(TradeOfferList offers) {
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
        if (rand == 0)
            offers.add(new TradeOffer(new TradedItem(Items.DIAMOND, 5), Optional.empty(), Items.NETHERITE_SCRAP.getDefaultStack(), 0, 999999, 0, 0, 0));
        else
            offers.add(new TradeOffer(new TradedItem(Items.DIAMOND, 10), Optional.empty(), Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE.getDefaultStack(), 0, 999999, 0, 0, 0));

        offers.add(new TradeOffer(new TradedItem(Items.DIAMOND, 3), Optional.empty(), Items.CREEPER_SPAWN_EGG.getDefaultStack(), 0, 999999, 0, 0, 0));
        offers.add(new TradeOffer(new TradedItem(Items.DIAMOND, 16), Optional.empty(), Items.END_CRYSTAL.getDefaultStack(), 0, 999999, 0, 0, 0));
    }

    private BlockPos getNearbySpawnPos(WorldView world, BlockPos pos, int range) {
        for (int i = 0; i < 10; i++) {
            int x = pos.getX() + rnd.nextInt(range * 2) - range;
            int z = pos.getZ() + rnd.nextInt(range * 2) - range;
            int y = world.getTopY(Heightmap.Type.WORLD_SURFACE, x, z);
            BlockPos candidate = new BlockPos(x, y, z);
            if (isTwoBlockHighAir(world, candidate)) return candidate;
        }
        return null;
    }

    private boolean isTwoBlockHighAir(BlockView world, BlockPos pos) {
        return world.getBlockState(pos).isAir() && world.getBlockState(pos.up()).isAir();
    }

    private void log(String msg) {
        System.out.println("[TraderSpawner] " + msg);
    }

    private void logActiveConfig() {
        System.out.println("[TraderSpawner] SIMPLE_LIFE=" + config().SIMPLE_LIFE.get(config()) +
                ", COMPLEX_LIFE_TRADES=" + config().COMPLEX_LIFE_TRADES.get(config()));
    }
}
