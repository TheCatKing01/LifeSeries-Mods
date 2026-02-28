import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.season.limitedlife.LimitedLife;

public class LimitedLastLife extends LimitedLife {

    @Override
    public Seasons getSeason() {
        return Seasons.LIMITED_LAST_LIFE;
    }

    @Override
    public ConfigManager createConfig() {
        return new LimitedLastLifeConfig();
    }
}