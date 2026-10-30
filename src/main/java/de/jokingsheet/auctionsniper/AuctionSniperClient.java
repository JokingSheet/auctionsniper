package de.jokingsheet.auctionsniper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
public final class AuctionSniperClient implements ClientModInitializer {
 private static KeyMapping open,toggle; private static boolean enabled=false,waiting=false; private static int waitTicks=0,nextScan=0;
 public void onInitializeClient(){
  var cat=KeyMapping.Category.register(Identifier.fromNamespaceAndPath("auctionsniper","main"));
  open=KeyBindingHelper.registerKeyBinding(new KeyMapping("key.auctionsniper.open",GLFW.GLFW_KEY_RIGHT_SHIFT,cat));
  toggle=KeyBindingHelper.registerKeyBinding(new KeyMapping("key.auctionsniper.toggle",GLFW.GLFW_KEY_UNKNOWN,cat));
  ClientTickEvents.END_CLIENT_TICK.register(mc->{
   while(open.consumeClick())mc.setScreen(new AuctionSniperScreen());
   while(toggle.consumeClick())setEnabled(!enabled,mc);
   if(!enabled||mc.player==null||mc.level==null)return;
   if(waiting){
    waitTicks++;
    if(mc.screen!=null&&!(mc.screen instanceof AuctionSniperScreen)){
     AuctionScanner.captureVisibleMenu(mc);
     if(waitTicks>=50){mc.setScreen(null);waiting=false;nextScan=20*20;}
    }else if(waitTicks>100){waiting=false;nextScan=20*20;}
    return;
   }
   if(nextScan>0)nextScan--;
   if(nextScan<=0&&mc.screen==null){mc.player.connection.sendCommand("ah");waiting=true;waitTicks=0;}
   if(mc.screen!=null)AuctionScanner.captureVisibleMenu(mc);
  });
 }
 private static void setEnabled(boolean v,Minecraft mc){enabled=v;nextScan=0;if(!v)waiting=false;if(mc.player!=null)mc.player.displayClientMessage(Component.literal("Auction Sniper: "+(v?"§aAN":"§cAUS")),true);}
 static boolean isEnabled(){return enabled;}
 static void setEnabled(boolean v){Minecraft mc=Minecraft.getInstance();setEnabled(v,mc);}
}