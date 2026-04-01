package net.mat0u5.lifeseries.seasons.season.aprilfools.simplelife;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.config.ConfigFileEntry;
import net.mat0u5.lifeseries.seasons.season.thirdlife.ThirdLifeConfig;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;

import java.util.ArrayList;
import java.util.List;

public class SimpleLifeConfig extends ThirdLifeConfig {
    public SimpleLifeConfig() {
        super("./config/"+ Main.MOD_ID+"/aprilfools","simplelife.properties");
    }

    @Override
    public void instantiateProperties() {
        SIMPLE_LIFE.defaultValue = true;
    }

    @Override
    protected List<ConfigFileEntry<?>> getSeasonSpecificConfigEntries() {
        return new ArrayList<>(List.of(
                new ConfigFileEntry<>(
                        SIMPLE_LIFE.key, SIMPLE_LIFE.defaultValue, ConfigTypes.BOOLEAN, "{season.simplelife}",
                        SIMPLE_LIFE.displayName, SIMPLE_LIFE.description
                ),
                new ConfigFileEntry<>(
                        TRADERS_MAX_AMOUNT.key, TRADERS_MAX_AMOUNT.defaultValue, ConfigTypes.INTEGER, "season.simplelife",
                        TRADERS_MAX_AMOUNT.displayName, TRADERS_MAX_AMOUNT.description
                ),
                new ConfigFileEntry<>(
                        COMPLEX_LIFE_TRADES.key, COMPLEX_LIFE_TRADES.defaultValue, ConfigTypes.BOOLEAN, "season.simplelife",
                        COMPLEX_LIFE_TRADES.displayName, COMPLEX_LIFE_TRADES.description
                )
        ));
    }
}
