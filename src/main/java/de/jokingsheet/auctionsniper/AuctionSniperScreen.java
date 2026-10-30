package de.jokingsheet.auctionsniper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
public final class AuctionSniperScreen extends Screen {
 private EditBox filter,max; private boolean ench=false;
 public AuctionSniperScreen(){super(Component.literal("Auction Sniper"));}
 protected void init(){
  int x=width/2;
  ench=AuctionScanner.enchanted();\n  filter=new EditBox(font,x-145,62,140,20,Component.literal("Item"));filter.setValue(AuctionScanner.filter());filter.setHint(Component.literal("z.B. diamond"));
  max=new EditBox(font,x+5,62,140,20,Component.literal("Preis"));max.setValue(AuctionScanner.limitText());max.setHint(Component.literal("Max. Stückpreis"));
  addRenderableWidget(filter);addRenderableWidget(max);
  addRenderableWidget(Button.builder(Component.literal("Alarm: "+(AuctionScanner.alert()?"AN":"AUS")),b->{AuctionScanner.configure(filter.getValue(),max.getValue(),ench,!AuctionScanner.alert());b.setMessage(Component.literal("Alarm: "+(AuctionScanner.alert()?"AN":"AUS")));}).bounds(x-145,91,140,20).build());
  addRenderableWidget(Button.builder(Component.literal("Variante: "+(ench?"MIT Enchants":"OHNE Enchants")),b->{ench=!ench;AuctionScanner.configure(filter.getValue(),max.getValue(),ench,AuctionScanner.alert());b.setMessage(Component.literal("Variante: "+(ench?"MIT Enchants":"OHNE Enchants")));}).bounds(x+5,91,140,20).build());
  addRenderableWidget(Button.builder(Component.literal("Auto-Scan: "+(AuctionSniperClient.isEnabled()?"ON":"OFF")),b->{AuctionSniperClient.setEnabled(!AuctionSniperClient.isEnabled());b.setMessage(Component.literal("Auto-Scan: "+(AuctionSniperClient.isEnabled()?"ON":"OFF")));}).bounds(x-145,119,140,20).build());
  addRenderableWidget(Button.builder(Component.literal("Angebote leeren"),b->AuctionScanner.clear()).bounds(x+5,119,140,20).build());
  addRenderableWidget(Button.builder(Component.literal("Schließen"),b->onClose()).bounds(x-70,height-30,140,20).build());
 }
 public void render(GuiGraphics g,int mx,int my,float d){
  // Avoid Screen.renderBackground(): it applies the global blur and can crash
  // when another screen hook has already applied blur during this frame.
  g.fill(0,0,width,height,0xC0101010);
  super.render(g,mx,my,d);
  g.drawCenteredString(font,title,width/2,20,0xffffff);
  g.drawString(font,"Item-Filter",width/2-145,49,0xaaaaaa);g.drawString(font,"Max. Stückpreis",width/2+5,49,0xaaaaaa);
  g.drawString(font,"Statistik - letzte 10 Minuten (rollierend)",width/2-145,151,0x55ddff);
  int sy=166;for(String s:AuctionScanner.statsView()){if(sy>height/2+12)break;g.drawString(font,s.length()>105?s.substring(0,102)+"...":s,Math.max(8,width/2-220),sy,0xffffff);sy+=12;}
  g.drawString(font,"Alle bisher günstigsten Angebote:",width/2-145,height/2+24,0x55ddff);
  int y=height/2+39;for(String s:AuctionScanner.view()){if(y>height-45)break;g.drawString(font,s.length()>100?s.substring(0,97)+"...":s,Math.max(8,width/2-220),y,0xdddddd);y+=12;}
  if(AuctionScanner.statsView().size()==1)g.drawCenteredString(font,"Noch keine Beobachtungen in den letzten 10 Minuten.",width/2,180,0xaaaaaa);
 }
 public void onClose(){AuctionScanner.configure(filter.getValue(),max.getValue(),ench,AuctionScanner.alert());super.onClose();}\n public boolean isPauseScreen(){return false;}
}