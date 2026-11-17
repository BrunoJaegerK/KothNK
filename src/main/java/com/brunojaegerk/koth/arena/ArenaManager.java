package com.brunojaegerk.koth.arena;
import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import java.util.*;

public final class ArenaManager {
 private final Config config,defaults;private final Map<String,Arena> arenas=new LinkedHashMap<>();
 public ArenaManager(Config config,Config defaults){this.config=config;this.defaults=defaults;load();}
 public void load(){arenas.clear();for(String n:config.getSection("arenas").keySet()){Arena a=new Arena(n);a.load(config,"arenas."+n);arenas.put(n.toLowerCase(Locale.ROOT),a);}}
 public Arena get(String n){return n==null?null:arenas.get(n.toLowerCase(Locale.ROOT));}
 public Collection<Arena> all(){return arenas.values();}public int size(){return arenas.size();}
 public boolean create(String n){
  if(n==null||!n.matches("[A-Za-z0-9_-]{1,32}"))throw new IllegalArgumentException("Usa un nombre de arena de 1 a 32 caracteres: letras, números, guion o guion bajo.");
  if(get(n)!=null)return false;Arena a=new Arena(n);
  a.setRadius(defaults.getDouble("event.capture-radius",6));a.setCaptureSeconds(defaults.getInt("event.capture-seconds",30));a.setMaxPlayers(defaults.getInt("event.max-players",50));
  arenas.put(n.toLowerCase(Locale.ROOT),a);a.save(config,"arenas."+a.name());return true;
 }
 public boolean delete(String n){Arena a=get(n);if(a==null)return false;arenas.remove(n.toLowerCase(Locale.ROOT));config.getSection("arenas").remove(a.name());config.save();return true;}
 public void corner(String n,Player p,boolean first){required(n).corner(p,first);save(n);}
 public void center(String n,Player p){required(n).center(p);save(n);}
 public void spawn(String n,Player p){required(n).spawn(p);save(n);}
 public void spectator(String n,Player p){required(n).spectator(p);save(n);}
 public void mode(String n,String v){required(n).setMode(v);save(n);}
 public void capture(String n,String v){required(n).setCaptureMethod(v);save(n);}
 public void time(String n,int v){required(n).setCaptureSeconds(v);save(n);}
 public void radius(String n,double v){required(n).setRadius(v);save(n);}
 public void maxPlayers(String n,int v){required(n).setMaxPlayers(v);save(n);}
 private void save(String n){Arena a=required(n);a.save(config,"arenas."+a.name());}
 private Arena required(String n){Arena a=get(n);if(a==null)throw new IllegalArgumentException("Arena inexistente: "+n);return a;}
}