package net.mat0u5.lifeseries.gui.seasons;

import net.mat0u5.lifeseries.gui.DefaultScreen;
import net.mat0u5.lifeseries.render.RenderUtils;
import net.mat0u5.lifeseries.seasons.season.Seasons;
import net.mat0u5.lifeseries.utils.TextColors;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class SeasonInfoScreen extends DefaultScreen {

    public static Seasons season;

    public final String adminCommands;
    public final String nonAdminCommands;

    public SeasonInfoScreen(Seasons season, String adminCommands, String nonAdminCommands) {
        super(Component.literal("Season Info Screen"), 410, 230);
        SeasonInfoScreen.season = season;
        this.adminCommands = adminCommands == null ? "" : adminCommands;
        this.nonAdminCommands = nonAdminCommands == null ? "" : nonAdminCommands;
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY) {
        // Background + images
        var logo = season.getLogo();
        if (logo != null) {
            RenderUtils.drawTextureScaled(context, logo, startX + 5, endY - 64, 0, 0, 256, 256, 0.25f, 0.25f);
            RenderUtils.drawTextureScaled(context, logo, endX - 64 - 5, endY - 64, 0, 0, 256, 256, 0.25f, 0.25f);
        }

        String seasonName = season.getName();
        RenderUtils.drawTextCenterScaled(context, this.font,
                Component.literal("§0" + seasonName),
                centerX, startY + 10,
                2.25f, 2.25f
        );

        int currentY = startY + 40;

        // Admin commands
        MutableComponent adminLabel = Component.literal("§8Available §nadmin§8 commands: ");
        MutableComponent adminActual = Component.literal(adminCommands);
        MutableComponent combinedAdmin = adminLabel.copy().append(adminActual);
        currentY += 3 + RenderUtils.drawTextLeftWrapLines(
                context, this.font, TextColors.DEFAULT,
                combinedAdmin,
                startX + 15, currentY,
                BG_WIDTH - 20, 6
        );

        // Non-admin commands
        MutableComponent nonAdminLabel = Component.literal("§8Available §nnon-admin§8 commands: ");
        MutableComponent nonAdminActual = Component.literal(nonAdminCommands);
        MutableComponent combinedNonAdmin = nonAdminLabel.copy().append(nonAdminActual);
        currentY += 3 + RenderUtils.drawTextLeftWrapLines(
                context, this.font, TextColors.DEFAULT,
                combinedNonAdmin,
                startX + 15, currentY,
                BG_WIDTH - 20, 6
        );

        // How to start
        RenderUtils.drawTextLeftScaled(context, this.font,
                Component.literal("§0§nHow to start a session"),
                startX + 15, currentY + 3,
                1.3f, 1.3f
        );
        currentY += font.lineHeight + 13;

        Component sessionTimer = Component.nullToEmpty("§8Run §3'/session timer set <time>'§8 to set the desired session time.");
        RenderUtils.drawTextLeft(context, this.font, sessionTimer, startX + 15, currentY);
        currentY += font.lineHeight + 5;

        Component sessionStart = Component.nullToEmpty("§8After that, run §3'/session start'§8 to start the session.");
        RenderUtils.drawTextLeft(context, this.font, sessionStart, startX + 15, currentY); // <-- fixed (was sessionTimer)
        currentY += font.lineHeight + 15;

        Component configText = Component.nullToEmpty("§0§nRun §8§n'/lifeseries config'§0§n to open the Life Series configuration!");
        RenderUtils.drawTextLeft(context, this.font, configText, startX + 15, currentY);
        //currentY += font.lineHeight + 5;
    }

    @Override
    public void onClose() {
        if (season == Seasons.PAST_LIFE && this.minecraft != null) {
            this.minecraft.setScreen(new PastLifeInfoScreen());
            return;
        }
        super.onClose();
    }
}