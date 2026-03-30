package net.mat0u5.lifeseries.gui.other;

import net.mat0u5.lifeseries.gui.DefaultScreen;
import net.mat0u5.lifeseries.network.NetworkHandlerClient;
import net.mat0u5.lifeseries.network.packets.simple.SimplePackets;
import net.mat0u5.lifeseries.render.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ChooseWildcardScreen extends DefaultScreen {

    public ChooseWildcardScreen() {
        super(Component.literal("Choose Wildcard Screen"), 230, 150);
    }

    @Override
    protected void init() {
        super.init();
        int buttonWidth = 120;
        int centerX = startX + (BG_WIDTH / 2);
        int buttonX = centerX - (buttonWidth / 2);
        int firstButtonY = startY + 50;
        int buttonSpacing = 25;

        this.addRenderableWidget(
                Button.builder(Component.literal("Trivia"), btn -> {
                            this.onClose();
                            SimplePackets.SELECTED_WILDCARD.sendToServer("trivia");
                        })
                        .pos(buttonX, firstButtonY)
                        .size(buttonWidth, 20)
                        .build()
        );
        this.addRenderableWidget(
                Button.builder(Component.literal("Snails"), btn -> {
                            this.onClose();
                            SimplePackets.SELECTED_WILDCARD.sendToServer("snails");
                        })
                        .pos(buttonX, firstButtonY + buttonSpacing)
                        .size(buttonWidth, 20)
                        .build()
        );
        this.addRenderableWidget(
                Button.builder(Component.literal("None"), btn -> {
                            this.onClose();
                            SimplePackets.SELECTED_WILDCARD.sendToServer("none");
                        })
                        .pos(buttonX, firstButtonY + buttonSpacing * 2)
                        .size(buttonWidth, 20)
                        .build()
        );
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY) {
        String prompt = "Select the Wildcard for this session.";
        RenderUtils.text(prompt, centerX, startY + 20).anchorCenter().render(context, this.font);
    }
}
