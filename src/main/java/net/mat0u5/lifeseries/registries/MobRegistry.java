package net.mat0u5.lifeseries.registries;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.mat0u5.lifeseries.entity.snail.Snail;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.entity.angrysnowman.AngrySnowman;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.block.Blocks;

//? if >= 1.21.2 {
/*import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
*///?}

public class MobRegistry {
    //? if >= 1.21.2 {
    /*public static final ResourceKey<EntityType<?>> SNAIL_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            Snail.ID
    );
    public static final ResourceKey<EntityType<?>> TRIVIA_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            TriviaBot.ID
    );
    public static final ResourceKey<EntityType<?>> SNOWMAN_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            AngrySnowman.ID
    );
    *///?}
    public static final EntityType<Snail> SNAIL = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Snail.ID,
            EntityType.Builder.of(Snail::new, MobCategory.MONSTER)
                    .sized(0.5f, 0.6f)
                    .clientTrackingRange(512)
                    //? if <= 1.20.3 {
                    /*.build(Snail.ID.toString())
                    *///?} else if <= 1.21 {
                    .build()
                    //?} else {
                    /*.build(SNAIL_KEY)
                    *///?}
    );
    public static final EntityType<TriviaBot> TRIVIA_BOT = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            TriviaBot.ID,
            EntityType.Builder.of(TriviaBot::new, MobCategory.AMBIENT)
                    .sized(0.65f, 1.8f)
                    .clientTrackingRange(512)
                    //? if <= 1.20.3 {
                    /*.build(TriviaBot.ID.toString())
                    *///?} else if <= 1.21 {
                    .build()
                     //?} else {
                    /*.build(TRIVIA_KEY)
                     *///?}
    );
    public static final EntityType<AngrySnowman> ANGRY_SNOWMAN = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            AngrySnowman.ID,
            EntityType.Builder.of(AngrySnowman::new, MobCategory.MONSTER)
                    .immuneTo(Blocks.POWDER_SNOW)
                    .sized(0.7F, 1.9F)
                    //? if >= 1.20.5 {
                    .eyeHeight(1.7F)
                    //?}
                    .clientTrackingRange(8)
                    //? if <= 1.20.3 {
                    /*.build(TriviaBot.ID.toString())
                     *///?} else if <= 1.21 {
                    .build()
                    //?} else {
                    /*.build(SNOWMAN_KEY)
                     *///?}
    );

    public static void registerMobs() {
        FabricDefaultAttributeRegistry.register(SNAIL, Snail.createAttributes());
        FabricDefaultAttributeRegistry.register(TRIVIA_BOT, TriviaBot.createAttributes());
        FabricDefaultAttributeRegistry.register(ANGRY_SNOWMAN, AngrySnowman.createAttributes());
        registerAngrySnowmanSpawns();
    }

    private static void registerAngrySnowmanSpawns() {
        SpawnPlacements.register(
                ANGRY_SNOWMAN,
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                AngrySnowman::canSpawnAboveGroundAtNight
        );

        BiomeModifications.addSpawn(
                BiomeSelectors.foundInOverworld(),
                MobCategory.MONSTER,
                ANGRY_SNOWMAN,
                95,
                1,
                2
        );
    }
}

