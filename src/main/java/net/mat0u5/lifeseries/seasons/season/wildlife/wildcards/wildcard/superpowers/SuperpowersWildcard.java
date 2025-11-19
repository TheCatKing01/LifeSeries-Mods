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

    // Multiple powers per player
    private static final Map<UUID, Set<Superpower>> playerSuperpowers = new LinkedHashMap<>();
    public static final Map<UUID, Superpowers> preAssignedSuperpowers = new HashMap<>();
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
        allPlayers.forEach(SuperpowersWildcard::resetSuperpower);

        List<ServerPlayer> prioritizedList = new ArrayList<>();
        //Put all the players with assigned superpowers first - to prevent duplicating powers.
        for (ServerPlayer player : allPlayers) {
            if (preAssignedSuperpowers.containsKey(player.getUUID())) prioritizedList.add(player);
        for (ServerPlayer player : allPlayers) {
            if (!prioritizedList.contains(player)) prioritizedList.add(player);
        }

        for (ServerPlayer player : prioritizedList) {
            if (hasPower(player)) continue;
            Superpowers power = getRandomPower(player); shouldIncludeNecromancy = false;
            }

            Superpower instance = power.getInstance(player);
            if (instance != null) playerSuperpowers.computeIfAbsent(player.getUUID(), k -> new HashSet<>()).add(instance);

        }

        if (!WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME) {
            PlayerUtils.playSoundToPlayers(allPlayers, SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("wildlife_superpowers")), 0.2f, 1);
        }
    }

    private static Superpowers getRandomPower(ServerPlayer player) {
        if (preAssignedSuperpowers.containsKey(player.getUUID())) {
            Superpowers power = preAssignedSuperpowers.get(player.getUUID());
            preAssignedSuperpowers.remove(player.getUUID());
            return power;
        }

        List<Superpowers> implemented = new ArrayList<>(Superpowers.getImplemented());
        blacklistedPowers.forEach(implemented::remove);
        if (CompatibilityManager.voicechatLoaded() && !VoicechatMain.isConnectedToSVC(player.getUUID())) {
            implemented.remove(Superpowers.LISTENING);
        }

        List<Superpowers> nonAssigned = new ArrayList<>(implemented);
        for (Superpower assignedPower : playerSuperpowers.values()) {
            Superpowers power = assignedPower.getSuperpower();
            nonAssigned.remove(power);
        }
		Collections.shuffle(nonAssigned);

        boolean canHaveNecromancy = false;
        if (nonAssigned.contains(Superpowers.NECROMANCY) && Necromancy.shouldBeIncluded()) {
            int alivePlayersNum = livesManager.getAlivePlayers().size();
            int deadPlayersNum = livesManager.getDeadPlayers().size();
            int totalPlayersNum = alivePlayersNum + deadPlayersNum;
            if (totalPlayersNum >= 6) {
                canHaveNecromancy = true;
                if (player.getRandom().nextDouble() <= (double)deadPlayersNum / (double)alivePlayersNum) {
                    return Superpowers.NECROMANCY;
                }
            }
        }
        if (!canHaveNecromancy) {
            implemented.remove(Superpowers.NECROMANCY);
            nonAssigned.remove(Superpowers.NECROMANCY);
        }

        //A shuffled queue with assigned superpowers at the very end - we can just choose the first power in the list.
        List<Superpowers> queue = new ArrayList<>(nonAssigned);
        for (Superpower assignedPower : playerSuperpowers.values()) {
            //This puts all the assigned powers at the end of the queue.
            Superpowers power = assignedPower.getSuperpower();
            if (!implemented.contains(power)) continue;
            queue.remove(power);
            queue.add(power);
        }
		
		return queue.getFirst();
    }

    public static void setSuperpower(ServerPlayer player, Superpowers superpower) {
        Superpower instance = superpower.getInstance(player);
        if (instance != null) playerSuperpowers.computeIfAbsent(player.getUUID(), k -> new HashSet<>()).add(instance);

        if (!WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME) {
            PlayerUtils.playSoundToPlayer(player, SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("wildlife_superpowers")), 0.2f, 1);
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
        return playerSuperpowers.containsKey(player.getUUID()) && !playerSuperpowers.get(player.getUUID()).isEmpty();
    }

    public static boolean hasActivePower(ServerPlayer player, Superpowers superpower) {
        if (!playerSuperpowers.containsKey(player.getUUID())) return false;
        for (Superpower power : playerSuperpowers.get(player.getUUID())) {
            if (power instanceof Mimicry mimicry && superpower != Superpowers.MIMICRY) {
                if (mimicry.getMimickedPower().getSuperpower() == superpower) return true;
            } else if (power.getSuperpower() == superpower) return true;
        }
        return false;
    }

    public static boolean hasActivatedPower(ServerPlayer player, Superpowers superpower) {
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
}
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

    // Multiple powers per player
    private static final Map<UUID, Set<Superpower>> playerSuperpowers = new LinkedHashMap<>();
    public static final Map<UUID, Superpowers> preAssignedSuperpowers = new HashMap<>();
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
		allPlayers.forEach(SuperpowersWildcard::resetSuperpower);

		// Build prioritized list
		List<ServerPlayer> prioritizedList = new ArrayList<>();

		// 1) Add players with preassigned powers first
		for (ServerPlayer p : allPlayers) {
			if (preAssignedSuperpowers.containsKey(p.getUUID()))
				prioritizedList.add(p);
		}

		// 2) Add everyone else afterwards
		for (ServerPlayer p : allPlayers) {
			if (!prioritizedList.contains(p))
				prioritizedList.add(p);
		}

		// Assign powers
		for (ServerPlayer p : prioritizedList) {
			if (hasPower(p)) continue;

			Superpowers power = getRandomPower(p);

			Superpower instance = power.getInstance(p);
			if (instance != null)
				playerSuperpowers.computeIfAbsent(p.getUUID(), k -> new HashSet<>())
					.add(instance);
		}

		if (!WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME) {
			PlayerUtils.playSoundToPlayers(
					allPlayers,
					SoundEvent.createVariableRangeEvent(
							IdentifierHelper.vanilla("wildlife_superpowers")),
					0.2f,
					1
			);
		}
	}

    private static Superpowers getRandomPower(ServerPlayer player) {
        if (preAssignedSuperpowers.containsKey(player.getUUID())) {
            Superpowers power = preAssignedSuperpowers.get(player.getUUID());
            preAssignedSuperpowers.remove(player.getUUID());
            return power;
        }

        List<Superpowers> implemented = new ArrayList<>(Superpowers.getImplemented());
        blacklistedPowers.forEach(implemented::remove);
        if (CompatibilityManager.voicechatLoaded() && !VoicechatMain.isConnectedToSVC(player.getUUID())) {
            implemented.remove(Superpowers.LISTENING);
        }

        List<Superpowers> nonAssigned = new ArrayList<>(implemented);
        for (Superpower assignedPower : playerSuperpowers.values()) {
            Superpowers power = assignedPower.getSuperpower();
            nonAssigned.remove(power);
        }
		Collections.shuffle(nonAssigned);

        boolean canHaveNecromancy = false;
        if (nonAssigned.contains(Superpowers.NECROMANCY) && Necromancy.shouldBeIncluded()) {
            int alivePlayersNum = livesManager.getAlivePlayers().size();
            int deadPlayersNum = livesManager.getDeadPlayers().size();
            int totalPlayersNum = alivePlayersNum + deadPlayersNum;
            if (totalPlayersNum >= 6) {
                canHaveNecromancy = true;
                if (player.getRandom().nextDouble() <= (double)deadPlayersNum / (double)alivePlayersNum) {
                    return Superpowers.NECROMANCY;
                }
            }
        }
        if (!canHaveNecromancy) {
            implemented.remove(Superpowers.NECROMANCY);
            nonAssigned.remove(Superpowers.NECROMANCY);
        }

        //A shuffled queue with assigned superpowers at the very end - we can just choose the first power in the list.
        List<Superpowers> queue = new ArrayList<>(nonAssigned);
        for (Superpower assignedPower : playerSuperpowers.values()) {
            //This puts all the assigned powers at the end of the queue.
            Superpowers power = assignedPower.getSuperpower();
            if (!implemented.contains(power)) continue;
            queue.remove(power);
            queue.add(power);
        }
		
		return queue.getFirst();
    }

    public static void setSuperpower(ServerPlayer player, Superpowers superpower) {
        Superpower instance = superpower.getInstance(player);
        if (instance != null) playerSuperpowers.computeIfAbsent(player.getUUID(), k -> new HashSet<>()).add(instance);

        if (!WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME) {
            PlayerUtils.playSoundToPlayer(player, SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("wildlife_superpowers")), 0.2f, 1);
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
        return playerSuperpowers.containsKey(player.getUUID()) && !playerSuperpowers.get(player.getUUID()).isEmpty();
    }

    public static boolean hasActivePower(ServerPlayer player, Superpowers superpower) {
        if (!playerSuperpowers.containsKey(player.getUUID())) return false;
        for (Superpower power : playerSuperpowers.get(player.getUUID())) {
            if (power instanceof Mimicry mimicry && superpower != Superpowers.MIMICRY) {
                if (mimicry.getMimickedPower().getSuperpower() == superpower) return true;
            } else if (power.getSuperpower() == superpower) return true;
        }
        return false;
    }

    public static boolean hasActivatedPower(ServerPlayer player, Superpowers superpower) {
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
}
