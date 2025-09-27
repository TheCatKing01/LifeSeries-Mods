package net.mat0u5.lifeseries.utils;

import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.utils.enums.Direction;
import net.mat0u5.lifeseries.utils.interfaces.IClientPlayer;
import net.mat0u5.lifeseries.utils.interfaces.IEntity;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.ItemStackUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Identifier;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

import java.util.UUID;

public class ClientUtils {

    public static boolean shouldPreventGliding() {
        if (!MainClient.preventGliding) return false;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return false;
        if (client.player == null) return false;
        ItemStack helmet = PlayerUtils.getEquipmentSlot(client.player, 3);
        return ItemStackUtils.hasCustomComponentEntry(helmet, "FlightSuperpower");
    }

    @Nullable
    public static PlayerEntity getPlayer(UUID uuid) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return null;
        if (client.world == null) return null;
        return client.world.getPlayerByUuid(uuid);
    }

    @Nullable
    public static Team getPlayerTeam() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return null;
        if (client.player == null) return null;
        return client.player.getScoreboardTeam();
    }

    public static void runCommand(String command) {
        ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();
        if (handler == null) return;

        if (command.startsWith("/")) {
            command = command.substring(1);
        }
        handler.sendChatCommand(command);
    }

    public static boolean handleUpdatedAttribute(ClientWorld world, EntityAttributeInstance instance, double baseValue, EntityAttributesS2CPacket packet) {
        Entity entity = world.getEntityById(packet.getEntityId());
        if (entity == null) return false;
        if (!(entity instanceof ClientPlayerEntity player)) return false;
        if (!MainClient.isClientPlayer(player.getUuid())) return false;
        //?if <= 1.21 {
        RegistryEntry<EntityAttribute> scaleAttribute = EntityAttributes.GENERIC_SCALE;
        //?} else {
        /*RegistryEntry<EntityAttribute> scaleAttribute = EntityAttributes.SCALE;
         *///?}
        if (instance.getAttribute() != scaleAttribute) return false;
        if (MainClient.clientCurrentSeason != Seasons.WILD_LIFE) return false;
        if (!MainClient.clientActiveWildcards.contains(Wildcards.SIZE_SHIFTING)) return false;
        if (!MainClient.FIX_SIZECHANGING_BUGS) return false;

        double oldBaseValue = player.getAttributeBaseValue(scaleAttribute);
        if (oldBaseValue == baseValue) return false;

        EntityDimensions oldEntityDimensions = player.getBaseDimensions(player.getPose()).scaled((float) oldBaseValue);
        Box oldBoundingBox = oldEntityDimensions.getBoxAt(player.getPos());
        double oldHitboxSize = oldEntityDimensions.width();

        EntityDimensions newEntityDimensions = player.getBaseDimensions(player.getPose()).scaled((float) baseValue);
        Box newBoundingBox = newEntityDimensions.getBoxAt(player.getPos());
        double newHitboxSize = newEntityDimensions.width();

        double changedBy = newHitboxSize - oldHitboxSize;

        Vec3d move = null;
        if (changedBy < 0) {
            boolean oldSpaceBelowEmpty = isSpaceEmpty(player, oldBoundingBox, 0, -1.0E-5, 0);
            boolean newSpaceBelowEmpty = isSpaceEmpty(player, newBoundingBox, 0, -1.0E-5, 0);
            if (!oldSpaceBelowEmpty && newSpaceBelowEmpty) {
                // The shrinking causes the player to fall when on the edge of blocks
                OtherUtils.log("Detected fall");
                move = findDesiredCollission(player, newBoundingBox, changedBy, - (double)1.0E-5F, false, false);
            }
        }
        else {

            boolean oldSpaceEmpty = isSpaceEmpty(player, oldBoundingBox, 0, (double)1.0E-5F, 0);
            boolean newSpaceEmpty = isSpaceEmpty(player, newBoundingBox, 0, (double)1.0E-5F, 0);
            if (oldSpaceEmpty && !newSpaceEmpty) {
                // Growing causes the player to clip into blocks
                OtherUtils.log("Detected clip");
                move = findDesiredCollission(player, newBoundingBox, changedBy, (double)1.0E-5F, true, false);
                if (move != null) {
                    move = move.multiply(5);
                }
            }
            if (!oldSpaceEmpty && !newSpaceEmpty) {
                OtherUtils.log("Detected double clip");
                move = recursivelyFindDesiredCollission(player, newBoundingBox, (double)1.0E-5F, true);
            }

        }

        if (move != null) {
            OtherUtils.log("Moving by " + move);
            /*
            if (changedBy > 0) {
                Vec3d playerVelocity = player.getVelocity();
                double speedX = playerVelocity.x;
                double speedZ = playerVelocity.z;
                if (move.x != 0) {
                    OtherUtils.log("Stopping X speed");
                    speedX = 0;
                }
                if (move.z != 0) {
                    OtherUtils.log("Stopping Z speed");
                    speedZ = 0;
                }
                if (player instanceof IClientPlayer clientPlayer) {
                    clientPlayer.ls$stopMovementFor(2);
                }
                player.setVelocity(speedX, playerVelocity.y, speedZ);
            }
            */
            player.setPos(player.getX() + move.x, player.getY(), player.getZ() + move.z);
            instance.setBaseValue(baseValue);
            player.calculateDimensions();
            return true;
        }
        if (changedBy > 0) {
            instance.setBaseValue(baseValue);
            player.calculateDimensions();
            return true;
        }
        return false;
    }

    
    public static boolean isSpaceEmpty(ClientPlayerEntity player, Box box, double offsetX, double offsetY, double offsetZ) {
        if (player.noClip || player.isSpectator()) return true;
        Box newBox = new Box(box.minX + offsetX, box.minY +offsetY, box.minZ + offsetZ, box.maxX + offsetX, box.minY, box.maxZ + offsetZ);
        return player.getWorld().isSpaceEmpty(player, newBox);
    }

    public static Vec3d recursivelyFindDesiredCollission(ClientPlayerEntity player, Box newBoundingBox, double offsetY, boolean desiredSpaceEmpty) {
        for (double changedBy = 0.05; changedBy <= 0.4; changedBy += 0.05) {
            Vec3d found = findDesiredCollission(player, newBoundingBox, changedBy, offsetY, desiredSpaceEmpty, true);
            if (found != null) return found;
        }
        return null;
    }

    public static Vec3d findDesiredCollission(ClientPlayerEntity player, Box newBoundingBox, double changedBy, double offsetY, boolean desiredSpaceEmpty, boolean onlyCardinal) {
        int i = 0;
        Direction[] directions = onlyCardinal ? Direction.getCardinalDirections() : Direction.values();
        for (Direction direction : directions) {
            i++;
            double offsetX = changedBy * direction.x;
            double offsetZ = changedBy * direction.z;

            boolean movedSpaceEmpty = isSpaceEmpty(player, newBoundingBox, offsetX, offsetY, offsetZ);
            if (movedSpaceEmpty == desiredSpaceEmpty) {
                OtherUtils.log("Success in " + i);
                return new Vec3d(offsetX, 0, offsetZ);
            }
        }
        return null;
    }
}
