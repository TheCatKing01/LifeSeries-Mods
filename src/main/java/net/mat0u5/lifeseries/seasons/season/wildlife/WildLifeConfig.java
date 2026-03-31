package net.mat0u5.lifeseries.seasons.season.wildlife;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.config.ConfigFileEntry;
import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia.TriviaQuestionManager;
import net.mat0u5.lifeseries.utils.enums.ConfigTypes;
import net.mat0u5.lifeseries.utils.other.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class WildLifeConfig extends ConfigManager {
    public static final List<String> BLACKLISTED_ITEMS = List.of(
            "lectern",
            "bookshelf",
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
            "copper_helmet",
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

    public static final ConfigFileEntry<Double> WILDCARD_SIZESHIFTING_MIN_SIZE = new ConfigFileEntry<>(
            "wildcard_sizeshifting_min_size", 0.25, "season.sizeshifting",
            "Min Size", "Smallest size you can achieve during Size Shifting."
    );
    public static final ConfigFileEntry<Double> WILDCARD_SIZESHIFTING_MAX_SIZE = new ConfigFileEntry<>(
            "wildcard_sizeshifting_max_size", 3.0, "season.sizeshifting",
            "Max Size", "Biggest size you can achieve during Size Shifting."
    );
    public static final ConfigFileEntry<Double> WILDCARD_SIZESHIFTING_SIZE_CHANGE_MULTIPLIER = new ConfigFileEntry<>(
            "wildcard_sizeshifting_size_change_multiplier", 1.0, "season.sizeshifting",
            "Change Multiplier", "The speed with which you change your size during Size Shifting."
    );
    public static final ConfigFileEntry<Boolean> WILDCARD_SIZESHIFTING_FIX_BUGS = new ConfigFileEntry<>(
            "wildcard_sizeshifting_fix_bugs", true, "season.sizeshifting",
            "Fix Bugs", "Fixes the bug where you fall from blocks when shifting and when you get stuck on blocks when jumping."
    );

    public static final ConfigFileEntry<Integer> WILDCARD_HUNGER_RANDOMIZE_INTERVAL = new ConfigFileEntry<>(
            "wildcard_hunger_randomize_interval_", 1800, ConfigTypes.SECONDS, "season.hunger",
            "Randomize Interval", "The duration between food changes, in seconds."
    );
    public static final ConfigFileEntry<Integer> WILDCARD_HUNGER_EFFECT_LEVEL = new ConfigFileEntry<>(
            "wildcard_hunger_effect_level", 3, "season.hunger",
            "Hunger Effect Level", "Controls the hunger effect level."
    );
    public static final ConfigFileEntry<Double> WILDCARD_HUNGER_NUTRITION_CHANCE = new ConfigFileEntry<>(
            "wildcard_hunger_nutrition_chance", 0.4, ConfigTypes.PERCENTAGE, "season.hunger",
            "Nutrition Chance", "Chance for food to have nutrition (give hunger bars)."
    );
    public static final ConfigFileEntry<Double> WILDCARD_HUNGER_SATURATION_CHANCE = new ConfigFileEntry<>(
            "wildcard_hunger_saturation_chance", 0.5, ConfigTypes.PERCENTAGE, "season.hunger",
            "Saturation Chance", "Chance for food to have saturation (needs to have nutrition to have saturation too btw)."
    );
    public static final ConfigFileEntry<Double> WILDCARD_HUNGER_EFFECT_CHANCE = new ConfigFileEntry<>(
            "wildcard_hunger_effect_chance", 0.65, ConfigTypes.PERCENTAGE, "season.hunger",
            "Effect Chance", "Chance for food to give a random effect."
    );
    public static final ConfigFileEntry<Integer> WILDCARD_HUNGER_AVG_EFFECT_DURATION = new ConfigFileEntry<>(
            "wildcard_hunger_avg_effect_duration", 10, ConfigTypes.SECONDS, "season.hunger",
            "Average Random Effect Duration", "Average random effect duration, in seconds."
    );
    public static final ConfigFileEntry<Double> WILDCARD_HUNGER_SOUND_CHANCE = new ConfigFileEntry<>(
            "wildcard_hunger_sound_chance", 0.01, ConfigTypes.PERCENTAGE, "season.hunger",
            "Play Sound Chance", "Chance for food to play a random sound to everyone on the server."
    );
    public static final ConfigFileEntry<String> WILDCARD_HUNGER_NON_EDIBLE_ITEMS = new ConfigFileEntry<>(
            "wildcard_hunger_non_edible_items", "[]", ConfigTypes.ITEM_LIST, "season.hunger",
            "Non Edible Items", "A list of items that you can't eat."
    );

    public static final ConfigFileEntry<Double> WILDCARD_TIMEDILATION_MIN_SPEED = new ConfigFileEntry<>(
            "wildcard_timedilation_min_speed", 0.05, "season.timedilation",
            "Min World Speed Multiplier", "Controls the minimum speed the WORLD can move."
    );
    public static final ConfigFileEntry<Double> WILDCARD_TIMEDILATION_MAX_SPEED = new ConfigFileEntry<>(
            "wildcard_timedilation_max_speed", 5.0, "season.timedilation",
            "Max World Speed Multiplier", "Controls the maximum speed the WORLD can move."
    );
    public static final ConfigFileEntry<Double> WILDCARD_TIMEDILATION_PLAYER_MAX_SPEED = new ConfigFileEntry<>(
            "wildcard_timedilation_player_max_speed", 2.0, "season.timedilation",
            "Max Player Speed Multiplier", "Controls the maximum speed the PLAYERS themselves can move (not the world)."
    );


    public static final ConfigFileEntry<Integer> WILDCARD_MOBSWAP_START_SPAWN_DELAY = new ConfigFileEntry<>(
            "wildcard_mobswap_start_spawn_delay_", 360, ConfigTypes.SECONDS, "season.mobswap",
            "Session Start Spawn Delay", "The delay between mob spawns at the START of the session, in seconds."
    );
    public static final ConfigFileEntry<Integer> WILDCARD_MOBSWAP_END_SPAWN_DELAY = new ConfigFileEntry<>(
            "wildcard_mobswap_end_spawn_delay_", 120, ConfigTypes.SECONDS, "season.mobswap",
            "Session End Spawn Delay", "The delay between mob spawns at the END of the session, in seconds."
    );
    public static final ConfigFileEntry<Integer> WILDCARD_MOBSWAP_SPAWN_MOBS = new ConfigFileEntry<>(
            "wildcard_mobswap_spawn_mobs", 250, "season.mobswap",
            "Number of Mobs", "The number of mobs that spawn each cycle."
    );
    public static final ConfigFileEntry<Double> WILDCARD_MOBSWAP_BOSS_CHANCE_MULTIPLIER = new ConfigFileEntry<>(
            "wildcard_mobswap_boss_chance_multiplier", 1.0, "season.mobswap",
            "Boss Chance Multiplier", "Multiplier for boss chance (wither / warden)."
    );

    public static final ConfigFileEntry<Integer> WILDCARD_SUPERPOWERS_WINDCHARGE_MAX_MACE_DAMAGE = new ConfigFileEntry<>(
            "wildcard_superpowers_windcharge_max_mace_damage", 2, "season.superpowers",
            "Wind Charge: Max Mace Damage", "The max amount of damage you can deal with a mace while using the Wind Charge superpower."
    );
    public static final ConfigFileEntry<Boolean> WILDCARD_SUPERPOWERS_ZOMBIES_FIRST_SPAWN_CLEAR_ITEMS = new ConfigFileEntry<>(
            "wildcard_superpowers_zombies_first_spawn_clear_items", true, "season.superpowers",
            "Necromancy: Zombies First Spawn Clear Items", "Controls whether zombies get cleared when they first get respawned."
    );
    public static final ConfigFileEntry<Boolean> WILDCARD_SUPERPOWERS_ZOMBIES_KEEP_INVENTORY = new ConfigFileEntry<>(
            "wildcard_superpowers_zombies_keep_inventory", true, "season.superpowers",
            "Necromancy: Zombies Keep Inventory", "Controls whether zombies keep their items when they die."
    );
    public static final ConfigFileEntry<Boolean> WILDCARD_SUPERPOWERS_ZOMBIES_REVIVE_BY_KILLING_DARK_GREEN = new ConfigFileEntry<>(
            "wildcard_superpowers_zombies_revive_by_killing_dark_green", false, "season.superpowers",
            "Necromancy: Zombies Can Revive", "Controls whether zombies can be revived (gain a life) by killing a dark green player."
    );
    public static final ConfigFileEntry<Integer> WILDCARD_SUPERPOWERS_ZOMBIES_HEALTH = new ConfigFileEntry<>(
            "wildcard_superpowers_zombies_health", 8, "season.superpowers",
            "Necromancy: Zombie Health Amount", "Controls how much health zombies will have."
    );
    public static final ConfigFileEntry<Boolean> WILDCARD_SUPERPOWERS_SUPERSPEED_STEP = new ConfigFileEntry<>(
            "wildcard_superpowers_superspeed_step", false, "season.superpowers",
            "Superspeed: Step Up Blocks", "Controls whether players with the superspeed power active can step up blocks without jumping (like when riding a horse)."
    );
    public static final ConfigFileEntry<Boolean> WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME = new ConfigFileEntry<>(
            "wildcard_superpowers_disable_intro_theme", false, "season.superpowers",
            "Disable Intro Theme", "Disables the theme music that plays when this wildcard is activated."
    );
    public static final ConfigFileEntry<String> WILDCARD_SUPERPOWERS_POWER_BLACKLIST = new ConfigFileEntry<>(
            "wildcard_superpowers_power_blacklist", "[]", ConfigTypes.STRING_LIST, "season.superpowers",
            "Blacklisted Powers", "List of superpowers that cannot be rolled randomly.", Superpowers.getAllStr()
    );
    public static final ConfigFileEntry<Boolean> WILDCARD_SUPERPOWERS_ANIMALDISGUISE_ARMOR = new ConfigFileEntry<>(
            "wildcard_superpowers_animaldisguise_armor", false, "season.superpowers",
            "Animal Disguise: Show Armor", "Controls whether armor is seen on players disguised as mobs."
    );
    public static final ConfigFileEntry<Boolean> WILDCARD_SUPERPOWERS_ANIMALDISGUISE_HANDS = new ConfigFileEntry<>(
            "wildcard_superpowers_animaldisguise_hands", true, "season.superpowers",
            "Animal Disguise: Show Hand Items", "Controls whether hand items are seen on players disguised as mobs."
    );
    public static final ConfigFileEntry<Integer> WILDCARD_SUPERPOWERS_POWERS_PER_PLAYER = new ConfigFileEntry<>(
            "wildcard_superpowers_powers_per_player", 1, "season.superpowers",
            "Max Player Powers", "Controls how many superpowers each player can have activated at once."
    );
	public static final ConfigFileEntry<Integer> WILDCARD_SUPERPOWERS_POWERS_PER_ROLL = new ConfigFileEntry<>(
            "wildcard_superpowers_powers_per_roll", 1, "season.superpowers",
            "Powers Per Roll", "Controls how many superpowers each player gets when powers are rolled."
    );
	public static final ConfigFileEntry<Boolean> WILDCARD_SUPERPOWERS_MAX_POWERS_MESSAGE = new ConfigFileEntry<>(
            "wildcard_superpowers_max_powers_message", true, "season.superpowers",
            "Max Powers Message", "Controls whether to show the messages when a player reaches max superpowers."
    );


    public static final ConfigFileEntry<String> WILDCARD_CALLBACK_WILDCARDS_BLACKLIST = new ConfigFileEntry<>(
            "wildcard_callback_wildcards_blacklist", "[hunger]", ConfigTypes.STRING_LIST, "season.callback",
            "Blacklisted Wildcards", "List of wildcards that cannot be activated in Callback.", Wildcards.getWildcardsStr()
    );
    public static final ConfigFileEntry<Double> WILDCARD_CALLBACK_TURN_OFF = new ConfigFileEntry<>(
            "wildcard_callback_turn_off", 0.75, ConfigTypes.PERCENTAGE, "season.callback",
            "Turn Off In Session", "Controls when in the session the callback wildcard turns off (percentage)."
    );
    public static final ConfigFileEntry<Boolean> WILDCARD_CALLBACK_NERFED_WILDCARDS = new ConfigFileEntry<>(
            "wildcard_callback_nerfed_wildcards", true, "season.callback",
            "Nerfed Wildcards", "Controls whether wildcards are nerfed in callback (recommended)."
    );
	public static final ConfigFileEntry<Boolean> WILDCARD_CALLBACK_POWER_STACKING = new ConfigFileEntry<>(
            "wildcard_callback_power_stacking", false, "{season.callback.stacking}",
            "Power Stacking", "Instead of the superpower wildcard deactivating, the powers stay allowing stacking when the superpower wildcard activates again."
    );
	public static final ConfigFileEntry<Boolean> WILDCARD_CALLBACK_OVERRIDE_TURN_OFF = new ConfigFileEntry<>(
            "wildcard_callback_override_turn_off", false, "season.callback.stacking",
            "Override Callback Turn-Off", "Controls whether power stacking overrides the callback wildcard turning off."
    );
	public static final ConfigFileEntry<Boolean> WILDCARD_CALLBACK_RESET_AT_MAX = new ConfigFileEntry<>(
            "wildcard_callback_reset_at_max", false, "season.callback.stacking",
            "Reset At Max Powers", "When a player reaches the max number of superpowers, the next roll resets them back to one power (Not through /superpower)."
    );

    //Groups
    public static final ConfigFileEntry<Object> GROUP_GENERAL = new ConfigFileEntry<>(
            "group_general", null, ConfigTypes.TEXT, "{season.general}",
            "General", ""
    );
    public static final ConfigFileEntry<Object> GROUP_SIZESHIFTING = new ConfigFileEntry<>(
            "group_sizeshifting", null, ConfigTypes.TEXT, "{season.sizeshifting}",
            "Size Shifting", ""
    );
    public static final ConfigFileEntry<Object> GROUP_HUNGER = new ConfigFileEntry<>(
            "group_hunger", null, ConfigTypes.TEXT, "{season.hunger}",
            "Hunger", ""
    );
    public static final ConfigFileEntry<Object> GROUP_SNAILS_SEASON = new ConfigFileEntry<>(
            "group_snails_season", null, ConfigTypes.TEXT, "{season.snails}",
            "Snails", ""
    );
    public static final ConfigFileEntry<Object> GROUP_TIMEDILATION = new ConfigFileEntry<>(
            "group_timedilation", null, ConfigTypes.TEXT, "{season.timedilation}",
            "Time Dilation", ""
    );
    public static final ConfigFileEntry<Object> GROUP_TRIVIA_SEASON = new ConfigFileEntry<>(
            "group_trivia_season", null, ConfigTypes.TEXT, "{season.trivia}",
            "Trivia Bots", ""
    );
    public static final ConfigFileEntry<Object> GROUP_TRIVIA_QUESTIONS_SEASON = new ConfigFileEntry<>(
            "group_trivia_questions_season", null, ConfigTypes.TEXT, "{season.trivia.questions}",
            "Trivia Questions", ""
    );

    public static final ConfigFileEntry<Object> GROUP_MOBSWAP = new ConfigFileEntry<>(
            "group_mobswap", null, ConfigTypes.TEXT, "{season.mobswap}",
            "Mob Swap", ""
    );
    public static final ConfigFileEntry<Object> GROUP_SUPERPOWERS = new ConfigFileEntry<>(
            "group_superpowers", null, ConfigTypes.TEXT, "{season.superpowers}",
            "Superpowers", ""
    );
    public static final ConfigFileEntry<Object> GROUP_POWERS = new ConfigFileEntry<>(
            "group_powers", null, ConfigTypes.TEXT, "{lifeseries_plus.powers}",
            "Superpowers", ""
    );
    public static final ConfigFileEntry<Object> GROUP_CALLBACK = new ConfigFileEntry<>(
            "group_callback", null, ConfigTypes.TEXT, "{season.callback}",
            "Callback", ""
    );

	public static final ConfigFileEntry<Boolean> WILD_MIDNIGHT_CHIMES = new ConfigFileEntry<>(
            "wild_midnight_chimes", false, "{lifeseries_plus.chimes}",
            "Midnight Chimes", "Controls whether the nice life midnight chimes play at midnight."
    );

    public static final ConfigFileEntry<Boolean> SPAWN_BOTS_AT_MIDNIGHT = new ConfigFileEntry<>(
            "spawn_bots_at_midnight", true, "lifeseries_plus.chimes",
            "Spawn Bots After Chimes", "Controls whether trivia bots spawn after the midnight chimes."
    );

    public WildLifeConfig() {
        super("./config/"+ Main.MOD_ID,"wildlife.properties");
    }

    @Override
    protected List<ConfigFileEntry<?>> getSeasonSpecificConfigEntries() {
        return new ArrayList<>(List.of(
                GROUP_GENERAL //Group
                ,GROUP_SIZESHIFTING //Group
                ,GROUP_HUNGER //Group
                ,GROUP_SNAILS_SEASON //Group
                ,GROUP_TIMEDILATION //Group
                ,GROUP_TRIVIA_SEASON //Group
                ,GROUP_MOBSWAP //Group
                ,GROUP_SUPERPOWERS //Group
                ,GROUP_CALLBACK //Group
				,WILD_MIDNIGHT_CHIMES //Group
				,GROUP_POWERS //Group

                //Group stuff
                ,new ConfigFileEntry<>(
                        ACTIVATE_WILDCARD_MINUTE.key, ACTIVATE_WILDCARD_MINUTE.defaultValue, ConfigTypes.MINUTES, "season.general",
                        ACTIVATE_WILDCARD_MINUTE.displayName, ACTIVATE_WILDCARD_MINUTE.description
                )
                ,new ConfigFileEntry<>(
                        WILDCARD_AUTO_ACTIVATE.key, WILDCARD_AUTO_ACTIVATE.defaultValue, ConfigTypes.BOOLEAN, "season.general",
                        WILDCARD_AUTO_ACTIVATE.displayName, WILDCARD_AUTO_ACTIVATE.description
                )
                ,WILDCARD_SIZESHIFTING_MIN_SIZE
                ,WILDCARD_SIZESHIFTING_MAX_SIZE
                ,WILDCARD_SIZESHIFTING_SIZE_CHANGE_MULTIPLIER
                ,WILDCARD_SIZESHIFTING_FIX_BUGS

                ,WILDCARD_HUNGER_EFFECT_LEVEL
                ,WILDCARD_HUNGER_RANDOMIZE_INTERVAL
                ,WILDCARD_HUNGER_NUTRITION_CHANCE
                ,WILDCARD_HUNGER_SATURATION_CHANCE
                ,WILDCARD_HUNGER_EFFECT_CHANCE
                ,WILDCARD_HUNGER_AVG_EFFECT_DURATION
                ,WILDCARD_HUNGER_SOUND_CHANCE
                ,WILDCARD_HUNGER_NON_EDIBLE_ITEMS

                ,new ConfigFileEntry<>(
                        WILDCARD_SNAILS_SPEED_MULTIPLIER.key, WILDCARD_SNAILS_SPEED_MULTIPLIER.defaultValue, ConfigTypes.DOUBLE, "season.snails",
                        WILDCARD_SNAILS_SPEED_MULTIPLIER.displayName, WILDCARD_SNAILS_SPEED_MULTIPLIER.description
                )
                ,new ConfigFileEntry<>(
                        WILDCARD_SNAILS_DROWN_PLAYERS.key, WILDCARD_SNAILS_DROWN_PLAYERS.defaultValue, ConfigTypes.BOOLEAN, "season.snails",
                        WILDCARD_SNAILS_DROWN_PLAYERS.displayName, WILDCARD_SNAILS_DROWN_PLAYERS.description
                )
                ,new ConfigFileEntry<>(
                        WILDCARD_SNAILS_EFFECTS.key, WILDCARD_SNAILS_EFFECTS.defaultValue, ConfigTypes.BOOLEAN, "season.snails",
                        WILDCARD_SNAILS_EFFECTS.displayName, WILDCARD_SNAILS_EFFECTS.description
                )

                ,WILDCARD_TIMEDILATION_MIN_SPEED
                ,WILDCARD_TIMEDILATION_MAX_SPEED
                ,WILDCARD_TIMEDILATION_PLAYER_MAX_SPEED
				
				,new ConfigFileEntry<>(
                    WILDCARD_TRIVIA_BOTS_CAN_ENTER_BOATS.key, WILDCARD_TRIVIA_BOTS_CAN_ENTER_BOATS.defaultValue, ConfigTypes.BOOLEAN, "season.trivia",
                    WILDCARD_TRIVIA_BOTS_CAN_ENTER_BOATS.displayName, WILDCARD_TRIVIA_BOTS_CAN_ENTER_BOATS.description
					)
				,new ConfigFileEntry<>(
                    WILDCARD_TRIVIA_BOTS_PER_PLAYER.key, WILDCARD_TRIVIA_BOTS_PER_PLAYER.defaultValue, ConfigTypes.INTEGER, "season.trivia",
                    WILDCARD_TRIVIA_BOTS_PER_PLAYER.displayName, WILDCARD_TRIVIA_BOTS_PER_PLAYER.description
					)
				,new ConfigFileEntry<>(
                    WILDCARD_TRIVIA_SECONDS_EASY.key, WILDCARD_TRIVIA_SECONDS_EASY.defaultValue, ConfigTypes.INTEGER, "season.trivia",
                    WILDCARD_TRIVIA_SECONDS_EASY.displayName, WILDCARD_TRIVIA_SECONDS_EASY.description
					)
				,new ConfigFileEntry<>(
                    WILDCARD_TRIVIA_SECONDS_NORMAL.key, WILDCARD_TRIVIA_SECONDS_NORMAL.defaultValue, ConfigTypes.INTEGER, "season.trivia",
                    WILDCARD_TRIVIA_SECONDS_NORMAL.displayName, WILDCARD_TRIVIA_SECONDS_NORMAL.description
					)
				,new ConfigFileEntry<>(
                    WILDCARD_TRIVIA_SECONDS_HARD.key, WILDCARD_TRIVIA_SECONDS_HARD.defaultValue, ConfigTypes.INTEGER, "season.trivia",
                    WILDCARD_TRIVIA_SECONDS_HARD.displayName, WILDCARD_TRIVIA_SECONDS_HARD.description
					)
				,new ConfigFileEntry<>(
                    GROUP_TRIVIA_QUESTIONS.key, GROUP_TRIVIA_QUESTIONS.defaultValue, ConfigTypes.GROUP, "{season.trivia.questions}",
                    GROUP_TRIVIA_QUESTIONS.displayName, GROUP_TRIVIA_QUESTIONS.description
					)
				,new ConfigFileEntry<>(
                    GROUP_TRIVIA_QUESTIONS_EASY.key, GROUP_TRIVIA_QUESTIONS_EASY.defaultValue, ConfigTypes.GROUP, "{season.trivia.questions.easy}",
                    GROUP_TRIVIA_QUESTIONS_EASY.displayName, GROUP_TRIVIA_QUESTIONS_EASY.description
					)
				,new ConfigFileEntry<>(
                    GROUP_TRIVIA_QUESTIONS_NORMAL.key, GROUP_TRIVIA_QUESTIONS_NORMAL.defaultValue, ConfigTypes.GROUP, "{season.trivia.questions.normal}",
                    GROUP_TRIVIA_QUESTIONS_NORMAL.displayName, GROUP_TRIVIA_QUESTIONS_NORMAL.description
					)
				,new ConfigFileEntry<>(
                    GROUP_TRIVIA_QUESTIONS_HARD.key, GROUP_TRIVIA_QUESTIONS_HARD.defaultValue, ConfigTypes.GROUP, "{season.trivia.questions.hard}",
                    GROUP_TRIVIA_QUESTIONS_HARD.displayName, GROUP_TRIVIA_QUESTIONS_HARD.description
					)

                ,WILDCARD_MOBSWAP_START_SPAWN_DELAY
                ,WILDCARD_MOBSWAP_END_SPAWN_DELAY
                ,WILDCARD_MOBSWAP_SPAWN_MOBS
                ,WILDCARD_MOBSWAP_BOSS_CHANCE_MULTIPLIER

                ,WILDCARD_SUPERPOWERS_POWER_BLACKLIST
                ,WILDCARD_SUPERPOWERS_POWERS_PER_PLAYER
                ,WILDCARD_SUPERPOWERS_POWERS_PER_ROLL	
				,WILDCARD_SUPERPOWERS_MAX_POWERS_MESSAGE			
                ,WILDCARD_SUPERPOWERS_DISABLE_INTRO_THEME
                ,WILDCARD_SUPERPOWERS_WINDCHARGE_MAX_MACE_DAMAGE
                ,WILDCARD_SUPERPOWERS_ZOMBIES_FIRST_SPAWN_CLEAR_ITEMS
                ,WILDCARD_SUPERPOWERS_ZOMBIES_KEEP_INVENTORY
                ,WILDCARD_SUPERPOWERS_ZOMBIES_REVIVE_BY_KILLING_DARK_GREEN
                ,WILDCARD_SUPERPOWERS_ZOMBIES_HEALTH
                //? if > 1.20.3 {
                ,WILDCARD_SUPERPOWERS_SUPERSPEED_STEP
                //?}
                ,WILDCARD_SUPERPOWERS_ANIMALDISGUISE_ARMOR
                ,WILDCARD_SUPERPOWERS_ANIMALDISGUISE_HANDS
				
				,new ConfigFileEntry<>(
				WILDCARD_SUPERPOWERS_POWERS_PER_PLAYER.key, WILDCARD_SUPERPOWERS_POWERS_PER_PLAYER.defaultValue, ConfigTypes.INTEGER, "lifeseries_plus.powers",
				WILDCARD_SUPERPOWERS_POWERS_PER_PLAYER.displayName, WILDCARD_SUPERPOWERS_POWERS_PER_PLAYER.description
                )
				,new ConfigFileEntry<>(
				WILDCARD_SUPERPOWERS_POWERS_PER_ROLL.key, WILDCARD_SUPERPOWERS_POWERS_PER_ROLL.defaultValue, ConfigTypes.INTEGER, "lifeseries_plus.powers",
				WILDCARD_SUPERPOWERS_POWERS_PER_ROLL.displayName, WILDCARD_SUPERPOWERS_POWERS_PER_ROLL.description
                )
				,new ConfigFileEntry<>(
				WILDCARD_SUPERPOWERS_MAX_POWERS_MESSAGE.key, WILDCARD_SUPERPOWERS_MAX_POWERS_MESSAGE.defaultValue, ConfigTypes.BOOLEAN, "lifeseries_plus.powers",
				WILDCARD_SUPERPOWERS_MAX_POWERS_MESSAGE.displayName, WILDCARD_SUPERPOWERS_MAX_POWERS_MESSAGE.description
                )

                ,WILDCARD_CALLBACK_WILDCARDS_BLACKLIST
                ,WILDCARD_CALLBACK_TURN_OFF
                ,WILDCARD_CALLBACK_NERFED_WILDCARDS
				,WILDCARD_CALLBACK_POWER_STACKING
				,WILDCARD_CALLBACK_OVERRIDE_TURN_OFF
				,WILDCARD_CALLBACK_RESET_AT_MAX
                ,SPAWN_BOTS_AT_MIDNIGHT
				
				,new ConfigFileEntry<>(
				WILDCARD_CALLBACK_POWER_STACKING.key, WILDCARD_CALLBACK_POWER_STACKING.defaultValue, ConfigTypes.BOOLEAN, "{lifeseries_plus.power_stacking}",
				"Callback Power Stacking", WILDCARD_CALLBACK_POWER_STACKING.description
                )
				,new ConfigFileEntry<>(
				WILDCARD_CALLBACK_OVERRIDE_TURN_OFF.key, WILDCARD_CALLBACK_OVERRIDE_TURN_OFF.defaultValue, ConfigTypes.BOOLEAN, "lifeseries_plus.power_stacking",
				WILDCARD_CALLBACK_OVERRIDE_TURN_OFF.displayName, WILDCARD_CALLBACK_OVERRIDE_TURN_OFF.description
                )
				,new ConfigFileEntry<>(
				WILDCARD_CALLBACK_RESET_AT_MAX.key, WILDCARD_CALLBACK_RESET_AT_MAX.defaultValue, ConfigTypes.BOOLEAN, "lifeseries_plus.power_stacking",
				WILDCARD_CALLBACK_RESET_AT_MAX.displayName, WILDCARD_CALLBACK_RESET_AT_MAX.description
                )
        ));
    }

    @Override
    protected List<ConfigFileEntry<?>> getDefaultConfigEntries() {
        List<ConfigFileEntry<?>> defaultEntries = super.getDefaultConfigEntries();
        defaultEntries.remove(MIDNIGHT_CHIMES);
        return defaultEntries;
    }

    @Override
    public void instantiateProperties() {
        CUSTOM_ENCHANTER_ALGORITHM.defaultValue = true;
        WILDCARD_AUTO_ACTIVATE.defaultValue = true;
        BLACKLIST_ITEMS.defaultValue = TextUtils.formatString("[{}]", BLACKLISTED_ITEMS);
        BLACKLIST_BLOCKS.defaultValue = TextUtils.formatString("[{}]", BLACKLISTED_BLOCKS);
        BLACKLIST_CLAMPED_ENCHANTS_LEVEL_1.defaultValue = TextUtils.formatString("[{}]", CLAMPED_ENCHANTMENTS);
        DEFAULT_LIVES.defaultValue = 6;
        SPAWN_EGG_ALLOW_ON_SPAWNER.defaultValue = true;
        SPAWNER_RECIPE.defaultValue = true;
        TAB_LIST_SHOW_LIVES.defaultValue = true;

        new TriviaQuestionManager("./config/lifeseries/wildlife","easy-trivia.json");
        new TriviaQuestionManager("./config/lifeseries/wildlife","normal-trivia.json");
        new TriviaQuestionManager("./config/lifeseries/wildlife","hard-trivia.json");

        super.instantiateProperties();
    }
}
