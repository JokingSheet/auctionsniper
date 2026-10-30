package de.jokingsheet.auctionsniper;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

/**
 * Reads only client-visible menu stacks. Server-specific prices and lore are
 * intentionally not guessed: the parser is extended once the exact tooltip
 * format and menu pagination behavior are verified.
 */
final class AuctionScanner {
    record Listing(String itemName, int amount, String detail) {}
    private static final List<Listing> LISTINGS = new ArrayList<>();

    static void captureVisibleMenu(Minecraft client) {
        if (client.player == null || client.screen == null) return;
        var menu = client.player.containerMenu;
        if (menu == client.player.inventoryMenu) return;
        LISTINGS.clear();
        int end = Math.max(0, menu.slots.size() - 36);
        for (int i = 0; i < end; i++) {
            ItemStack stack = menu.slots.get(i).getItem();
            if (stack.isEmpty()) continue;
            LISTINGS.add(new Listing(stack.getHoverName().getString(), stack.getCount(), stack.getItem().toString()));
        }
    }

    static List<Listing> listings() { return List.copyOf(LISTINGS); }
}
