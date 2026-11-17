package com.brunojaegerk.koth.arena;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import java.util.*;

public final class ArenaManager {
    private final Config config;private final Map<String,Arena> arenas=new LinkedHashMap<>();
    public ArenaManager(Config c){config=c;load();}
    public void load(){arenas.clear();for(String n:config.getSection("arenas").keySet()){Arena a=new Arena(n);a.load(config,"arenas."+n);arenas.put(n.toLowerCase(Locale.ROOT),a);}}
    public Arena get(String n){return n==null?null:arenas.get(n.toLowerCase(Locale.ROOT));}
    public Collection<Arena> all(){return arenas.values();}public int size(){return arenas.size();}
    public boolean create(String n){if(get(n)!=null)return false;Arena a=new Arena(n);arenas.put(n.toLowerCase(Locale.ROOT),a);a.save(config,"arenas."+n);return true;}
    public boolean delete(String n){if(get(n)==null)return false;arenas.remove(n.toLowerCase(Locale.ROOT));config.getSection("arenas").remove(n);config.save();return true;}
    public void corner(String n,Player p,boolean first){required(n).corner(p,first);save(n);}
    public void center(String n,Player p){required(n).center(p);save(n);}
    public void spawn(String n,Player p){required(n).spawn(p);save(n);}
    public void spectator(String n,Player p){required(n).spectator(p);save(n);}
    public void mode(String n,String v){required(n).setMode(v);save(n);}
    public void capture(String n,String v){required(n).setCaptureMethod(v);save(n);}
    public void time(String n,int v){required(n).setCaptureSeconds(v);save(n);}
    public void radius(String n,double v){required(n).setRadius(v);save(n);}
    public void maxPlayers(String n,int v){required(n).setMaxPlayers(v);save(n);}
    private void save(String n){required(n).save(config,"arenas."+n);}
    private Arena required(String n){Arena a=get(n);if(a==null)throw new IllegalArgumentException("Arena inexistente: "+n);return a;}
}
