package net.mat0u5.lifeseries.block;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.mat0u5.lifeseries.Main;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

//? if == 1.21.11 {
import net.minecraft.util.Identifier;
//?} else {
import net.minecraft.resources.ResourceLocation;
//?}

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class Blocks {

    private static BlockBehaviour.Properties copyProps(Block block) {
        //? if >= 1.20.3 {
        return BlockBehaviour.Properties.ofFullCopy(block);
        //?} else {
        return BlockBehaviour.Properties.copy(block);
        //?}
    }

    private static Block xpOre(Block base, int minXp, int maxXp) {
        //? if >= 1.20.3 {
        return new DropExperienceBlock(UniformInt.of(minXp, maxXp), copyProps(base));
        //?} else {
        return new DropExperienceBlock(copyProps(base), UniformInt.of(minXp, maxXp));
        //?}
    }


    //? if == 1.21.11 {

    private static Identifier id(String name) {
        return Identifier.of(Main.MOD_ID, name);
    }

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(
                BuiltInRegistries.BLOCK,
                id(name),
                block
        );
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(
                BuiltInRegistries.ITEM,
                id(name),
                new BlockItem(block, new Item.Properties())
        );
    }

    //?} else {

    private static ResourceLocation id(String name) {
        //? if >= 1.21 {
        return ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, name);
        //?} else {
        return new ResourceLocation(Main.MOD_ID, name);
        //?}
    }

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(
                BuiltInRegistries.BLOCK,
                id(name),
                block
        );
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(
                BuiltInRegistries.ITEM,
                id(name),
                new BlockItem(block, new Item.Properties())
        );
    }

    //?}

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
