package net.mat0u5.lifeseries.entity;

import net.mat0u5.lifeseries.entity.angrysnowman.AngrySnowman;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {

    public static final EntityType<AngrySnowman> ANGRY_SNOWMAN =
            Registry.register(
                    BuiltInRegistries.ENTITY_TYPE,
                    AngrySnowman.ID,
                    EntityType.Builder.of(AngrySnowman::new, MobCategory.MONSTER) // spawn like zombie
                            .sized(0.7F, 1.9F)
                            .build()
            );

    public static void register() {
        // called from Main
    }
}
