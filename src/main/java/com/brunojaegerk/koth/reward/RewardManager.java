package com.brunojaegerk.koth.reward;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Config;
import com.brunojaegerk.koth.KingOfTheHillNK;
import java.util.*;

public final class RewardManager {
 private final KingOfTheHillNK plugin;private final Config config;
 public RewardManager(KingOfTheHillNK plugin,Config config){this.plugin=plugin;this.config=config;}
 public void participation(Player p,String arena){give(p,"participation",arena);}
 public void winner(Player p,String arena,boolean team){give(p,team?"team-winner":"winner",arena);}
 public void kill(Player p,String arena){give(p,"kill",arena);}
 private void give(Player p,String kind,String arena){
  String specific="arenas."+arena+"."+kind,fallback="default."+kind;
  String path=config.getSection(specific).isEmpty()?fallback:specific;
  Object commands=config.get(path+".commands",List.of());
  if(commands instanceof List<?> list)for(Object raw:list){
   String command=String.valueOf(raw).replace("{player}",p.getName()).replace("{uuid}",p.getUniqueId().toString())
    .replace("{arena}",arena).replace("{team}","").replace("{position}","1");
   plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),command.replace('&','§'));
  }
  Object items=config.get(path+".items",List.of());
  if(items instanceof List<?> list)for(Object raw:list)if(raw instanceof Map<?,?> map){
   int id=number(map.get("id"),0),meta=number(map.get("meta"),0),amount=number(map.get("amount"),1);
   if(id>0&&amount>0){Item[] leftovers=p.getInventory().addItem(Item.get(id,meta,amount));for(Item leftover:leftovers)if(p.getLevel()!=null)p.getLevel().dropItem(p.getPosition(),leftover);}
  }
 }
 private int number(Object o,int fallback){return o instanceof Number?((Number)o).intValue():fallback;}
}