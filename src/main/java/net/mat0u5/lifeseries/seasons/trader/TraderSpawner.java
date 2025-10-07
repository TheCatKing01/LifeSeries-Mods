package net.mat0u5.lifeseries.seasons.trader;

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

import java.util.Optional;
import java.util.Random;

public class TraderSpawner extends ThirdLife {

    private final DefaultConfigValues config;
    private final Random rnd = new Random();
    private int checkCooldown = 0;

    @Override
    public void tick(MinecraftServer server) {
        super.tick(server);

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
