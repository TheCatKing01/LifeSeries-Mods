package net.mat0u5.lifeseries.seasons.season;

import net.mat0u5.lifeseries.config.DefaultConfigValues;
import net.mat0u5.lifeseries.dependencies.DependencyManager;
import net.mat0u5.lifeseries.seasons.season.aprilfools.reallife.RealLife;
import net.mat0u5.lifeseries.seasons.season.aprilfools.simplelife.SimpleLife;
import net.mat0u5.lifeseries.seasons.season.doublelife.DoubleLife;
import net.mat0u5.lifeseries.seasons.season.lastlife.LastLife;
import net.mat0u5.lifeseries.seasons.season.limitedlife.LimitedLife;
import net.mat0u5.lifeseries.seasons.season.pastlife.PastLife;
import net.mat0u5.lifeseries.seasons.season.secretlife.SecretLife;
import net.mat0u5.lifeseries.seasons.season.thirdlife.ThirdLife;
import net.mat0u5.lifeseries.seasons.season.unassigned.UnassignedSeason;
import net.mat0u5.lifeseries.seasons.season.wildlife.WildLife;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Seasons {

    UNASSIGNED("Unassigned", "unassigned"),

    THIRD_LIFE("Third Life", "thirdlife"),
    LAST_LIFE("Last Life", "lastlife"),
    DOUBLE_LIFE("Double Life", "doublelife"),
    LIMITED_LIFE("Limited Life", "limitedlife"),
    SECRET_LIFE("Secret Life", "secretlife"),
    WILD_LIFE("Wild Life", "wildlife"),
    PAST_LIFE("Past Life", "pastlife"),

    REAL_LIFE("Real Life", "reallife"),
    SIMPLE_LIFE("Simple Life", "simplelife");

    private final String name;
    private final String id;

    // Static config instance for SimpleLife
    private static final DefaultConfigValues CONFIG_VALUES = new DefaultConfigValues();

    Seasons(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    /** Returns a new instance of the Season associated with this enum. */
    public Season getSeasonInstance() {
        return switch (this) {
            case THIRD_LIFE -> new ThirdLife();
            case LAST_LIFE -> new LastLife();
            case DOUBLE_LIFE -> new DoubleLife();
            case LIMITED_LIFE -> new LimitedLife();
            case SECRET_LIFE -> new SecretLife();
            case WILD_LIFE -> DependencyManager.wildLifeModsLoaded() ? new WildLife() : new UnassignedSeason();
            case PAST_LIFE -> new PastLife();
            case REAL_LIFE -> new RealLife();
            case SIMPLE_LIFE -> new SimpleLife(CONFIG_VALUES);
            default -> new UnassignedSeason();
        };
    }

    /** Returns the path to the logo for this season. */
    public Identifier getLogo() {
        return Identifier.of("lifeseries", "textures/gui/" + id + ".png");
    }

    /** Finds a season by its name or ID (case-insensitive). */
    public static Seasons getSeasonFromStringName(String input) {
        return Arrays.stream(values())
                .filter(season -> season.name.equalsIgnoreCase(input) || season.id.equalsIgnoreCase(input))
                .findFirst()
                .orElse(UNASSIGNED);
    }

    /** Returns all seasons except UNASSIGNED. */
    public static List<Seasons> getSeasons() {
        return Arrays.stream(values())
                .filter(season -> season != UNASSIGNED)
                .collect(Collectors.toList());
    }

    /** Returns only the April Fools seasons. */
    public static List<Seasons> getAprilFoolsSeasons() {
        return List.of(REAL_LIFE, SIMPLE_LIFE);
    }

    /** Returns a list of all season IDs except UNASSIGNED. */
    public static List<String> getSeasonIds() {
        return getSeasons().stream()
                .map(Seasons::getId)
                .collect(Collectors.toList());
    }
}