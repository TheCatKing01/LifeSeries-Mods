package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers;

import net.mat0u5.lifeseries.compatibilities.CompatibilityManager;
import net.mat0u5.lifeseries.compatibilities.voicechat.VoicechatMain;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.Mimicry;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.Necromancy;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.mat0u5.lifeseries.Main.livesManager;

public class SuperpowersWildcard extends Wildcard {
    public static boolean WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME = false;
    public static List<Superpowers> blacklistedPowers = List.of();

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
        rollRandomSuperpowers(livesManager.getAlivePlayers());
    }

    public static void rollRandomSuperpowers(List<ServerPlayer> allPlayers) {
        allPlayers.removeIf(ServerPlayer::ls$isDead);
        allPlayers.removeIf(ServerPlayer::ls$isWatcher);

        List<ServerPlayer> prioritizedList = new ArrayList<>();
        for (ServerPlayer player : allPlayers) {
            if (assignedSuperpowers.containsKey(player.getUUID())) prioritizedList.add(player);
        }
        for (ServerPlayer player : allPlayers) {
            if (!prioritizedList.contains(player)) prioritizedList.add(player);
        }

        for (ServerPlayer player : prioritizedList) {
            if (hasPower(player)) continue;

            Superpowers power = null;

            if (assignedSuperpowers.containsKey(player.getUUID())) {
                power = assignedSuperpowers.get(player.getUUID());
                assignedSuperpowers.remove(player.getUUID());
            }

            if (power == null) {
                List<Superpowers> implemented = new ArrayList<>(Superpowers.getImplemented());
                blacklistedPowers.forEach(implemented::remove);

                if (CompatibilityManager.voicechatLoaded() && !VoicechatMain.isConnectedToSVC(player.getUUID())) {
                    implemented.remove(Superpowers.LISTENING);
                }

                boolean canHaveNecromancy = false;
                if (implemented.contains(Superpowers.NECROMANCY) && Necromancy.shouldBeIncluded()) {
                    int alivePlayersNum = livesManager.getAlivePlayers().size();
                    int deadPlayersNum = livesManager.getDeadPlayers().size();
                    int totalPlayersNum = alivePlayersNum + deadPlayersNum;
                    if (totalPlayersNum >= 6) {
                        canHaveNecromancy = true;
                        if (player.getRandom().nextDouble() <= (double) deadPlayersNum / (double) alivePlayersNum) {
                            power = Superpowers.NECROMANCY;
                        }
                    }
                }

                if (power == null) {
                    implemented.remove(Superpowers.NECROMANCY);
                    List<Superpowers> nonAssigned = new ArrayList<>(implemented);

                    for (Set<Superpower> assigned : playerSuperpowers.values()) {
                        for (Superpower sp : assigned) nonAssigned.remove(sp.getSuperpower());
                    }

                    Collections.shuffle(nonAssigned);
                    if (!nonAssigned.isEmpty()) power = nonAssigned.get(0);
                }
            }

            if (power != null) {
                Superpower instance = power.getInstance(player);
                if (instance != null) {
                    playerSuperpowers.computeIfAbsent(player.getUUID(), k -> new HashSet<>()).add(instance);

                    // removed: DatapackIntegration.activateSuperpower(player, power);
                }
            }
        }

        if (!WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME) {
            PlayerUtils.playSoundToPlayers(
                    allPlayers,
                    SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("wildlife_superpowers")),
                    0.2f,
                    1
            );
        }
    }

    public static void rollRandomSuperpowerForPlayer(ServerPlayer player) {
        List<Superpowers> implemented = new ArrayList<>(Superpowers.getImplemented());
        implemented.remove(Superpowers.NECROMANCY);

        if (CompatibilityManager.voicechatLoaded() && !VoicechatMain.isConnectedToSVC(player.getUUID())) {
            implemented.remove(Superpowers.LISTENING);
        }

        Collections.shuffle(implemented);
        Superpowers power = implemented.get(0);

        if (assignedSuperpowers.containsKey(player.getUUID())) {
            power = assignedSuperpowers.get(player.getUUID());
            assignedSuperpowers.remove(player.getUUID());
        }

        Superpower instance = power.getInstance(player);
        if (instance != null) playerSuperpowers.computeIfAbsent(player.getUUID(), k -> new HashSet<>()).add(instance);

        if (!WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME) {
            PlayerUtils.playSoundToPlayer(
                    player,
                    SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("wildlife_superpowers")),
                    0.2f,
                    1
            );
        }
    }

    public static void setSuperpower(ServerPlayer player, Superpowers superpower) {
        Superpower instance = superpower.getInstance(player);
        if (instance != null) playerSuperpowers.computeIfAbsent(player.getUUID(), k -> new HashSet<>()).add(instance);

        if (!WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME) {
            PlayerUtils.playSoundToPlayer(
                    player,
                    SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("wildlife_superpowers")),
                    0.2f,
                    1
            );
        }
        Necromancy.checkRessurectedPlayersReset();
    }

    public static void pressedSuperpowerKey(ServerPlayer player) {
        if (!playerSuperpowers.containsKey(player.getUUID())) return;
        if (!player.ls$isAlive()) {
            PlayerUtils.displayMessageToPlayer(player, Component.literal("Dead players can't use superpowers!"), 60);
            return;
        }
        playerSuperpowers.get(player.getUUID()).forEach(Superpower::onKeyPressed);
    }

    public static boolean hasPower(ServerPlayer player) {
        return playerSuperpowers.containsKey(player.getUUID()) &&
                !playerSuperpowers.get(player.getUUID()).isEmpty();
    }

    public static boolean hasActivePower(ServerPlayer player, Superpowers superpower) {
        if (!playerSuperpowers.containsKey(player.getUUID())) return false;
        for (Superpower power : playerSuperpowers.get(player.getUUID())) {
            if (power instanceof Mimicry mimicry && superpower != Superpowers.MIMICRY) {
                if (mimicry.getMimickedPower().active) return true;
            } else if (power.active && power.getSuperpower() == superpower) return true;
        }
        return false;
    }

    @Nullable
    public static Superpower getSuperpowerInstance(ServerPlayer player) {
        Set<Superpower> powers = playerSuperpowers.get(player.getUUID());
        if (powers == null || powers.isEmpty()) return null;
        for (Superpower power : powers) {
            if (power.active) return power;
        }
        return powers.iterator().next();
    }

    public static Superpowers getSuperpower(ServerPlayer player) {
        Superpower instance = getSuperpowerInstance(player);
        if (instance == null) return Superpowers.NULL;
        if (instance instanceof Mimicry mimicry) {
            return mimicry.getMimickedPower().getSuperpower();
        }
        return instance.getSuperpower();
    }
	
	public static boolean hasActivatedPower(ServerPlayer player, Superpowers superpower) {
		return hasActivePower(player, superpower);
}

}
