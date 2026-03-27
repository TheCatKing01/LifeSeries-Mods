package net.mat0u5.lifeseries.config;

import net.mat0u5.lifeseries.Main;

import java.util.List;

public class MainConfig extends ConfigManager {
    public MainConfig() {
        super("./config/lifeseries/main", Main.MOD_ID+".properties");
    }

    @Override
    protected List<ConfigFileEntry<?>> getDefaultConfigEntries() {
        return List.of();
    }
    @Override
    public void instantiateProperties() {
        getOrCreateProperty("currentSeries", Main.DEFAULT_SEASON.getId());
        getOrCreateProperty("clientMode", "false");
    }
}
