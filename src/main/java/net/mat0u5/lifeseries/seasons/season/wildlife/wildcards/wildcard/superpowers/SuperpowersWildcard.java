package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers;

import net.mat0u5.lifeseries.compatibilities.CompatibilityManager;
import net.mat0u5.lifeseries.compatibilities.voicechat.VoicechatMain;
import net.mat0u5.lifeseries.config.ModifiableText;
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
import java.util.stream.Collectors;

import static net.mat0u5.lifeseries.Main.livesManager;

public class SuperpowersWildcard extends Wildcard {
    public static boolean WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME = false;
    public static boolean WILDCARD_SUPERPOWERS_MAX_POWERS_MESSAGE = true;	
	public static boolean WILDCARD_CALLBACK_POWER_STACKING = false;	
	public static boolean WILDCARD_CALLBACK_OVERRIDE_TURN_OFF = false;
	public static boolean WILDCARD_CALLBACK_RESET_AT_MAX = false;	
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
	
	public static ChatFormatting getTeamColor(ServerPlayer player) {
		if (player.getTeam() != null) return player.getTeam().getColor();
		return ChatFormatting.WHITE;
	}

    @Override
    public Wildcards getType() {
        return Wildcards.SUPERPOWERS;
    }
	
	@Override
	public void activate() {
		List<ServerPlayer> allPlayers = PlayerUtils.getAllPlayers();
		rollRandomSuperpowers(allPlayers);
		super.activate();
	}

    @Override
    public void deactivate() {
		if (!WILDCARD_CALLBACK_POWER_STACKING) {
			resetAllSuperpowers();
			DatapackIntegration.initSuperpowers();
			for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
				resetSuperpower(player); // per-player cleanup
			}

			playerSuperpowers.clear();
			DatapackIntegration.initSuperpowers();
		}
        super.deactivate();
    }
	
	public static void externalResetAllPowers() {
		Wildcard instance = Wildcards.SUPERPOWERS.getInstance();
		if (instance instanceof SuperpowersWildcard sp) {
			if (!WILDCARD_CALLBACK_OVERRIDE_TURN_OFF) {
				sp.resetAllSuperpowers();
			}
		}
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

	private static final Set<UUID> pendingReset = new HashSet<>();

	public static int rollRandomSuperpowers(List<ServerPlayer> allPlayers) {
		allPlayers.removeIf(ServerPlayer::ls$isDead);
		allPlayers.removeIf(ServerPlayer::ls$isWatcher);
		
		if (allPlayers.isEmpty()) return 0;

		int grantedPowers = 0;

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

		Set<UUID> maxedPlayers = new HashSet<>();

		for (ServerPlayer player : allPlayers) {
			playerSuperpowers.putIfAbsent(player.getUUID(), new HashSet<>());
			Set<Superpower> currentPowers = playerSuperpowers.get(player.getUUID());

			if (WILDCARD_CALLBACK_RESET_AT_MAX && pendingReset.contains(player.getUUID())) {
				pendingReset.remove(player.getUUID());
				currentPowers.forEach(Superpower::turnOff);
				currentPowers.clear();
				DatapackIntegration.initSuperpowers();

				for (int r = 0; r < POWERS_PER_ROLL; r++) {
					if (implemented.isEmpty()) break;
					Superpowers power = implemented.get(player.getRandom().nextInt(implemented.size()));
					Superpower instance = power.getInstance(player);
					if (instance != null) {
						currentPowers.add(instance);
						DatapackIntegration.activateSuperpower(player, power);
					}
				}
				continue;
			}

			Set<Superpowers> ownedPowers = currentPowers.stream()
					.map(Superpower::getSuperpower)
					.collect(Collectors.toSet());

			List<Superpowers> availablePowers = implemented.stream()
					.filter(p -> !ownedPowers.contains(p))
					.toList();

			if (availablePowers.isEmpty()) {
				if (currentPowers.size() >= POWERS_PER_PLAYER) {
					maxedPlayers.add(player.getUUID());
					if (WILDCARD_CALLBACK_RESET_AT_MAX) pendingReset.add(player.getUUID());
				}
				continue;
			}

			for (int i = 0; i < POWERS_PER_ROLL; i++) {
				if (currentPowers.size() >= POWERS_PER_PLAYER) {
					maxedPlayers.add(player.getUUID());
					if (WILDCARD_CALLBACK_RESET_AT_MAX) pendingReset.add(player.getUUID());
					break;
				}

				Superpowers power = null;

				if (assignedSuperpowers.containsKey(player.getUUID())) {
					power = assignedSuperpowers.remove(player.getUUID());
				}

				if (power == null && necroAllowed && player.getRandom().nextDouble() <= necroChance) {
					power = Superpowers.NECROMANCY;
					necroAllowed = false;
				}
						
				if (power == null) {
					List<Superpowers> remaining = implemented.stream()
							.filter(p -> {
								final Superpowers finalPower = p; 
								return currentPowers.stream().noneMatch(sp -> sp.getSuperpower() == finalPower);
							})
							.toList();

					if (remaining.isEmpty()) {
						if (currentPowers.size() >= POWERS_PER_PLAYER) {
							maxedPlayers.add(player.getUUID());
							if (WILDCARD_CALLBACK_RESET_AT_MAX) pendingReset.add(player.getUUID());
						}
						break;
					}

					power = remaining.get(player.getRandom().nextInt(remaining.size()));
					final Superpowers chosenPower = power;

					if (currentPowers.stream().anyMatch(p -> p.getSuperpower() == chosenPower)) {
						continue;
					}

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

		if (!maxedPlayers.isEmpty() && WILDCARD_SUPERPOWERS_MAX_POWERS_MESSAGE) {
			List<ServerPlayer> maxedPlayerList = allPlayers.stream()
				.filter(player -> maxedPlayers.contains(player.getUUID()))
				.toList();

			MutableComponent message;

			if (maxedPlayerList.size() == 1) {
				ServerPlayer player = maxedPlayerList.get(0);
				message = ModifiableText.WILDLIFE_SUPERPOWER_MAX_ROLL_SINGLE
					.get(Component.literal(player.getScoreboardName()).withStyle(getTeamColor(player)))
					.copy()
					.withStyle(ChatFormatting.RED);
			} else {
				message = ModifiableText.WILDLIFE_SUPERPOWER_MAX_ROLL_MULTIPLE
					.get(maxedPlayerList.size())
					.copy()
					.withStyle(ChatFormatting.RED);

				for (int i = 0; i < maxedPlayerList.size(); i++) {
					ServerPlayer player = maxedPlayerList.get(i);
					ChatFormatting teamColor = getTeamColor(player);
					message.append(Component.literal(player.getScoreboardName()).withStyle(teamColor));
					if (i < maxedPlayerList.size() - 1) {
						message.append(Component.literal(", ").withStyle(ChatFormatting.WHITE));
					}
				}
			}

			PlayerUtils.broadcastMessageToAdmins(message);
			}
		}
		return grantedPowers;
	}

	public static boolean setSuperpower(ServerPlayer player, Superpowers superpower) {
		Set<Superpower> currentPowers = playerSuperpowers.computeIfAbsent(player.getUUID(), k -> new HashSet<>());

		boolean alreadyHas = currentPowers.stream()
				.anyMatch(p -> p.getSuperpower() == superpower);

		if (alreadyHas) {
				MutableComponent message = ModifiableText.WILDLIFE_SUPERPOWER_ALREADY_HAS
					.get(Component.literal(player.getScoreboardName()).withStyle(getTeamColor(player)), superpower.getString())
					.copy()
					.withStyle(ChatFormatting.RED);
				PlayerUtils.broadcastMessageToAdmins(message);
			return false;
		}

		if (currentPowers.size() >= POWERS_PER_PLAYER) {
			if (WILDCARD_SUPERPOWERS_MAX_POWERS_MESSAGE) {
				MutableComponent message = ModifiableText.WILDLIFE_SUPERPOWER_MAX_SET_SINGLE
					.get(Component.literal(player.getScoreboardName()).withStyle(getTeamColor(player)), superpower.getString())
					.copy()
					.withStyle(ChatFormatting.RED);
				PlayerUtils.broadcastMessageToAdmins(message);
			}

			if (WILDCARD_CALLBACK_RESET_AT_MAX) {
				pendingReset.add(player.getUUID());
			}

			return false;
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
		
		return instance != null;
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
            PlayerUtils.displayMessageToPlayer(
                player,
                ModifiableText.WILDLIFE_SUPERPOWES_DEAD.get(),
                60
            );
            return;
        }

        playerSuperpowers.get(player.getUUID())
            .forEach(Superpower::onKeyPressed);
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
		if (powers == null || powers.isEmpty()) return 0;

		Set<Superpowers> uniquePowers = new HashSet<>();
		for (Superpower power : powers) {
			uniquePowers.add(power.getSuperpower());
		}

		return uniquePowers.size();
	}

}