package net.mat0u5.lifeseries.registries;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.mat0u5.lifeseries.Main;

public class ShipwreckRegister {

    public static void registerShipwrecks() {
        ServerWorldEvents.LOAD.register((server, world) -> {
            if (!world.dimension().equals(Level.OVERWORLD)) return;

            ChunkGenerator generator = world.getChunkSource().getGenerator();
            WorldGenSettings settings = generator.getSettings();

            if (!settings.toString().contains("COMPLEX_LIFE")) return;

            BiomeModifications.addStructure(
                    BiomeSelectors.includeByKey(Biome.PLAINS),
                    StructureFeature.SHIPWRECK
            );
        });
    }
}