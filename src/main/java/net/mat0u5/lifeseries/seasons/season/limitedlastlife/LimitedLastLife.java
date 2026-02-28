package net.mat0u5.lifeseries.seasons.season.limitedlastlife;

import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.limitedlife.LimitedLife;
import net.mat0u5.lifeseries.seasons.season.limitedlife.LimitedLifeConfig;
import net.mat0u5.lifeseries.utils.other.Time;

public class LimitedLastLife extends LimitedLife {

    @Override
    public Seasons getSeason() {
        return Seasons.LIMITED_LAST_LIFE;
    }

    @Override
    public ConfigManager createConfig() {
        return new Config();
    }

    @Override
    public LivesManager createLivesManager() {
        return new LimitedLastLifeLivesManager();
    }

    public static class Config extends LimitedLifeConfig {
        public Config() {
            super("limitedlastlife.properties");
        }

        @Override
        protected void applyLimitedLifeDefaults() {
            super.applyLimitedLifeDefaults();
            LIVES_RANDOMIZE.defaultValue = true;
            LIVES_RANDOMIZE_MIN.defaultValue = Time.hours(12).getSeconds();
            LIVES_RANDOMIZE_MAX.defaultValue = Time.hours(36).getSeconds();
            EXTRA_LIFE_COLORS.defaultValue = "0-3600:dark_red;115201-2147483647:blue";
        }
    }
}