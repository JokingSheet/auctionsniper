# Auction Sniper

Client-side Fabric mod for Minecraft 1.21.11 / Java 21.

## Build
Run `gradle build` with Java 21. GitHub Actions builds the JAR on every push to `main`; download it from the workflow run's Artifacts section.

## Current prototype
- Right Shift opens the mod GUI.
- GUI toggle enables/disables the 20-second auction-house command polling.
- Captures visible listing item names and stack amounts from the open menu.

## Important limitations
This initial scaffold does **not** yet parse server-specific price tooltips, enchantment lore, spawner entity types, pagination, or price alerts. Those require reading the actual tooltip data exposed by the target server and verifying its menu behavior. It deliberately does not auto-buy or interact with purchase controls.
