package net.mat0u5.lifeseries.seasons.season.aprilfools.simplelife;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.season.thirdlife.ThirdLifeConfig;

public class SimpleLifeConfig extends ThirdLifeConfig {
    public SimpleLifeConfig() {
        super("./config/"+ Main.MOD_ID+"/aprilfools","simplelife.properties");
    }

    @Override
    public void instantiateProperties() {
        SIMPLE_LIFE.defaultValue = true;
    }
}
