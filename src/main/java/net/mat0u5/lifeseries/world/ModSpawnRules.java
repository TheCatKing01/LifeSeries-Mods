package net.mat0u5.lifeseries.world;

import net.mat0u5.lifeseries.entity.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;

public class ModSpawnRules {

    public static void register() {
        SpawnPlacements.register(
            ModEntities.ANGRY_SNOWMAN,
            SpawnPlacementTypes.ON_GROUND,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            ModSpawnRules::canSpawn
        );
    }

    private static boolean canSpawn(
        EntityType<?> type,
        LevelAccessor level,
        MobSpawnType reason,
        BlockPos pos,
        RandomSource random
    ) {
        // Zombie-style darkness check
        if (!Monster.isDarkEnoughToSpawn(level, pos, random)) return false;

        // No caves
        if (!level.canSeeSky(pos)) return false;

        // Optional: strictly night only
        if (level.getLevelData().isDay()) return false;

        return Monster.checkMonsterSpawnRules(
            type, level, reason, pos, random
        );
    }
}
