package net.mat0u5.lifeseries.mixin;

import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = ServerLevel.class, priority = 2)
public interface ServerLevelAccessor {
    @Invoker("wakeUpAllPlayers")
    void ls$wakeUpAllPlayers();
}
