package net.mat0u5.lifeseries.block;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.mat0u5.lifeseries.Main;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

//? if >= 1.21.11 {
import net.minecraft.util.Identifier;
//?} else {
import net.minecraft.resources.ResourceLocation;
//?}

public class Blocks {

    /* ============================
       Identifier helper
       ============================ */

    //? if >= 1.21.11 {
    private static Identifier id(String path) {
        return Identifier.of(Main.MOD_ID, path);
    }
    //?} else {
    private static ResourceLocation id(String path) {
        return new ResourceLocation(Main.MOD_ID, path);
    }
    //?}

    /* ============================
       Properties copy helper
       ============================ */

    private static BlockBehaviour.Properties copyProps(Block base) {
        //? if >= 1.20.3 {
        return BlockBehaviour.Properties.ofFullCopy(base);
        //?} else {
        return BlockBehaviour.Properties.copy(base);
        //?}
    }

    /* ============================
       XP ore helper
       ============================ */

    private static Block xpOreLike(Block base, int minXp, int maxXp) {
        BlockBehaviour.Properties props = copyProps(base);
        UniformInt xp = UniformInt.of(minXp, maxXp);

        //? if >= 1.20.3 {
        return new DropExperienceBlock(xp, props);
        //?} else {
        return new DropExperienceBlock(props, xp);
        //?}
    }

    /* ============================
       Registration helpers
       ============================ */

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

    /* ============================
       Blocks
       ============================ */

    // Nether Gold Ore XP: 0–1
    public static final Block SNOWY_GOLD_ORE = registerBlock(
            "snowy_gold_ore",
            xpOreLike(
                    net.minecraft.world.level.block.Blocks.NETHER_GOLD_ORE,
                    0,
                    1
            )
    );

    // Nether Quartz Ore XP: 2–5
    public static final Block SNOWY_QUARTZ_ORE = registerBlock(
            "snowy_quartz_ore",
            xpOreLike(
                    net.minecraft.world.level.block.Blocks.NETHER_QUARTZ_ORE,
                    2,
                    5
            )
    );

    /* ============================
       Creative tab
       ============================ */

    public static void registerBlocks() {
        Main.LOGGER.info("Registering Blocks for " + Main.MOD_ID);

        ItemGroupEvents
                .modifyEntriesEvent(CreativeModeTabs.NATURAL_BLOCKS)
                .register(entries -> {
                    entries.accept(SNOWY_GOLD_ORE);
                    entries.accept(SNOWY_QUARTZ_ORE);
                });
    }
}
