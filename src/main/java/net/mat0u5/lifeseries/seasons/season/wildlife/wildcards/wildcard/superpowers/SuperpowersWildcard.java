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

    private static final Map<UUID, Set<Superpower>> playerSuperpowers = new LinkedHashMap<>();
    public static final Map<UUID, Superpowers> preAssignedSuperpowers = new HashMap<>();

    public static int ZOMBIES_HEALTH = 8;

    public static void setBlacklist(String blacklist) {
        blacklistedPowers = new ArrayList<>();

        String[] powers = blacklist.replace("[", "").replace("]", "").split(",");
        for (String p : powers) {
            Superpowers sp = Superpowers.fromString(p.trim());
            if (sp != null && sp != Superpowers.NULL) {
                blacklistedPowers.add(sp);
            }
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
        super.deactivate();
    }

    public static void onTick() {
        playerSuperpowers.values().forEach(set -> set.forEach(Superpower::tick));
    }

    public static void resetSuperpower(ServerPlayer player) {
        UUID uuid = player.getUUID();
        Set<Superpower> set = playerSuperpowers.get(uuid);

        if (set != null) {
            set.forEach(Superpower::turnOff);
            playerSuperpowers.remove(uuid);
        }

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

    public static void rollRandomSuperpowers(List<ServerPlayer> players) {
        players.removeIf(ServerPlayer::ls$isWatcher);

        List<ServerPlayer> prioritized = new ArrayList<>();

        // 2) Others after
        for (ServerPlayer p : players) {
            if (!prioritized.contains(p))
                prioritized.add(p);
        }

        // Assign powers
        for (ServerPlayer p : prioritized) {
            if (hasPower(p))
                continue;

            Superpowers sp = getRandomPower(p);
            Superpower inst = sp.getInstance(p);

            if (inst != null)
                playerSuperpowers.computeIfAbsent(p.getUUID(), k -> new HashSet<>()).add(inst);
        }

        // Play theme
        if (!WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME) {
            PlayerUtils.playSoundToPlayers(
                    players,
                    SoundEvent.createVariableRangeEvent(
                            IdentifierHelper.vanilla("wildlife_superpowers")
                    ),
                    0.2f,
                    1.0f
            );
        }
    }

    private static Superpowers getRandomPower(ServerPlayer player) {

        // Preassigned takes priority
        UUID uuid = player.getUUID();
        if (preAssignedSuperpowers.containsKey(uuid)) {
            Superpowers s = preAssignedSuperpowers.remove(uuid);
            return s;
        }

        // Collect eligible powers
        List<Superpowers> implemented = new ArrayList<>(Superpowers.getImplemented());

        implemented.removeAll(blacklistedPowers);

        if (CompatibilityManager.voicechatLoaded() &&
            !VoicechatMain.isConnectedToSVC(player.getUUID())) {
            implemented.remove(Superpowers.LISTENING);
        }

        // Remove already-assigned powers
        List<Superpowers> available = new ArrayList<>(implemented);

        for (Set<Superpower> set : playerSuperpowers.values()) {
            for (Superpower sp : set) {
                available.remove(sp.getSuperpower());
            }
        }

        Collections.shuffle(available);

        boolean necroPossible = false;

        if (available.contains(Superpowers.NECROMANCY) && Necromancy.shouldBeIncluded()) {

            int alive = livesManager.getAlivePlayers().size();
            int dead = livesManager.getDeadPlayers().size();

            if (alive + dead >= 6) {
                necroPossible = true;

                double chance = (double) dead / (double) alive;
                if (player.getRandom().nextDouble() <= chance) {
                    return Superpowers.NECROMANCY;
                }
            }
        }

        if (!necroPossible) {
            available.remove(Superpowers.NECROMANCY);
        }

        // Queue: unassigned first, assigned powers at end
        List<Superpowers> queue = new ArrayList<>(available);

        for (Set<Superpower> set : playerSuperpowers.values()) {
            for (Superpower sp : set) {
                Superpowers type = sp.getSuperpower();
                if (implemented.contains(type)) {
                    queue.remove(type);
                    queue.add(type);
                }
            }
        }

        return queue.get(0);
    }

    public static void setSuperpower(ServerPlayer player, Superpowers sp) {
        Superpower inst = sp.getInstance(player);

        if (inst != null)
            playerSuperpowers.computeIfAbsent(player.getUUID(), k -> new HashSet<>()).add(inst);

        if (!WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME) {
            PlayerUtils.playSoundToPlayer(
                    player,
                    SoundEvent.createVariableRangeEvent(
                            IdentifierHelper.vanilla("wildlife_superpowers")
                    ),
                    0.2f,
                    1f
            );
        }

        Necromancy.checkRessurectedPlayersReset();
    }

    public static void pressedSuperpowerKey(ServerPlayer player) {
        UUID id = player.getUUID();

        if (!playerSuperpowers.containsKey(id)) return;


        playerSuperpowers.get(id).forEach(Superpower::onKeyPressed);
    }

    public static boolean hasPower(ServerPlayer player) {
        Set<Superpower> set = playerSuperpowers.get(player.getUUID());
        return set != null && !set.isEmpty();
    }

    public static boolean hasActivePower(ServerPlayer player, Superpowers type) {
        Set<Superpower> set = playerSuperpowers.get(player.getUUID());
        if (set == null) return false;

        for (Superpower p : set) {
            if (p instanceof Mimicry mimic && type != Superpowers.MIMICRY) {
                if (mimic.getMimickedPower().getSuperpower() == type) return true;
            } else if (p.getSuperpower() == type) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasActivatedPower(ServerPlayer player, Superpowers type) {
        Set<Superpower> set = playerSuperpowers.get(player.getUUID());
        if (set == null) return false;

        for (Superpower p : set) {
            if (p instanceof Mimicry mimic && type != Superpowers.MIMICRY) {
                if (mimic.getMimickedPower().active) return true;
            } else if (p.active && p.getSuperpower() == type) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    public static Superpower getSuperpowerInstance(ServerPlayer player) {
        Set<Superpower> set = playerSuperpowers.get(player.getUUID());
        if (set == null || set.isEmpty()) return null;

        for (Superpower p : set) {
            if (p.active) return p;
        }

        return set.iterator().next();
    }

    public static Superpowers getSuperpower(ServerPlayer player) {
        Superpower inst = getSuperpowerInstance(player);
        if (inst == null) return Superpowers.NULL;

        if (inst instanceof Mimicry mimic) {
            return mimic.getMimickedPower().getSuperpower();
        }

        return inst.getSuperpower();
    }
}
