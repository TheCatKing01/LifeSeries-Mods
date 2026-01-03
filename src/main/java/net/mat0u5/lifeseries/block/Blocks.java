package net.mat0u5.lifeseries.block;

public class Blocks {

    public static final Block SNOWY_GOLD_ORE = registerBlock(name:"snowy_gold_ore",
        new Block(AbstractBlock.Settings.create()));

    public static final Block SNOWY_QUARTZ_ORE = registerBlock(name:"snowy_quartz_ore",
            new Block(AbstractBlock.Settings.create()));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(lifeseries.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(lifeseries.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    };

    public static void registerBlocks() {
        lifeseries.LOGGER.info("Registering Blocks for" + lifeseries.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(entries ->{
            entries.add(Blocks.SNOWY_GOLD_ORE);
            entries.add(Blocks.SNOWY_QUARTZ_ORE);
        });
    }
}