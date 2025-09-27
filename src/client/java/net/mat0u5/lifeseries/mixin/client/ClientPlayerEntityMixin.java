package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.events.ClientEvents;
import net.mat0u5.lifeseries.utils.interfaces.IClientPlayer;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientPlayerEntity.class, priority = 1)
public abstract class ClientPlayerEntityMixin implements IClientPlayer {

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickTail(CallbackInfo ci) {
        ClientEvents.onClientTickEnd();
    }

    @Redirect(method = "tickNewAi", at = @At(value = "FIELD", target = "Lnet/minecraft/client/input/Input;forwardMovement:F"))
    private float overwriteForwardMovement(Input instance) {
        if (ls$stopMovementForTicks > 0) {
            ls$stopMovementForTicks--;
            return 0;
        }
        return instance.forwardMovement;
    }

    @Redirect(method = "tickNewAi", at = @At(value = "FIELD", target = "Lnet/minecraft/client/input/Input;sidewaysMovement:F"))
    private float overwriteSidewaysMovement(Input instance) {
        if (ls$stopMovementForTicks > 0) {
            ls$stopMovementForTicks--;
            return 0;
        }
        return instance.sidewaysMovement;
    }

    @Unique
    private int ls$stopMovementForTicks = 0;

    @Unique
    @Override
    public void ls$stopMovementFor(int ticks) {
        if (ticks > ls$stopMovementForTicks) {
            OtherUtils.log("Stopping movement for " + ticks + " ticks.");
            ls$stopMovementForTicks = ticks;
        }
    }
}