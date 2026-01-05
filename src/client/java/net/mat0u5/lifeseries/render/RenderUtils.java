package net.mat0u5.lifeseries.render;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class RenderUtils {

    // ---------- Basic text: left ----------
    public static void drawTextLeft(GuiGraphics g, Font font, Component text, int x, int y) {
        g.drawString(font, text, x, y, 0xFFFFFFFF, false);
    }

    public static void drawTextLeft(GuiGraphics g, Font font, net.minecraft.network.chat.MutableComponent text, int x, int y) {
        g.drawString(font, text, x, y, 0xFFFFFFFF, false);
    }

    public static void drawTextLeft(GuiGraphics g, Font font, int color, Component text, int x, int y) {
        g.drawString(font, text, x, y, color, false);
    }

    public static void drawTextLeft(GuiGraphics g, Font font, int color, net.minecraft.network.chat.MutableComponent text, int x, int y) {
        g.drawString(font, text, x, y, color, false);
    }

    // ---------- Center ----------
    public static void drawTextCenter(GuiGraphics g, Font font, Component text, int centerX, int y) {
        int w = font.width(text);
        g.drawString(font, text, centerX - (w / 2), y, 0xFFFFFFFF, false);
    }

    // ---------- Right ----------
    public static void drawTextRight(GuiGraphics g, Font font, int color, Component text, int rightX, int y) {
        int w = font.width(text);
        g.drawString(font, text, rightX - w, y, color, false);
    }

    // Overload used in TextHud: includes dropShadow boolean
    public static void drawTextRight(GuiGraphics g, Font font, int color, Component text, int rightX, int y, boolean dropShadow) {
        int w = font.width(text);
        g.drawString(font, text, rightX - w, y, color, dropShadow);
    }

    // ---------- Scaled text ----------
    public static void drawTextLeftScaled(GuiGraphics g, Font font, Component text, int x, int y, float scaleX, float scaleY) {
        var pose = g.pose();
        pose.pushPose();
        pose.translate(x, y, 0);
        pose.scale(scaleX, scaleY, 1.0f);
        g.drawString(font, text, 0, 0, 0xFFFFFFFF, false);
        pose.popPose();
    }

    public static void drawTextCenterScaled(GuiGraphics g, Font font, Component text, int centerX, int y, float scaleX, float scaleY) {
        int w = font.width(text);
        var pose = g.pose();
        pose.pushPose();
        pose.translate(centerX, y, 0);
        pose.scale(scaleX, scaleY, 1.0f);
        // center around 0 after translating to centerX
        g.drawString(font, text, -(w / 2), 0, 0xFFFFFFFF, false);
        pose.popPose();
    }

    public static void drawTextRightScaled(
            GuiGraphics g, Font font, int color, Component text,
            int rightX, int y, float scaleX, float scaleY, boolean dropShadow
    ) {
        int w = font.width(text);
        var pose = g.pose();
        pose.pushPose();
        pose.translate(rightX, y, 0);
        pose.scale(scaleX, scaleY, 1.0f);
        g.drawString(font, text, -w, 0, color, dropShadow);
        pose.popPose();
    }

    // ---------- Wrapped lines ----------
    public static void drawTextLeftWrapLines(
            GuiGraphics g, Font font, int color, Component text,
            int x, int y, int maxWidth, int lineSpacing
    ) {
        List<FormattedCharSequence> lines = font.split(text, maxWidth);
        int yy = y;
        for (FormattedCharSequence line : lines) {
            g.drawString(font, line, x, yy, color, false);
            yy += font.lineHeight + lineSpacing;
        }
    }

    // ---------- Scaled texture ----------
    public static void drawTextureScaled(
            GuiGraphics g, ResourceLocation texture,
            int x, int y, int u, int v, int w, int h,
            float scaleX, float scaleY
    ) {
        var pose = g.pose();
        pose.pushPose();
        pose.translate(x, y, 0);
        pose.scale(scaleX, scaleY, 1.0f);
        // draw at (0,0) because we translated to x,y already
        g.blit(texture, 0, 0, u, v, w, h);
        pose.popPose();
    }
}
