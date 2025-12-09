package net.mat0u5.lifeseries.registries;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;

public class ShipwreckRegister {

    private static final ResourceKey<Structure<?>> SHIPWRECK_KEY = ResourceKey.create(
            Registry.STRUCTURE_REGISTRY,
            new ResourceLocation("minecraft", "shipwreck")
    );

    public static void registerShipwrecks() {
        ServerWorldEvents.LOAD.register((server, world) -> {
            if (!world.dimension().equals(Level.OVERWORLD)) return;

            if (!(world instanceof ServerLevel serverLevel)) return;
            ResourceLocation generatorName = serverLevel.getChunkSource().getGenerator().getType().getRegistryName();
            if (generatorName == null || !generatorName.toString().equals("generator.lifeseries.complex_life")) return;

            BiomeModifications.addStructure(
                    BiomeSelectors.all(),
                    SHIPWRECK_KEY
            );
        });
    }
}
