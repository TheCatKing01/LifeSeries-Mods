package net.mat0u5.lifeseries.registries;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeKeys;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.ShipwreckFeatureConfiguration;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.mat0u5.lifeseries.Main;

public class ShipwreckRegister {

    private static ConfiguredStructureFeature<ShipwreckFeatureConfiguration, ?> PLAINS_SHIPWRECK;

    public static void registerShipwrecks() {
        PLAINS_SHIPWRECK = StructureFeature.SHIPWRECK
                .configured(new ShipwreckFeatureConfiguration(true, true))
        ;

        Registry.register(
                BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE,
                new ResourceLocation(Main.MODID, "plains_shipwreck"),
                PLAINS_SHIPWRECK
        );

        ServerWorldEvents.LOAD.register((server, world) -> {
            if (!world.dimension().equals(Level.OVERWORLD)) return;

            WorldPreset preset = world.getChunkSource().getGenerator().getSettings();
            if (!preset.key().equals(ModRegistries.COMPLEX_LIFE)) return;

            BiomeModifications.addStructure(
                    BiomeSelectors.includeByKey(BiomeKeys.PLAINS),
                    PLAINS_SHIPWRECK
            );
        });
    }
}