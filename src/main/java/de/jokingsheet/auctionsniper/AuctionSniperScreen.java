package de.jokingsheet.auctionsniper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import java.util.*;
public final class AuctionSniperScreen extends Screen {
 private EditBox filter,max; private boolean ench=false;
 private List<String> suggestions=List.of();
 private int suggestionX,suggestionY;
 public AuctionSniperScreen(){super(Component.literal("Auction Sniper"));}
 protected void init(){
  int x=width/2;
  ench=AuctionScanner.enchanted();
  filter=new EditBox(font,x-145,62,140,20,Component.literal("Item"));filter.setValue(AuctionScanner.filter());filter.setHint(Component.literal("z.B. diamond"));
  max=new EditBox(font,x+5,62,140,20,Component.literal("Preis"));max.setValue(AuctionScanner.limitText());max.setHint(Component.literal("Max. Stückpreis"));
  addRenderableWidget(filter);addRenderableWidget(max);
  suggestionX=x-145;suggestionY=84;
  addRenderableWidget(Button.builder(Component.literal("Alarm: "+(AuctionScanner.alert()?"AN":"AUS")),b->{AuctionScanner.configure(filter.getValue(),max.getValue(),ench,!AuctionScanner.alert());b.setMessage(Component.literal("Alarm: "+(AuctionScanner.alert()?"AN":"AUS")));}).bounds(x-145,151,140,20).build());
  addRenderableWidget(Button.builder(Component.literal("Variante: "+(ench?"MIT Enchants":"OHNE Enchants")),b->{ench=!ench;AuctionScanner.configure(filter.getValue(),max.getValue(),ench,AuctionScanner.alert());b.setMessage(Component.literal("Variante: "+(ench?"MIT Enchants":"OHNE Enchants")));}).bounds(x+5,151,140,20).build());
  addRenderableWidget(Button.builder(Component.literal("Auto-Scan: "+(AuctionSniperClient.isEnabled()?"ON":"OFF")),b->{AuctionSniperClient.setEnabled(!AuctionSniperClient.isEnabled());b.setMessage(Component.literal("Auto-Scan: "+(AuctionSniperClient.isEnabled()?"ON":"OFF")));}).bounds(x-145,179,140,20).build());
  addRenderableWidget(Button.builder(Component.literal("Angebote leeren"),b->AuctionScanner.clear()).bounds(x+5,179,140,20).build());
  addRenderableWidget(Button.builder(Component.literal("Schließen"),b->onClose()).bounds(x-70,height-30,140,20).build());
 }
 public void render(GuiGraphics g,int mx,int my,float d){
  // Avoid Screen.renderBackground(): it applies the global blur and can crash
  // when another screen hook has already applied blur during this frame.
  g.fill(0,0,width,height,0xC0101010);
  super.render(g,mx,my,d);
  g.drawCenteredString(font,title,width/2,20,0xffffff);
  g.drawString(font,"Item-Filter",width/2-145,49,0xaaaaaa);g.drawString(font,"Max. Stückpreis",width/2+5,49,0xaaaaaa);
  suggestions=findSuggestions(filter.getValue());
  if(!suggestions.isEmpty()){
   for(int i=0;i<suggestions.size();i++){
    int yy=suggestionY+i*14;
    g.fill(suggestionX,yy,suggestionX+140,yy+14,0xF0202020);
    g.drawString(font,suggestions.get(i),suggestionX+4,yy+3,0xffffff);
   }
  }
  g.drawString(font,"Statistik - letzte 10 Minuten (rollierend)",width/2-145,211,0x55ddff);
  int sy=226;for(String s:AuctionScanner.statsView()){if(sy>height/2+12)break;g.drawString(font,s.length()>105?s.substring(0,102)+"...":s,Math.max(8,width/2-220),sy,0xffffff);sy+=12;}
  g.drawString(font,"Alle bisher günstigsten Angebote:",width/2-145,height/2+24,0x55ddff);
  int y=height/2+39;for(String s:AuctionScanner.view()){if(y>height-45)break;g.drawString(font,s.length()>100?s.substring(0,97)+"...":s,Math.max(8,width/2-220),y,0xdddddd);y+=12;}
  if(AuctionScanner.statsView().size()==1)g.drawCenteredString(font,"Noch keine Beobachtungen in den letzten 10 Minuten.",width/2,180,0xaaaaaa);
 }
 public void onClose(){AuctionScanner.configure(filter.getValue(),max.getValue(),ench,AuctionScanner.alert());super.onClose();}
 private List<String> findSuggestions(String typed){
  if(typed==null||typed.trim().length()<2)return List.of();
  String q=typed.trim().toLowerCase(Locale.ROOT);
  List<String> starts=new ArrayList<>(),contains=new ArrayList<>();
  for(Item item:BuiltInRegistries.ITEM){
   String name=new ItemStack(item).getHoverName().getString();
   String key=BuiltInRegistries.ITEM.getKey(item).getPath().replace('_',' ');
   String n=name.toLowerCase(Locale.ROOT);
   if(n.startsWith(q)||key.startsWith(q))starts.add(name);
   else if(n.contains(q)||key.contains(q))contains.add(name);
  }
  starts.addAll(contains);
  return starts.stream().distinct().limit(5).toList();
 }
 @Override
 public boolean mouseClicked(double mx,double my,int button){
  if(button==0&&!suggestions.isEmpty()){
   for(int i=0;i<suggestions.size();i++){
    int yy=suggestionY+i*14;
    if(mx>=suggestionX&&mx<=suggestionX+140&&my>=yy&&my<=yy+14){
     filter.setValue(suggestions.get(i));
     AuctionScanner.configure(filter.getValue(),max.getValue(),ench,AuctionScanner.alert());
     suggestions=List.of();
     return true;
    }
   }
  }
  return super.mouseClicked(mx,my,button);
 }
 public boolean isPauseScreen(){return false;}
}