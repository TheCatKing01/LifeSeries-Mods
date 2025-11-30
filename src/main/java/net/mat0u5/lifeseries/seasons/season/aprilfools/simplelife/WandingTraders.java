package net.mat0u5.lifeseries.seasons.season.aprilfools.simplelife;

import net.mat0u5.lifeseries.utils.world.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Iterator;
import java.util.Optional;
import java.util.Random;

import net.minecraft.world.entity.animal.horse.TraderLlama;
import net.minecraft.world.entity.npc.WanderingTrader;

public class WandingTraders {

    private final Random rnd = new Random();
    private int checkCooldown = 0;

    public void tickSessionOn(MinecraftServer server) {

        checkCooldown--;

        if (checkCooldown > 0) return;

        ServerLevel level = server.overworld();
        if (level == null) return;

        int traderCount = 0;
        for (Entity e : level.getAllEntities()) {
            if (e instanceof WanderingTrader) traderCount++;
        }

        if (traderCount == 0)       checkCooldown = 1200;
        else if (traderCount == 1) checkCooldown = 3600;
        else if (traderCount == 2) checkCooldown = 200;
        else return; // traderCount >= 3

        for (int i = 0; i < 5; i++) {
            if (trySpawnTrader(level)) break;
        }
    }

    public boolean trySpawnTrader(ServerLevel level) {

        Player player = level.getRandomPlayer();
        if (player == null) return true;

        BlockPos pos = player.blockPosition();
        PoiManager poi = level.getPoiManager();

        Optional<BlockPos> nearest = poi.find(
                type -> type.is(PoiTypes.MEETING),
                p -> true,
                pos, 64,
                PoiManager.Occupancy.ANY
        );

        BlockPos meetingPos = nearest.orElse(pos);
        BlockPos spawnPos = getNearbySpawnPos(level, meetingPos, 64);

        if (spawnPos == null) return false;
        if (!doesNotSuffocateAt(level, spawnPos)) return false;

        WanderingTrader trader =
                LevelUtils.spawnEntity(EntityType.WANDERING_TRADER, level, spawnPos);

        if (trader == null) return false;

        // spawn llamas
        for (int i = 0; i < 2; i++) {
            spawnLlama(level, trader, 4);
        }

        trader.setDespawnDelay(12000);

        // custom trades
        MerchantOffers offers = trader.getOffers();
        offers.clear();

        offers.add(new MerchantOffer(new ItemCost(Items.DIRT, 5), Optional.empty(),
                Items.IRON_INGOT.getDefaultInstance(), 0, 999999, 0, 0, 0));

        offers.add(new MerchantOffer(new ItemCost(Items.DIRT, 40), Optional.empty(),
                Items.WATER_BUCKET.getDefaultInstance(), 0, 999999, 0, 0, 0));

        offers.add(new MerchantOffer(new ItemCost(Items.DIRT, 40), Optional.empty(),
                Items.LAVA_BUCKET.getDefaultInstance(), 0, 999999, 0, 0, 0));

        offers.add(new MerchantOffer(new ItemCost(Items.DIRT, 1), Optional.empty(),
                Items.SAND.getDefaultInstance(), 0, 999999, 0, 0, 0));

        offers.add(new MerchantOffer(new ItemCost(Items.DIRT, 1), Optional.empty(),
                Items.GRAVEL.getDefaultInstance(), 0, 999999, 0, 0, 0));

        offers.add(new MerchantOffer(new ItemCost(Items.DIRT, 5), Optional.empty(),
                Items.GOLD_INGOT.getDefaultInstance(), 0, 999999, 0, 0, 0));

        offers.add(new MerchantOffer(new ItemCost(Items.DIRT, 1), Optional.empty(),
                Items.REDSTONE.getDefaultInstance(), 0, 999999, 0, 0, 0));

        int rand = rnd.nextInt(2);
        if (rand == 0) {
            offers.add(new MerchantOffer(new ItemCost(Items.DIRT, 32), Optional.empty(),
                    Items.OAK_SAPLING.getDefaultInstance(), 0, 999999, 0, 0, 0));
        } else {
            offers.add(new MerchantOffer(new ItemCost(Items.DIRT, 32), Optional.empty(),
                    Items.SPRUCE_SAPLING.getDefaultInstance(), 0, 999999, 0, 0, 0));
        }

        trader.overrideOffers(offers);
        trader.addTag("SimpleLifeTrader");

        return true;
    }

    private void spawnLlama(ServerLevel level, WanderingTrader trader, int range) {
        BlockPos pos = getNearbySpawnPos(level, trader.blockPosition(), range);
        if (pos != null) {
            TraderLlama llama = LevelUtils.spawnEntity(EntityType.TRADER_LLAMA, level, pos);
            if (llama != null) llama.setLeashedTo(trader, true);
        }
    }

    private BlockPos getNearbySpawnPos(LevelReader world, BlockPos pos, int range) {
        for (int i = 0; i < 10; i++) {
            int x = pos.getX() + rnd.nextInt(range * 2) - range;
            int z = pos.getZ() + rnd.nextInt(range * 2) - range;
            int y = world.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);

            BlockPos tryPos = new BlockPos(x, y, z);
            // NOTE: update if using new SpawnPlacements API
            if (SpawnPlacements.getPlacementType(EntityType.WANDERING_TRADER)
                    .isSpawnPositionOk(world, tryPos, EntityType.WANDERING_TRADER)) {
                return tryPos;
            }
        }
        return null;
    }

    private boolean doesNotSuffocateAt(BlockGetter world, BlockPos pos) {
        for (BlockPos p : BlockPos.betweenClosed(pos, pos.offset(1, 2, 1))) {
            if (!world.getBlockState(p).getCollisionShape(world, p).isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
