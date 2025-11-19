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
        resetAllSuperpowers();
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
        players.removeIf(ServerPlayer::ls$isDead);
        players.removeIf(ServerPlayer::ls$isWatcher);

        List<ServerPlayer> prioritized = new ArrayList<>();

        // Preassigned first
        for (ServerPlayer p : players) {
            if (preAssignedSuperpowers.containsKey(p.getUUID()))
                prioritized.add(p);
        }

        // Others afterward
        for (ServerPlayer p : players) {
            if (!prioritized.contains(p))
                prioritized.add(p);
        }

        // Always give a new stacking power
        for (ServerPlayer p : prioritized) {
            Superpowers sp = getRandomPower(p);
            if (sp == Superpowers.NULL) continue;

            Superpower inst = sp.getInstance(p);
            if (inst != null) {
                playerSuperpowers.computeIfAbsent(p.getUUID(), k -> new HashSet<>()).add(inst);
            }
        }

        if (!WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME) {
            PlayerUtils.playSoundToPlayers(
                    players,
                    SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("wildlife_superpowers")),
                    0.2f,
                    1.0f
            );
        }
    }

    private static Superpowers getRandomPower(ServerPlayer player) {
        UUID uuid = player.getUUID();

        // Preassigned takes priority
        if (preAssignedSuperpowers.containsKey(uuid)) {
            return preAssignedSuperpowers.remove(uuid);
        }

        // Eligible powers
        List<Superpowers> implemented = new ArrayList<>(Superpowers.getImplemented());
        implemented.removeAll(blacklistedPowers);

        // Listening requires voicechat
        if (CompatibilityManager.voicechatLoaded() &&
            !VoicechatMain.isConnectedToSVC(uuid)) {
            implemented.remove(Superpowers.LISTENING);
        }

        // Determine owned powers
        Set<Superpower> existing = playerSuperpowers.get(uuid);
        Set<Superpowers> owned = new HashSet<>();
        if (existing != null) {
            for (Superpower sp : existing)
                owned.add(sp.getSuperpower());
        }

        // Available = all not yet owned
        List<Superpowers> available = new ArrayList<>();
        for (Superpowers s : implemented) {
            if (!owned.contains(s))
                available.add(s);
        }

        // Necromancy probability
        if (available.contains(Superpowers.NECROMANCY) && Necromancy.shouldBeIncluded()) {
            int alive = livesManager.getAlivePlayers().size();
            int dead = livesManager.getDeadPlayers().size();

            if (alive + dead >= 6) {
                double chance = (double) dead / (double) alive;
                if (player.getRandom().nextDouble() <= chance) {
                    return Superpowers.NECROMANCY;
                }
            }

            available.remove(Superpowers.NECROMANCY);
        }

        if (available.isEmpty())
            return Superpowers.NULL;

        Collections.shuffle(available, player.getRandom());
        return available.get(0);
    }

    public static void setSuperpower(ServerPlayer player, Superpowers sp) {
        Superpower inst = sp.getInstance(player);
        if (inst != null)
            playerSuperpowers.computeIfAbsent(player.getUUID(), k -> new HashSet<>()).add(inst);

        if (!WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME) {
            PlayerUtils.playSoundToPlayer(
                    player,
                    SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("wildlife_superpowers")),
                    0.2f,
                    1f
            );
        }

        Necromancy.checkRessurectedPlayersReset();
    }

    public static void pressedSuperpowerKey(ServerPlayer player) {
        UUID id = player.getUUID();
        if (!playerSuperpowers.containsKey(id)) return;

        if (!player.ls$isAlive()) {
            PlayerUtils.displayMessageToPlayer(
                    player,
                    Component.literal("Dead players can't use superpowers!"),
                    60
            );
            return;
        }

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
