package net.mat0u5.lifeseries.seasons.season.thirdlife;

import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.aprilfools.simplelife.WandingTraders;

public class ThirdLife extends Season {

    private final WandingTraders traders = new WandingTraders();

    @Override
    public Seasons getSeason() {
        return Seasons.THIRD_LIFE;
    }

    @Override
    public ConfigManager createConfig() {
        return new ThirdLifeConfig();
    }

    @Override
    public void tickSessionOn(net.minecraft.server.MinecraftServer server) {
        super.tickSessionOn(server);
        traders.tickSessionOn(server);

    }
	
}