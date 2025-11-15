package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers;

import net.mat0u5.lifeseries.compatibilities.CompatibilityManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.Mimicry;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.Necromancy;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.mat0u5.lifeseries.Main.livesManager;

public class SuperpowersWildcard extends Wildcard {
    public static boolean WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME = false;
    public static List<Superpowers> blacklistedPowers = List.of();

    // Multiple powers per player now
    private static final Map<UUID, Set<Superpower>> playerSuperpowers = new HashMap<>();
    public static final Map<UUID, Superpowers> assignedSuperpowers = new HashMap<>();
    public static int ZOMBIES_HEALTH = 8;

    public static void setBlacklist(String blacklist) {
        blacklistedPowers = new ArrayList<>();
        String[] powers = blacklist.replace("[", "").replace("]", "").split(",");
        for (String powerName : powers) {
            Superpowers power = Superpowers.fromString(powerName.trim());
            if (power == null || power == Superpowers.NULL) continue;
            blacklistedPowers.add(power);
        }
    }

    @Override
    public Wildcards getType() {
        return Wildcards.SUPERPOWERS;
    }

    @Override
    public void activate() {
        rollRandomSuperpowers();
        super.activate();
    }

    @Override
    public void deactivate() {
        resetAllSuperpowers();
        super.deactivate();
    }

    public static void onTick() {
        playerSuperpowers.values().forEach(set -> set.forEach(Superpower::tick));
    }

    public static void resetSuperpower(ServerPlayer player) {
        UUID uuid = player.getUUID();
        if (!playerSuperpowers.containsKey(uuid)) return;
        playerSuperpowers.get(uuid).forEach(Superpower::turnOff);
        playerSuperpowers.remove(uuid);
        Necromancy.checkRessurectedPlayersReset();
    }

    public static void resetAllSuperpowers() {
        playerSuperpowers.values().forEach(set -> set.forEach(Superpower::turnOff));
        playerSuperpowers.clear();
        Necromancy.checkRessurectedPlayersReset();
    }

    public static void rollRandomSuperpowers() {
        List<Superpowers> implemented = new ArrayList<>(Superpowers.getImplemented());
        blacklistedPowers.forEach(implemented::remove);
        implemented.remove(Superpowers.LISTENING); // disabled

        boolean shouldIncludeNecromancy = implemented.contains(Superpowers.NECROMANCY) && Necromancy.shouldBeIncluded();
        boolean shouldRandomizeNecromancy = false;
        double necromancyRandomizeChance = 0;

        if (shouldIncludeNecromancy) {
            int alivePlayersNum = livesManager.getAlivePlayers().size();
            int deadPlayersNum = livesManager.getDeadPlayers().size();
            int totalPlayersNum = alivePlayersNum + deadPlayersNum;
            if (totalPlayersNum >= 6) {
                implemented.remove(Superpowers.NECROMANCY);
                shouldRandomizeNecromancy = true;
                necromancyRandomizeChance = (double) deadPlayersNum / (double) alivePlayersNum;
            }
        } else {
            implemented.remove(Superpowers.NECROMANCY);
        }

        Collections.shuffle(implemented);
        int pos = 0;
        List<ServerPlayer> allPlayers = livesManager.getAlivePlayers();
        Collections.shuffle(allPlayers);

        for (ServerPlayer player : allPlayers) {
            Superpowers power = implemented.get(pos % implemented.size());
            if (assignedSuperpowers.containsKey(player.getUUID())) {
                power = assignedSuperpowers.get(player.getUUID());
                assignedSuperpowers.remove(player.getUUID());
            } else if (shouldIncludeNecromancy && shouldRandomizeNecromancy) {
                if (player.getRandom().nextDouble() <= necromancyRandomizeChance) {
                    power = Superpowers.NECROMANCY;
                }
            }

            if (power == Superpowers.NECROMANCY) {
                implemented.remove(Superpowers.NECROMANCY);
                shouldIncludeNecromancy = false;
            }

            Superpower instance = power.getInstance(player);
            if (instance != null) {
                playerSuperpowers.computeIfAbsent(player.getUUID(), k -> new HashSet<>()).add(instance);
            }
            pos++;
        }

        if (!WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME) {
            PlayerUtils.playSoundToPlayers(allPlayers, SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("wildlife_superpowers")), 0.2f, 1);
        }
    }

    public static void rollRandomSuperpowerForPlayer(ServerPlayer player) {
        List<Superpowers> implemented = new ArrayList<>(Superpowers.getImplemented());
        implemented.remove(Superpowers.NECROMANCY);
        implemented.remove(Superpowers.LISTENING);
        Collections.shuffle(implemented);

        Superpowers power = implemented.getFirst();
        if (assignedSuperpowers.containsKey(player.getUUID())) {
            power = assignedSuperpowers.get(player.getUUID());
            assignedSuperpowers.remove(player.getUUID());
        }

        Superpower instance = power.getInstance(player);
        if (instance != null) {
            playerSuperpowers.computeIfAbsent(player.getUUID(), k -> new HashSet<>()).add(instance);
        }

        if (!WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME) {
            PlayerUtils.playSoundToPlayer(player, SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("wildlife_superpowers")), 0.2f, 1);
        }
        Necromancy.checkRessurectedPlayersReset();
    }

    // Allows stacking powers instead of replacing
    public static void setSuperpower(ServerPlayer player, Superpowers superpower) {
    Superpower instance = superpower.getInstance(player);
        if (instance != null) {
            playerSuperpowers.computeIfAbsent(player.getUUID(), k -> new HashSet<>()).add(instance);
        }

        if (!WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME) {
            PlayerUtils.playSoundToPlayer(player, SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("wildlife_superpowers")), 0.2f, 1);
        }
    }

    public static void pressedSuperpowerKey(ServerPlayer player) {
        if (playerSuperpowers.containsKey(player.getUUID())) {
        if (player.ls$isAlive()) {
            PlayerUtils.displayMessageToPlayer(player, Component.literal("Dead players can't use superpowers!"), 60);            return;
        }
        playerSuperpowers.get(player.getUUID()).forEach(Superpower::onKeyPressed);
    }

        public static boolean hasPower(ServerPlayer player) {
            return playerSuperpowers.containsKey(player.getUUID());
                !playerSuperpowers.get(player.getUUID()).isEmpty();
    }

        public static boolean hasActivatedPower(ServerPlayer player, Superpowers superpower) {
            if (!playerSuperpowers.containsKey(player.getUUID())) return false;
            Superpower power = playerSuperpowers.get(player.getUUID());
            if (power instanceof Mimicry mimicry && superpower != Superpowers.MIMICRY) {
                if (mimicry.getMimickedPower().getSuperpower() == superpower) return true;
            } else if (power.getSuperpower() == superpower) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasActivatedPower(ServerPlayerEntity player, Superpowers superpower) {
        if (!playerSuperpowers.containsKey(player.getUUID())) return false;
        for (Superpower power : playerSuperpowers.get(player.getUUID())) {
            if (power instanceof Mimicry mimicry && superpower != Superpowers.MIMICRY) {
                if (mimicry.getMimickedPower().active) return true;
            } else if (power.active && power.getSuperpower() == superpower) {
                return true;
            }
        }
        return false;
    }

    // --- BACKWARD COMPATIBILITY SECTION ---

    @Nullable
    public static Superpowers getSuperpower(ServerPlayer player) {
        if (playerSuperpowers.containsKey(player.getUUID())) {
            Superpower power = playerSuperpowers.get(player.getUUID());

        // Prefer an active one
        for (Superpower power : powers) {
            if (power.active) return power;
        }
        // Otherwise return first
        return powers.iterator().next();
    }

        public static Superpower getSuperpowerInstance(ServerPlayer player) {
            if (!playerSuperpowers.containsKey(player.getUUID())) return null;
            Superpower power = playerSuperpowers.get(player.getUUID());
        if (instance instanceof Mimicry mimicry) {
            return mimicry.getMimickedPower().getSuperpower();
        }
        return instance.getSuperpower();
    }
}