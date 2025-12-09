package net.mat0u5.lifeseries.registries;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.StructureSets;
import net.minecraft.world.level.levelgen.WorldGenSettings;

public class ShipwreckRegister {

    public static void registerShipwrecks() {
        ServerWorldEvents.LOAD.register((server, world) -> {
            if (!world.dimension().equals(Level.OVERWORLD)) return;

            WorldGenSettings generatorSettings = world.getChunkSource().getGenerator().getSettings();
            if (!"generator.lifeseries.complex_life".equals(generatorSettings.toString())) {
                return;
            }

            BiomeModifications.addStructure(
                    BiomeSelectors.all(),
                    StructureSets.SHIPWRECK
            );
        });
    }
}
