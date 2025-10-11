package net.mat0u5.lifeseries.seasons.season.aprilfools.reallife;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.season.thirdlife.ThirdLifeConfig;
import net.mat0u5.lifeseries.config.ConfigFileEntry;

import java.util.List;

public class RealLifeConfig extends ThirdLifeConfig {
    public RealLifeConfig() {
        super("./config/"+ Main.MOD_ID+"/aprilfools","reallife.properties");
    }
    @Override
    protected List<ConfigFileEntry<?>> getDefaultConfigEntries() {
        List<ConfigFileEntry<?>> defaultEntries = super.getDefaultConfigEntries();
        defaultEntries.remove(RANDOM_LIVES);
        defaultEntries.remove(RANDOM_LIVES_MIN);
        defaultEntries.remove(RANDOM_LIVES_MAX);
        return defaultEntries;
    }
}