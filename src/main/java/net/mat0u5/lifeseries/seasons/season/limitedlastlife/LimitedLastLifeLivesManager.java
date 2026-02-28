package net.mat0u5.lifeseries.seasons.season.limitedlastlife;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.season.limitedlife.LimitedLifeLivesManager;
import net.mat0u5.lifeseries.utils.other.Time;
import net.mat0u5.lifeseries.utils.player.TeamUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.world.scores.PlayerTeam;

import java.util.Locale;

public class LimitedLastLifeLivesManager extends LimitedLifeLivesManager {
    public static int DARK_RED_TIME = Time.hours(1).getSeconds();
    public static int BLUE_TIME = Time.hours(32).getSeconds() + 1;

    @Override
    public ChatFormatting getColorForLives(Integer lives) {
        lives = getEquivalentLives(lives);
        if (lives == null) return ChatFormatting.GRAY;
        if (lives == 1) return ChatFormatting.DARK_RED;
        if (lives == 2) return ChatFormatting.RED;
        if (lives == 3) return ChatFormatting.YELLOW;
        if (lives == 4) return ChatFormatting.GREEN;
        if (lives == 5) return ChatFormatting.DARK_GREEN;
        if (lives >= 6) return ChatFormatting.BLUE;
        return ChatFormatting.DARK_GRAY;
    }

    @Override
    public Integer getEquivalentLives(Integer limitedLifeLives) {
        if (limitedLifeLives == null) return null;
        if (limitedLifeLives <= 0) return 0;
        if (limitedLifeLives <= DARK_RED_TIME) return 1;
        if (limitedLifeLives <= RED_TIME) return 2;
        if (limitedLifeLives <= YELLOW_TIME) return 3;
        if (limitedLifeLives <= DEFAULT_TIME) return 4;
        if (limitedLifeLives < BLUE_TIME) return 5;
        return 6;
    }

    @Override
    public String getTeamForLives(Integer lives) {
        lives = getEquivalentLives(lives);
        if (lives == null) return "lives_null";
        if (lives <= 0) return "lives_0";
        if (lives >= 6) return "lives_6";
        return "lives_" + lives;
    }

    @Override
    public void createTeams() {
        TeamUtils.createTeam("lives_null", "Unassigned", ChatFormatting.GRAY);
        TeamUtils.createTeam("lives_0", "Dead", ChatFormatting.DARK_GRAY);

        boolean alreadyOverridden = Main.seasonConfig.getOrCreateBoolean("limitedlastlife_teams_overridden", false);
        if (!alreadyOverridden) {
            backupTeam("lives_1");
            backupTeam("lives_2");
            backupTeam("lives_3");
            backupTeam("lives_4");
            backupTeam("lives_5");
            backupTeam("lives_6");
            Main.seasonConfig.setProperty("limitedlastlife_teams_overridden", "true");
        }

        replaceTeam("lives_1", "Dark Red", ChatFormatting.DARK_RED);
        replaceTeam("lives_2", "Red", ChatFormatting.RED);
        replaceTeam("lives_3", "Yellow", ChatFormatting.YELLOW);
        replaceTeam("lives_4", "Green", ChatFormatting.GREEN);
        replaceTeam("lives_5", "Dark Green", ChatFormatting.DARK_GREEN);
        replaceTeam("lives_6", "Blue", ChatFormatting.BLUE);
    }

    @Override
    public int defaultTeamCanKill(String teamName) {
        if (teamName.equals("lives_1")) return 1;
        if (teamName.equals("lives_2")) return YELLOW_TIME;
        return -1;
    }

    @Override
    public int defaultTeamGainLife(String teamName) {
        if (teamName.equals("lives_1")) return 1;
        if (teamName.equals("lives_2")) return YELLOW_TIME;
        return -1;
    }

    public static void restoreTeamsFromBackup() {
        restoreTeam("lives_1", "Red", ChatFormatting.RED);
        restoreTeam("lives_2", "Yellow", ChatFormatting.YELLOW);
        restoreTeam("lives_3", "Green", ChatFormatting.GREEN);
        restoreTeam("lives_4", "Dark Green", ChatFormatting.DARK_GREEN);
        restoreTeam("lives_5", "Blue", ChatFormatting.BLUE);
        restoreTeam("lives_6", "Dark Blue", ChatFormatting.DARK_BLUE);
        Main.seasonConfig.setProperty("limitedlastlife_teams_overridden", "false");
    }

    private static void replaceTeam(String teamName, String displayName, ChatFormatting color) {
        TeamUtils.deleteTeam(teamName);
        TeamUtils.createTeam(teamName, displayName, color);
    }

    private static void backupTeam(String teamName) {
        PlayerTeam team = TeamUtils.getTeam(teamName);
        String keyPrefix = "limitedlastlife_backup_" + teamName;
        boolean exists = team != null;
        Main.seasonConfig.setProperty(keyPrefix + "_exists", String.valueOf(exists));
        if (exists) {
            Main.seasonConfig.setProperty(keyPrefix + "_display", team.getDisplayName().getString());
            ChatFormatting color = team.getColor();
            Main.seasonConfig.setProperty(keyPrefix + "_color", color == null ? "white" : color.getName().toLowerCase(Locale.ROOT));
        }
    }

    private static void restoreTeam(String teamName, String fallbackDisplayName, ChatFormatting fallbackColor) {
        String keyPrefix = "limitedlastlife_backup_" + teamName;
        boolean existed = Main.seasonConfig.getOrCreateBoolean(keyPrefix + "_exists", true);

        TeamUtils.deleteTeam(teamName);
        if (!existed) return;

        String display = Main.seasonConfig.getOrCreateProperty(keyPrefix + "_display", fallbackDisplayName);
        String colorName = Main.seasonConfig.getOrCreateProperty(keyPrefix + "_color", fallbackColor.getName());
        ChatFormatting color = ChatFormatting.getByName(colorName);
        if (color == null) color = fallbackColor;

        TeamUtils.createTeam(teamName, display, color);
    }
}