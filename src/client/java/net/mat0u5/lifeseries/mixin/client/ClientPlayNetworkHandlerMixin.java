package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.utils.ClientUtils;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ClientPlayNetworkHandler.class, priority = 1)
public class ClientPlayNetworkHandlerMixin {
    @Shadow
    private ClientWorld world;

    @Redirect(method = "onEntityAttributes", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/EntityAttributeInstance;setBaseValue(D)V"))
    private void onUpdatedValue(EntityAttributeInstance instance, double baseValue, EntityAttributesS2CPacket packet) {
        if (ClientUtils.handleUpdatedAttribute(world, instance, baseValue, packet)) return;
        instance.setBaseValue(baseValue);
    }
}