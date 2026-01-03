package net.mat0u5.lifeseries.block;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.mat0u5.lifeseries.Main;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class Blocks {

    //? if <= 1.20.5 {
    private static BlockBehaviour.Properties copyProps(Block block) {
        return BlockBehaviour.Properties.ofFullCopy(block);
    }
    //?} else {
    private static BlockBehaviour.Properties copyProps(Block block) {
        return BlockBehaviour.Properties.copy(block);
    }
    //?}

    public static final Block SNOWY_GOLD_ORE = registerBlock(
            "snowy_gold_ore",
            new DropExperienceBlock(
                    copyProps(net.minecraft.world.level.block.Blocks.NETHER_GOLD_ORE),
                    UniformInt.of(0, 1)
            )
    );

    public static final Block SNOWY_QUARTZ_ORE = registerBlock(
            "snowy_quartz_ore",
            new DropExperienceBlock(
                    copyProps(net.minecraft.world.level.block.Blocks.NETHER_QUARTZ_ORE),
                    UniformInt.of(2, 5)
            )
    );

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(
                BuiltInRegistries.BLOCK,
                new ResourceLocation(Main.MOD_ID, name),
                block
        );
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(
                BuiltInRegistries.ITEM,
                new ResourceLocation(Main.MOD_ID, name),
                new BlockItem(block, new Item.Properties())
        );
    }

    public static void registerBlocks() {
        Main.LOGGER.info("Registering Blocks for " + Main.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.NATURAL_BLOCKS).register(entries -> {
            entries.accept(SNOWY_GOLD_ORE);
            entries.accept(SNOWY_QUARTZ_ORE);
        });
    }
}
