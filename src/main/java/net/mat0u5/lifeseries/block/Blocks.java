package net.mat0u5.lifeseries.block;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.mat0u5.lifeseries.Main;
import net.minecraft.block.Block;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class Blocks {

    public static final Block SNOWY_GOLD_ORE = registerBlock("snowy_gold_ore",
            new ExperienceDroppingBlock(
                    FabricBlockSettings.copyOf(net.minecraft.block.Blocks.NETHER_GOLD_ORE),
                    UniformIntProvider.create(0, 1)));
					
    public static final Block SNOWY_QUARTZ_ORE = registerBlock("snowy_quartz_ore",
            new ExperienceDroppingBlock(
                    FabricBlockSettings.copyOf(net.minecraft.block.Blocks.NETHER_QUARTZ_ORE),
                    UniformIntProvider.create(2, 5)));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(Main.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(Main.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    };

    public static void registerBlocks() {
        lifeseries.LOGGER.info("Registering Blocks for" + Main.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(entries -> {
            entries.add(SNOWY_GOLD_ORE);
            entries.add(SNOWY_QUARTZ_ORE);
		});
    }
}