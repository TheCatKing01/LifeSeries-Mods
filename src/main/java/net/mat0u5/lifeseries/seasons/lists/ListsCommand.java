package net.mat0u5.lifeseries.seasons.lists;

import com.mojang.brigadier.CommandDispatcher;
import net.mat0u5.lifeseries.command.manager.Command;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PermissionManager;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static net.mat0u5.lifeseries.Main.currentSeason;

public class ListsCommand extends Command {

    @Override
    public boolean isAllowed() {
        return getBM().LISTS_ENABLED;
    }

    @Override
    public Component getBannedText() {
        return Component.nullToEmpty("This command is only available when the naughty/nice lists are enabled in the Life Series config.");
    }

	private boolean isNaughty(ServerPlayer player) {
		return getBM().isNaughtyListMember(player);
	}

    public List<String> getAdminCommands() {
        return List.of("lists");
    }
	
    public List<String> getNonAdminCommands() {
        return List.of("lists");
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            literal("lists")
                .then(literal("clear")
                    .requires(PermissionManager::isAdmin)
                    .executes(context -> listsClear(
                        context.getSource()
                    ))
                )

                .then(literal("reset")
                        .requires(PermissionManager::isAdmin)
                        .then(argument("player", EntityArgument.players())
                                .executes(context -> resetNaughty(context.getSource(), EntityArgument.getPlayers(context, "player")))
                        )
                )
                .then(literal("cure")
                    .requires(PermissionManager::isAdmin)
                    .then(argument("player", EntityArgument.players())
                        .executes(context -> cureNaughty(context.getSource(), EntityArgument.getPlayers(context, "player")))
                    )
                )
                .then(literal("randomize")
                    .requires(PermissionManager::isAdmin)
                    .executes(context -> listsChooseRandom(
                        context.getSource()
                    ))
                )
        );
    }

    public ListsManager getBM() {
        return currentSeason.listsManager;
    }
	
	public int resetNaughty(CommandSourceStack source, Collection<ServerPlayer> targets) {
		if (checkBanned(source)) return -1;
		ListsManager bm = getBM();
		if (bm == null) return -1;

		if (targets.size() == 1) {
			ServerPlayer target = targets.iterator().next();
			if (!isNaughty(target)) {
				source.sendFailure(Component.nullToEmpty("That player is not on the naughty list"));
				return -1;
			}
		}

		for (ServerPlayer player : targets) {
			if (!isNaughty(player)) {
				source.sendFailure(Component.nullToEmpty(
					player.getName().getString() + " is not on the naughty list"
				));
				return -1;
			}
		}

		if (targets.size() == 1) {
			OtherUtils.sendCommandFeedback(source, TextUtils.format("§7Resetting naughty list cure status for {}§7...", targets.iterator().next()));
		} else {
			OtherUtils.sendCommandFeedback(source, TextUtils.format("§7Resetting naughty list cure status for {} targets§7...", targets.size()));
		}

		bm.resetNaughtyStatus(targets);
		return 1;
	}
	
    public int resetNaughty(CommandSourceStack source, Collection<ServerPlayer> targets) {
        if (checkBanned(source)) return -1;
        ListsManager bm = getBM();
        if (bm == null) return -1;

        if (targets.size() == 1) {
            ServerPlayer target = targets.iterator().next();
            if (!isNaughty(target)) {
                source.sendFailure(Component.nullToEmpty("That player is not on the naughty list"));
                return -1;
            }
        }

        for (ServerPlayer player : targets) {
			if (!isNaughty(player)) {
				source.sendFailure(Component.nullToEmpty(
					player.getName().getString() + " is not on the naughty list"
				));
			return -1;
		}

        if (targets.size() == 1) {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("§7Resetting naughty list cure status for {}§7...", targets.iterator().next()));
        }
        else {
            OtherUtils.sendCommandFeedback(source, TextUtils.format("§7Resetting naughty list cure status for {} targets§7...", targets.size()));
        }
		
		bm.resetNaughtyStatus(targets);

        return 1;
    }
	
	public int cureNaughty(CommandSourceStack source, Collection<ServerPlayer> targets) {
		if (checkBanned(source)) return -1;
		ListsManager bm = getBM();
		if (bm == null) return -1;

		for (ServerPlayer player : targets) {
			if (!isNaughty(player)) {
				source.sendFailure(Component.nullToEmpty(
					player.getName().getString() + " is not on the naughty list"
				));
				return -1;
			}
		}

		bm.cureNaughtyList(targets);
		return 1;
	}

    public int listsClear(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        ListsManager bm = getBM();
        if (bm == null) return -1;

        bm.resetLists();
        OtherUtils.sendCommandFeedback(source, Component.nullToEmpty("All players have been cleared from the naughty/nice lists"));
        return 1;
    }

    public int listsChooseRandom(CommandSourceStack source) {
        if (checkBanned(source)) return -1;
        ListsManager bm = getBM();
        if (bm == null) return -1;

        OtherUtils.sendCommandFeedback(source, Component.nullToEmpty("§7Rolling naughty/nice lists..."));

        bm.resetLists();
        bm.prepareToChooseLists();

        return 1;
    }
	
}