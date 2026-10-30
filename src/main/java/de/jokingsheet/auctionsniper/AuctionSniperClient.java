package de.jokingsheet.auctionsniper;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public final class AuctionSniperClient implements ClientModInitializer {
    private static KeyMapping openGuiKey;
    private static KeyMapping toggleKey;
    private static int ticksUntilScan = 0;
    private static boolean enabled = false;

    @Override
    public void onInitializeClient() {
        var category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("auctionsniper", "main"));
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.auctionsniper.open", GLFW.GLFW_KEY_RIGHT_SHIFT, category));
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.auctionsniper.toggle", GLFW.GLFW_KEY_UNKNOWN, category));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKey.consumeClick()) client.setScreen(new AuctionSniperScreen());
            while (toggleKey.consumeClick()) toggle(client);
            if (!enabled || client.player == null || client.level == null) return;
            if (--ticksUntilScan <= 0) {
                // Do not issue a command while another screen is open. This avoids
                // stealing focus from chat, containers, or other menus.
                if (client.screen == null) {
                    client.player.connection.sendCommand("ah");
                    ticksUntilScan = 20 * 20;
                } else {
                    ticksUntilScan = 20;
                }
            }
            AuctionScanner.captureVisibleMenu(client);
        });
    }

    static void toggle(Minecraft client) {
        enabled = !enabled;
        ticksUntilScan = 1;
        if (client.player != null) client.player.displayClientMessage(
            Component.literal("Auction Sniper: " + (enabled ? "§aAN" : "§cAUS")), true);
    }

    static boolean isEnabled() { return enabled; }
    static void setEnabled(boolean value) { enabled = value; ticksUntilScan = 1; }
}
