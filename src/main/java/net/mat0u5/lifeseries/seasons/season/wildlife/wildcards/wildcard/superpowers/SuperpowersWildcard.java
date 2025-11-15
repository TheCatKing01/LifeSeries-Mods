package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers;

import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.Mimicry;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.Necromancy;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
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

    // Multiple powers per player
    protected static final Map<UUID, Set<Superpower>> playerSuperpowers = new HashMap<>();
    public static final Map<UUID, Superpowers> assignedSuperpowers = new HashMap<>();

    // ✅ Your new function — correctly placed inside the class
    public static void showPlayerSuperpowers(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        Set<Superpower> powers = playerSuperpowers.get(uuid);

        if (powers == null || powers.isEmpty()) {
            PlayerUtils.displayMessageToPlayer(player, Text.literal("You don't have any superpowers!"), 80);
            return;
        }

        PlayerUtils.displayMessageToPlayer(player, Text.literal("Your superpowers:"), 100);

        for (Superpower power : powers) {
            String name = power.getSuperpower().getString();
            PlayerUtils.displayMessageToPlayer(player, Text.literal(" - " + name), 80);
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

    public static void resetSuperpower(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        if (!playerSuperpowers.containsKey(uuid)) return;
        playerSuperpowers.get(uuid).forEach(Superpower::turnOff);
        playerSuperpowers.remove(uuid);
    }

    public static void resetAllSuperpowers() {
        playerSuperpowers.values().forEach(set -> set.forEach(Superpower::turnOff));
        playerSuperpowers.clear();
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
                necromancyRandomizeChance = (double) deadPlayersNum / alivePlayersNum;
            }
        } else {
            implemented.remove(Superpowers.NECROMANCY);
        }

        Collections.shuffle(implemented);
        int pos = 0;
        List<ServerPlayerEntity> allPlayers = livesManager.getAlivePlayers();
        Collections.shuffle(allPlayers);

        for (ServerPlayerEntity player : allPlayers) {
            Superpowers power = implemented.get(pos % implemented.size());

            if (assignedSuperpowers.containsKey(player.getUuid())) {
                power = assignedSuperpowers.remove(player.getUuid());
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
                playerSuperpowers.computeIfAbsent(player.getUuid(), k -> new HashSet<>()).add(instance);
            }
            pos++;
        }

        if (!WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME) {
            PlayerUtils.playSoundToPlayers(allPlayers, SoundEvent.of(Identifier.of("minecraft", "wildlife_superpowers")), 0.2f, 1);
        }
    }

    public static void rollRandomSuperpowerForPlayer(ServerPlayerEntity player) {
        List<Superpowers> implemented = new ArrayList<>(Superpowers.getImplemented());
        implemented.remove(Superpowers.NECROMANCY);
        implemented.remove(Superpowers.LISTENING);
        Collections.shuffle(implemented);

        Superpowers power = implemented.getFirst();

        if (assignedSuperpowers.containsKey(player.getUuid())) {
            power = assignedSuperpowers.remove(player.getUuid());
        }

        Superpower instance = power.getInstance(player);
        if (instance != null) {
            playerSuperpowers.computeIfAbsent(player.getUuid(), k -> new HashSet<>()).add(instance);
        }

        if (!WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME) {
            PlayerUtils.playSoundToPlayer(player, SoundEvent.of(Identifier.of("minecraft", "wildlife_superpowers")), 0.2f, 1);
        }
    }

    // Now adds instead of replacing
    public static void setSuperpower(ServerPlayerEntity player, Superpowers superpower) {
        Superpower instance = superpower.getInstance(player);
        if (instance != null) {
            playerSuperpowers.computeIfAbsent(player.getUuid(), k -> new HashSet<>()).add(instance);
        }

        if (!WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME) {
            PlayerUtils.playSoundToPlayer(player, SoundEvent.of(Identifier.of("minecraft", "wildlife_superpowers")), 0.2f, 1);
        }
    }

    public static void pressedSuperpowerKey(ServerPlayerEntity player) {
        if (!playerSuperpowers.containsKey(player.getUuid())) return;
        if (!livesManager.isAlive(player)) {
            PlayerUtils.displayMessageToPlayer(player, Text.literal("Dead players can't use superpowers!"), 60);
            return;
        }
        playerSuperpowers.get(player.getUuid()).forEach(Superpower::onKeyPressed);
    }

    public static boolean hasPower(ServerPlayerEntity player) {
        return playerSuperpowers.containsKey(player.getUuid()) &&
                !playerSuperpowers.get(player.getUuid()).isEmpty();
    }

    public static boolean hasActivePower(ServerPlayerEntity player, Superpowers superpower) {
        if (!playerSuperpowers.containsKey(player.getUuid())) return false;
        for (Superpower power : playerSuperpowers.get(player.getUuid())) {
            if (power instanceof Mimicry mimicry && superpower != Superpowers.MIMICRY) {
                if (mimicry.getMimickedPower().getSuperpower() == superpower) return true;
            } else if (power.getSuperpower() == superpower) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasActivatedPower(ServerPlayerEntity player, Superpowers superpower) {
        if (!playerSuperpowers.containsKey(player.getUuid())) return false;
        for (Superpower power : playerSuperpowers.get(player.getUuid())) {
            if (power instanceof Mimicry mimicry && superpower != Superpowers.MIMICRY) {
                if (mimicry.getMimickedPower().active) return true;
            } else if (power.active && power.getSuperpower() == superpower) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    public static Superpower getSuperpowerInstance(ServerPlayerEntity player) {
        Set<Superpower> powers = playerSuperpowers.get(player.getUuid());
        if (powers == null || powers.isEmpty()) return null;

        for (Superpower power : powers) {
            if (power.active) return power;
        }
        return powers.iterator().next();
    }

    public static Superpowers getSuperpower(ServerPlayerEntity player) {
        Superpower instance = getSuperpowerInstance(player);
        if (instance == null) return Superpowers.NULL;
        if (instance instanceof Mimicry mimicry) {
            return mimicry.getMimickedPower().getSuperpower();
        }
        return instance.getSuperpower();
    }
}