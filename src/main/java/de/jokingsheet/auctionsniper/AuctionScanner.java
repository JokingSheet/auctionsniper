package de.jokingsheet.auctionsniper;

import java.math.*;
import java.util.*;
import java.util.regex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;

final class AuctionScanner {
 record Listing(String id,String name,int amount,BigDecimal total,BigDecimal unit,String enchants,String spawner) {
  String key(){return id+"|"+enchants+"|"+spawner;}
  String price(){return total==null?"Preis unbekannt":"$"+total.stripTrailingZeros().toPlainString()+" ("+unit.stripTrailingZeros().toPlainString()+"/Stk.)";}
 }
 private static final Map<String,Listing> BEST=new LinkedHashMap<>();
 private static final List<String> VIEW=new ArrayList<>();
 private static final Set<String> ALERTED=new HashSet<>();
 private static final Pattern PRICE=Pattern.compile("(?i)(?:price|preis|kosten|buy\\s*now)\\s*[:=]?\\s*\\$?\\s*([0-9][0-9.,]*\\s*[kmb]?)");
 private static final Pattern SPAWNER=Pattern.compile("(?i)(?:spawner\\s*type|mob|entity)\\s*[:=]\\s*(.+)");
 private static final Pattern ENCHANT=Pattern.compile("(?i).*(?:protection|sharpness|efficiency|unbreaking|fortune|mending|silk touch|power|feather falling|thorns|schutz|schärfe|schaerfe|effizienz|haltbarkeit|glück|glueck|reparatur|behutsamkeit|stärke|staerke|federfall|dornen).*");
 private static String filter="";
 private static BigDecimal limit;
 private static boolean enchanted,alert;

 static void captureVisibleMenu(Minecraft mc){
  if(mc.player==null||mc.screen==null||mc.player.containerMenu==mc.player.inventoryMenu)return;
  if(!isAuctionScreen(mc.screen.getTitle().getString()))return;
  var menu=mc.player.containerMenu;
  for(int i=0;i<Math.max(0,menu.slots.size()-36);i++){
   ItemStack s=menu.slots.get(i).getItem(); if(s.isEmpty())continue;
   Listing l=parse(s); if(l==null)continue;
   Listing old=BEST.get(l.key());
   if(old==null||(l.unit()!=null&&(old.unit()==null||l.unit().compareTo(old.unit())<0))) {BEST.put(l.key(),l); rebuild();}
   checkAlert(mc,l);
  }
 }
 static boolean isAuctionScreen(String title){
  String t=title==null?"":title.toLowerCase(Locale.ROOT);
  return t.matches(".*(?:auction|ah|market|markt|auktion|börse|boerse).*");
 }
 private static Listing parse(ItemStack s){
  List<String> lore=new ArrayList<>(); ItemLore data=s.get(DataComponents.LORE);
  if(data!=null)for(Component c:data.lines())lore.add(c.getString().trim());
  String ench=lore.stream().filter(x->ENCHANT.matcher(x).matches())
    .sorted(String.CASE_INSENSITIVE_ORDER).reduce((a,b)->a+";"+b).orElse("");
  String spawner="";
  for(String x:lore){Matcher m=SPAWNER.matcher(x);
   if(m.find()&&!m.group(1).trim().equalsIgnoreCase("mob"))spawner=m.group(1).trim().toLowerCase(Locale.ROOT);}
  BigDecimal total=null;
  for(String x:lore){Matcher m=PRICE.matcher(x);if(m.find()){total=amount(m.group(1));if(total!=null)break;}}
  int count=Math.max(1,s.getCount());
  BigDecimal unit=total==null?null:total.divide(BigDecimal.valueOf(count),2,RoundingMode.HALF_UP);
  return new Listing(BuiltInRegistries.ITEM.getKey(s.getItem()).toString(),s.getHoverName().getString(),count,total,unit,ench,spawner);
 }
 private static BigDecimal amount(String raw){
  Matcher m=Pattern.compile("(?i)^([0-9][0-9.,]*)([kmb]?)$").matcher(raw.replace(" ",""));
  if(!m.matches())return null;
  String n=m.group(1);
  try{
   BigDecimal v;
   if(n.contains(",")&&n.contains("."))v=new BigDecimal(n.replace(",",""));
   else if(n.matches("[0-9]{1,3}(,[0-9]{3})+"))v=new BigDecimal(n.replace(",",""));
   else if(n.matches("[0-9]{1,3}(\\.[0-9]{3})+"))v=new BigDecimal(n.replace(".",""));
   else v=new BigDecimal(n.replace(",","."));
   return v.multiply(switch(m.group(2).toLowerCase(Locale.ROOT)){case "k"->BigDecimal.valueOf(1_000);case "m"->BigDecimal.valueOf(1_000_000);case "b"->BigDecimal.valueOf(1_000_000_000);default->BigDecimal.ONE;});
  }catch(NumberFormatException e){return null;}
 }
 private static void checkAlert(Minecraft mc,Listing l){
  if(!alert||limit==null||l.unit()==null)return;
  if(!filter.isBlank()&&!l.name().toLowerCase(Locale.ROOT).contains(filter.toLowerCase(Locale.ROOT))&&!l.id().toLowerCase(Locale.ROOT).contains(filter.toLowerCase(Locale.ROOT)))return;
  if(enchanted!=!l.enchants().isBlank()||l.unit().compareTo(limit)>=0)return;
  String key=l.key()+"|"+l.total()+"|"+l.amount();
  if(ALERTED.add(key))mc.player.displayClientMessage(Component.literal("§6[Auction Sniper] §a"+l.name()+" x"+l.amount()+" §7"+l.price()),false);
 }
 static void configure(String f,String max,boolean e,boolean a){
  filter=f==null?"":f.trim();enchanted=e;alert=a;
  try{limit=max==null||max.isBlank()?null:new BigDecimal(max.replace(",","."));}catch(Exception ex){limit=null;}
  ALERTED.clear();
 }
 static boolean alert(){return alert;} static int count(){return BEST.size();}
 static List<String> view(){return List.copyOf(VIEW);}
 static void clear(){BEST.clear();VIEW.clear();ALERTED.clear();}
 private static void rebuild(){
  VIEW.clear();BEST.values().stream().sorted(Comparator.comparing(Listing::unit,Comparator.nullsLast(Comparator.naturalOrder())))
   .forEach(l->VIEW.add(l.name()+" x"+l.amount()+" | "+l.price()+(l.enchants().isBlank()?" | ohne Enchants":" | "+l.enchants())+(l.spawner().isBlank()?"":" | Spawner: "+l.spawner())));
 }
}
