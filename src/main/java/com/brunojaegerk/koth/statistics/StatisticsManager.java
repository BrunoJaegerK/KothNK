package com.brunojaegerk.koth.statistics;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import java.util.*;

public final class StatisticsManager {
    private final Config data;
    public StatisticsManager(Config data){this.data=data;}
    private void add(Player p,String field){String path="players."+p.getUniqueId()+"."+field;data.set(path,data.getInt(path,0)+1);data.set("players."+p.getUniqueId()+".name",p.getName());data.save();}
    public void death(Player p){add(p,"deaths");} public void kill(Player p){add(p,"kills");}
    public void win(Player p){add(p,"wins");} public void loss(Player p){add(p,"losses");} public void capture(Player p){add(p,"captures");}
    public void save(){data.save();}
    public String summary(Player p){String path="players."+p.getUniqueId()+".";return "§6KOTH §8| §aVictorias §f"+data.getInt(path+"wins",0)+" §cDerrotas §f"+data.getInt(path+"losses",0)+" §eCapturas §f"+data.getInt(path+"captures",0)+" §bKills §f"+data.getInt(path+"kills",0)+" §7Muertes §f"+data.getInt(path+"deaths",0);}
    public List<String> top(int max){
        List<Map.Entry<String,Integer>> rows=new ArrayList<>();
        for(String id:data.getSection("players").keySet())rows.add(Map.entry(data.getString("players."+id+".name",id),data.getInt("players."+id+".wins",0)));
        rows.sort((a,b)->Integer.compare(b.getValue(),a.getValue()));
        List<String> out=new ArrayList<>();for(int i=0;i<Math.min(max,rows.size());i++)out.add((i+1)+". "+rows.get(i).getKey()+" - "+rows.get(i).getValue()+" victorias");return out;
    }
}
