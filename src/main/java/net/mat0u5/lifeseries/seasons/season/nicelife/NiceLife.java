package net.mat0u5.lifeseries.seasons.season.nicelife;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.compatibilities.CompatibilityManager;
import net.mat0u5.lifeseries.compatibilities.voicechat.VoicechatMain;
import net.mat0u5.lifeseries.config.ConfigManager;
import net.mat0u5.lifeseries.config.ModifiableText;
import net.mat0u5.lifeseries.entity.triviabot.TriviaBot;
import net.mat0u5.lifeseries.entity.triviabot.server.trivia.NiceLifeTriviaHandler;
import net.mat0u5.lifeseries.mixin.ServerLevelAccessor;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.seasons.season.Season;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.seasons.session.Session;
import net.mat0u5.lifeseries.utils.other.*;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

import net.minecraft.world.level.GameRules;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.mat0u5.lifeseries.Main.*;

public class NiceLife extends Season {

    public static boolean SNOWY_NETHER = true;
    public static boolean LIGHT_MELTS_SNOW = false;
    public boolean SNOW_WHEN_NOT_IN_SESSION = false;
    public static boolean ADVANCE_TIME_WHEN_NOT_IN_SESSION = false;
    public static boolean FREEZE_TIME_AT_MIDNIGHT = true;
    public Time SNOW_LAYER_INCREASE_INTERVAL = Time.seconds(600);
    public Time snowTicks = Time.zero();
    public double snowLayerTickChance = 1.0 / 43;
    public int precipitationTicks = 1;
    public double chancePerTick = snowLayerTickChance;

    public int currentMaxSnowLayers = -1;
    public static boolean reachedSunset = false;
    public static boolean reachedPreSunset = false;
    public static boolean redWinter = false;
    public static Time naughtyListGlowTimeInterval = Time.seconds(60);
    public static Time naughtyListGlowTime = Time.seconds(5);
    public static Time timePassed = Time.zero();
    public static Time triviaCannotStartFor = Time.zero();

    @Override
    public void initialize() {
        super.initialize();
        NiceLifeTriviaHandler.initializeItemSpawner();
        NiceLifeTriviaManager.initialize();
    }

    @Override
    public Seasons getSeason() {
        return Seasons.NICE_LIFE;
    }

    @Override
    public ConfigManager createConfig() {
        return new NiceLifeConfig();
    }

    @Override
    public void switchOutOfSeason(Seasons changedTo) {
        if (server == null) return;
        ServerLevel overworld = server.overworld();
        boolean advanceTime = true;
        OtherUtils.setBooleanGameRule(overworld, GameRules.DO_DAYLIGHT_CYCLE, advanceTime);

        NiceLifeTriviaManager.killAllSnowmen();
        NiceLifeTriviaManager.killAllBots();
        Season.setSkyColor(null, false);
        Season.setFogColor(null, false);
        Season.setCloudColor(null, false);
    }

    @Override
    public void reload() {
        super.reload();
        NiceLifeVotingManager.createTeams();

        LIGHT_MELTS_SNOW = NiceLifeConfig.LIGHT_MELTS_SNOW.get();
        SNOW_WHEN_NOT_IN_SESSION = NiceLifeConfig.SNOW_WHEN_NOT_IN_SESSION.get();
        SNOW_LAYER_INCREASE_INTERVAL = Time.seconds(NiceLifeConfig.SNOW_LAYER_INCREMENT_DELAY.get());
        ADVANCE_TIME_WHEN_NOT_IN_SESSION = NiceLifeConfig.ADVANCE_TIME_WHEN_NOT_IN_SESSION.get();
        SNOWY_NETHER = NiceLifeConfig.SNOWY_NETHER.get();
        FREEZE_TIME_AT_MIDNIGHT = NiceLifeConfig.FREEZE_TIME_AT_MIDNIGHT.get();

        snowLayerTickChance = 280.0 / Math.max(SNOW_LAYER_INCREASE_INTERVAL.getTicks(), 1);
        if (currentMaxSnowLayers == -1) {
            currentMaxSnowLayers = seasonConfig.getOrCreateInt("current_snow_layers", 1);
        }
        updateSnowTick();
        NiceLifeTriviaManager.initialize();

        NiceLifeTriviaManager.QUESTION_TIME = NiceLifeConfig.TRIVIA_QUESTION_TIME.get();
        NiceLifeTriviaManager.CAN_BREAK_BEDS = NiceLifeConfig.BOT_CAN_BREAK_BEDS.get();
        NiceLifeTriviaManager.BREAKING_DROPS_RESOURCES = NiceLifeConfig.BOT_BREAKING_BLOCKS_DROP_RESOURCES.get();
        NiceLifeVotingManager.NICE_LIST_CHANCE = NiceLifeConfig.NICE_LIST_CHANCE.get();
        NiceLifeVotingManager.VOTING_TIME = Time.seconds(NiceLifeConfig.VOTING_TIME.get());
        NiceLifeVotingManager.REDS_ON_NAUGHTY_LIST = NiceLifeConfig.ALLOW_REDS_ON_NAUGHTY_LIST.get();
        NiceLifeVotingManager.NAUGHTY_LIST_COUNT = NiceLifeConfig.NAUGHTY_LIST_PLAYERS.get();
        NiceLifeVotingManager.NICE_LIST_COUNT = NiceLifeConfig.NICE_LIST_PLAYERS.get();
    }

    public void updateSnowTick() {
        double chance = snowLayerTickChance;

        if (redWinter) chance *= 5;
        if (currentMaxSnowLayers >= 8) chance *= 3;

        precipitationTicks = Math.max(1, (int) Math.ceil(chance));
        chancePerTick = chance / precipitationTicks;
    }

    @Override
    public void tick(MinecraftServer server) {
        super.tick(server);
        timePassed.tick();
        boolean freezeAtMidnight = shouldFreezeAtMidnight();

        if (!freezeAtMidnight && (currentSession.statusStarted() || SNOW_WHEN_NOT_IN_SESSION)) {
            snowTicks.tick();
            if (snowTicks.isLarger(SNOW_LAYER_INCREASE_INTERVAL)) {
                snowTicks = Time.zero();
                currentMaxSnowLayers++;
                if (currentMaxSnowLayers > 8) currentMaxSnowLayers = 1;
                updateSnowTick();
                seasonConfig.setProperty("current_snow_layers", String.valueOf(currentMaxSnowLayers));
            }
        }

        ServerLevel overworld = server.overworld();
        boolean advanceTime = (currentSession.statusStarted() || ADVANCE_TIME_WHEN_NOT_IN_SESSION)
                && (!isMidnight() || !isTimeFreezeEnabled());
        OtherUtils.setBooleanGameRule(overworld, GameRules.DO_DAYLIGHT_CYCLE, advanceTime);

        if (!isMidnight()) {
            for (ServerPlayer serverPlayer : PlayerUtils.getAllPlayers()) {
                if (serverPlayer.isSleeping()) {
                    serverPlayer.ls$message(ModifiableText.NICELIFE_SLEEP_FAIL_EARLY.get(), true);
                }
            }
        }

        if (triviaCannotStartFor.isSmaller(Time.zero())) {
            int percentage = overworld.getGameRules().getInt(GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE);
            if (areEnoughSleeping(percentage) && isMidnight() && currentSession.statusStarted()) {
                if (!NiceLifeTriviaManager.triviaInProgress) {
                    List<ServerPlayer> triviaPlayers = new ArrayList<>();
                    for (ServerPlayer player : livesManager.getAlivePlayers()) {
                        if (player.isSpectator()) continue;
                        if (!player.isSleeping()) continue;
                        triviaPlayers.add(player);
                    }
                    if (!triviaPlayers.isEmpty()) NiceLifeTriviaManager.startTrivia(triviaPlayers);
                }
            }
        } else {
            triviaCannotStartFor.add(Time.ticks(-1));
        }

        if (isMidnight() && NiceLifeTriviaManager.triviaInProgress && !NiceLifeTriviaManager.preparingForSpawn) {
            List<ServerPlayer> remainingTriviaPlayers = new ArrayList<>();
            for (UUID playerUUID : NiceLifeTriviaManager.triviaPlayersUUID) {
                ServerPlayer player = PlayerUtils.getPlayer(playerUUID);
                if (player == null) continue;
                if (!player.isSleeping()) continue;
                if (player.ls$isDead()) continue;
                TriviaBot bot = NiceLifeTriviaManager.bots.get(playerUUID);
                if (bot == null) continue;
                if (!bot.isAlive()) continue;
                if (!(bot.triviaHandler instanceof NiceLifeTriviaHandler triviaHandler)) continue;
                if (triviaHandler.currentState == NiceLifeTriviaHandler.BotState.FINISHED) continue;
                remainingTriviaPlayers.add(player);
            }
            if (remainingTriviaPlayers.isEmpty()) sleepThroughNight();
        }

        if (!reachedPreSunset && isTimeBetween(11800, 13000)) {
            if (!NiceLifeVotingManager.niceListMembers.isEmpty()) {
                NiceLifeVotingManager.warnNiceListMembers();
            }
        }

        if (!reachedSunset && isSunset()) {
            NiceLifeVotingManager.endListsIfNecessary();
        }

        reachedSunset = isSunset();
        reachedPreSunset = isTimeBetween(11800, 13000);

        boolean shouldRedWinter = shouldRedWinter();
        if (redWinter != shouldRedWinter) {
            redWinter = shouldRedWinter;
            if (redWinter) triggerRedWinter();
            else {
                Season.setSkyColor(null, false);
                Season.setFogColor(null, false);
                Season.setCloudColor(null, false);
            }
        }

        if (timePassed.getTicks() % 20 == 0 && CompatibilityManager.voicechatLoaded()) {
            VoicechatMain.niceLifeTick();
        }
    }

    public static boolean areEnoughSleeping(int percentage) {
        List<ServerPlayer> players = Main.livesManager.getAlivePlayers();
        int allPlayers = 0;
        int sleepingPlayers = 0;
        for (ServerPlayer player : players) {
            if (player.isSpectator()) continue;
            allPlayers++;
            if (player.isSleeping()) sleepingPlayers++;
        }
        return sleepingPlayers >= Math.max(1, Mth.ceil((float) (allPlayers * percentage) / 100.0F));
    }

    public static void postponeTriviaStart(Time time) {
        if (!triviaCannotStartFor.isLarger(time)) triviaCannotStartFor = time;
    }

}
