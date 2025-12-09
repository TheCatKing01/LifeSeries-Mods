import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.core.registries.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeatureKeys;
import net.mat0u5.lifeseries.Main;

public class ShipwreckRegister {

    public static void registerShipwrecks() {
        ServerWorldEvents.LOAD.register((server, world) -> {
            if (!world.dimension().equals(Level.OVERWORLD)) return;

            ResourceKey<StructureFeature<?>> shipwreckKey = StructureFeatureKeys.SHIPWRECK;

            BiomeModifications.addStructure(
                    BiomeSelectors.includeByKey(BiomeKeys.PLAINS),
                    BuiltinRegistries.STRUCTURE_FEATURE.get(shipwreckKey.location())
            );
        });
    }
}
