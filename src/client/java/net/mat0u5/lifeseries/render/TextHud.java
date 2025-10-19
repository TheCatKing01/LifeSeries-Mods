package net.mat0u5.lifeseries.render;

import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.events.ClientKeybinds;
import net.mat0u5.lifeseries.features.Trivia;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.TextColors;
import net.mat0u5.lifeseries.utils.enums.SessionTimerStates;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class TextHud {

    public static int sideTitleRemainTicks = 0;
    private static long lastPressed = 0;
    private static long lastSessionSeconds = 0;
    private static long sessionSeconds = -1;
    private static boolean sessionSecondChanged = true;
    private static long limitedLifeTime = -1;
    private static int triviaTimer = -1;

    public static void renderText(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.options.hudHidden) return;

        int yPos = client.getWindow().getScaledHeight() - (5 + (int) Math.ceil(client.textRenderer.fontHeight * MainClient.TEXT_HUD_SCALE));

        yPos += renderGameNotBroken(client, context, yPos);
        yPos += renderSessionTimer(client, context, yPos);
        yPos += renderLimitedLifeTimer(client, context, yPos);
        yPos += renderMimicryTimer(client, context, yPos);
        yPos += renderSuperpowerCooldown(client, context, yPos);
        yPos += renderTriviaTimer(client, context, yPos);
        yPos += renderSidetitle(client, context, yPos);
    }

    public static void tick() {
        if (sideTitleRemainTicks > 0) sideTitleRemainTicks--;
    }

    public static int renderSidetitle(MinecraftClient client, DrawContext context, int y) {
        if (MainClient.sideTitle == null || MainClient.sideTitle.getString().isEmpty() || sideTitleRemainTicks <= 0)
            return 0;
        return drawHudText(client, context, MainClient.sideTitle, y);
    }

    public static int renderGameNotBroken(MinecraftClient client, DrawContext context, int y) {
        if (client.player == null || !ClientRenderer.isGameFullyFrozen) return 0;

        long currentMillis = System.currentTimeMillis();
        int guiScale = client.options.getGuiScale().getValue();

        if (guiScale <= 3 && guiScale != 0) {
            String textString = "Don't worry, the game is not broken ";
            textString = (currentMillis % 1500 <= 750 ? "§7§n" : "§7") + textString;
            textString += (currentMillis % 500 <= 250 ? "/o/" : "\\o\\");
            Text text = Text.literal(textString);
            return drawHudText(client, context, text, y) - 5;
        } else {
            String text0 = (currentMillis % 1500 <= 750 ? "§7§nDon't worry," : "§7Don't worry,");
            String text1 = (currentMillis % 1500 <= 750 ? "§7§nthe game isn't broken " : "§7the game isn't broken ");
            text1 += (currentMillis % 500 <= 250 ? "/o/" : "\\o\\");

            Text t0 = Text.literal(text0);
            Text t1 = Text.literal(text1);

            int screenWidth = client.getWindow().getScaledWidth();
            int x = screenWidth - 5;

            int draw1 = drawHudText(client, context, t1, y);
            int draw2 = drawHudText(client, context, t0,
                    x - ((client.textRenderer.getWidth(t1) - client.textRenderer.getWidth(t0)) / 2),
                    y - (client.textRenderer.fontHeight + 1));

            return draw1 + draw2 - 5;
        }
    }

    public static int renderSessionTimer(MinecraftClient client, DrawContext context, int y) {
        sessionSecondChanged = true;
        sessionSeconds = -1;

        if (!MainClient.SESSION_TIMER) return 0;
        if (System.currentTimeMillis() - MainClient.sessionTimeLastUpdated > 15000) return 0;
        if (MainClient.sessionTime == SessionTimerStates.OFF.getValue()) return 0;

        Text timerText;

        if (MainClient.sessionTime == SessionTimerStates.ENDED.getValue()) timerText = Text.of("§7Session has ended");
        else if (MainClient.sessionTime == SessionTimerStates.PAUSED.getValue()) timerText = Text.of("§7Session has been paused");
        else if (MainClient.sessionTime == SessionTimerStates.NOT_STARTED.getValue()) timerText = Text.of("§7Session has not started");
        else {
            long remainingTime = roundTime(MainClient.sessionTime) - System.currentTimeMillis();
            sessionSeconds = (int) Math.ceil(remainingTime / 1000.0);
            sessionSecondChanged = lastSessionSeconds != sessionSeconds;
            lastSessionSeconds = sessionSeconds;

            timerText = (remainingTime < 0) ? Text.of("§7Session has ended") :
                    TextUtils.formatLoosely("§7Session {}", OtherUtils.formatTimeMillis(remainingTime));
        }

        return drawHudText(client, context, timerText, y);
    }

    public static int renderLimitedLifeTimer(MinecraftClient client, DrawContext context, int y) {
        if (MainClient.clientCurrentSeason != Seasons.LIMITED_LIFE) return 0;
        if (System.currentTimeMillis() - MainClient.limitedLifeTimeLastUpdated > 15000) return 0;

        if (sessionSecondChanged || MainClient.sessionTime <= 0 || Math.abs(limitedLifeTime - MainClient.limitedLifeLives) > 10) {
            limitedLifeTime = MainClient.limitedLifeLives;
        }

        Text timerText;
        if (limitedLifeTime == -1) timerText = Text.of(MainClient.limitedLifeTimerColor + "0:00:00");
        else {
            long remainingTime = limitedLifeTime * 1000;
            if (remainingTime < 0) timerText = Text.of(MainClient.limitedLifeTimerColor + "0:00:00");
            else timerText = Text.of(MainClient.limitedLifeTimerColor + OtherUtils.formatTimeMillis(remainingTime));
        }

        return drawHudText(client, context, timerText, y);
    }

    public static int renderTriviaTimer(MinecraftClient client, DrawContext context, int y) {
        if (!Trivia.isDoingTrivia()) return 0;

        if (sessionSecondChanged || MainClient.sessionTime <= 0 || Math.abs(triviaTimer - Trivia.getRemainingSeconds()) >= 2) {
            triviaTimer = Trivia.getRemainingSeconds();
        }

        int secondsLeft = triviaTimer;
        Text actualTimer = Text.of(OtherUtils.formatTimeMillis(secondsLeft * 1000));
        Text timerText = Text.of("§7Trivia timer: ");

        int screenWidth = client.getWindow().getScaledWidth();
        int x = screenWidth - 5;

        if (secondsLeft <= 5) drawHudText(client, context, TextColors.RED, actualTimer, x, y);
        else if (secondsLeft <= 30) drawHudText(client, context, TextColors.ORANGE, actualTimer, x, y);
        else drawHudText(client, context, TextColors.WHITE, actualTimer, x, y);

        return drawHudText(client, context, timerText, x - client.textRenderer.getWidth(actualTimer), y);
    }

    public static int renderSuperpowerCooldown(MinecraftClient client, DrawContext context, int y) {
        if (isAnyPowerPressed()) lastPressed = System.currentTimeMillis();

        if (MainClient.SUPERPOWER_COOLDOWN_TIMESTAMP == 0) return 0;

        long currentMillis = System.currentTimeMillis();
        if (currentMillis >= MainClient.SUPERPOWER_COOLDOWN_TIMESTAMP) return 0;

        long millisLeft = roundTime(MainClient.SUPERPOWER_COOLDOWN_TIMESTAMP) - currentMillis;
        if (millisLeft > 10_000_000) return 0;

        long pressedAgo = System.currentTimeMillis() - lastPressed;
        boolean keyPressed = pressedAgo < 500;
        if (pressedAgo > 6000) return 0;

        Text timerText = TextUtils.formatLoosely("{}Superpower cooldown:§f {}", (keyPressed ? "§c§n" : "§7"),
                OtherUtils.formatTimeMillis(millisLeft));

        return drawHudText(client, context, timerText, y);
    }

    public static int renderMimicryTimer(MinecraftClient client, DrawContext context, int y) {
        if (MainClient.MIMICRY_COOLDOWN_TIMESTAMP == 0) return 0;

        long currentMillis = System.currentTimeMillis();
        if (currentMillis >= MainClient.MIMICRY_COOLDOWN_TIMESTAMP) return 0;

        long millisLeft = roundTime(MainClient.MIMICRY_COOLDOWN_TIMESTAMP) - currentMillis;
        if (millisLeft > 10_000_000) return 0;

        Text timerText = TextUtils.formatLoosely("§7Mimic power cooldown: §f{}", OtherUtils.formatTimeMillis(millisLeft));
        return drawHudText(client, context, timerText, y);
    }

    private static boolean isAnyPowerPressed() {
        return (ClientKeybinds.timeControl != null && ClientKeybinds.timeControl.isPressed())
                || (ClientKeybinds.creaking != null && ClientKeybinds.creaking.isPressed())
                || (ClientKeybinds.windCharge != null && ClientKeybinds.windCharge.isPressed())
                || (ClientKeybinds.astralProjection != null && ClientKeybinds.astralProjection.isPressed())
                || (ClientKeybinds.superPunch != null && ClientKeybinds.superPunch.isPressed())
                || (ClientKeybinds.mimicry != null && ClientKeybinds.mimicry.isPressed())
                || (ClientKeybinds.teleportation != null && ClientKeybinds.teleportation.isPressed())
                || (ClientKeybinds.listening != null && ClientKeybinds.listening.isPressed())
                || (ClientKeybinds.shadowPlay != null && ClientKeybinds.shadowPlay.isPressed())
                || (ClientKeybinds.flight != null && ClientKeybinds.flight.isPressed())
                || (ClientKeybinds.playerDisguise != null && ClientKeybinds.playerDisguise.isPressed())
                || (ClientKeybinds.animalDisguise != null && ClientKeybinds.animalDisguise.isPressed())
                || (ClientKeybinds.tripleJump != null && ClientKeybinds.tripleJump.isPressed())
                || (ClientKeybinds.invisibility != null && ClientKeybinds.invisibility.isPressed())
                || (ClientKeybinds.superspeed != null && ClientKeybinds.superspeed.isPressed())
                || (ClientKeybinds.necromancy != null && ClientKeybinds.necromancy.isPressed());
    }

    public static int drawHudText(MinecraftClient client, DrawContext context, Text text, int y) {
        int screenWidth = client.getWindow().getScaledWidth();
        int x = screenWidth - 5;
        return drawHudText(client, context, text, x, y);
    }

    public static int drawHudText(MinecraftClient client, DrawContext context, Text text, int x, int y) {
        return drawHudText(client, context, TextColors.DEFAULT, text, x, y);
    }

    public static int drawHudText(MinecraftClient client, DrawContext context, int color, Text text, int x, int y) {
        if (MainClient.TEXT_HUD_SCALE != 1) {
            RenderUtils.drawTextRightScaled(context, client.textRenderer, color, text, x, y,
                    (float) MainClient.TEXT_HUD_SCALE, (float) MainClient.TEXT_HUD_SCALE, true);
            return -((int) Math.ceil(client.textRenderer.fontHeight * MainClient.TEXT_HUD_SCALE) + 5);
        }
        RenderUtils.drawTextRight(context, client.textRenderer, color, text, x, y, true);
        return -client.textRenderer.fontHeight - 5;
    }

    public static long roundTime(long time) {
        return time - (time % 1000);
    }
}
