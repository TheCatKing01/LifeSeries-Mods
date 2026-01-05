package net.mat0u5.lifeseries.render;

import net.mat0u5.lifeseries.utils.TextColors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

//? if >= 1.21.2 && <= 1.21.5
import net.minecraft.client.renderer.RenderType;
//? if >= 1.21.6
import net.minecraft.client.renderer.RenderPipelines;

//? if <= 1.21.9 {
import net.minecraft.resources.ResourceLocation;
//?} else {
import net.minecraft.resources.Identifier;
//?}

public class RenderUtils {

    public static void debugX(GuiGraphics context, int x) {
        context.fill(x, 0, x + 1, context.guiHeight(), TextColors.DEBUG);
    }

    public static void debugY(GuiGraphics context, int y) {
        context.fill(0, y, context.guiWidth(), y + 1, TextColors.DEBUG);
    }

    //? if <= 1.21.9 {
    public static void drawTextureScaled(GuiGraphics context, ResourceLocation texture, float x, float y, int u, int v, int width, int height, float scaleX, float scaleY) {
    //?} else {
    public static void drawTextureScaled(GuiGraphics context, Identifier texture, float x, float y, int u, int v, int width, int height, float scaleX, float scaleY) {
    //?}
        //? if <= 1.21 {
        context.pose().pushPose();
        context.pose().scale(scaleX, scaleY, 1.0f);
        context.blit(texture, (int) (x / scaleX), (int) (y / scaleY), u, v, width, height);
        context.pose().popPose();
        //?} else {
        drawTextureScaled(context, texture, x, y, u, v, width, height, width, height, scaleX, scaleY);
        //?}
    }

    //? if <= 1.21.9 {
    public static void drawTexture(GuiGraphics context, ResourceLocation texture, int x, int y, int u, int v, int width, int height) {
    //?} else {
    public static void drawTexture(GuiGraphics context, Identifier texture, int x, int y, int u, int v, int width, int height) {
    //?}
        //? if <= 1.21 {
        context.blit(texture, x, y, u, v, width, height);
        //?} else {
        drawTexture(context, texture, x, y, u, v, width, height, width, height);
        //?}
    }

    //? if >= 1.21.2 && <= 1.21.5 {
    public static void drawTextureScaled(GuiGraphics context, ResourceLocation texture, float x, float y, int u, int v, int width, int height, int textureWidth, int textureHeight, float scaleX, float scaleY) {
        context.pose().pushPose();
        context.pose().scale(scaleX, scaleY, 1.0f);
        context.blit(RenderType::guiTextured, texture, (int) (x / scaleX), (int) (y / scaleY), u, v, width, height, textureWidth, textureHeight);
        context.pose().popPose();
    }

    public static void drawTexture(GuiGraphics context, ResourceLocation texture, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight) {
        context.blit(RenderType::guiTextured, texture, x, y, u, v, width, height, textureWidth, textureHeight);
    }
    //?} else if >= 1.21.6 && <= 1.21.9 {
    public static void drawTextureScaled(GuiGraphics context, ResourceLocation texture, float x, float y, int u, int v, int width, int height, int textureWidth, int textureHeight, float scaleX, float scaleY) {
        context.pose().pushMatrix();
        context.pose().scale(scaleX, scaleY);
        context.blit(RenderPipelines.GUI_TEXTURED, texture, (int) (x / scaleX), (int) (y / scaleY), u, v, width, height, textureWidth, textureHeight);
        context.pose().popMatrix();
    }

    public static void drawTexture(GuiGraphics context, ResourceLocation texture, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight) {
        context.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height, textureWidth, textureHeight);
    }
    //?} else if > 1.21.9 {
    public static void drawTextureScaled(GuiGraphics context, Identifier texture, float x, float y, int u, int v, int width, int height, int textureWidth, int textureHeight, float scaleX, float scaleY) {
        context.pose().pushMatrix();
        context.pose().scale(scaleX, scaleY);
        context.blit(RenderPipelines.GUI_TEXTURED, texture, (int) (x / scaleX), (int) (y / scaleY), u, v, width, height, textureWidth, textureHeight);
        context.pose().popMatrix();
    }

    public static void drawTexture(GuiGraphics context, Identifier texture, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight) {
        context.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height, textureWidth, textureHeight);
    }
    //?}

    //Center Fixed Text
    public static void drawTextCenter(GuiGraphics context, Font textRenderer, Component text, int x, int y) {
        drawTextCenter(context, textRenderer, TextColors.DEFAULT, text, x, y);
    }

    public static void drawTextCenterScaled(GuiGraphics context, Font textRenderer, Component text, double x, double y, float scaleX, float scaleY) {
        drawTextCenterScaled(context, textRenderer, TextColors.DEFAULT, text, x, y, scaleX, scaleY);
    }

    public static void drawTextCenter(GuiGraphics context, Font textRenderer, int textColor, Component text, int x, int y) {
        context.drawString(textRenderer, text, x - textRenderer.width(text)/2, y, textColor, false);
    }

    public static void drawTextCenterScaled(GuiGraphics context, Font textRenderer, int textColor, Component text, double x, double y, float scaleX, float scaleY) {
        //? if <= 1.21.5 {
        context.pose().pushPose();
        context.pose().scale(scaleX, scaleY, 1.0f);
        //?} else {
        context.pose().pushMatrix();
        context.pose().scale(scaleX, scaleY);
        //?}
        context.drawString(textRenderer, text, (int)(x / scaleX - textRenderer.width(text)/2.0), (int)(y / scaleY), textColor, false);
        //? if <= 1.21.5 {
        context.pose().popPose();
        //?} else {
        context.pose().popMatrix();
        //?}
    }


    //Left Fixed Text
    public static void drawTextLeft(GuiGraphics context, Font textRenderer, Component text, int x, int y) {
        drawTextLeft(context, textRenderer, TextColors.DEFAULT, text, x, y);
    }

    public static void drawTextLeftScaled(GuiGraphics context, Font textRenderer, Component text, double x, double y, float scaleX, float scaleY) {
        drawTextLeftScaled(context, textRenderer, TextColors.DEFAULT, text, x, y, scaleX, scaleY);
    }

    public static void drawTextLeft(GuiGraphics context, Font textRenderer, int textColor, Component text, int x, int y) {
        context.drawString(textRenderer, text, x, y, textColor, false);
    }

    public static void drawOrderedTextLeft(GuiGraphics context, Font textRenderer, int textColor, FormattedCharSequence text, int x, int y) {
        context.drawString(textRenderer, text, x, y, textColor, false);
    }

    public static void drawTextLeftScaled(GuiGraphics context, Font textRenderer, int textColor, Component text, double x, double y, float scaleX, float scaleY) {
        //? if <= 1.21.5 {
        context.pose().pushPose();
        context.pose().scale(scaleX, scaleY, 1.0f);
        //?} else {
        context.pose().pushMatrix();
        context.pose().scale(scaleX, scaleY);
        //?}
        context.drawString(textRenderer, text, (int)(x / scaleX), (int)(y / scaleY), textColor, false);
        //? if <= 1.21.5 {
        context.pose().popPose();
        //?} else {
        context.pose().popMatrix();
        //?}
    }

    public static int drawTextLeftWrapLines(GuiGraphics context, Font textRenderer, int textColor, Component text, int x, int y, int maxWidth, int gapY) {
        List<FormattedCharSequence> wrappedText = textRenderer.split(text, maxWidth);
        int offsetY = 0;
        for (FormattedCharSequence line : wrappedText) {
            context.drawString(textRenderer, line, x, y + offsetY, textColor, false);
            offsetY += textRenderer.lineHeight + gapY;
        }
        return offsetY;
    }

    //Right Fixed Text
    public static void drawTextRight(GuiGraphics context, Font textRenderer, Component text, int x, int y) {
        drawTextRight(context, textRenderer, TextColors.DEFAULT, text, x, y);
    }

    public static void drawTextRightScaled(GuiGraphics context, Font textRenderer, Component text, double x, double y, float scaleX, float scaleY) {
        drawTextRightScaled(context, textRenderer, TextColors.DEFAULT, text, x, y, scaleX, scaleY);
    }

    public static void drawTextRight(GuiGraphics context, Font textRenderer, int textColor, Component text, int x, int y) {
        drawTextRight(context, textRenderer, textColor, text, x, y, false);
    }

    public static void drawTextRight(GuiGraphics context, Font textRenderer, int textColor, Component text, int x, int y, boolean shadow) {
        context.drawString(textRenderer, text, x - textRenderer.width(text), y, textColor, shadow);
    }

    public static void drawTextRightScaled(GuiGraphics context, Font textRenderer, int textColor, Component text, double x, double y, float scaleX, float scaleY) {
        drawTextRightScaled(context, textRenderer, textColor, text, x, y, scaleX, scaleY, false);
    }

    public static void drawTextRightScaled(GuiGraphics context, Font textRenderer, int textColor, Component text, double x, double y, float scaleX, float scaleY, boolean shadow) {
        int width = textRenderer.width(text);
        //? if <= 1.21.5 {
        context.pose().pushPose();
        context.pose().scale(scaleX, scaleY, 1.0f);
        //?} else {
        context.pose().pushMatrix();
        context.pose().scale(scaleX, scaleY);
        //?}
        context.drawString(textRenderer, text, (int)(x / scaleX - width), (int)(y / scaleY), textColor, shadow);
        //? if <= 1.21.5 {
        context.pose().popPose();
        //?} else {
        context.pose().popMatrix();
        //?}
    }


    public static void drawBorder(GuiGraphics context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + 1, color);
        context.fill(x, y + height - 1, x + width, y + height, color);
        context.fill(x, y + 1, x + 1, y + height - 1, color);
        context.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
    }

    //Builder API
    public static TextBuilder text(String s, int x, int y) {
        return new TextBuilder(Component.literal(s), x, y);
    }

    public static TextBuilder text(Component c, int x, int y) {
        return new TextBuilder(c, x, y);
    }

    public static TextBuilder text(FormattedCharSequence seq, int x, int y) {
        return new TextBuilder(seq, x, y);
    }

    //? if <= 1.21.9 {
    public static TextureBuilder texture(ResourceLocation tex, int x, int y, int w, int h) {
    //?} else {
    public static TextureBuilder texture(Identifier tex, int x, int y, int w, int h) {
    //?}
        return new TextureBuilder(tex, x, y, w, h);
    }

    public static class TextBuilder {
        private final int x;
        private final int y;

        private Component component;
        private FormattedCharSequence sequence;

        private boolean anchorCenter = false;
        private boolean anchorRight = false;
        private boolean shadow = false;

        private int color = 0xFFFFFFFF;
        private float scaleX = 1f;
        private float scaleY = 1f;

        private int wrapWidth = -1;
        private int maxLines = Integer.MAX_VALUE;

        TextBuilder(Component c, int x, int y) {
            this.component = c;
            this.x = x;
            this.y = y;
        }

        TextBuilder(FormattedCharSequence seq, int x, int y) {
            this.sequence = seq;
            this.x = x;
            this.y = y;
        }

        public TextBuilder anchorCenter() { anchorCenter = true; anchorRight = false; return this; }
        public TextBuilder anchorRight() { anchorRight = true; anchorCenter = false; return this; }

        public TextBuilder colored(int color) { this.color = color; return this; }
        public TextBuilder withShadow() { this.shadow = true; return this; }

        public TextBuilder scaled(float sx, float sy) { this.scaleX = sx; this.scaleY = sy; return this; }

        public TextBuilder wrapLines(int width, int maxLines) { this.wrapWidth = width; this.maxLines = maxLines; return this; }

        /** Returns height used (your code does: currentY += ...render(...)) */
        public int render(GuiGraphics g, Font font) {
            var pose = g.pose();
            //? if <= 1.21.5 {
            pose.pushPose();
            pose.translate(x, y, 0);
            pose.scale(scaleX, scaleY, 1f);
            //?} else {
            pose.pushMatrix();
            pose.translate(x, y, 0);
            pose.scale(scaleX, scaleY);
            //?}

            // FormattedCharSequence path
            if (sequence != null) {
                int w = font.width(sequence);
                int drawX = 0;
                if (anchorCenter) drawX = -(w / 2);
                else if (anchorRight) drawX = -w;

                g.drawString(font, sequence, drawX, 0, color, shadow);
                //? if <= 1.21.5 {
                pose.popPose();
                //?} else {
                pose.popMatrix();
                //?}
                return (int) (font.lineHeight * scaleY);
            }

            // Component path (with optional wrap)
            if (component != null && wrapWidth > 0) {
                List<FormattedCharSequence> lines = font.split(component, wrapWidth);
                int shown = Math.min(lines.size(), maxLines);
                int yy = 0;

                for (int i = 0; i < shown; i++) {
                    FormattedCharSequence line = lines.get(i);
                    int w = font.width(line);
                    int drawX = 0;
                    if (anchorCenter) drawX = -(w / 2);
                    else if (anchorRight) drawX = -w;

                    g.drawString(font, line, drawX, yy, color, shadow);
                    yy += font.lineHeight;
                }

                //? if <= 1.21.5 {
                pose.popPose();
                //?} else {
                pose.popMatrix();
                //?}
                return (int) (yy * scaleY);
            } else {
                int w = component == null ? 0 : font.width(component);
                int drawX = 0;
                if (anchorCenter) drawX = -(w / 2);
                else if (anchorRight) drawX = -w;

                if (component != null) {
                    g.drawString(font, component, drawX, 0, color, shadow);
                }
                //? if <= 1.21.5 {
                pose.popPose();
                //?} else {
                pose.popMatrix();
                //?}
                return (int) (font.lineHeight * scaleY);
            }
        }
    }

    public static class TextureBuilder {
        //? if <= 1.21.9 {
        private final ResourceLocation tex;
        //?} else {
        private final Identifier tex;
        //?}
        private final int x, y;
        private int u = 0, v = 0;
        private int outW, outH;
        private int texW = 256, texH = 256;
        private float scaleX = 1f, scaleY = 1f;

        //? if <= 1.21.9 {
        TextureBuilder(ResourceLocation tex, int x, int y, int w, int h) {
        //?} else {
        TextureBuilder(Identifier tex, int x, int y, int w, int h) {
        //?}
            this.tex = tex;
            this.x = x;
            this.y = y;
            this.outW = w;
            this.outH = h;
        }

        public TextureBuilder uv(int u, int v) { this.u = u; this.v = v; return this; }
        public TextureBuilder textureSize(int w, int h) { this.texW = w; this.texH = h; return this; }
        public TextureBuilder outSize(int w, int h) { this.outW = w; this.outH = h; return this; }
        public TextureBuilder scaled(float sx, float sy) { this.scaleX = sx; this.scaleY = sy; return this; }

        public void render(GuiGraphics g) {
            var pose = g.pose();
            //? if <= 1.21.5 {
            pose.pushPose();
            pose.translate(x, y, 0);
            pose.scale(scaleX, scaleY, 1f);
            //?} else {
            pose.pushMatrix();
            pose.translate(x, y, 0);
            pose.scale(scaleX, scaleY);
            //?}

            //? if >= 1.21.6 {
            g.blit(RenderPipelines.GUI_TEXTURED, tex, 0, 0, u, v, outW, outH, texW, texH);
            //?} else if >= 1.21.2 {
            g.blit(RenderType::guiTextured, tex, 0, 0, u, v, outW, outH, texW, texH);
            //?} else {
            g.blit(tex, 0, 0, u, v, outW, outH, texW, texH);
            //?}

            //? if <= 1.21.5 {
            pose.popPose();
            //?} else {
            pose.popMatrix();
            //?}
        }
    }
}