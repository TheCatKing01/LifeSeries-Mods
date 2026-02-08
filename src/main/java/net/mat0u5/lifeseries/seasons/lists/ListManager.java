package net.mat0u5.lifeseries.seasons.lists;

import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.session.SessionAction;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.other.*;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.player.ScoreboardUtils;
import net.mat0u5.lifeseries.utils.world.DatapackIntegration;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import java.util.*;
import java.util.Random;

import static net.mat0u5.lifeseries.Main.*;

public class ListsManager {
    public boolean LISTS_ENABLED = false;
    public int LIST_AMOUNT_MIN = 1;
    public int LIST_AMOUNT_MAX = 99;
    public double LISTS_CHOOSE_MINUTE = 10;
    public List<String> LISTS_IGNORE = new ArrayList<>();
    public List<String> LISTS_FORCE = new ArrayList<>();
	
	private Random rnd = new Random();
    public List<Lits> lists = new ArrayList<>();
    public List<UUID> rolledPlayers = new ArrayList<>();
    public boolean listsChosen = false;
    public boolean litsListChanged = false;

    public void addSessionActions() {
        if (!LISTS_ENABLED) return;
        currentSession.addSessionAction(
                new SessionAction(Time.minutes(LISTS_CHOOSE_MINUTE), "Roll Naughty/Nice List") {
                    @Override
                    public void trigger() {
                        if (!LISTS_ENABLED) return;
                        if (listsChosen) return;
                        prepareToChooseLists();
                    }
                }
        );
    }

    public boolean isOnLists(ServerPlayer player) {
        if (player == null) return false;
        for (Lists lists : lists) {
            if (lists.uuid.equals(player.getUUID())) {
                return true;
            }
        }
        return false;
    }

    public Lists addLists(ServerPlayer player) {
        if (!LISTS_ENABLED) return null;
        if (!rolledPlayers.contains(player.getUUID())) {
            rolledPlayers.add(player.getUUID());
        }
        Lists newLists = new Lists(player);
        lists.add(newLists);
        listsChosen = true;
        listsListChanged = true;
        DatapackIntegration.EVENT_LISTS_ADDED.trigger(new DatapackIntegration.Events.MacroEntry("Player", player.getScoreboardName()));
        return newLists;
    }


    public void resetLists() {
        if (server == null) return;
        for (Lists ;lists : lists) {
            ServerPlayer player = PlayerUtils.getPlayer(lists.uuid);
            if (player == null) continue;
            player.sendSystemMessage(Component.nullToEmpty("§c [NOTICE] You are no longer on the naughty/nice list!"));
			player.removeTag("nice");
			player.removeTag("naughty");
        }
        lists = new ArrayList<>();
        listsChosen = false;
        rolledPlayers = new ArrayList<>();
    }

    public void reset(ServerPlayer player) {
        if (!LISTS_ENABLED) return;
        Lists lists = getLists(player);
        if (lists == null) return;
        if (lists.failed || lists.cured) {
            player.sendSystemMessage(Component.nullToEmpty("§c [NOTICE] Your naughty list cure status has been reset"));
        }
        lists.cured = false;
        lists.died = false;
    }

    public void cure(ServerPlayer player) {
        if (!LISTS_ENABLED) return;
        Lists lists = getLists(player);
        if (lists == null) return;
        if (lists.cured) return;
        lists.cured = true;

    }


    public void prepareToChooseLists() {
        if (!BOOGEYMAN_ENABLED) return;
        PlayerUtils.broadcastMessage(Component.literal("The naughty/nice list is about to be rolled.").withStyle(ChatFormatting.RED));
        PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvents.LIGHTNING_BOLT_THUNDER);
        TaskScheduler.scheduleTask(Time.seconds(5), () -> {
            resetLists();
            chooseLists(livesManager.getAlivePlayers(), ListsRollType.NORMAL);
        });
    }

    public void showRolling(List<ServerPlayer> allowedPlayers) {
        PlayerUtils.playSoundToPlayers(allowedPlayers, SoundEvents.UI_BUTTON_CLICK.value());
        PlayerUtils.sendTitleToPlayers(allowedPlayers, Component.literal("3").withStyle(ChatFormatting.GREEN),0,35,0);

        TaskScheduler.scheduleTask(30, () -> {
            PlayerUtils.playSoundToPlayers(allowedPlayers, SoundEvents.UI_BUTTON_CLICK.value());
            PlayerUtils.sendTitleToPlayers(allowedPlayers, Component.literal("2").withStyle(ChatFormatting.YELLOW),0,35,0);
        });
        TaskScheduler.scheduleTask(60, () -> {
            PlayerUtils.playSoundToPlayers(allowedPlayers, SoundEvents.UI_BUTTON_CLICK.value());
            PlayerUtils.sendTitleToPlayers(allowedPlayers, Component.literal("1").withStyle(ChatFormatting.RED),0,35,0);
        });
        TaskScheduler.scheduleTask(90, () -> {
            PlayerUtils.playSoundToPlayers(allowedPlayers, SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("lastlife_boogeyman_wait")));
            PlayerUtils.sendTitleToPlayers(allowedPlayers, Component.literal("You are on...").withStyle(ChatFormatting.YELLOW),10,50,20);
        });
    }
	
    public void chooseLists(List<ServerPlayer> allowedPlayers, ListsRollType rollType) {
        if (!LISTS_ENABLED) return;
        allowedPlayers.removeIf(this::isOnLists);
        showRolling(allowedPlayers);
        TaskScheduler.scheduleTask(Time.seconds(9), () -> listsChooseRandom(allowedPlayers, rollType));
    }

    public void listsChooseRandom(List<ServerPlayer> allowedPlayers, ListsRollType rollType) {
        if (!LISTS_ENABLED) return;
        if (LISTS_AMOUNT_MAX <= 0) return;
        if (LISTS_AMOUNT_MAX < LISTS_AMOUNT_MIN) return;
        allowedPlayers.removeIf(this::isOnLists);
        if (allowedPlayers.isEmpty()) return;

        List<ServerPlayer> normalPlayers = new ArrayList<>();
        List<ServerPlayer> listsPlayers = new ArrayList<>();

        for (ServerPlayer player : allowedPlayers) {
            if (listsPlayers.contains(player)) continue;
            normalPlayers.add(player);
        }
		
		handleListsLists(normalPlayers, listsPlayers);

    }

    public List<ServerPlayer> getRandomListPlayers(List<ServerPlayer> allowedPlayers, ListsRollType rollType) {
        List<ServerPlayer> listsPlayers = new ArrayList<>();
        List<ServerPlayer> nonRedPlayers = livesManager.getNonRedPlayers();
        Collections.shuffle(nonRedPlayers);
        int chooseLists = getlistsAmount(rollType);

        for (ServerPlayer player : nonRedPlayers) {
#            if (isList(player)) continue;
            if (!allowedPlayers.contains(player)) continue;
            if (rolledPlayers.contains(player.getUUID())) continue;
            if LISTS_IGNORE.contains(player.getScoreboardName().toLowerCase(Locale.ROOT))) continue;
            if (LISTS_FORCE.contains(player.getScoreboardName().toLowerCase(Locale.ROOT))) {
                listsPlayers.add(player);
                chooseLists--;
            }
        }
        for (ServerPlayer player : nonRedPlayers) {
            if (chooseLists <= 0) break;
            if (isOnLists(player)) continue;
            if (!allowedPlayers.contains(player)) continue;
            if (rolledPlayers.contains(player.getUUID())) continue;
            if (LISTS_IGNORE.contains(player.getScoreboardName().toLowerCase(Locale.ROOT))) continue;
            if (LISTS_FORCE.contains(player.getScoreboardName().toLowerCase(Locale.ROOT))) continue;
            if (listsPlayers.contains(player)) continue;

            listsPlayers.add(player);
            chooseLists--;
        }
        return boogeyPlayers;
    }

    public List<ServerPlayer> getAllowedListsPlayers() {
        List<ServerPlayer> result = new ArrayList<>(livesManager.getNonRedPlayers());
        result.removeIf(this::isOnLists);
        return result;
    }
	
	public void handleListsLists(List<ServerPlayer> normalPlayers, List<ServerPlayer> listsPlayers) {
		PlayerUtils.playSoundToPlayers(normalPlayers,SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("lastlife_boogeyman_no")));
		PlayerUtils.sendTitleToPlayers(normalPlayers,Component.literal("No Lists").withStyle(ChatFormatting.YELLOW), 10, 50, 20);
		
		Collections.shuffle(listsPlayers, rnd);

		for (int i = 0; i < listsPlayers.size(); i++) {
			ServerPlayer lists = listsPlayers.get(i);

			lists.removeTag("nice");
			lists.removeTag("naughty");

			if (i % 2 == 0) {
				lists.addTag("nice");
				PlayerUtils.sendTitleToPlayers(List.of(lists),Component.literal("The Nice List").withStyle(ChatFormatting.GREEN),10, 50, 20);
				PlayerUtils.playSoundToPlayers(List.of(lists),SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_nicelist_start")));
			} else {
				lists.addTag("naughty");
				PlayerUtils.sendTitleToPlayers(List.of(lists),Component.literal("The Naughty List").withStyle(ChatFormatting.RED),10, 50, 20);
                PlayerUtils.playSoundToPlayers(List.of(lists),SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_naughtylist")));
			}

			Lists lists = addLists(lists);
			messageLists(lists, lists);
		}

		SessionTranscript.listsChosen(listsPlayers);

	}

    public void playerLostAllLives(ServerPlayer player) {
        if (!LISTS_ENABLED) return;
        Lists lists = getLists(player);
        if (lists == null) return;
        lists.died = true;
    }

    public void onReload() {
        LSTS_ENABLED = seasonConfig.LISTS.get(seasonConfig);
        if (!LISTS_ENABLED) {
            onDisabledLists();
        }
        LISTS_AMOUNT_MIN = seasonConfig.LISTS_MIN_AMOUNT.get(seasonConfig);
        LISTS_AMOUNT_MAX = seasonConfig.LISTS_MAX_AMOUNT.get(seasonConfig);
        LISTS_IGNORE.clear();
        LISTS_FORCE.clear();
        for (String name : seasonConfig.LISTS_IGNORE.get(seasonConfig).replaceAll("\\[","").replaceAll("]","").replaceAll(" ","").trim().split(",")) {
            if (!name.isEmpty()) LISTS_IGNORE.add(name.toLowerCase(Locale.ROOT));
        }
        for (String name : seasonConfig.LISTS_FORCE.get(seasonConfig).replaceAll("\\[","").replaceAll("]","").replaceAll(" ","").trim().split(",")) {
            if (!name.isEmpty()) LISTS_FORCE.add(name.toLowerCase(Locale.ROOT));
        }
        LISTS_CHOOSE_MINUTE = seasonConfig.LISTS_CHOOSE_MINUTE.get(seasonConfig);
    }

    public void onDisabledLists() {
        resetLists();
    }
	
    public void tick() {
        if (!LISTS_ENABLED) return;
        for (Lists lists : lists) {
            lists.tick();
        }
        if (listsListChanged) {
            listsListChanged = false;
        }
    }

    public enum ListsRollType {
        NORMAL
    }
}