package com.brunojaegerk.koth.event;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.Particle;
import com.brunojaegerk.koth.KingOfTheHillNK;
import com.brunojaegerk.koth.arena.Arena;
import com.brunojaegerk.koth.arena.ArenaManager;
import com.brunojaegerk.koth.effect.CaptureEffects;
import com.brunojaegerk.koth.reward.RewardManager;
import com.brunojaegerk.koth.scoreboard.BossBarManager;
import com.brunojaegerk.koth.statistics.StatisticsManager;
import com.brunojaegerk.koth.team.TeamManager;
import java.util.*;

public final class KothEventManager {
 private final KingOfTheHillNK plugin;private final ArenaManager arenas;private final TeamManager teams;
 private final StatisticsManager stats;private final RewardManager rewards;private final BossBarManager bars;private final CaptureEffects effects;
 private final Map<String,KothEvent> active=new LinkedHashMap<>();private final Map<UUID,UUID> lastDamager=new HashMap<>();private final Set<UUID> teleportBypass=new HashSet<>();
 public KothEventManager(KingOfTheHillNK p,ArenaManager a,TeamManager t,StatisticsManager s,RewardManager r){plugin=p;arenas=a;teams=t;stats=s;rewards=r;bars=new BossBarManager(p);effects=new CaptureEffects(p);}
 private String key(String s){return s.toLowerCase(Locale.ROOT);} public boolean running(String n){return active.containsKey(key(n));}
 public boolean start(String name){
  Arena a=arenas.get(name);if(a==null||running(name)||!a.ready()||plugin.getServer().getLevelByName(a.world())==null)return false;
  int delay=plugin.getConfig().getInt("event.countdown-seconds",60);active.put(key(name),new KothEvent(a,delay));effects.sound(a,Sound.BLOCK_BEACON_ACTIVATE);
  plugin.getServer().broadcastMessage(plugin.message("event-starting").replace("{arena}",name).replace("{seconds}",String.valueOf(delay)));return true;
 }
 public boolean stop(String name){KothEvent e=active.remove(key(name));if(e==null)return false;e.state(KothState.ENDING);restore(e);plugin.getServer().broadcastMessage(plugin.message("event-stopped").replace("{arena}",name));return true;}
 public boolean join(String name,Player p){
  KothEvent e=active.get(key(name));if(e==null||e.participants().size()>=e.arena().maxPlayers())return false;
  for(KothEvent other:active.values())if(other.contains(p.getUniqueId()))return false;
  e.add(p);teleportToArena(p,e.arena(),false);p.sendMessage(plugin.message("joined").replace("{arena}",name));
  if(plugin.getConfig().getBoolean("event.show-bossbar",true))bars.update(name,p,"§6KOTH §f"+name+" §7Preparando",0);return true;
 }
 public boolean spectate(String name,Player p){
  KothEvent e=active.get(key(name));if(e==null)return false;for(KothEvent other:active.values())if(other.contains(p.getUniqueId()))return false;
  e.addSpectator(p);teleportToArena(p,e.arena(),true);p.setGamemode(Player.SPECTATOR);return true;
 }
 public void leave(Player p){for(KothEvent e:active.values())if(e.participants().remove(p.getUniqueId())||e.spectators().remove(p.getUniqueId())){restoreOne(e,p);p.sendMessage(plugin.message("left"));return;}}
 private void safeTeleport(Player p,Position target){if(target==null||target.getLevel()==null)return;teleportBypass.add(p.getUniqueId());if(!p.teleport(target))teleportBypass.remove(p.getUniqueId());}
 private void teleportToArena(Player p,Arena a,boolean spectator){
  Level level=plugin.getServer().getLevelByName(a.world());if(level==null)return;
  if(spectator&&a.hasSpectator())safeTeleport(p,new Position(a.specX(),a.specY(),a.specZ(),level));
  else if(!spectator&&a.hasSpawn())safeTeleport(p,new Position(a.spawnX(),a.spawnY(),a.spawnZ(),level));
  else if(a.hasCenter())safeTeleport(p,new Position(a.centerX(),a.centerY()+1,a.centerZ(),level));
 }
 public void teleport(PlayerTeleportEvent event){
  UUID id=event.getPlayer().getUniqueId();if(teleportBypass.remove(id))return;
  if(!plugin.getConfig().getBoolean("event.protect-teleports",true)||event.getPlayer().getHealth()<=0)return;
  KothEvent e=find(id);if(e==null||event.getTo()==null)return;
  if(!e.arena().insideRegion(event.getTo().getLevel(),event.getTo().getX(),event.getTo().getY(),event.getTo().getZ()))event.setCancelled();
 }
 /** Tick centralizado a un segundo: evita tareas independientes por jugador. */
 public void tick(){
  Iterator<KothEvent> it=active.values().iterator();
  while(it.hasNext()){
   KothEvent e=it.next();Arena a=e.arena();
   if(e.state()==KothState.COUNTDOWN){
    if(e.countdown()>0){if(e.countdown()<=10||e.countdown()%15==0)plugin.getServer().broadcastMessage("§eKOTH "+a.name()+" inicia en "+e.countdown()+"s");e.countdown(e.countdown()-1);}
    else{e.state(KothState.AVAILABLE);plugin.getServer().broadcastMessage(plugin.message("event-started").replace("{arena}",a.name()));}continue;
   }
   if(e.state()==KothState.ENDING)continue;e.tickElapsed();
   if(e.elapsed()>=plugin.getConfig().getInt("event.max-duration-seconds",1800)){it.remove();for(UUID id:e.participants()){Player p=online(id);if(p!=null)rewards.participation(p,a.name());}restore(e);continue;}
   Map<String,List<Player>> groups=eligible(e);int count=groups.values().stream().mapToInt(List::size).sum();
   if(count==0){e.state(KothState.AVAILABLE);e.owner(null);if(plugin.getConfig().getBoolean("event.reset-progress-when-empty",false))e.progress(0);hud(e,"§7Esperando jugadores",0);continue;}
   String owner=choose(e,groups);
   if(owner==null){
    if(groups.size()>1){if(e.state()!=KothState.CONTESTED)effects.sound(a,Sound.BLOCK_BELL_HIT);e.state(KothState.CONTESTED);effects.ring(a,Particle.TYPE_CRITICAL);hud(e,"§c¡DISPUTADO! §7Resuelve el combate",e.progress()*100/Math.max(1,a.captureSeconds()));}
    else{e.state(KothState.AVAILABLE);hud(e,"§eCumple la condición de captura",e.progress()*100/Math.max(1,a.captureSeconds()));}
    continue;
   }
   if(e.state()==KothState.CONTESTED)effects.sound(a,Sound.BLOCK_BEACON_POWER);
   if(!owner.equals(e.owner())){e.owner(owner);e.progress(0);}
   e.state(KothState.CAPTURING);e.progress(e.progress()+1);int percent=Math.min(100,e.progress()*100/Math.max(1,a.captureSeconds()));
   effects.ring(a,Particle.TYPE_END_ROD);hud(e,"§aCaptura §f"+percent+"% §8("+e.progress()+"/"+a.captureSeconds()+"s)",percent);
   if(e.progress()>=a.captureSeconds()){finish(e,groups.get(owner));it.remove();}
  }
 }
 private Map<String,List<Player>> eligible(KothEvent e){
  Map<String,List<Player>> groups=new LinkedHashMap<>();boolean team="teams".equalsIgnoreCase(e.arena().mode());
  for(Player p:plugin.getServer().getOnlinePlayersList())if(e.participants().contains(p.getUniqueId())&&p.getHealth()>0&&e.arena().inside(p)){
   if(team&&plugin.getConfig().getBoolean("teams.require-assignment",false)&&!teams.isAssigned(e.arena().name(),p))continue;
   groups.computeIfAbsent(teams.group(e.arena().name(),team,p),k->new ArrayList<>()).add(p);
  }return groups;
 }
 /** Un grupo enemigo siempre disputa; majority exige que más de la mitad del grupo esté en el punto. */
 private String choose(KothEvent e,Map<String,List<Player>> groups){
  if(groups.size()!=1)return null;Map.Entry<String,List<Player>> only=groups.entrySet().iterator().next();
  if("individual".equalsIgnoreCase(e.arena().captureMethod()))return only.getValue().size()==1?only.getKey():null;
  boolean team="teams".equalsIgnoreCase(e.arena().mode());int roster=0;
  for(Player p:plugin.getServer().getOnlinePlayersList())if(e.participants().contains(p.getUniqueId())&&p.getHealth()>0&&teams.group(e.arena().name(),team,p).equals(only.getKey()))roster++;
  return only.getValue().size()>roster/2?only.getKey():null;
 }
 private void finish(KothEvent e,List<Player> winners){
  e.state(KothState.CAPTURED);Set<UUID> ids=new HashSet<>();boolean team="teams".equalsIgnoreCase(e.arena().mode());
  for(Player p:winners){ids.add(p.getUniqueId());stats.win(p);stats.capture(p);rewards.winner(p,e.arena().name(),team);}
  for(UUID id:e.participants()){Player p=online(id);if(p!=null&&!ids.contains(id)){stats.loss(p);rewards.participation(p,e.arena().name());}}
  effects.sound(e.arena(),Sound.RANDOM_LEVELUP);effects.ring(e.arena(),Particle.TYPE_VILLAGER_HAPPY);
  String names=String.join(", ",winners.stream().map(Player::getName).toList());
  plugin.getServer().broadcastMessage(plugin.message("winner").replace("{player}",names).replace("{arena}",e.arena().name()));restore(e);
 }
 private void hud(KothEvent e,String text,int percent){
  for(UUID id:e.participants()){Player p=online(id);if(p==null)continue;
   if(plugin.getConfig().getBoolean("event.show-actionbar",true))p.sendPopup("§6KOTH §f"+e.arena().name()+" §8| "+text);
   if(plugin.getConfig().getBoolean("event.show-bossbar",true))bars.update(e.arena().name(),p,"§6KOTH §f"+e.arena().name()+" §8| "+text,percent);
  }
 }
 public void damage(EntityDamageByEntityEvent event){
  if(!(event.getEntity() instanceof Player victim))return;Player attacker=event.getDamager() instanceof Player?(Player)event.getDamager():null;
  KothEvent ve=find(victim.getUniqueId()),ae=attacker==null?null:find(attacker.getUniqueId());if(ve==null&&ae==null)return;
  if(ve!=null&&ve.spectators().contains(victim.getUniqueId())){event.setCancelled();return;}
  if(ae!=null&&attacker!=null&&ae.spectators().contains(attacker.getUniqueId())){event.setCancelled();return;}
  if(ve!=null&&ve==ae&&attacker!=null){
   if(!ve.arena().inside(victim)||!ve.arena().inside(attacker)){event.setCancelled();return;}
   boolean team="teams".equalsIgnoreCase(ve.arena().mode());
   if(teams.group(ve.arena().name(),team,victim).equals(teams.group(ve.arena().name(),team,attacker))&&!plugin.getConfig().getBoolean("event.friendly-fire",false)){event.setCancelled();return;}
   lastDamager.put(victim.getUniqueId(),attacker.getUniqueId());
  }else if(ve!=ae)event.setCancelled();
 }
 public void death(PlayerDeathEvent event){Player victim=event.getEntity();stats.death(victim);UUID id=lastDamager.remove(victim.getUniqueId());Player killer=id==null?null:online(id);if(killer!=null)stats.kill(killer);}
 public void quit(Player p){lastDamager.remove(p.getUniqueId());teleportBypass.remove(p.getUniqueId());for(KothEvent e:active.values()){e.participants().remove(p.getUniqueId());e.spectators().remove(p.getUniqueId());e.returnPositions().remove(p.getUniqueId());e.previousGamemodes().remove(p.getUniqueId());bars.forget(e.arena().name(),p.getUniqueId());}}
 public void protectBlock(Block b,Cancellable event){if(!plugin.getConfig().getBoolean("event.protect-region",true))return;for(KothEvent e:active.values())if(e.arena().containsBlock(b)){event.setCancelled();return;}}
 public void protectFlow(Block from,Block to,Cancellable event){if(plugin.getConfig().getBoolean("event.protect-liquids",true)&&(isProtected(from)||isProtected(to)))event.setCancelled();}
 public void protectExplosion(Position pos,Cancellable event){if(plugin.getConfig().getBoolean("event.protect-explosions",true))for(KothEvent e:active.values())if(e.arena().insideRegion(pos.getLevel(),pos.getX(),pos.getY(),pos.getZ())){event.setCancelled();return;}}
 public void filterExplosion(List<Block> blocks){if(plugin.getConfig().getBoolean("event.protect-explosions",true))blocks.removeIf(this::isProtected);}
 private boolean isProtected(Block b){if(!plugin.getConfig().getBoolean("event.protect-region",true))return false;for(KothEvent e:active.values())if(e.arena().containsBlock(b))return true;return false;}
 private KothEvent find(UUID id){for(KothEvent e:active.values())if(e.contains(id))return e;return null;}
 private Player online(UUID id){for(Player p:plugin.getServer().getOnlinePlayersList())if(p.getUniqueId().equals(id))return p;return null;}
 private void restore(KothEvent e){for(UUID id:new HashSet<>(e.participants())){Player p=online(id);if(p!=null)restoreOne(e,p);}for(UUID id:new HashSet<>(e.spectators())){Player p=online(id);if(p!=null)restoreOne(e,p);}}
 private void restoreOne(KothEvent e,Player p){bars.remove(e.arena().name(),p);Position pos=e.returnPositions().get(p.getUniqueId());if(pos!=null&&pos.getLevel()!=null)safeTeleport(p,pos);Integer gm=e.previousGamemodes().get(p.getUniqueId());if(gm!=null)p.setGamemode(gm);}
 public void shutdown(){for(KothEvent e:active.values())restore(e);active.clear();lastDamager.clear();teleportBypass.clear();bars.clear();}
}