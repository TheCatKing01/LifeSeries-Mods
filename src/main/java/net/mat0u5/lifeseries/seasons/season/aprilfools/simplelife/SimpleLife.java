package net.mat0u5.lifeseries.seasons.season.aprilfools.simplelife;

import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.config.DefaultConfigValues;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.thirdlife.ThirdLife;
import net.minecraft.entity.*;
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

import java.util.Iterator;
import java.util.Optional;
import java.util.Random;

public class SimpleLife extends ThirdLife {
    private final Random rnd = new Random();
    private int checkCooldown = 0;

    @Override
    public Seasons getSeason() {
        return Seasons.SIMPLE_LIFE;
    }

    @Override
    public ConfigManager createConfig() {
        return new SimpleLifeConfig();
    }

    @Override
    public void tick(MinecraftServer server) {
        super.tick(server);

        // Config access
        ConfigManager config = getConfig();
        DefaultConfigValues defaults = new DefaultConfigValues();

        boolean enableSimpleLife = config.getBoolean(defaults.ENABLE_SIMPLE_LIFE_TRADERS);
        boolean complexTrades = config.getBoolean(defaults.COMPLEX_LIFE_TRADES);

        // Only run trader spawning if the "Simple Life" config tab is enabled
        if (!enableSimpleLife) return;

        checkCooldown--;
        if (checkCooldown > 0) return;

        checkCooldown = 1200; // 1 minute default
        ServerWorld world = server.getOverworld();
        if (world == null) return;

        int traderCount = 0;
        for (Entity entity : world.iterateEntities()) {
            if (entity instanceof WanderingTraderEntity) {
                traderCount++;
            }
        }

        if (traderCount == 0) checkCooldown = 1200; // 1 Minute
        else if (traderCount == 1) checkCooldown = 3600; // 3 Minutes
        else if (traderCount >= 2) checkCooldown = 200;
        if (traderCount >= 3) return;

        for (int i = 0; i < 5; i++) {
            if (trySpawnTrader(world, complexTrades)) break;
        }
    }

    public boolean trySpawnTrader(ServerWorld world, boolean complexTrades) {
        PlayerEntity playerEntity = world.getRandomAlivePlayer();
        if (playerEntity == null) return true;

        BlockPos blockPos = playerEntity.getBlockPos();
        PointOfInterestStorage poiStorage = world.getPointOfInterestStorage();
        Optional<BlockPos> optional = poiStorage.getPosition(
                (poiType) -> poiType.matchesKey(PointOfInterestTypes.MEETING),
                (pos) -> true,
                blockPos, 64, PointOfInterestStorage.OccupationStatus.ANY
        );
        BlockPos meetingPos = optional.orElse(blockPos);
        BlockPos spawnPos = this.getNearbySpawnPos(world, meetingPos, 64);

        if (spawnPos == null || !this.doesNotSuffocateAt(world, spawnPos)) return false;

        WanderingTraderEntity trader = EntityType.WANDERING_TRADER.spawn(world, spawnPos, SpawnReason.EVENT);
        if (trader == null) return false;

        for (int j = 0; j < 2; ++j) {
            this.spawnLlama(world, trader, 4);
        }

        trader.setDespawnDelay(12000);

        // Assign trade set based on config
        TradeOfferList offers = new TradeOfferList();
        if (!complexTrades) {
            // Default Simple Life trades
              offers.add(new TradeOffer(new TradedItem(Items.DIRT, 5), Optional.empty(), Items.IRON_INGOT.getDefaultStack(), 0, 999999, 0, 0, 0));
              offers.add(new TradeOffer(new TradedItem(Items.DIRT, 40), Optional.empty(), Items.WATER_BUCKET.getDefaultStack(), 0, 999999, 0, 0, 0));
              offers.add(new TradeOffer(new TradedItem(Items.DIRT, 40), Optional.empty(), Items.LAVA_BUCKET.getDefaultStack(), 0, 999999, 0, 0, 0));
              offers.add(new TradeOffer(new TradedItem(Items.DIRT, 1), Optional.empty(), Items.SAND.getDefaultStack(), 0, 999999, 0, 0, 0));
              offers.add(new TradeOffer(new TradedItem(Items.DIRT, 1), Optional.empty(), Items.GRAVEL.getDefaultStack(), 0, 999999, 0, 0, 0));
              offers.add(new TradeOffer(new TradedItem(Items.DIRT, 5), Optional.empty(), Items.GOLD_INGOT.getDefaultStack(), 0, 999999, 0, 0, 0));
              offers.add(new TradeOffer(new TradedItem(Items.DIRT, 1), Optional.empty(), Items.REDSTONE.getDefaultStack(), 0, 999999, 0, 0, 0));
        } else {
            // Complex Life trades (placeholder – same as above for now)
            offers.add(new TradeOffer(new TradedItem(Items.DIRT, 32), Optional.empty(), Items.OAK_SAPLING.getDefaultStack(), 0, 999999, 0, 0, 0));
            offers.add(new TradeOffer(new TradedItem(Items.DIRT, 10), Optional.empty(), Items.SKELETON_SPAWN_EGG.getDefaultStack(), 0, 999999, 0, 0, 0));
            offers.add(new TradeOffer(new TradedItem(Items.DIRT, 40), Optional.empty(), Items.LAVA_BUCKET.getDefaultStack(), 0, 999999, 0, 0, 0));
            offers.add(new TradeOffer(new TradedItem(Items.DIRT, 1), Optional.empty(), Items.SAND.getDefaultStack(), 0, 999999, 0, 0, 0));
            offers.add(new TradeOffer(new TradedItem(Items.DIRT, 1), Optional.empty(), Items.GRAVEL.getDefaultStack(), 0, 999999, 0, 0, 0));
            offers.add(new TradeOffer(new TradedItem(Items.DIRT, 5), Optional.empty(), Items.GOLD_INGOT.getDefaultStack(), 0, 999999, 0, 0, 0));
            offers.add(new TradeOffer(new TradedItem(Items.DIRT, 1), Optional.empty(), Items.REDSTONE.getDefaultStack(), 0, 999999, 0, 0, 0));
        }

        trader.setOffersFromServer(offers);
        return true;
    }

    private void spawnLlama(ServerWorld world, WanderingTraderEntity wanderingTrader, int range) {
        BlockPos pos = this.getNearbySpawnPos(world, wanderingTrader.getBlockPos(), range);
        if (pos != null) {
            TraderLlamaEntity llama = EntityType.TRADER_LLAMA.spawn(world, pos, SpawnReason.EVENT);
            if (llama != null) llama.attachLeash(wanderingTrader, true);
        }
    }

    private BlockPos getNearbySpawnPos(WorldView world, BlockPos pos, int range) {
        SpawnLocation spawnLocation = SpawnRestriction.getLocation(EntityType.WANDERING_TRADER);
        for (int i = 0; i < 10; ++i) {
            int x = pos.getX() + rnd.nextInt(range * 2) - range;
            int z = pos.getZ() + rnd.nextInt(range * 2) - range;
            int y = world.getTopY(Heightmap.Type.WORLD_SURFACE, x, z);
            BlockPos testPos = new BlockPos(x, y, z);
            if (spawnLocation.isSpawnPositionOk(world, testPos, EntityType.WANDERING_TRADER)) return testPos;
        }
        return null;
    }

    private boolean doesNotSuffocateAt(BlockView world, BlockPos pos) {
        for (BlockPos blockPos : BlockPos.iterate(pos, pos.add(1, 2, 1))) {
            if (!world.getBlockState(blockPos).getCollisionShape(world, blockPos).isEmpty()) return false;
        }
        return true;
    }
}