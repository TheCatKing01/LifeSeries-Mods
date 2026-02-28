package net.mat0u5.lifeseries.seasons.season.limitedlastlife;

import net.mat0u5.lifeseries.seasons.season.limitedlife.LimitedLifeConfig;
import net.mat0u5.lifeseries.utils.other.Time;

public class LimitedLastLifeConfig extends LimitedLifeConfig {

    public LimitedLastLifeConfig() {
        super("limitedlastlife.properties");
    }

    @Override
    protected void applyLimitedLifeDefaults() {
        super.applyLimitedLifeDefaults();
        LIVES_RANDOMIZE.defaultValue = true;
        LIVES_RANDOMIZE_MIN.defaultValue = Time.hours(12).getSeconds();
        LIVES_RANDOMIZE_MAX.defaultValue = Time.hours(36).getSeconds();
        EXTRA_LIFE_COLORS.defaultValue = "0-3600:dark_red;115200-2147483647:blue";
    }
}