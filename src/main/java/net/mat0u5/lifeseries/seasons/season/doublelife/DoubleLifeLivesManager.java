package net.mat0u5.lifeseries.seasons.season.doublelife;

import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static net.mat0u5.lifeseries.Main.currentSeason;

public class DoubleLifeLivesManager extends LivesManager {

    @Override
    public void resetPlayerLife(ServerPlayer player) {
        super.resetPlayerLife(player);
        if (currentSeason instanceof DoubleLife doubleLife) {
            doubleLife.syncSoulboundLives(player);
        }
    }


    @Override
    public void receiveLifeFromOtherPlayer(Component playerName, ServerPlayer target, boolean isRevive) {
        super.receiveLifeFromOtherPlayer(playerName, target, isRevive);
        if (currentSeason instanceof DoubleLife doubleLife) {
            doubleLife.syncSoulboundLives(target);
        }
    }

    @Override
    public void setPlayerLives(ServerPlayer player, int lives) {
        if (player == null) return;
        super.setPlayerLives(player, lives);

        if (!(currentSeason instanceof DoubleLife doubleLife)) return;
        if (doubleLife.shouldSuppressSoulboundLivesSync(player.getUUID())) return;
        if (!doubleLife.shouldShareLives()) return;

        UUID soulmateUUID = doubleLife.getSoulmateUUID(player.getUUID());
        if (soulmateUUID == null) return;

        ServerPlayer soulmate = PlayerUtils.getPlayer(soulmateUUID);
        if (soulmate == null) return;

        Integer soulmateLives = soulmate.ls$getLives();
        if (soulmateLives != null && Objects.equals(soulmateLives, lives)) return;

        super.setPlayerLives(soulmate, lives);
    }

    @Override
    public Map<ServerPlayer, Integer> getFinalRandomLives(List<ServerPlayer> players) {
        if (!(currentSeason instanceof DoubleLife doubleLife) || !doubleLife.shouldRollTogether()) return super.getFinalRandomLives(players);

        Map<UUID, Integer> pairLives = new HashMap<>();
        Map<ServerPlayer, Integer> lives = new HashMap<>();
        for (ServerPlayer player : players) {
            int randomLives = getRandomLife();

            UUID soulmateUUID = doubleLife.getSoulmateUUID(player.getUUID());
            if (soulmateUUID != null) {
                ServerPlayer soulmate = PlayerUtils.getPlayer(soulmateUUID);
                if (soulmate != null && soulmate.ls$hasAssignedLives()) {
                    randomLives = soulmate.ls$getLives();
                }

                UUID pairKey = player.getUUID().compareTo(soulmateUUID) < 0 ? player.getUUID() : soulmateUUID;
                if (pairLives.containsKey(pairKey)) {
                    randomLives = pairLives.get(pairKey);
                }
                else {
                    pairLives.put(pairKey, randomLives);
                }
            }

            lives.put(player, randomLives);
        }
        return lives;
    }
}

