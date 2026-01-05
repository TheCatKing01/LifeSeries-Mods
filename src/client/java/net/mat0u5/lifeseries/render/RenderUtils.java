import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

public class RenderUtils {

    // -------------------- TEXT BUILDER --------------------
    public static TextBuilder text(String s, int x, int y) {
        return new TextBuilder(Component.literal(s), x, y);
    }

    public static TextBuilder text(Component c, int x, int y) {
        return new TextBuilder(c, x, y);
    }

    public static TextBuilder text(MutableComponent c, int x, int y) {
        return new TextBuilder(c, x, y);
    }

    public static TextBuilder text(FormattedCharSequence seq, int x, int y) {
        return new TextBuilder(seq, x, y);
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
        private int wrapSpacing = 0;

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

        public TextBuilder anchorCenter() { this.anchorCenter = true; this.anchorRight = false; return this; }
        public TextBuilder anchorRight()  { this.anchorRight = true; this.anchorCenter = false; return this; }

        public TextBuilder colored(int color) { this.color = color; return this; }

        public TextBuilder withShadow() { this.shadow = true; return this; }

        public TextBuilder scaled(float sx, float sy) { this.scaleX = sx; this.scaleY = sy; return this; }

        public TextBuilder wrapLines(int width, int spacing) { this.wrapWidth = width; this.wrapSpacing = spacing; return this; }

        /** returns the pixel height drawn (so callers can do currentY += render(...)) */
        public int render(GuiGraphics g, Font font) {
            var pose = g.pose();
            pose.pushPose();
            pose.translate(x, y, 0);
            pose.scale(scaleX, scaleY, 1f);

            // compute x offset for anchor
            int drawX = 0;
            if (sequence == null && component != null) {
                int w = font.width(component);
                if (anchorCenter) drawX = -(w / 2);
                else if (anchorRight) drawX = -w;
            } else if (sequence != null) {
                int w = font.width(sequence);
                if (anchorCenter) drawX = -(w / 2);
                else if (anchorRight) drawX = -w;
            }

            int height = 0;

            if (wrapWidth > 0 && component != null) {
                List<FormattedCharSequence> lines = font.split(component, wrapWidth);
                int yy = 0;
                for (FormattedCharSequence line : lines) {
                    g.drawString(font, line, drawX, yy, color, shadow);
                    yy += font.lineHeight + wrapSpacing;
                }
                height = yy;
            } else {
                if (sequence != null) g.drawString(font, sequence, drawX, 0, color, shadow);
                else g.drawString(font, component, drawX, 0, color, shadow);

                height = font.lineHeight;
            }

            pose.popPose();

            // height is in scaled coordinates; convert back to screen pixels
            return (int) (height * scaleY);
        }
    }

    // -------------------- TEXTURE BUILDER --------------------
    public static TextureBuilder texture(ResourceLocation tex, int x, int y, int w, int h) {
        return new TextureBuilder(tex, x, y, w, h);
    }

    public static class TextureBuilder {
        private final ResourceLocation tex;
        private final int x, y;
        private int u = 0, v = 0;
        private int inW, inH;

        private int outW, outH;
        private int texW = 256, texH = 256;

        private float scaleX = 1f, scaleY = 1f;

        TextureBuilder(ResourceLocation tex, int x, int y, int w, int h) {
            this.tex = tex;
            this.x = x;
            this.y = y;
            this.inW = w;
            this.inH = h;
            this.outW = w;
            this.outH = h;
        }

        public TextureBuilder uv(int u, int v) { this.u = u; this.v = v; return this; }

        public TextureBuilder textureSize(int w, int h) { this.texW = w; this.texH = h; return this; }

        public TextureBuilder outSize(int w, int h) { this.outW = w; this.outH = h; return this; }

        public TextureBuilder scaled(float sx, float sy) { this.scaleX = sx; this.scaleY = sy; return this; }

        public void render(GuiGraphics g) {
            var pose = g.pose();
            pose.pushPose();
            pose.translate(x, y, 0);
            pose.scale(scaleX, scaleY, 1f);

            // blit takes size in pixels; using outW/outH at (0,0) after translation
            g.blit(tex, 0, 0, u, v, outW, outH, texW, texH);

            pose.popPose();
        }
    }

    // -------------------- BORDER --------------------
    public static void drawBorder(GuiGraphics g, int x, int y, int width, int height, int color) {
        // top
        g.fill(x, y, x + width, y + 1, color);
        // bottom
        g.fill(x, y + height - 1, x + width, y + height, color);
        // left
        g.fill(x, y, x + 1, y + height, color);
        // right
        g.fill(x + width - 1, y, x + width, y + height, color);
    }
}