package net.mat0u5.lifeseries.block;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.mat0u5.lifeseries.Main;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

// Default (raw file) must compile on :1.21 (Mojang mappings):
import net.minecraft.resources.ResourceLocation;

/*//? if >= 1.21.11 {
import net.minecraft.util.Identifier;
*///?}

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class Blocks {

    private static BlockBehaviour.Properties copyProps(Block block) {
        return BlockBehaviour.Properties.ofFullCopy(block);

        /*//? if <= 1.20.2 {
        return BlockBehaviour.Properties.copy(block);
        *///?}
    }

    private static Block xpOre(Block base, int minXp, int maxXp) {
        return new DropExperienceBlock(UniformInt.of(minXp, maxXp), copyProps(base));

        /*//? if <= 1.20.2 {
        return new DropExperienceBlock(copyProps(base), UniformInt.of(minXp, maxXp));
        *///?}
    }

    private static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, name);

        /*//? if <= 1.20.5 {
        return new ResourceLocation(Main.MOD_ID, name);
        *///?}
    }

    /*//? if >= 1.21.11 {
    private static Identifier id(String name) {
        return new Identifier(Main.MOD_ID, name);
    }
    *///?}

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(BuiltInRegistries.BLOCK, id(name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(
                BuiltInRegistries.ITEM,
                id(name),
                new BlockItem(block, new Item.Properties())
        );
    }

    public static final Block SNOWY_GOLD_ORE = registerBlock(
            "snowy_gold_ore",
            xpOre(net.minecraft.world.level.block.Blocks.NETHER_GOLD_ORE, 0, 1)
    );

    public static final Block SNOWY_QUARTZ_ORE = registerBlock(
            "snowy_quartz_ore",
            xpOre(net.minecraft.world.level.block.Blocks.NETHER_QUARTZ_ORE, 2, 5)
    );

    public static void registerBlocks() {
        Main.LOGGER.info("Registering Blocks for " + Main.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.NATURAL_BLOCKS).register(entries -> {
            entries.accept(SNOWY_GOLD_ORE);
            entries.accept(SNOWY_QUARTZ_ORE);
        });
    }
}