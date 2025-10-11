package net.mat0u5.lifeseries.seasons.season.lastlife;

import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.mat0u5.lifeseries.Main.currentSession;
import static net.mat0u5.lifeseries.Main.seasonConfig;

public class LastLife extends Season {
    public static final String COMMANDS_ADMIN_TEXT = "/lifeseries, /session, /claimkill, /lives, /givelife, /boogeyman";
    public static final String COMMANDS_TEXT = "/claimkill, /lives, /givelife";

    public static int ROLL_MAX_LIVES = 6;
    public static int ROLL_MIN_LIVES = 2;
    public static int DEFAULT_LIVES = 3;
    public static boolean RANDOM_LIVES_ENABLED = false;

    @Override
    public Seasons getSeason() {
        return Seasons.LAST_LIFE;
    }

    @Override
    public ConfigManager createConfig() {
        return new LastLifeConfig();
    }

    @Override
    public LivesManager createLivesManager() {
        return new LastLifeLivesManager();
    }

    @Override
    public String getAdminCommands() {
        return COMMANDS_ADMIN_TEXT;
    }

    @Override
    public String getNonAdminCommands() {
        return COMMANDS_TEXT;
    }

    @Override
    public void addSessionActions() {
        super.addSessionActions();
        if (!(seasonConfig instanceof LastLifeConfig config)) return;

        // Only add the rolling action when random lives are enabled
        if (config.RANDOM_LIVES.get(config) && livesManager instanceof LastLifeLivesManager lastLifeLivesManager) {
            currentSession.addSessionAction(lastLifeLivesManager.actionChooseLives);
        }
    }

    @Override
    public void reload() {
        super.reload();
        if (!(seasonConfig instanceof LastLifeConfig config)) return;

        // Get whether random lives are enabled
        RANDOM_LIVES_ENABLED = config.RANDOM_LIVES.get(config);

        // Always get the default lives value
        DEFAULT_LIVES = config.DEFAULT_LIVES.get(config);

        if (RANDOM_LIVES_ENABLED) {
            // Load random life range
            int minLivesConfig = config.RANDOM_LIVES_MIN.get(config);
            int maxLivesConfig = config.RANDOM_LIVES_MAX.get(config);
            ROLL_MIN_LIVES = Math.min(minLivesConfig, maxLivesConfig);
            ROLL_MAX_LIVES = Math.max(minLivesConfig, maxLivesConfig);
        } else {
            // Disable rolling — only use default lives
            ROLL_MIN_LIVES = 0;
            ROLL_MAX_LIVES = 0;
        }
    }

    @Override
    public Integer getDefaultLives() {
        // If random lives are off, return the configured default lives
        return RANDOM_LIVES_ENABLED ? null : DEFAULT_LIVES;
    }

    @Override
    public boolean sessionStart() {
        if (livesManager instanceof LastLifeLivesManager lastLifeLivesManager) {
            lastLifeLivesManager.reset();
        }
        return super.sessionStart();
    }

    @Override
    public void sessionEnd() {
        super.sessionEnd();
        if (livesManager instanceof LastLifeLivesManager lastLifeLivesManager) {
            lastLifeLivesManager.reset();
        }
    }

    @Override
    public void onPlayerFinishJoining(ServerPlayerEntity player) {
        super.onPlayerFinishJoining(player);
        if (livesManager instanceof LastLifeLivesManager lastLifeLivesManager) {
            lastLifeLivesManager.onPlayerFinishJoining(player);
        }
    }
}