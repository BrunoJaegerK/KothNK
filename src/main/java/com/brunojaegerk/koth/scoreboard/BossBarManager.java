package com.brunojaegerk.koth.scoreboard;

import cn.nukkit.Player;
import com.brunojaegerk.koth.KingOfTheHillNK;
import java.util.*;

/** Administra BossBars nativas de PM1E y sus IDs por arena/jugador. */
public final class BossBarManager {
    private final KingOfTheHillNK plugin;
    private final Map<String,Map<UUID,Long>> ids=new HashMap<>();
    public BossBarManager(KingOfTheHillNK plugin){this.plugin=plugin;}
    public void update(String arena,Player player,String title,int percent){
        Map<UUID,Long> arenaBars=ids.computeIfAbsent(key(arena),k->new HashMap<>());
        Long id=arenaBars.get(player.getUniqueId());
        if(id==null){id=player.createBossBar(title,Math.max(0,Math.min(100,percent)));arenaBars.put(player.getUniqueId(),id);}
        else player.updateBossBar(title,Math.max(0,Math.min(100,percent)),id);
    }
    public void remove(String arena,Player player){
        Map<UUID,Long> arenaBars=ids.get(key(arena));if(arenaBars==null)return;
        Long id=arenaBars.remove(player.getUniqueId());if(id!=null)player.removeBossBar(id);
        if(arenaBars.isEmpty())ids.remove(key(arena));
    }
    public void forget(String arena,UUID player){Map<UUID,Long> map=ids.get(key(arena));if(map!=null){map.remove(player);if(map.isEmpty())ids.remove(key(arena));}}
    public void clear(){for(Player p:plugin.getServer().getOnlinePlayersList())for(Map.Entry<String,Map<UUID,Long>> e:ids.entrySet()){Long id=e.getValue().get(p.getUniqueId());if(id!=null)p.removeBossBar(id);}ids.clear();}
    private String key(String s){return s.toLowerCase(Locale.ROOT);}
}
