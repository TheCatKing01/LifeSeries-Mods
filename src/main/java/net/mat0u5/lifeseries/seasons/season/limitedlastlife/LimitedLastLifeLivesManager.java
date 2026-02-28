package net.mat0u5.lifeseries.seasons.season.limitedlastlife;

import net.mat0u5.lifeseries.seasons.season.limitedlife.LimitedLifeLivesManager;
import net.mat0u5.lifeseries.utils.other.Time;
import net.mat0u5.lifeseries.utils.player.TeamUtils;
import net.minecraft.ChatFormatting;

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
        TeamUtils.createTeam("lives_1", "Dark Red", ChatFormatting.DARK_RED);
        TeamUtils.createTeam("lives_2", "Red", ChatFormatting.RED);
        TeamUtils.createTeam("lives_3", "Yellow", ChatFormatting.YELLOW);
        TeamUtils.createTeam("lives_4", "Green", ChatFormatting.GREEN);
        TeamUtils.createTeam("lives_5", "Dark Green", ChatFormatting.DARK_GREEN);
        TeamUtils.createTeam("lives_6", "Blue", ChatFormatting.BLUE);
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
}