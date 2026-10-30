# Auction Sniper

Fabric Client-Mod für Minecraft 1.21.11 / Java 21.

- Rechts-Shift öffnet das Menü.
- Auto-Scan sendet /ah, liest erkannte Auktions-Menüs aus und schließt nur diese nach 50 Ticks. Unbekannte Menüs bleiben geöffnet. Danach wartet er 20 Sekunden.
- Preise werden aus Item-Lore-Zeilen mit Price, Preis, Kosten oder Buy Now gelesen.
- k, m und b werden als Tausend, Million und Milliarde ausgewertet.
- Stückpreis = Gesamtpreis / Stackgröße.
- Vergleichsgruppen trennen Item-ID, erkannte Verzauberungs-Lore und Spawner-Typ; angezeigt wird je Gruppe das günstigste Angebot.
- Preisalarm mit Itemfilter, maximalem Stückpreis und Auswahl MIT/OHNE erkannte Enchantments; Meldung im Chat, ohne Spam für dasselbe Listing.
- Kein Auto-Buy und keine Klicks auf Kaufbuttons.

## Grenzen
Es werden nur Item-Lore-Daten ausgewertet, die der Server tatsächlich an den Client überträgt. Server-spezifische Tooltip-Komponenten außerhalb der Lore können fehlen. Enchantments werden anhand üblicher englischer und deutscher Lore-Bezeichnungen erkannt. Ein Spawner-Typ wird nur getrennt, wenn eine konkrete Lore-Zeile wie Spawner Type: zombie oder Mob: zombie existiert; allgemeines Type: mob reicht nicht. Es wird nur die sichtbare Seite erfasst; automatische Seitennavigation ist nicht aktiviert. Ein erfolgreicher Build ist kein Ingame-Test.
