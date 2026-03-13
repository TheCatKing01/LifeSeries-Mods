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
		resetLists();
        chooseLists(livesManager.getAlivePlayers(), ListsRollType.NORMAL);
    }

    public void chooseLists(List<ServerPlayer> allowedPlayers, ListsRollType rollType) {
        allowedPlayers.removeIf(this::isOnLists);
        if (allowedPlayers.isEmpty()) return;

        showRolling(allowedPlayers);
        TaskScheduler.scheduleTask(Time.seconds(9),
            () -> listsChooseRandom(allowedPlayers, rollType));
    }
	
	public void showRolling(List<ServerPlayer> players) {
		PlayerUtils.playSoundToPlayers(players, SoundEvents.UI_BUTTON_CLICK.value());
		PlayerUtils.sendTitleToPlayers(
			players,
			Component.literal("3").withStyle(ChatFormatting.GREEN),
			0, 35, 0
		);

		TaskScheduler.scheduleTask(30, () -> {
			PlayerUtils.playSoundToPlayers(players, SoundEvents.UI_BUTTON_CLICK.value());
			PlayerUtils.sendTitleToPlayers(
				players,
				Component.literal("2").withStyle(ChatFormatting.YELLOW),
				0, 35, 0
			);
		});

		TaskScheduler.scheduleTask(60, () -> {
			PlayerUtils.playSoundToPlayers(players, SoundEvents.UI_BUTTON_CLICK.value());
			PlayerUtils.sendTitleToPlayers(
				players,
				Component.literal("1").withStyle(ChatFormatting.RED),
				0, 35, 0
			);
		});

		TaskScheduler.scheduleTask(90, () -> {
			PlayerUtils.playSoundToPlayers(
				players,
				SoundEvent.createVariableRangeEvent(
					IdentifierHelper.vanilla("lastlife_boogeyman_wait")
				)
			);
			PlayerUtils.sendTitleToPlayers(
				players,
				Component.literal("You are on...").withStyle(ChatFormatting.YELLOW),
				10, 50, 20
			);
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
		PlayerUtils.playSoundToPlayers(
            normalPlayers,
            SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("lastlife_boogeyman_no"))
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
					PlayerUtils.playSoundToPlayers(
						List.of(player),
						SoundEvent.createVariableRangeEvent(
							IdentifierHelper.vanilla("nicelife_nicelist_start")
						)
					);
            } else {
                player.addTag("naughty");
                PlayerUtils.sendTitle(player,
                    Component.literal("The Naughty List").withStyle(ChatFormatting.RED),10, 50, 20);
					PlayerUtils.playSoundToPlayers(
						List.of(player),
						SoundEvent.createVariableRangeEvent(
							IdentifierHelper.vanilla("nicelife_naughtylist")
						)
					);
            }

            Lists.ListType listType = i % 2 == 0 ? Lists.ListType.NICE : Lists.ListType.NAUGHTY;
            Lists entry = addLists(player, listType);
            messageLists(entry, player);
        }

        SessionTranscript.listsChosen(listPlayers);
		
    }

    public boolean isOnLists(ServerPlayer player) {
        return lists.stream().anyMatch(l -> l.uuid.equals(player.getUUID()));
    }

    public boolean isNaughtyListMember(ServerPlayer player) {
        Lists entry = getListEntry(player);
        return entry != null && entry.listType == Lists.ListType.NAUGHTY;
    }

    public Lists addLists(ServerPlayer player, Lists.ListType listType) {        rolledPlayers.add(player.getUUID());
        Lists entry = new Lists(player, listType);
        lists.add(entry);
        listsListChanged = true;

        DatapackIntegration.EVENT_LISTS_ADDED.trigger(
            new DatapackIntegration.Events.MacroEntry("Player", player.getScoreboardName())
        );
        return entry;
    }

    public void resetLists() {
        if (server != null) {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                clearListTags(player, true);
            }
        }
        lists.clear();
        rolledPlayers.clear();
        listsChosen = false;
    }
	
    public void onPlayerJoin(ServerPlayer player) {
        if (!isListsActive()) {
            removePlayerFromLists(player, true);
        }

    }

    public boolean isListsActive() {
        return LISTS_ENABLED && listsChosen;
    }

    public void resetNaughtyStatus(Collection<ServerPlayer> targets) {
        for (ServerPlayer player : targets) {
            Lists entry = getListEntry(player);
            if (entry == null || entry.listType != Lists.ListType.NAUGHTY) {
                continue;
            }
            entry.cured = false;
            if (!EntityTagUtils.hasTag(player, "naughty")) {
                player.addTag("naughty");
            }
            livesManager.applyCorrectTeam(player);
        }
    }

    public void cureNaughtyList(Collection<ServerPlayer> targets) {
        for (ServerPlayer player : targets) {
            Lists entry = getListEntry(player);
            if (entry == null || entry.listType != Lists.ListType.NAUGHTY) {
                continue;
            }
            entry.cured = true;
            if (EntityTagUtils.hasTag(player, "naughty")) {
                player.removeTag("naughty");
            }
            livesManager.applyCorrectTeam(player);
        }
    }

    private Lists getListEntry(ServerPlayer player) {
        for (Lists entry : lists) {
            if (entry.uuid.equals(player.getUUID())) {
                return entry;
            }
        }
        return null;
    }

    public Optional<Lists.ListType> removePlayerFromLists(ServerPlayer player, boolean notify) {
        Lists entry = getListEntry(player);
        lists.removeIf(listEntry -> listEntry.uuid.equals(player.getUUID()));
        rolledPlayers.remove(player.getUUID());
        clearListTags(player, notify);
        livesManager.applyCorrectTeam(player);
        if (entry == null) {
            return Optional.empty();
        }
        return Optional.of(entry.listType);
    }

    public void addPlayerToList(ServerPlayer player, Lists.ListType listType, boolean notify) {
        removePlayerFromLists(player, false);

        if (listType == Lists.ListType.NAUGHTY) {
            player.addTag("naughty");
            player.removeTag("nice");
        }
        else {
            player.addTag("nice");
            player.removeTag("naughty");
        }

        addLists(player, listType);
        livesManager.applyCorrectTeam(player);

        if (notify) {
            String listName = listType == Lists.ListType.NAUGHTY ? "naughty" : "nice";
            player.sendSystemMessage(Component.literal("§6[NOTICE] You were added to the " + listName + " list."));
        }
    }

    public Optional<Lists.ListType> getPlayerListType(ServerPlayer player) {
        Lists entry = getListEntry(player);
        if (entry == null) {
            return Optional.empty();
        }
        return Optional.of(entry.listType);
    }

    private void clearListTags(ServerPlayer player, boolean notify) {
        boolean removed = false;
        if (EntityTagUtils.hasTag(player, "nice")) {
            player.removeTag("nice");
            removed = true;
        }
        if (EntityTagUtils.hasTag(player, "naughty")) {
            player.removeTag("naughty");
            removed = true;
        }
        if (removed && notify) {
            player.sendSystemMessage(Component.literal("§c[NOTICE] You are no longer on the naughty/nice list."));
        }
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



