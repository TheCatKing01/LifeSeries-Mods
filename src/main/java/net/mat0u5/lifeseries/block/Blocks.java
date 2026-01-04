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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class Blocks {

    // ---------- version-safe helpers (no ResourceLocation/Identifier imports) ----------

    private static BlockBehaviour.Properties copyProps(Block block) {
        try {
            // Newer: BlockBehaviour.Properties.ofFullCopy(Block)
            Method m = BlockBehaviour.Properties.class.getMethod("ofFullCopy", Block.class);
            return (BlockBehaviour.Properties) m.invoke(null, block);
        } catch (Throwable ignored) {
        }

        try {
            // Older: BlockBehaviour.Properties.copy(Block)
            Method m = BlockBehaviour.Properties.class.getMethod("copy", Block.class);
            return (BlockBehaviour.Properties) m.invoke(null, block);
        } catch (Throwable t) {
            throw new RuntimeException("No suitable Properties copy method found", t);
        }
    }

    private static Block xpOre(Block base, int minXp, int maxXp) {
        BlockBehaviour.Properties props = copyProps(base);
        UniformInt xp = UniformInt.of(minXp, maxXp);

        // Try constructors in both orders:
        try {
            // Newer: DropExperienceBlock(IntProvider, Properties)
            Constructor<?> c = DropExperienceBlock.class.getConstructor(
                    net.minecraft.util.valueproviders.IntProvider.class,
                    BlockBehaviour.Properties.class
            );
            return (Block) c.newInstance(xp, props);
        } catch (Throwable ignored) {
        }

        try {
            // Older: DropExperienceBlock(Properties, IntProvider)
            Constructor<?> c = DropExperienceBlock.class.getConstructor(
                    BlockBehaviour.Properties.class,
                    net.minecraft.util.valueproviders.IntProvider.class
            );
            return (Block) c.newInstance(props, xp);
        } catch (Throwable t) {
            throw new RuntimeException("No suitable DropExperienceBlock constructor found", t);
        }
    }

    private static Object makeId(String path) {
        // Try Mojang mappings ResourceLocation first
        try {
            Class<?> rl = Class.forName("net.minecraft.resources.ResourceLocation");

            // Newer factory: fromNamespaceAndPath(String, String)
            try {
                Method m = rl.getMethod("fromNamespaceAndPath", String.class, String.class);
                return m.invoke(null, Main.MOD_ID, path);
            } catch (Throwable ignored) {
            }

            // Older public ctor: new ResourceLocation(String, String)
            try {
                Constructor<?> c = rl.getConstructor(String.class, String.class);
                return c.newInstance(Main.MOD_ID, path);
            } catch (Throwable ignored) {
            }

            // Some versions: ResourceLocation(String) taking "modid:path"
            try {
                Constructor<?> c = rl.getConstructor(String.class);
                return c.newInstance(Main.MOD_ID + ":" + path);
            } catch (Throwable ignored) {
            }
        } catch (Throwable ignored) {
        }

        // Try Yarn mappings Identifier
        try {
            Class<?> id = Class.forName("net.minecraft.util.Identifier");

            // Identifier.of(String, String) if present
            try {
                Method m = id.getMethod("of", String.class, String.class);
                return m.invoke(null, Main.MOD_ID, path);
            } catch (Throwable ignored) {
            }

            // Older: new Identifier(String, String)
            try {
                Constructor<?> c = id.getConstructor(String.class, String.class);
                return c.newInstance(Main.MOD_ID, path);
            } catch (Throwable ignored) {
            }

            // Identifier(String) taking "modid:path"
            try {
                Constructor<?> c = id.getConstructor(String.class);
                return c.newInstance(Main.MOD_ID + ":" + path);
            } catch (Throwable ignored) {
            }
        } catch (Throwable ignored) {
        }

        throw new RuntimeException("Could not construct an identifier/ResourceLocation for: " + Main.MOD_ID + ":" + path);
    }

    @SuppressWarnings("unchecked")
    private static <T> T registerAny(Registry<T> registry, String name, T value) {
        Object id = makeId(name);

        // Find Registry.register(Registry, <IdType>, Object)
        for (Method m : Registry.class.getMethods()) {
            if (!m.getName().equals("register")) continue;
            if (m.getParameterCount() != 3) continue;

            Class<?> p0 = m.getParameterTypes()[0];
            Class<?> p1 = m.getParameterTypes()[1];

            if (!p0.isAssignableFrom(registry.getClass()) && p0 != Registry.class) continue;
            if (!p1.isInstance(id)) continue;

            try {
                return (T) m.invoke(null, registry, id, value);
            } catch (Throwable ignored) {
            }
        }

        // fallback: just try any register with 3 params where param1 matches id
        for (Method m : Registry.class.getMethods()) {
            if (!m.getName().equals("register")) continue;
            if (m.getParameterCount() != 3) continue;
            if (!m.getParameterTypes()[1].isInstance(id)) continue;

            try {
                return (T) m.invoke(null, registry, id, value);
            } catch (Throwable ignored) {
            }
        }

        throw new RuntimeException("Could not find a compatible Registry.register method for id type: " + id.getClass());
    }

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return registerAny((Registry<Block>) BuiltInRegistries.BLOCK, name, block);
    }

    private static void registerBlockItem(String name, Block block) {
        registerAny((Registry<Item>) BuiltInRegistries.ITEM, name, new BlockItem(block, new Item.Properties()));
    }

    // ---------- your blocks ----------

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
