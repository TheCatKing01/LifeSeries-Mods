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
    public int NAUGHTY_LIST_PLAYERS = 1;
    public int NICE_LIST_PLAYERS = 1;
    public double LISTS_ROLL_TIME = 10;

    public List<String> NAUGHTY_LIST_IGNORE = new ArrayList<>();
    public List<String> NAUGHTY_LIST_FORCE = new ArrayList<>();
    public List<String> NICE_LIST_IGNORE = new ArrayList<>();
    public List<String> NICE_LIST_FORCE = new ArrayList<>();

    private final Random rnd = new Random();

    public List<Lists> lists = new ArrayList<>();
    public List<UUID> rolledPlayers = new ArrayList<>();

    public boolean listsChosen = false;
    public boolean listsListChanged = false;

    public void addSessionActions() {
        if (!LISTS_ENABLED) return;

        currentSession.addSessionAction(
            new SessionAction(Time.minutes(LISTS_ROLL_TIME), "Roll naughty/nice lists") {
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
        if (NAUGHTY_LIST_PLAYERS < 0 || NICE_LIST_PLAYERS < 0) return;

        List<ServerPlayer> candidates = new ArrayList<>(livesManager.getNonRedPlayers());
        candidates.removeIf(p ->
            !allowedPlayers.contains(p) ||
            rolledPlayers.contains(p.getUUID())
        );

        List<ServerPlayer> naughtyList = getRandomListPlayers(candidates, NAUGHTY_LIST_PLAYERS, NAUGHTY_LIST_FORCE, NAUGHTY_LIST_IGNORE);
        List<ServerPlayer> remaining = new ArrayList<>(candidates);
        remaining.removeAll(naughtyList);
        List<ServerPlayer> niceList = getRandomListPlayers(remaining, NICE_LIST_PLAYERS, NICE_LIST_FORCE, NICE_LIST_IGNORE);

        List<ServerPlayer> normalPlayers = new ArrayList<>(allowedPlayers);
        normalPlayers.removeAll(naughtyList);
        normalPlayers.removeAll(niceList);

        handleListsLists(normalPlayers, niceList, naughtyList);
        listsChosen = true;
    }
    public List<ServerPlayer> getRandomListPlayers(List<ServerPlayer> candidates, int desiredCount, List<String> forceList, List<String> ignoreList) {
        List<ServerPlayer> result = new ArrayList<>();
        List<ServerPlayer> filteredCandidates = new ArrayList<>(candidates);
        filteredCandidates.removeIf(p ->
            ignoreList.contains(p.getScoreboardName().toLowerCase(Locale.ROOT))
        );

        Collections.shuffle(filteredCandidates);
        int amount = Math.max(desiredCount, 0);

        for (ServerPlayer p : filteredCandidates) {
            if (forceList.contains(p.getScoreboardName().toLowerCase(Locale.ROOT))) {
                result.add(p);
            }
        }
        amount = Math.max(desiredCount - result.size(), 0);

        for (ServerPlayer p : filteredCandidates) {
            if (amount <= 0) break;
            if (result.contains(p)) continue;
            result.add(p);
            amount--;
        }

        return result;
    }

    public void handleListsLists(List<ServerPlayer> normalPlayers, List<ServerPlayer> niceListPlayers, List<ServerPlayer> naughtyListPlayers) {

        PlayerUtils.sendTitleToPlayers(
            normalPlayers,
            Component.literal("No List").withStyle(ChatFormatting.YELLOW),
            10, 50, 20
        );
		PlayerUtils.playSoundToPlayers(
            normalPlayers,
            SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("lastlife_boogeyman_no"))
        );

        Collections.shuffle(niceListPlayers, rnd);
        Collections.shuffle(naughtyListPlayers, rnd);

        for (ServerPlayer player : niceListPlayers) {
            player.removeTag("nice");
            player.removeTag("naughty");
            player.addTag("nice");
            PlayerUtils.sendTitle(player,
                Component.literal("The Nice List").withStyle(ChatFormatting.GREEN),10, 50, 20 );
            PlayerUtils.playSoundToPlayers(
                List.of(player),
                SoundEvent.createVariableRangeEvent(
                    IdentifierHelper.vanilla("nicelife_nicelist_start")
                )
            );
            Lists entry = addLists(player, Lists.ListType.NICE);
            messageLists(entry, player);
            livesManager.applyCorrectTeam(player);
        }

        for (ServerPlayer player : naughtyListPlayers) {
            player.removeTag("nice");
            player.removeTag("naughty");
            player.addTag("naughty");
            PlayerUtils.sendTitle(player,
                Component.literal("The Naughty List").withStyle(ChatFormatting.RED),10, 50, 20);
            PlayerUtils.playSoundToPlayers(
                List.of(player),
                SoundEvent.createVariableRangeEvent(
                    IdentifierHelper.vanilla("nicelife_naughtylist")
                )
            );
            Lists entry = addLists(player, Lists.ListType.NAUGHTY);
            messageLists(entry, player);
            livesManager.applyCorrectTeam(player);
        }

        for (ServerPlayer player : normalPlayers) {
            livesManager.applyCorrectTeam(player);
        }

        List<ServerPlayer> listPlayers = new ArrayList<>();
        listPlayers.addAll(niceListPlayers);
        listPlayers.addAll(naughtyListPlayers);
        SessionTranscript.listsChosen(listPlayers);
        PlayerUtils.updatePlayerLists();
		
    }
    public boolean isOnLists(ServerPlayer player) {
        return lists.stream().anyMatch(l -> l.uuid.equals(player.getUUID()));
    }

    public boolean isNaughtyListMember(ServerPlayer player) {
        Lists entry = getListEntry(player);
        return entry != null && entry.listType == Lists.ListType.NAUGHTY;
    }

    public Lists addLists(ServerPlayer player, Lists.ListType listType) {
        rolledPlayers.add(player.getUUID());
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
                livesManager.applyCorrectTeam(player);
            }
        }
        lists.clear();
        rolledPlayers.clear();
        listsChosen = false;
        PlayerUtils.updatePlayerLists();
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
            if (!TagUtils.hasTag(player, "naughty")) {
                player.addTag("naughty");
            }
            livesManager.applyCorrectTeam(player);
        }
        PlayerUtils.updatePlayerLists();
    }

    public void cureNaughtyList(Collection<ServerPlayer> targets) {
        for (ServerPlayer player : targets) {
            Lists entry = getListEntry(player);
            if (entry == null || entry.listType != Lists.ListType.NAUGHTY) {
                continue;
            }
            entry.cured = true;
            if (TagUtils.hasTag(player, "naughty")) {
                player.removeTag("naughty");
            }
            livesManager.applyCorrectTeam(player);
        }
        PlayerUtils.updatePlayerLists();
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
        PlayerUtils.updatePlayerLists();
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
        PlayerUtils.updatePlayerLists();
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
        if (TagUtils.hasTag(player, "nice")) {
            player.removeTag("nice");
            removed = true;
        }
        if (TagUtils.hasTag(player, "naughty")) {
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
        NAUGHTY_LIST_PLAYERS = seasonConfig.NAUGHTY_LIST_PLAYERS.get(seasonConfig);
		NICE_LIST_PLAYERS = seasonConfig.NICE_LIST_PLAYERS.get(seasonConfig);
		LISTS_ROLL_TIME = seasonConfig.LISTS_ROLL_TIME.get(seasonConfig);

		NAUGHTY_LIST_IGNORE.clear();
		NAUGHTY_LIST_FORCE.clear();
		NICE_LIST_IGNORE.clear();
		NICE_LIST_FORCE.clear();

		for (String s : seasonConfig.NAUGHTY_LIST_IGNORE.get(seasonConfig)
				.replaceAll("\\[","")
				.replaceAll("]","")
				.replaceAll(" ","")
				.trim()
				.split(",")) {
			if (!s.isEmpty()) NAUGHTY_LIST_IGNORE.add(s.toLowerCase(Locale.ROOT));
		}

		for (String s : seasonConfig.NAUGHTY_LIST_FORCE.get(seasonConfig)
				.replaceAll("\\[","")
				.replaceAll("]","")
				.replaceAll(" ","")
				.trim()
				.split(",")) {
			if (!s.isEmpty()) NAUGHTY_LIST_FORCE.add(s.toLowerCase(Locale.ROOT));
		}

		for (String s : seasonConfig.NICE_LIST_IGNORE.get(seasonConfig)
				.replaceAll("\\[","")
				.replaceAll("]","")
				.replaceAll(" ","")
				.trim()
				.split(",")) {
			if (!s.isEmpty()) NICE_LIST_IGNORE.add(s.toLowerCase(Locale.ROOT));
		}

		for (String s : seasonConfig.NICE_LIST_FORCE.get(seasonConfig)
				.replaceAll("\\[","")
				.replaceAll("]","")
				.replaceAll(" ","")
				.trim()
				.split(",")) {
			if (!s.isEmpty()) NICE_LIST_FORCE.add(s.toLowerCase(Locale.ROOT));
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





















