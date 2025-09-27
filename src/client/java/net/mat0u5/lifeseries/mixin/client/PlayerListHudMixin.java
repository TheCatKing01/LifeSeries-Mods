package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.MainClient;
import net.mat0u5.lifeseries.seasons.other.LivesManager;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.*;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerListHud.class, priority = 1)
public class PlayerListHudMixin {

    @Redirect(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/scoreboard/ReadableScoreboardScore;getFormattedScore(Lnet/minecraft/scoreboard/ReadableScoreboardScore;Lnet/minecraft/scoreboard/number/NumberFormat;)Lnet/minecraft/text/MutableText;"))
    private MutableText modifyFormattedScore(ReadableScoreboardScore readableScoreboardScore, NumberFormat numberFormat) {
        ScoreboardObjective objective = ls$getDisplayedObjective();
        MutableText originalText = ReadableScoreboardScore.getFormattedScore(readableScoreboardScore, numberFormat);
        if (readableScoreboardScore == null || originalText == null) return originalText;

        if (objective != null && objective.getName().equals(LivesManager.SCOREBOARD_NAME)) {
            int score = readableScoreboardScore.getScore();
            if (MainClient.clientCurrentSeason != Seasons.LIMITED_LIFE) {
                if (score >= 4 && !MainClient.TAB_LIST_SHOW_EXACT_LIVES && !Main.DEBUG) {
                    return Text.literal("4+").setStyle(originalText.getStyle());
                }
            }
            else {
                return Text.literal(OtherUtils.formatTime(score*20)).setStyle(originalText.getStyle());
            }
        }

        return originalText;
    }

    @Unique
    private ScoreboardObjective ls$getDisplayedObjective() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return null;

        Scoreboard scoreboard = client.world.getScoreboard();
        return scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.LIST);
    }


    @Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true)
    private void getName(PlayerListEntry entry, CallbackInfoReturnable<Text> cir) {
        if (!MainClient.COLORBLIND_SUPPORT) return;
        Text original = cir.getReturnValue();
        if (entry == null) return;
        Team team = entry.getScoreboardTeam();
        if (team == null) return;
        cir.setReturnValue(TextUtils.format("[{}] ", team.getDisplayName().getString()).formatted(team.getColor()).append(original));
    }
}
