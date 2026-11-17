package com.brunojaegerk.koth.team;

import cn.nukkit.Player;
import java.util.*;

public final class TeamManager {
    private final Map<String,Map<UUID,String>> assigned=new HashMap<>();
    public void assign(String arena,Player player,String team){assigned.computeIfAbsent(key(arena),k->new HashMap<>()).put(player.getUniqueId(),team.toLowerCase(Locale.ROOT));}
    public String group(String arena,boolean teams,Player player){
        if(!teams)return "player:"+player.getUniqueId();
        String team=assigned.getOrDefault(key(arena),Map.of()).get(player.getUniqueId());
        return team==null?"solo:"+player.getUniqueId():"team:"+team;
    }
    private String key(String s){return s.toLowerCase(Locale.ROOT);}
}
