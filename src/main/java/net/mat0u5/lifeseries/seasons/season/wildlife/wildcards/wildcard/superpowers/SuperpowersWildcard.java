package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers;

import net.mat0u5.lifeseries.compatibilities.CompatibilityManager;
import net.mat0u5.lifeseries.compatibilities.voicechat.VoicechatMain;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcard;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.Mimicry;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.Necromancy;
import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.DatapackIntegration;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.mat0u5.lifeseries.Main.livesManager;

public class SuperpowersWildcard extends Wildcard {
    public static boolean WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME = false;
    public static boolean WILDCARD_CALLBACK_POWER_STACKING = false;	
    public static List<Superpowers> blacklistedPowers = List.of();

    private static final Map<UUID, Set<Superpower>> playerSuperpowers = new HashMap<>();
    public static final Map<UUID, Superpowers> assignedSuperpowers = new HashMap<>();
    public static int ZOMBIES_HEALTH = 8;
	public static int POWERS_PER_PLAYER = 1;
    public static int POWERS_PER_ROLL = 1;

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
		List<ServerPlayer> allPlayers = new ArrayList<>();
		rollRandomSuperpowers(allPlayers);
		super.activate();
	}


    @Override
    public void deactivate() {
		if (!WILDCARD_CALLBACK_POWER_STACKING) {
			resetAllSuperpowers();
		}
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
		DatapackIntegration.deactivateSuperpower(player);

    }

    public static void resetAllSuperpowers() {
        playerSuperpowers.values().forEach(set -> set.forEach(Superpower::turnOff));
        playerSuperpowers.clear();
        Necromancy.checkRessurectedPlayersReset();
        DatapackIntegration.initSuperpowers();
    }

	public static void rollRandomSuperpowers(List<ServerPlayer> allPlayers) {
		allPlayers.removeIf(ServerPlayer::ls$isDead);
		allPlayers.removeIf(ServerPlayer::ls$isWatcher);

		Collections.shuffle(allPlayers);

		List<Superpowers> implemented = new ArrayList<>(Superpowers.getImplemented());
		blacklistedPowers.forEach(implemented::remove);

		implemented.removeIf(p ->
			p == Superpowers.LISTENING &&
			CompatibilityManager.voicechatLoaded() &&
			!VoicechatMain.isConnectedToSVC(allPlayers.get(0).getUUID())
		);

		boolean necroAllowed = implemented.contains(Superpowers.NECROMANCY) && Necromancy.shouldBeIncluded();
		double necroChance = 0;
		if (necroAllowed) {
			int alive = livesManager.getAlivePlayers().size();
			int dead = livesManager.getDeadPlayers().size();
			if (alive + dead >= 6) {
				necroChance = (double) dead / alive;
			} else {
				implemented.remove(Superpowers.NECROMANCY);
				necroAllowed = false;
			}
		} else {
			implemented.remove(Superpowers.NECROMANCY);
		}

		Collections.shuffle(implemented);

		List<ServerPlayer> maxedPlayers = new ArrayList<>();

		for (ServerPlayer player : allPlayers) {
			playerSuperpowers.putIfAbsent(player.getUUID(), new HashSet<>());
			Set<Superpower> currentPowers = playerSuperpowers.get(player.getUUID());

			for (int i = 0; i < POWERS_PER_ROLL; i++) {
				if (currentPowers.size() >= POWERS_PER_PLAYER) {
					if (!maxedPlayers.contains(player)) maxedPlayers.add(player);
					break;
				}

				Superpowers power = null;

				if (assignedSuperpowers.containsKey(player.getUUID())) {
					power = assignedSuperpowers.remove(player.getUUID());
				}

				if (power == null && necroAllowed && player.getRandom().nextDouble() <= necroChance) {
					power = Superpowers.NECROMANCY;
					implemented.remove(Superpowers.NECROMANCY);
					necroAllowed = false;
				}

				if (power == null) {
					if (implemented.isEmpty()) break; // no powers left
					power = implemented.get(player.getRandom().nextInt(implemented.size()));
				}

				Superpower instance = power.getInstance(player);
				if (instance != null) {
					currentPowers.add(instance);
					DatapackIntegration.activateSuperpower(player, power);
				}
			}
		}

		if (!WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME) {
			PlayerUtils.playSoundToPlayers(
				allPlayers,
				SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("wildlife_superpowers")),
				0.2f, 1
			);
		}
		

		if (!maxedPlayers.isEmpty()) {
			MutableComponent message = Component.literal(maxedPlayers.size() + " player(s) have reached max powers and did not receive additional powers: ")
												.withStyle(ChatFormatting.RED);

			for (int i = 0; i < maxedPlayers.size(); i++) {
				var player = maxedPlayers.get(i);
				message.append(Component.literal(player.getScoreboardName()));
				if (i < maxedPlayers.size() - 1) {
					message.append(Component.literal(", ").withStyle(ChatFormatting.RED));
				}
			}

			PlayerUtils.broadcastMessageToAdmins(message);
		}

	}

	public static void setSuperpower(ServerPlayer player, Superpowers superpower) {
		Set<Superpower> currentPowers = playerSuperpowers.computeIfAbsent(player.getUUID(), k -> new HashSet<>());

		if (currentPowers.size() >= POWERS_PER_PLAYER) {
			MutableComponent message = Component.literal("").withStyle(ChatFormatting.RED)
				.append(Component.literal(player.getScoreboardName()))
				.append(Component.literal(" has reached max powers and did not receive " + superpower.getString())
						.withStyle(ChatFormatting.RED));

			PlayerUtils.broadcastMessageToAdmins(message);
			return;
		}

		Superpower instance = superpower.getInstance(player);
		if (instance != null) {
			currentPowers.add(instance);
			DatapackIntegration.activateSuperpower(player, superpower);
		}

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
		if (instance != null) {
			// ensure we store a Set<Superpower> and add the instance
			playerSuperpowers.computeIfAbsent(player.getUUID(), k -> new HashSet<>()).add(instance);
			DatapackIntegration.activateSuperpower(player, power);
		}

		if (!WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME) {
			PlayerUtils.playSoundToPlayer(
					player,
					SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("wildlife_superpowers")),
					0.2f,
					1
			);
		}
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

	public static int getSuperpowerCount(ServerPlayer player) {
    Set<Superpower> powers = playerSuperpowers.get(player.getUUID());
    return powers == null ? 0 : powers.size();
	}

}