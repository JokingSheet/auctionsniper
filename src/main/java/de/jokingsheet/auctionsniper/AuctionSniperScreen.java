package de.jokingsheet.auctionsniper;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class AuctionSniperScreen extends Screen {
    public AuctionSniperScreen() { super(Component.literal("Auction Sniper")); }

    @Override
    protected void init() {
        int cx = width / 2;
        addRenderableWidget(net.minecraft.client.gui.components.Button.builder(
            Component.literal(AuctionSniperClient.isEnabled() ? "Auto-Scan: ON" : "Auto-Scan: OFF"),
            b -> {
                AuctionSniperClient.setEnabled(!AuctionSniperClient.isEnabled());
                b.setMessage(Component.literal(AuctionSniperClient.isEnabled() ? "Auto-Scan: ON" : "Auto-Scan: OFF"));
            }).bounds(cx - 100, height / 2 - 70, 200, 20).build());
        addRenderableWidget(net.minecraft.client.gui.components.Button.builder(
            Component.literal("Close"), b -> onClose()).bounds(cx - 100, height / 2 + 80, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics, mouseX, mouseY, delta);
        super.render(graphics, mouseX, mouseY, delta);
        graphics.drawCenteredString(font, title, width / 2, 24, 0xFFFFFF);
        graphics.drawCenteredString(font, "Captured visible listings: " + AuctionScanner.listings().size(), width / 2, height / 2 - 25, 0xFFFFFF);
        graphics.drawCenteredString(font, "Prices and alerts require verified server tooltip parsing.", width / 2, height / 2, 0xFFCC55);
        graphics.drawCenteredString(font, "Right Shift opens this menu. Auto scan toggle is in the menu.", width / 2, height / 2 + 22, 0xAAAAAA);
    }

    @Override public boolean isPauseScreen() { return false; }
}
