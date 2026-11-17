package com.brunojaegerk.koth.statistics;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import java.util.*;

public final class StatisticsManager {
 private final Config data;private int dirty;
 public StatisticsManager(Config data){this.data=data;}
 private String path(Player p){return "players."+p.getUniqueId()+".";}
 private void touch(Player p){data.set(path(p)+"name",p.getName());persistMaybe();}
 private void increment(Player p,String field){String key=path(p)+field;data.set(key,data.getInt(key,0)+1);touch(p);}
 private void persistMaybe(){if(++dirty>=20){data.save();dirty=0;}}
 public void participation(Player p){increment(p,"participations");}
 public void death(Player p){increment(p,"deaths");}
 public void kill(Player p){increment(p,"kills");}
 public void capture(Player p){increment(p,"captures");}
 public void captureTime(Player p){increment(p,"capture-seconds");}
 public void win(Player p){
  increment(p,"wins");String base=path(p);int streak=data.getInt(base+"win-streak",0)+1;
  data.set(base+"win-streak",streak);data.set(base+"best-streak",Math.max(streak,data.getInt(base+"best-streak",0)));persistMaybe();
 }
 public void loss(Player p){increment(p,"losses");data.set(path(p)+"win-streak",0);persistMaybe();}
 public int streak(Player p){return data.getInt(path(p)+"win-streak",0);}
 public void save(){data.save();dirty=0;}
 public String summary(Player p){
  String b=path(p);
  return "§6KOTH §8| §aW §f"+data.getInt(b+"wins",0)+" §cL §f"+data.getInt(b+"losses",0)
   +" §eCapturas §f"+data.getInt(b+"captures",0)+" §bKills §f"+data.getInt(b+"kills",0)
   +" §7Muertes §f"+data.getInt(b+"deaths",0)+" §dRacha §f"+streak(p)
   +" §3Participaciones §f"+data.getInt(b+"participations",0)
   +" §6Tiempo capturando §f"+data.getInt(b+"capture-seconds",0)+"s";
 }
 public List<String> top(int max){
  List<Map.Entry<String,Integer>> rows=new ArrayList<>();
  for(String id:data.getSection("players").keySet())rows.add(Map.entry(data.getString("players."+id+".name",id),data.getInt("players."+id+".wins",0)));
  rows.sort((a,b)->Integer.compare(b.getValue(),a.getValue()));
  List<String> out=new ArrayList<>();for(int i=0;i<Math.min(max,rows.size());i++)out.add((i+1)+". "+rows.get(i).getKey()+" - "+rows.get(i).getValue()+" victorias");return out;
 }
}