package net.mat0u5.lifeseries.block;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.mat0u5.lifeseries.Main;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;

public class Blocks {

    public static final Block SNOWY_GOLD_ORE = registerBlock("snowy_gold_ore",
            new DropExperienceBlock(
                    FabricBlockSettings.copyOf(Blocks.NETHER_GOLD_ORE),
                    UniformInt.of(0, 1)));
					
    public static final Block SNOWY_QUARTZ_ORE = registerBlock("snowy_quartz_ore",
            new DropExperienceBlock(
                    FabricBlockSettings.copyOf(Blocks.NETHER_QUARTZ_ORE),
                    UniformInt.of(2, 5)));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return net.minecraft.core.Registry.register(BuiltInRegistries.BLOCK,
                ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, name), block);
	}

    private static void registerBlockItem(String name, Block block) {
        net.minecraft.core.Registry.register(BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, name),
                new BlockItem(block, new Item.Properties()));
    }

    public static void registerBlocks() {
        Main.LOGGER.info("Registering Blocks for " + Main.MOD_ID);
		
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.NATURAL_BLOCKS).register(entries -> {
            entries.accept(SNOWY_GOLD_ORE);
            entries.accept(SNOWY_QUARTZ_ORE);
		});
    }
}