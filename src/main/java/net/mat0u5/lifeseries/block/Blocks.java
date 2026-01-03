package net.mat0u5.lifeseries.block;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.mat0u5.lifeseries.Main;

//? if <= 1.20.5 {
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
//?} else {
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
//?}

public class Blocks {

    //? if <= 1.20.5 {
    public static final Block SNOWY_GOLD_ORE = registerBlock("snowy_gold_ore",
            new ExperienceDroppingBlock(
                    FabricBlockSettings.copyOf(net.minecraft.block.Blocks.NETHER_GOLD_ORE),
                    UniformIntProvider.create(0, 1)));

    public static final Block SNOWY_QUARTZ_ORE = registerBlock("snowy_quartz_ore",
            new ExperienceDroppingBlock(
                    FabricBlockSettings.copyOf(net.minecraft.block.Blocks.NETHER_QUARTZ_ORE),
                    UniformIntProvider.create(2, 5)));
    //?} else {
    public static final Block SNOWY_GOLD_ORE = registerBlock("snowy_gold_ore",
            new DropExperienceBlock(
                    BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.NETHER_GOLD_ORE),
                    UniformInt.of(0, 1)));

    public static final Block SNOWY_QUARTZ_ORE = registerBlock("snowy_quartz_ore",
            new DropExperienceBlock(
                    BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.NETHER_QUARTZ_ORE),
                    UniformInt.of(2, 5)));
    //?}

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
		//? if <= 1.20.5 {
        return Registry.register(Registries.BLOCK, new Identifier(Main.MOD_ID, name), block);
        //?} else {
        return Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(Main.MOD_ID, name), block);
        //?}
#    }

    private static void registerBlockItem(String name, Block block) {
        //? if <= 1.20.5 {
		Registry.register(Registries.ITEM, new Identifier(Main.MOD_ID, name),
			new BlockItem(block, new Item.Settings()));
        //?} else {
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Main.MOD_ID, name),
                new BlockItem(block, new Item.Properties()));		
        //?}
    }

    public static void registerBlocks() {
        Main.LOGGER.info("Registering Blocks for " + Main.MOD_ID);

        //? if <= 1.20.5 {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(entries -> {
            entries.add(SNOWY_GOLD_ORE);
            entries.add(SNOWY_QUARTZ_ORE);
        });
        //?} else {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.NATURAL_BLOCKS).register(entries -> {
            entries.accept(SNOWY_GOLD_ORE);
            entries.accept(SNOWY_QUARTZ_ORE);
        });
        //?}
    }
}
