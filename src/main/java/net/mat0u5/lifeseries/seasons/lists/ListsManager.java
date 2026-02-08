package net.mat0u5.lifeseries.seasons.lists;

import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.mat0u5.lifeseries.seasons.session.SessionAction;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.other.*;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.mat0u5.lifeseries.utils.world.DatapackIntegration;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import java.util.*;

import static net.mat0u5.lifeseries.Main.*;

public class ListsManager {

    public boolean LISTS_ENABLED = false;
    public int LISTS_AMOUNT_MIN = 1;
    public int LISTS_AMOUNT_MAX = 99;
    public double LISTS_CHOOSE_MINUTE = 10;

    public List<String> LISTS_IGNORE = new ArrayList<>();
    public List<String> LISTS_FORCE = new ArrayList<>();

    private final Random rnd = new Random();

    public List<Lists> lists = new ArrayList<>();
    public List<UUID> rolledPlayers = new ArrayList<>();

    public boolean listsChosen = false;
    public boolean listsListChanged = false;

    public void addSessionActions() {
        if (!LISTS_ENABLED) return;

        currentSession.addSessionAction(
            new SessionAction(Time.minutes(LISTS_CHOOSE_MINUTE), "Roll naughty/nice lists") {
                @Override
                public void trigger() {
                    if (!LISTS_ENABLED || listsChosen) return;
                    prepareToChooseLists();
                }
            }
        );
    }

    public void prepareToChooseLists() {
        if (!LISTS_ENABLED) return;

        PlayerUtils.broadcastMessage(
            Component.literal("The naughty/nice list is about to be rolled.")
                .withStyle(ChatFormatting.RED)
        );
        PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvents.LIGHTNING_BOLT_THUNDER);

        TaskScheduler.scheduleTask(Time.seconds(5), () -> {
            resetLists();
            chooseLists(livesManager.getAlivePlayers(), ListsRollType.NORMAL);
        });
    }

    public void chooseLists(List<ServerPlayer> allowedPlayers, ListsRollType rollType) {
        allowedPlayers.removeIf(this::isOnLists);
        if (allowedPlayers.isEmpty()) return;

        showRolling(allowedPlayers);
        TaskScheduler.scheduleTask(Time.seconds(9),
            () -> listsChooseRandom(allowedPlayers, rollType));
    }
	
	public void showRolling(List<ServerPlayer> players) {
		PlayerUtils.playSoundToPlayers(players, SoundEvents.UI_BUTTON_CLICK);
		PlayerUtils.sendTitleToPlayers(players, Component.literal("3").withStyle(ChatFormatting.GREEN), 0, 35, 0);

		TaskScheduler.scheduleTask(30, () -> {
			PlayerUtils.playSoundToPlayers(players, SoundEvents.UI_BUTTON_CLICK);
			PlayerUtils.sendTitleToPlayers(players, Component.literal("2").withStyle(ChatFormatting.YELLOW), 0, 35, 0);
		});

		TaskScheduler.scheduleTask(60, () -> {
			PlayerUtils.playSoundToPlayers(players, SoundEvents.UI_BUTTON_CLICK);
			PlayerUtils.sendTitleToPlayers(players, Component.literal("1").withStyle(ChatFormatting.RED), 0, 35, 0);
		});

		TaskScheduler.scheduleTask(90, () -> {
			PlayerUtils.playSoundToPlayers(players,
				SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("lastlife_boogeyman_wait")));
			PlayerUtils.sendTitleToPlayers(players,
				Component.literal("You are on...").withStyle(ChatFormatting.YELLOW), 10, 50, 20);
		});
	}

    public void listsChooseRandom(List<ServerPlayer> allowedPlayers, ListsRollType rollType) {
        if (!LISTS_ENABLED) return;
        if (LISTS_AMOUNT_MAX < LISTS_AMOUNT_MIN) return;

        List<ServerPlayer> listPlayers = getRandomListPlayers(allowedPlayers, rollType);
        List<ServerPlayer> normalPlayers = new ArrayList<>(allowedPlayers);
        normalPlayers.removeAll(listPlayers);

        handleListsLists(normalPlayers, listPlayers);
        listsChosen = true;
    }

    public List<ServerPlayer> getRandomListPlayers(List<ServerPlayer> allowedPlayers, ListsRollType rollType) {
        List<ServerPlayer> result = new ArrayList<>();
        List<ServerPlayer> candidates = new ArrayList<>(livesManager.getNonRedPlayers());

        candidates.removeIf(p ->
            !allowedPlayers.contains(p) ||
            rolledPlayers.contains(p.getUUID()) ||
            LISTS_IGNORE.contains(p.getScoreboardName().toLowerCase(Locale.ROOT))
        );

        Collections.shuffle(candidates);
        int amount = getListsAmount(rollType);

        for (ServerPlayer p : candidates) {
            if (LISTS_FORCE.contains(p.getScoreboardName().toLowerCase(Locale.ROOT))) {
                result.add(p);
                amount--;
            }
        }

        for (ServerPlayer p : candidates) {
            if (amount <= 0) break;
            if (result.contains(p)) continue;
            result.add(p);
            amount--;
        }

        return result;
    }

    public int getListsAmount(ListsRollType rollType) {
        int count = LISTS_AMOUNT_MIN;
        List<ServerPlayer> nonReds = livesManager.getNonRedPlayers();

        while (Math.random() < 0.5 && count < nonReds.size()) {
            count++;
        }
        return Math.min(count, LISTS_AMOUNT_MAX);
    }

    public void handleListsLists(List<ServerPlayer> normalPlayers, List<ServerPlayer> listPlayers) {

        PlayerUtils.sendTitleToPlayers(
            normalPlayers,
            Component.literal("No List").withStyle(ChatFormatting.YELLOW),
            10, 50, 20
        );

        Collections.shuffle(listPlayers, rnd);

        for (int i = 0; i < listPlayers.size(); i++) {
            ServerPlayer player = listPlayers.get(i);

            player.removeTag("nice");
            player.removeTag("naughty");

            if (i % 2 == 0) {
                player.addTag("nice");
                PlayerUtils.sendTitle(player,
                    Component.literal("The Nice List").withStyle(ChatFormatting.GREEN),10, 50, 20 );
					PlayerUtils.playSoundToPlayers(List.of(lists),SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_nicelist_start")));
            } else {
                player.addTag("naughty");
                PlayerUtils.sendTitle(player,
                    Component.literal("The Naughty List").withStyle(ChatFormatting.RED),10, 50, 20);
					PlayerUtils.playSoundToPlayers(List.of(lists),SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_naughtylist")));
            }

            Lists entry = addLists(player);
            messageLists(entry, player);
        }

        SessionTranscript.listsChosen(listPlayers);
    }

    public boolean isOnLists(ServerPlayer player) {
        return lists.stream().anyMatch(l -> l.uuid.equals(player.getUUID()));
    }

    public Lists addLists(ServerPlayer player) {
        rolledPlayers.add(player.getUUID());
        Lists entry = new Lists(player);
        lists.add(entry);
        listsListChanged = true;

        DatapackIntegration.EVENT_LISTS_ADDED.trigger(
            new DatapackIntegration.Events.MacroEntry("Player", player.getScoreboardName())
        );
        return entry;
    }

    public void resetLists() {
        for (Lists l : lists) {
            ServerPlayer p = PlayerUtils.getPlayer(l.uuid);
            if (p != null) {
                p.removeTag("nice");
                p.removeTag("naughty");
                p.sendSystemMessage(Component.literal("§c[NOTICE] You are no longer on the naughty/nice list."));
            }
        }
        lists.clear();
        rolledPlayers.clear();
        listsChosen = false;
    }

	public void onReload() {
		LISTS_ENABLED = seasonConfig.LISTS.get(seasonConfig);
		if (!LISTS_ENABLED) {
			resetLists();
		}

		LISTS_AMOUNT_MIN = seasonConfig.LISTS_MIN_AMOUNT.get(seasonConfig);
		LISTS_AMOUNT_MAX = seasonConfig.LISTS_MAX_AMOUNT.get(seasonConfig);
		LISTS_CHOOSE_MINUTE = seasonConfig.LISTS_CHOOSE_MINUTE.get(seasonConfig);

		LISTS_IGNORE.clear();
		LISTS_FORCE.clear();

		for (String s : seasonConfig.LISTS_IGNORE.get(seasonConfig)
				.replaceAll("\\[","")
				.replaceAll("]","")
				.replaceAll(" ","")
				.trim()
				.split(",")) {
			if (!s.isEmpty()) LISTS_IGNORE.add(s.toLowerCase(Locale.ROOT));
		}

		for (String s : seasonConfig.LISTS_FORCE.get(seasonConfig)
				.replaceAll("\\[","")
				.replaceAll("]","")
				.replaceAll(" ","")
				.trim()
				.split(",")) {
			if (!s.isEmpty()) LISTS_FORCE.add(s.toLowerCase(Locale.ROOT));
		}
	}

    public void tick() {
        if (!LISTS_ENABLED) return;
        for (Lists l : lists) l.tick();
        listsListChanged = false;
    }

    public enum ListsRollType {
        NORMAL
    }
	
	private void messageLists(Lists lists, ServerPlayer player) {
    // TODO: implement later
	}
}
