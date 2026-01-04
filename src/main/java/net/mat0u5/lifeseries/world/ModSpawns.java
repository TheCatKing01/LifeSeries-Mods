package net.mat0u5.lifeseries.world;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.mat0u5.lifeseries.entity.ModEntities;
import net.minecraft.world.entity.MobCategory;

public class ModSpawns {

    public static void register() {
        BiomeModifications.addSpawn(
            BiomeSelectors.foundInOverworld(),
            MobCategory.MONSTER,
            ModEntities.ANGRY_SNOWMAN,
            80, // weight (similar to zombies)
            1,  // min group
            3   // max group
        );
    }
}
