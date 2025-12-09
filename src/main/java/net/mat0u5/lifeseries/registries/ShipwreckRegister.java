package net.mat0u5.lifeseries.registries;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.StructureFeatures;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.mat0u5.lifeseries.Main;

public class ShipwreckRegister {

    public static void registerShipwrecks() {
        ServerWorldEvents.LOAD.register((server, world) -> {
            if (!world.dimension().equals(Level.OVERWORLD)) return;

            ChunkGenerator generator = world.getChunkSource().getGenerator();

            WorldPreset preset = generator.getSettings();
            if (!preset.key().equals(ModRegistries.COMPLEX_LIFE)) return;

            BiomeModifications.addStructure(
                    BiomeSelectors.foundInOverworld(),
                    StructureFeatures.SHIPWRECK
            );
        });
    }
}
