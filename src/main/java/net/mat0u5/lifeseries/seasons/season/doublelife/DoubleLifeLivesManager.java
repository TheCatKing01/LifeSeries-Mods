package net.mat0u5.lifeseries.seasons.season.doublelife;

import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.mat0u5.lifeseries.utils.other.TaskScheduler;
import net.mat0u5.lifeseries.utils.other.Time;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static net.mat0u5.lifeseries.LifeSeries.currentSeason;

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
    public void assignRandomLives(List<ServerPlayer> players) {
        assignedLives = true;
        players.forEach(this::resetPlayerLife);

        Component title = ModifiableText.LIVES_RANDOMIZE_TITLE.get();
        if (currentSeason instanceof DoubleLife doubleLife) {
            if (doubleLife.shouldShareLives()) {
                title = ModifiableText.DOUBLELIFE_LIVES_RANDOMIZE_TITLE_BOTH.get();
            }
            else {
                title = ModifiableText.DOUBLELIFE_LIVES_RANDOMIZE_TITLE_SOLO.get();
            }
        }

        PlayerUtils.sendTitleToPlayers(players, title, 10, 40, 10);
        TaskScheduler.scheduleTask(Time.seconds(3), () -> rollLives(players));
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

    @Override
    public void showDeathTitle(ServerPlayer player) {
        if (currentSeason instanceof DoubleLife doubleLife && doubleLife.isSoulmateOnline(player)) {
            ServerPlayer soulmate = doubleLife.getSoulmate(player);
            if (soulmate != null && doubleLife.SOULBOUND_LIVES) {
                if (doubleLife.isMainSoulmate(player)) {
                    if (SHOW_DEATH_TITLE) {
                        PlayerUtils.sendTitleWithSubtitleToPlayers(PlayerUtils.getAllPlayers(), ModifiableText.DOUBLELIFE_FINAL_DEATH_TITLE.get(player, soulmate), ModifiableText.DOUBLELIFE_FINAL_DEATH_TITLE_SUBTITLE.get(), 20, 80, 20);
                    }
                    Component deathMessage = ModifiableText.DOUBLELIFE_FINAL_DEATH.get(player, soulmate);
                    if (!deathMessage.getString().isEmpty()) {
                        if (SHOW_LIFE_DIFF) {
                            TaskScheduler.schedulePriorityTask(1, () -> {
                                PlayerUtils.broadcastMessage(deathMessage);
                            });
                        }
                        else {
                            PlayerUtils.broadcastMessage(deathMessage);
                        }
                    }
                }
                return;
            }
        }


        super.showDeathTitle(player);
    }
}

