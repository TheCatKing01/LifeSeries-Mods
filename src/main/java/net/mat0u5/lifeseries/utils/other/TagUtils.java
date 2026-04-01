package net.mat0u5.lifeseries.utils.other;

import net.minecraft.world.entity.Entity;

import java.util.Collection;
import java.util.Collections;

public final class TagUtils {
    private TagUtils() {}

    public static Collection<String> getTags(Entity entity) {
        if (entity == null) return Collections.emptySet();
        //? if <= 1.21.11 && < 26.1 {
        return entity.getTags();
        //?} else {
        /*return entity.entityTags();
        *///?}
    }

    public static boolean hasTag(Entity entity, String tag) {
        if (tag == null || tag.isEmpty()) return false;
        return getTags(entity).contains(tag);
    }
}
