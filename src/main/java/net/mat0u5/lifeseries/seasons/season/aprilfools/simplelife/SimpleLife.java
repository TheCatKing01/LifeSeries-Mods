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
    private final DefaultConfigValues config;
    private int checkCooldown = 0;

    public SimpleLife(DefaultConfigValues config) {
        this.config = config;
    }

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
        checkCooldown--;
        if (checkCooldown <= 0) {
            checkCooldown = 1200; // 1 Minute
            ServerWorld world = server.getOverworld();
            if (world == null) return;

            int traderCount = 0;
            for (Entity entity : world.iterateEntities()) {
                if (entity instanceof WanderingTraderEntity) traderCount++;
            }

            if (traderCount == 0) checkCooldown = 1200;
            if (traderCount == 1) checkCooldown = 3600;
            if (traderCount >= 2) checkCooldown = 200;
            if (traderCount >= 3) return;

            for (int i = 0; i < 5; i++) {
                if (trySpawnTrader(world)) break;
            }
        }
    }

    public boolean trySpawnTrader(ServerWorld world) {
        PlayerEntity playerEntity = world.getRandomAlivePlayer();
        if (playerEntity == null) return true;

        BlockPos playerPos = playerEntity.getBlockPos();
        PointOfInterestStorage poiStorage = world.getPointOfInterestStorage();

        Optional<BlockPos> poiPosOpt = poiStorage.getPosition(
                poiType -> poiType.matchesKey(PointOfInterestTypes.MEETING),
                pos -> true,
                playerPos, 64, PointOfInterestStorage.OccupationStatus.ANY
        );

        BlockPos referencePos = poiPosOpt.orElse(playerPos);
        BlockPos spawnPos = getNearbySpawnPos(world, referencePos, 64);

        if (spawnPos != null && doesNotSuffocateAt(world, spawnPos)) {
            WanderingTraderEntity trader = (WanderingTraderEntity) EntityType.WANDERING_TRADER.spawn(
                    world, spawnPos, SpawnReason.EVENT
            );

            if (trader != null) {
                // Spawn llamas
                for (int j = 0; j < 2; j++) spawnLlama(world, trader, 4);

                trader.setDespawnDelay(12000);

                TradeOfferList offers = trader.getOffers();
                offers.clear();

                // Use custom Complex Life trades if enabled
                if (config.COMPLEX_LIFE_TRADES.get()) {
                    offers.add(new TradeOffer(new TradedItem(Items.DIAMOND, 1), Optional.empty(), Items.EMERALD.getDefaultStack(), 0, 999999, 0, 0, 0));
                    offers.add(new TradeOffer(new TradedItem(Items.WHEAT, 32), Optional.empty(), Items.BREAD.getDefaultStack(), 0, 999999, 0, 0, 0));
                    offers.add(new TradeOffer(new TradedItem(Items.IRON_INGOT, 10), Optional.empty(), Items.GOLD_INGOT.getDefaultStack(), 0, 999999, 0, 0, 0));
                    // Add more custom trades here
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
        BlockPos pos = getNearbySpawnPos(world, trader.getBlockPos(), range);
        if (pos != null) {
            TraderLlamaEntity llama = (TraderLlamaEntity) EntityType.TRADER_LLAMA.spawn(world, pos, SpawnReason.EVENT);
            if (llama != null) llama.attachLeash(trader, true);
        }
    }

    private BlockPos getNearbySpawnPos(WorldView world, BlockPos pos, int range) {
        BlockPos result = null;
        SpawnLocation spawnLocation = SpawnRestriction.getLocation(EntityType.WANDERING_TRADER);

        for (int i = 0; i < 10; i++) {
            int x = pos.getX() + rnd.nextInt(range * 2) - range;
            int z = pos.getZ() + rnd.nextInt(range * 2) - range;
            int y = world.getTopY(Heightmap.Type.WORLD_SURFACE, x, z);
            BlockPos checkPos = new BlockPos(x, y, z);
            if (spawnLocation.isSpawnPositionOk(world, checkPos, EntityType.WANDERING_TRADER)) {
                result = checkPos;
                break;
            }
        }

        return result;
    }

    private boolean doesNotSuffocateAt(BlockView world, BlockPos pos) {
        Iterator<BlockPos> it = BlockPos.iterate(pos, pos.add(1, 2, 1)).iterator();
        while (it.hasNext()) {
            BlockPos check = it.next();
            if (!world.getBlockState(check).getCollisionShape(world, check).isEmpty()) return false;
        }
        return true;
    }
}