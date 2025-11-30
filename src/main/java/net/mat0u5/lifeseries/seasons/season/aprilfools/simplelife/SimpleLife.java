package net.mat0u5.lifeseries.seasons.season.aprilfools.simplelife;

import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.thirdlife.ThirdLife;
import net.mat0u5.lifeseries.seasons.season.aprilfools.wandingtraders.WandingTraders;

public class SimpleLife extends ThirdLife {

    private final WandingTraders traders = new WandingTraders();
    
    @Override
    public Seasons getSeason() {
        return Seasons.SIMPLE_LIFE;
    }

    @Override
    public ConfigManager createConfig() {
        return new SimpleLifeConfig();
    }

    @Override
    public void tickSessionOn(net.minecraft.server.MinecraftServer server) {
        super.tickSessionOn(server);
        traders.tickSessionOn(server);
    }

}