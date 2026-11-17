package com.brunojaegerk.koth.scoreboard;

import cn.nukkit.Player;
import cn.nukkit.scoreboard.Scoreboard;
import cn.nukkit.scoreboard.data.DisplaySlot;
import cn.nukkit.scoreboard.data.SortOrder;
import com.brunojaegerk.koth.KingOfTheHillNK;
import java.util.*;

/** Sidebar nativa PM1E, independiente para cada jugador y actualizada solo si cambia su contenido. */
public final class ScoreboardManager {
 private final KingOfTheHillNK plugin;
 private final Map<UUID,Scoreboard> boards=new HashMap<>();
 private final Map<UUID,List<String>> lastLines=new HashMap<>();
 public ScoreboardManager(KingOfTheHillNK plugin){this.plugin=plugin;}
 public void update(Player player,String arena,String state,int progress,int participants,int remaining){
  UUID id=player.getUniqueId();Scoreboard board=boards.get(id);
  if(board==null){board=new Scoreboard("§6§lKING OF THE HILL",SortOrder.DESCENDING,DisplaySlot.SIDEBAR);boards.put(id,board);board.showTo(player);}
  List<String> lines=List.of("§7Arena: §f"+arena,"§7Estado: §f"+state,"§7Captura: §a"+progress+"%","§7Jugadores: §f"+participants,"§7Tiempo: §f"+Math.max(0,remaining)+"s");
  if(lines.equals(lastLines.get(id)))return;
  board.holdUpdates();board.clear();int score=lines.size();
  for(String line:lines)board.setScore(line,score--);
  board.unholdUpdates();lastLines.put(id,lines);
 }
 public void remove(Player player){UUID id=player.getUniqueId();Scoreboard board=boards.remove(id);lastLines.remove(id);if(board!=null)board.hideFor(player);}
 public void forget(UUID id){boards.remove(id);lastLines.remove(id);}
 public void clear(){
  for(Player player:plugin.getServer().getOnlinePlayersList())remove(player);
  boards.clear();lastLines.clear();
 }
}
