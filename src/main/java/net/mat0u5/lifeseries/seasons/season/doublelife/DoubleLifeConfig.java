package net.mat0u5.lifeseries.seasons.season.doublelife;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.config.ConfigFileEntry;
import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.mat0u5.lifeseries.utils.other.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class DoubleLifeConfig extends ConfigManager {
    public static final List<String> BLACKLISTED_ITEMS = List.of(
            "lectern",
            "bookshelf",
            "enchanting_table",
            //? if >= 1.21
            "mace",
            "end_crystal",
            "leather_helmet",
            "chainmail_helmet",
            "golden_helmet",
            "iron_helmet",
            "diamond_helmet",
            "netherite_helmet",
            "turtle_helmet",
            //? if >= 1.21.9
            /*"copper_helmet",*/
            "elytra"
    );

    public static final List<String> BLACKLISTED_BLOCKS = List.of(
            "lectern",
            "bookshelf"
    );
    public static final List<String> CLAMPED_ENCHANTMENTS = List.of(
            "sharpness",
            "smite",
            "bane_of_arthropods",
            "fire_aspect",
            "knockback",
            //? if <= 1.20.3 {
            /*"sweeping",
            *///?} else {
            "sweeping_edge",
            //?}

            "power",
            "punch",

            "protection",
            "projectile_protection",
            "blast_protection",
            "fire_protection",
            "feather_falling",
            "thorns",

            //? if >= 1.21 {
            "breach",
            "density",
            "wind_burst",
            //?}

            "multishot",
            "piercing",
            "quick_charge"
    );

    public static final ConfigFileEntry<Boolean> ANNOUNCE_SOULMATES = new ConfigFileEntry<>(
            "announce_soulmates", false, "season",
            "Announce Soulmates", "Tells you who your soulmate is instead of it saying 'Your soulmate is ????'"
    );

    public static final ConfigFileEntry<Boolean> SOULBOUND_FOOD = new ConfigFileEntry<>(
            "soulbound_food", false, "season.soulbind",
            "Soulbound Food", "Makes your food bar shared with your soulmate, just like the health bar."
    );
    public static final ConfigFileEntry<Boolean> SOULBOUND_EFFECTS = new ConfigFileEntry<>(
            "soulbound_effects", false, "season.soulbind",
            "Soulbound Effects", "Makes your effects be shared with your soulmate."
    );
    public static final ConfigFileEntry<Boolean> SOULBOUND_INVENTORIES = new ConfigFileEntry<>(
            "soulbound_inventories", false, "season.soulbind",
            "Soulbound Inventories", "Makes your inventory be shared with your soulmate. \nWARNING: There could be some ways of abusing this (duping etc). Use with caution."
    );
    public static final ConfigFileEntry<Boolean> BREAKUP_LAST_PAIR_STANDING = new ConfigFileEntry<>(
            "breakup_last_pair_standing", false, "season",
            "Breakup Last Pair Standing", "Once only two players are left, they will be broken up as soulmates for a final showdown."
    );
    public static final ConfigFileEntry<Boolean> DISABLE_START_TELEPORT = new ConfigFileEntry<>(
            "disable_start_teleport", false, "season",
            "Disable Start Teleport", "Disables the player spreading over the map when the first session starts."
    );
    public static final ConfigFileEntry<Boolean> SOULMATE_LOCATOR_BAR = new ConfigFileEntry<>(
            "soulbound_locator_bar", false, "season",
            "Soulmate Locator Bar", "Makes ONLY your soulmate appear on the locator bar."
    );
    public static final ConfigFileEntry<Boolean> SOULBOUND_BOOGEYMAN = new ConfigFileEntry<>(
            "soulbound_boogeyman", false, "season.soulbind",
            "Soulbound Boogeymen (If enabled)", "Makes you become the Boogeyman if your soulmate is one - curing one will cure the other as well."
    );
    public static final ConfigFileEntry<Boolean> SOULMATES_PVP_ALLOWED = new ConfigFileEntry<>(
            "soulmates_pvp_allowed", true, "season",
            "Soulmates PvP Allowed", "Controls whether soulmates can hit each other."
    );
	
	public static final ConfigFileEntry<Boolean> SOULMATES_SHARE_LIVES = new ConfigFileEntry<>(
			"soulmates_share_lives", true, "season",
            "Soulmates Share Lives", "Controls whether soulmates share the same life count."
    );
	
	public static final ConfigFileEntry<Boolean> SOULMATES_SHARE_ROLL = new ConfigFileEntry<>(
			"soulmates_share_roll", false, "season.lives",
            "Share Lives Rolled", "Controls whether soulmates are rolled the same amount of lives (only works when share lives is false)."
    );
	
	public static final ConfigFileEntry<Boolean> SPLIT_SOULMATES_WHEN_RED = new ConfigFileEntry<>(
            "split_soulmates_when_red", false, "season",
            "Breakup Soulmates When Red", "Controls whether soulmates are broken up when one of them become red"
    );
	
	    public static final ConfigFileEntry<Boolean> RANDOM_LIVES_ENABLED = new ConfigFileEntry<>(
			"random_lives_enabled", false, "{season.lives}",
            "Roll Random Lives", "Controls whether random lives are assigned after the soulmate roll."
    );
    public static final ConfigFileEntry<Integer> RANDOM_LIVES_MIN = new ConfigFileEntry<>(
            "random_lives_min", 2, "season.lives",
            "Random Lives Min", "The minimum lives you can get from the random roll."
    );
    public static final ConfigFileEntry<Integer> RANDOM_LIVES_MAX = new ConfigFileEntry<>(
            "random_lives_max", 6, "season.lives",
            "Random Lives Max", "The maximum lives you can get from the random roll."
    );
	public static final ConfigFileEntry<Boolean> REROLL_SESSION = new ConfigFileEntry<>(
			"reroll_session", false, "season.reroll",
            "Reroll Soulmates Each Session", "Controls whether soulmates are given a new soulbound each session. "
    );
	public static final ConfigFileEntry<Boolean> REROLL_MIDSESSION = new ConfigFileEntry<>(
			"reroll_midsession", false, "{season.reroll.midsession}",
            "Reroll Soulmates Mid-Session", "Controls whether soulmates are given a new soulbound mid-session. "
    );
	public static final ConfigFileEntry<Double> REROLL_TIME = new ConfigFileEntry<>(
			"reroll_time", 30.0, ConfigTypes.MINUTES, "season.reroll.midsession",
            "Reroll Interval", "How often soulmates are rerolled within a session. "
    );
	public static final ConfigFileEntry<Boolean> REROLL_REDS = new ConfigFileEntry<>(
			"reroll_reds", true, "season.reroll",
            "Reroll Red Names", "Controls whether red names can be given a new soulbound. "
    );
	public static final ConfigFileEntry<Boolean> REROLL_UNBOUND = new ConfigFileEntry<>(
			"reroll_unbound", false, "season.reroll",
            "Only Reroll Unbound Players", "Controls if only players without a soulmate are rerolled. "
    );
	public static final ConfigFileEntry<Boolean> REROLL_LIVES = new ConfigFileEntry<>(
			"reroll_lives", false, "season.reroll",
            "Reroll Based On Life Count", "Controls if soulbounds are rerolled by based on life counts. "
    );

    public static final ConfigFileEntry<Object> GROUP_SOULBIND = new ConfigFileEntry<>(
            "group_soulbind", null, ConfigTypes.TEXT, "{season.soulbind}",
            "More Soulbind Options", ""
    );
	public static final ConfigFileEntry<Object> GROUP_REROLL = new ConfigFileEntry<>(
            "group_reroll", null, ConfigTypes.TEXT, "{season.reroll}",
            "Soulmate Rerolling Options", ""
    );

    public DoubleLifeConfig() {
        super("./config/"+ Main.MOD_ID,"doublelife.properties");
    }

    @Override
    protected List<ConfigFileEntry<?>> getSeasonSpecificConfigEntries() {
        List<ConfigFileEntry<?>> result =  new ArrayList<>(List.of(
				GROUP_REROLL //Group
				,GROUP_SOULBIND //Group
				,RANDOM_LIVES_ENABLED
				,SOULMATES_SHARE_LIVES
				,ANNOUNCE_SOULMATES
				,SPLIT_SOULMATES_WHEN_RED
                ,BREAKUP_LAST_PAIR_STANDING
                ,DISABLE_START_TELEPORT
			
				,REROLL_SESSION
				,REROLL_MIDSESSION
				,REROLL_TIME
				,REROLL_LIVES
				.REROLL_UNBOUND
				,REROLL_REDS

                ,SOULBOUND_FOOD
                ,SOULBOUND_EFFECTS
                ,SOULBOUND_INVENTORIES
                , SOULBOUND_BOOGEYMAN
                ,SOULMATES_PVP_ALLOWED
								
				,RANDOM_LIVES_MIN
                ,RANDOM_LIVES_MAX	
				,SOULMATES_SHARE_ROLL					
				
        ));
        //? if >= 1.21.6 {
        /*result.add(SOULMATE_LOCATOR_BAR);
        *///?}
        return result;
    }

    @Override
    public void instantiateProperties() {
        CUSTOM_ENCHANTER_ALGORITHM.defaultValue = true;
        BLACKLIST_ITEMS.defaultValue = TextUtils.formatString("[{}]", BLACKLISTED_ITEMS);
        BLACKLIST_BLOCKS.defaultValue = TextUtils.formatString("[{}]", BLACKLISTED_BLOCKS);
        BLACKLIST_CLAMPED_ENCHANTS.defaultValue = TextUtils.formatString("[{}]", CLAMPED_ENCHANTMENTS);
        super.instantiateProperties();
    }
}
