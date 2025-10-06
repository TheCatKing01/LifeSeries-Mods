package net.mat0u5.lifeseries.seasons.season.aprilfools.simplelife;

import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.config.DefaultConfigValues;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.thirdlife.ThirdLife;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
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

        // Access the config
        ConfigManager config = ConfigManager.get();
        DefaultConfigValues defaults = new DefaultConfigValues();

        // Read config values
        boolean enableSimpleLife = config.get(defaults.GROUP_SIMPLE_LIFE);
        boolean complexTrades = config.get(defaults.SIMPLE_LIFE_COMPLEX_TRADES);

        // If the main Simple Life toggle is off, skip
        if (!enableSimpleLife) return;

        checkCooldown--;
        if (checkCooldown <= 0) {
            checkCooldown = 1200; // 1 minute
            ServerWorld world = server.getOverworld();
            if (world == null) return;

            int traderCount = 0;
            for (Entity entity : world.iterateEntities()) {
                if (entity instanceof WanderingTraderEntity) {
                    traderCount++;
                }
            }

            if (traderCount == 0) checkCooldown = 1200;
            if (traderCount == 1) checkCooldown = 3600;
            if (traderCount >= 2) checkCooldown = 200;
            if (traderCount >= 3) return;

            for (int i = 0; i < 5; i++) {
                if (trySpawnTrader(world, complexTrades)) {
                    break;
                }
            }
        }
    }

    public boolean trySpawnTrader(ServerWorld world, boolean complexTrades) {
        PlayerEntity playerEntity = world.getRandomAlivePlayer();
        if (playerEntity == null) {
            return true;
        } else {
            BlockPos blockPos = playerEntity.getBlockPos();
            PointOfInterestStorage pointOfInterestStorage = world.getPointOfInterestStorage();
            Optional<BlockPos> optional = pointOfInterestStorage.getPosition(
                    poiType -> poiType.matchesKey(PointOfInterestTypes.MEETING),
                    pos -> true,
                    blockPos,
                    64,
                    PointOfInterestStorage.OccupationStatus.ANY
            );

            BlockPos blockPos2 = optional.orElse(blockPos);
            BlockPos blockPos3 = this.getNearbySpawnPos(world, blockPos2, 64);
            if (blockPos3 != null && this.doesNotSuffocateAt(world, blockPos3)) {
                WanderingTraderEntity wanderingTraderEntity =
                        (WanderingTraderEntity) EntityType.WANDERING_TRADER.spawn(world, blockPos3, SpawnReason.EVENT);

                if (wanderingTraderEntity != null) {
                    for (int j = 0; j < 2; ++j) {
                        this.spawnLlama(world, wanderingTraderEntity, 4);
                    }

                    wanderingTraderEntity.setDespawnDelay(12000);

                    // Create trade offers based on config
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
                        // Complex Life trades (alternate set)
                        offers.add(new TradeOffer(new TradedItem(Items.STONE, 16), Optional.empty(), Items.DIAMOND.getDefaultStack(), 0, 999999, 0, 0, 0));
                        offers.add(new TradeOffer(new TradedItem(Items.GRAVEL, 20), Optional.empty(), Items.EMERALD.getDefaultStack(), 0, 999999, 0, 0, 0));
                        offers.add(new TradeOffer(new TradedItem(Items.COBBLESTONE, 40), Optional.empty(), Items.NETHERITE_SCRAP.getDefaultStack(), 0, 999999, 0, 0, 0));
                        offers.add(new TradeOffer(new TradedItem(Items.STONE, 10), Optional.empty(), Items.WATER_BUCKET.getDefaultStack(), 0, 999999, 0, 0, 0));
                        offers.add(new TradeOffer(new TradedItem(Items.STONE, 1), Optional.empty(), Items.IRON_INGOT.getDefaultStack(), 0, 999999, 0, 0, 0));
                        offers.add(new TradeOffer(new TradedItem(Items.STONE, 5), Optional.empty(), Items.REDSTONE_BLOCK.getDefaultStack(), 0, 999999, 0, 0, 0));
                        offers.add(new TradeOffer(new TradedItem(Items.STONE, 2), Optional.empty(), Items.EXPERIENCE_BOTTLE.getDefaultStack(), 0, 999999, 0, 0, 0));
                    }

                    wanderingTraderEntity.setOffersFromServer(offers);
                    return true;
                }
            }

            return false;
        }
    }

    private void spawnLlama(ServerWorld world, WanderingTraderEntity wanderingTrader, int range) {
        BlockPos blockPos = this.getNearbySpawnPos(world, wanderingTrader.getBlockPos(), range);
        if (blockPos != null) {
            TraderLlamaEntity traderLlamaEntity =
                    (TraderLlamaEntity) EntityType.TRADER_LLAMA.spawn(world, blockPos, SpawnReason.EVENT);
            if (traderLlamaEntity != null) {
                traderLlamaEntity.attachLeash(wanderingTrader, true);
            }
        }
    }

    private BlockPos getNearbySpawnPos(WorldView world, BlockPos pos, int range) {
        BlockPos blockPos = null;
        SpawnRestriction.Location spawnLocation = SpawnRestriction.getLocation(EntityType.WANDERING_TRADER);

        for (int i = 0; i < 10; ++i) {
            int j = pos.getX() + rnd.nextInt(range * 2) - range;
            int k = pos.getZ() + rnd.nextInt(range * 2) - range;
            int l = world.getTopY(Heightmap.Type.WORLD_SURFACE, j, k);
            BlockPos blockPos2 = new BlockPos(j, l, k);
            if (spawnLocation.isSpawnPositionOk(world, blockPos2, EntityType.WANDERING_TRADER)) {
                blockPos = blockPos2;
                break;
            }
        }

        return blockPos;
    }

    private boolean doesNotSuffocateAt(BlockView world, BlockPos pos) {
        Iterator<BlockPos> it = BlockPos.iterate(pos, pos.add(1, 2, 1)).iterator();

        while (it.hasNext()) {
            BlockPos checkPos = it.next();
            if (!world.getBlockState(checkPos).getCollisionShape(world, checkPos).isEmpty()) {
                return false;
            }
        }
        return true;
    }
}