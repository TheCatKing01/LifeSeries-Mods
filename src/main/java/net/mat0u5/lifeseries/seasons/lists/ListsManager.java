package net.mat0u5.lifeseries.seasons.lists;

import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.network.NetworkHandlerServer;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.*;

import static net.mat0u5.lifeseries.Main.*;

public class ListsManager {

    public boolean LISTS_ENABLED = false;
    public int NAUGHTY_LIST_PLAYERS = 1;
    public int NICE_LIST_PLAYERS = 1;
    public double LISTS_ROLL_TIME = 10;
    public double LISTS_DURATION = 10;
    public boolean LISTS_GLOW = true;

    private static final Time LISTS_GLOW_TIME_INTERVAL = Time.seconds(60);
    private static final Time LISTS_GLOW_TIME = Time.seconds(10);

    public List<String> NAUGHTY_LIST_IGNORE = new ArrayList<>();
    public List<String> NAUGHTY_LIST_FORCE = new ArrayList<>();
    public List<String> NICE_LIST_IGNORE = new ArrayList<>();
    public List<String> NICE_LIST_FORCE = new ArrayList<>();

    private final Random rnd = new Random();
    private int listsCycleId = 0;
    private Time listsGlowTimePassed = Time.zero();

    public List<Lists> lists = new ArrayList<>();
    public List<UUID> rolledPlayers = new ArrayList<>();
    public Map<UUID, UUID> votesByPerson = new HashMap<>();

    public boolean listsChosen = false;
    public boolean listsListChanged = false;
    public boolean listsVoteActive = false;

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
        prepareToChooseLists(ListsRollType.NORMAL);
    }

    public void prepareToChooseLists(ListsRollType rollType) {
        if (!LISTS_ENABLED) return;
        resetLists();
        chooseLists(livesManager.getAlivePlayers(), rollType);
    }

    public void chooseLists(List<ServerPlayer> allowedPlayers, ListsRollType rollType) {
        allowedPlayers.removeIf(this::isOnLists);
        if (allowedPlayers.isEmpty()) return;

        showRolling(allowedPlayers);
        TaskScheduler.scheduleTask(Time.seconds(9),
            () -> listsChooseRandom(allowedPlayers, rollType));
    }
	
	public void showRolling(List<ServerPlayer> players) {
		PlayerUtils.playSoundToPlayers(
			players,
			SoundEvent.createVariableRangeEvent(
				IdentifierHelper.vanilla("nicelife_nicelist_countdown_3")
			)
		);
		PlayerUtils.sendTitleToPlayers(
			players,
			Component.literal("3").withStyle(ChatFormatting.DARK_GREEN),
			0, 25, 0
		);

		TaskScheduler.scheduleTask(25, () -> {
			PlayerUtils.playSoundToPlayers(
				players,
				SoundEvent.createVariableRangeEvent(
					IdentifierHelper.vanilla("nicelife_nicelist_countdown_2")
				)
			);
			PlayerUtils.sendTitleToPlayers(
				players,
				Component.literal("2").withStyle(ChatFormatting.YELLOW),
				0, 25, 0
			);
		});

		TaskScheduler.scheduleTask(50, () -> {
			PlayerUtils.playSoundToPlayers(
				players,
				SoundEvent.createVariableRangeEvent(
					IdentifierHelper.vanilla("nicelife_nicelist_countdown_1")
				)
			);
			PlayerUtils.sendTitleToPlayers(
				players,
				Component.literal("1").withStyle(ChatFormatting.RED),
				0, 25, 0
			);
		});

		TaskScheduler.scheduleTask(75, () -> {
			PlayerUtils.playSoundToPlayers(
				players,
				SoundEvent.createVariableRangeEvent(
					IdentifierHelper.vanilla("nicelife_vote_result")
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

        List<ServerPlayer> candidatesAll = new ArrayList<>(allowedPlayers);
        candidatesAll.removeIf(p -> rolledPlayers.contains(p.getUUID()));

        List<ServerPlayer> candidatesNonRed = new ArrayList<>(livesManager.getNonRedPlayers());
        candidatesNonRed.removeIf(p ->
            !allowedPlayers.contains(p) ||
            rolledPlayers.contains(p.getUUID())
        );

        List<ServerPlayer> remainingAll = new ArrayList<>(candidatesAll);
        List<ServerPlayer> naughtyList = new ArrayList<>();
        List<ServerPlayer> niceList = new ArrayList<>();

        if (rollType == ListsRollType.NAUGHTY_ONLY) {
            naughtyList = getRandomListPlayers(candidatesNonRed, NAUGHTY_LIST_PLAYERS, NAUGHTY_LIST_FORCE, NAUGHTY_LIST_IGNORE);
            remainingAll.removeAll(naughtyList);
        }
        else if (rollType == ListsRollType.NICE_ONLY) {
            niceList = getRandomListPlayers(remainingAll, NICE_LIST_PLAYERS, NICE_LIST_FORCE, NICE_LIST_IGNORE);
            remainingAll.removeAll(niceList);
        }
        else {
            naughtyList = getRandomListPlayers(candidatesNonRed, NAUGHTY_LIST_PLAYERS, NAUGHTY_LIST_FORCE, NAUGHTY_LIST_IGNORE);
            remainingAll.removeAll(naughtyList);
            niceList = getRandomListPlayers(remainingAll, NICE_LIST_PLAYERS, NICE_LIST_FORCE, NICE_LIST_IGNORE);
            remainingAll.removeAll(niceList);
        }

        List<ServerPlayer> normalPlayers = new ArrayList<>(allowedPlayers);
        normalPlayers.removeAll(naughtyList);
        normalPlayers.removeAll(niceList);

        handleListsLists(normalPlayers, niceList, naughtyList);
        listsChosen = true;
        startListsVoteCountdown();
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

    public boolean isNiceListMember(ServerPlayer player) {
        Lists entry = getListEntry(player);
        return entry != null && entry.listType == Lists.ListType.NICE;
    }

    private boolean isNiceLifeSeason() {
        return currentSeason != null && currentSeason.getSeason() == Seasons.NICE_LIFE;
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
        votesByPerson.clear();
        listsVoteActive = false;
        listsCycleId++;
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
        LISTS_DURATION = seasonConfig.LISTS_DURATION.get(seasonConfig);
        LISTS_GLOW = seasonConfig.LISTS_GLOW.get(seasonConfig);

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

        listsGlowTimePassed.tick();
        if (LISTS_GLOW && listsChosen && listsGlowTimePassed.isMultipleOf(LISTS_GLOW_TIME_INTERVAL)) {
            MobEffectInstance glowing = new MobEffectInstance(MobEffects.GLOWING, LISTS_GLOW_TIME.getTicks(), 0);
            for (Lists entry : lists) {
                if (entry.listType != Lists.ListType.NAUGHTY) continue;
                ServerPlayer player = entry.getPlayer();
                if (player != null && !player.ls$isDead()) {
                    player.addEffect(glowing);
                }
            }
        }
    }

    public enum ListsRollType {
        NORMAL,
        NICE_ONLY,
        NAUGHTY_ONLY
    }
	
	private void messageLists(Lists lists, ServerPlayer player) {
		if (lists == null || player == null) return;

		ModifiableText message;

		String minutesText = (LISTS_DURATION % 1 == 0)
			? String.valueOf((int) LISTS_DURATION)
			: String.valueOf(LISTS_DURATION);

		if (lists.listType == Lists.ListType.NICE) {
			Component clickableVote = TextUtils.clickableText("§f§l/vote", TextUtils.runCommandClickEvent("/vote"));
			message = ModifiableText.LISTS_NICELIST_START_INFO;
			player.ls$message(message.get(minutesText, clickableVote));
		} else {
			message = ModifiableText.LISTS_NAUGHTYLIST_START_INFO;
			player.ls$message(message.get(minutesText));
		}
	}
	
    public void endListsNow() {
        if (!LISTS_ENABLED) return;
        if (!listsChosen) {
            resetLists();
            return;
        }
        if (listsVoteActive) {
            int runId = ++listsCycleId;
            endListsVote(runId);
            return;
        }
        resetLists();
    }
    public boolean isListsVoteActive() {
        return LISTS_ENABLED && listsChosen && listsVoteActive;
    }

    public boolean openListsLifeVote(ServerPlayer player) {
        List<String> availableForVoting = new ArrayList<>();
        for (ServerPlayer availableVotePlayer : livesManager.getAlivePlayers()) {
            if (isOnLists(availableVotePlayer)) continue;
            availableForVoting.add(availableVotePlayer.getScoreboardName());
        }
        if (availableForVoting.isEmpty()) {
            return false;
        }

        ModifiableText voteTitle = isNiceLifeSeason()
            ? ModifiableText.NICELIFE_NICELIST_VOTE_TITLE
            : ModifiableText.LISTS_NICELIST_VOTE_TITLE;

        NetworkHandlerServer.sendVoteScreenPacket(
            player,
            voteTitle.getString(),
            false,
            true,
            false,
            availableForVoting
        );
        return true;
    }

    public void handleVote(ServerPlayer player, String vote) {
        if (!isListsVoteActive()) return;
        if (player == null) return;
        if (player.ls$isDead()) return;
        if (!isNiceListMember(player)) return;
        if (vote == null || vote.isEmpty()) return;
        ServerPlayer votedFor = PlayerUtils.getPlayer(vote);
        if (votedFor == null) return;
        if (votedFor.ls$isDead()) return;
        if (isOnLists(votedFor)) return;

        PlayerUtils.playSoundToPlayer(player, SoundEvents.NOTE_BLOCK_BELL.value(), 1f, 1);
        ModifiableText voteText = isNiceLifeSeason()
            ? ModifiableText.NICELIFE_NICELIST_VOTE
            : ModifiableText.LISTS_NICELIST_VOTE;
        player.ls$message(voteText.get(PlayerUtils.getPlayerNameWithIcon(votedFor)));
        votesByPerson.put(player.getUUID(), votedFor.getUUID());
    }

    private void startListsVoteCountdown() {
        if (!LISTS_ENABLED || !listsChosen) return;

        List<ServerPlayer> niceListPlayers = new ArrayList<>();
        for (Lists entry : lists) {
            if (entry.listType != Lists.ListType.NICE) continue;
            ServerPlayer player = entry.getPlayer();
            if (player != null && !player.ls$isDead()) {
                niceListPlayers.add(player);
            }
        }

        listsVoteActive = !niceListPlayers.isEmpty();
        votesByPerson.clear();
        int runId = ++listsCycleId;

        if (LISTS_DURATION <= 0) {
            if (listsVoteActive) {
                endListsVote(runId);
            }
            else {
                resetLists();
            }
            return;
        }

        if (!listsVoteActive) {
            TaskScheduler.scheduleTask(Time.minutes(LISTS_DURATION), () -> {
                if (runId != listsCycleId) return;
                resetLists();
            });
            return;
        }

        if (listsVoteActive && LISTS_DURATION > 1) {
            TaskScheduler.scheduleTask(Time.minutes(LISTS_DURATION - 1), () -> {
                if (runId != listsCycleId) return;
                warnNiceListMembers(niceListPlayers);
            });
        }

        TaskScheduler.scheduleTask(Time.minutes(LISTS_DURATION), () -> {
            if (runId != listsCycleId) return;
            endListsVote(runId);
        });
    }

    private void warnNiceListMembers(List<ServerPlayer> niceListPlayers) {
        if (niceListPlayers.isEmpty()) return;

        ModifiableText reminderText = isNiceLifeSeason()
            ? ModifiableText.NICELIFE_NICELIST_VOTE_REMINDER
            : ModifiableText.LISTS_NICELIST_VOTE_REMINDER;
        Component message = reminderText.get(
            TextUtils.clickableText("§f§l/vote", TextUtils.runCommandClickEvent("/vote"))
        );
        PlayerUtils.playSoundToPlayers(niceListPlayers, SoundEvents.NOTE_BLOCK_BELL.value(), 1f, 1);
        PlayerUtils.broadcastMessage(niceListPlayers, message);
        TaskScheduler.scheduleTask(Time.seconds(30), () -> {
            PlayerUtils.playSoundToPlayers(niceListPlayers, SoundEvents.NOTE_BLOCK_BELL.value(), 1f, 1);
            PlayerUtils.broadcastMessage(niceListPlayers, message);
        });
    }

    private void endListsVote(int runId) {
        if (!isListsVoteActive() || runId != listsCycleId) return;

        SoundEvent voteSound = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_nicelist_end"));
        PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), voteSound, 1f, 1);
        PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), ModifiableText.LISTS_VOTE_END_TITLE.get(), 15, 80, 20);

        ModifiableText countdown3 = isNiceLifeSeason()
            ? ModifiableText.NICELIFE_VOTE_COUNTDOWN_3
            : ModifiableText.LISTS_VOTE_COUNTDOWN_3;
        ModifiableText countdown2 = isNiceLifeSeason()
            ? ModifiableText.NICELIFE_VOTE_COUNTDOWN_2
            : ModifiableText.LISTS_VOTE_COUNTDOWN_2;
        ModifiableText countdown1 = isNiceLifeSeason()
            ? ModifiableText.NICELIFE_VOTE_COUNTDOWN_1
            : ModifiableText.LISTS_VOTE_COUNTDOWN_1;

        int delay = 95;
        TaskScheduler.scheduleTask(delay, () -> {
            if (runId != listsCycleId) return;
            SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_nicelist_countdown_3"));
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 1f, 1);
            PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), countdown3.get(), 15, 25, 15);
        });
        delay += 25;
        TaskScheduler.scheduleTask(delay, () -> {
            if (runId != listsCycleId) return;
            SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_nicelist_countdown_2"));
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 1f, 1);
            PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), countdown2.get(), 15, 25, 15);
        });
        delay += 25;
        TaskScheduler.scheduleTask(delay, () -> {
            if (runId != listsCycleId) return;
            SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_nicelist_countdown_1"));
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 1f, 1);
            PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), countdown1.get(), 15, 25, 15);
        });
        delay += 55;
        TaskScheduler.scheduleTask(delay, () -> {
            if (runId != listsCycleId) return;
            actuallyEndListsVote();
        });
    }

    private void actuallyEndListsVote() {
        ModifiableText insufficientText = isNiceLifeSeason()
            ? ModifiableText.NICELIFE_NICELIST_VOTE_ERROR_INSUFFICIENT
            : ModifiableText.LISTS_NICELIST_VOTE_ERROR_INSUFFICIENT;
        ModifiableText agreementText = isNiceLifeSeason()
            ? ModifiableText.NICELIFE_NICELIST_VOTE_ERROR_AGREEMENT
            : ModifiableText.LISTS_NICELIST_VOTE_ERROR_AGREEMENT;
        ModifiableText voteResultText = isNiceLifeSeason()
            ? ModifiableText.NICELIFE_NICELIST_VOTE_RESULT
            : ModifiableText.LISTS_VOTE_RESULT;
        Map<UUID, Integer> reloadedVotesByCount = new HashMap<>();
        int availableVotes = 0;
        int validVotes = 0;

        for (Lists entry : lists) {
            if (entry.listType != Lists.ListType.NICE) continue;
            ServerPlayer player = entry.getPlayer();
            if (player == null) continue;
            if (player.ls$isDead()) continue;
            availableVotes++;
        }

        for (Map.Entry<UUID, UUID> entry : votesByPerson.entrySet()) {
            UUID votedForUUID = entry.getValue();
            ServerPlayer votingPlayer = PlayerUtils.getPlayer(entry.getKey());
            ServerPlayer votedFor = PlayerUtils.getPlayer(votedForUUID);
            if (votingPlayer == null || votedFor == null) continue;
            if (votingPlayer.ls$isDead() || votedFor.ls$isDead()) continue;
            if (!isNiceListMember(votingPlayer) || isOnLists(votedFor)) continue;
            validVotes++;
            if (!reloadedVotesByCount.containsKey(votedForUUID)) {
                reloadedVotesByCount.put(votedForUUID, 0);
            }
            reloadedVotesByCount.put(votedForUUID, reloadedVotesByCount.get(votedForUUID)+1);
        }

        if (reloadedVotesByCount.isEmpty() || availableVotes == 0 || validVotes == 0) {
            SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_naughtylist"));
            PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 1f, 1);
            PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), insufficientText.get(), 15, 80, 20);
        }
        else {
            UUID mostVotedFor = reloadedVotesByCount.keySet().stream().iterator().next();
            int mostVotes = 0;
            for (Map.Entry<UUID, Integer> entry : reloadedVotesByCount.entrySet()) {
                UUID vote = entry.getKey();
                int votes = entry.getValue();
                if (votes > mostVotes) {
                    mostVotedFor = vote;
                    mostVotes = votes;
                }
            }
            double requiredVotes = (double)validVotes/2.0;
            ServerPlayer winner = PlayerUtils.getPlayer(mostVotedFor);

            if (mostVotes > requiredVotes && winner != null) {
                SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_vote_result"));
                PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 1f, 1);
                PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), voteResultText.get(), 15, 80, 20);

                TaskScheduler.scheduleTask(85, () -> {
                    if (winner.ls$isAlive()) {
                        winner.ls$addLife();
                    }
                    currentSeason.reloadPlayerTeam(winner);
                    PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), SoundEvents.FIREWORK_ROCKET_LAUNCH, 1f, 1);
                    PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), PlayerUtils.getPlayerNameWithIcon(winner), 15, 80, 20);
                });
            }
            else {
                SoundEvent sound = SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("nicelife_naughtylist"));
                PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(), sound, 1f, 1);
                if (validVotes > requiredVotes) {
                    PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), agreementText.get(), 15, 80, 20);
                }
                else {
                    PlayerUtils.sendTitleToPlayers(PlayerUtils.getAllPlayers(), insufficientText.get(), 15, 80, 20);
                }
            }
        }

        resetLists();
    }
}

























































