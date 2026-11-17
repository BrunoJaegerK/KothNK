package com.brunojaegerk.koth.event;

import cn.nukkit.Player;
import cn.nukkit.level.Position;
import com.brunojaegerk.koth.arena.Arena;
import java.util.*;

public final class KothEvent {
    private final Arena arena;
    private final Set<UUID> participants=new LinkedHashSet<>(),spectators=new LinkedHashSet<>();
    private final Map<UUID,Position> returnPositions=new HashMap<>();
    private final Map<UUID,Integer> previousGamemodes=new HashMap<>();
    private KothState state=KothState.COUNTDOWN;
    private int countdown,elapsed,progress;
    private String owner;
    public KothEvent(Arena arena,int countdown){this.arena=arena;this.countdown=Math.max(0,countdown);}
    public Arena arena(){return arena;} public Set<UUID> participants(){return participants;} public Set<UUID> spectators(){return spectators;}
    public Map<UUID,Position> returnPositions(){return returnPositions;} public Map<UUID,Integer> previousGamemodes(){return previousGamemodes;}
    public KothState state(){return state;} public void state(KothState v){state=v;}
    public int countdown(){return countdown;} public void countdown(int v){countdown=v;}
    public int elapsed(){return elapsed;} public void tickElapsed(){elapsed++;}
    public int progress(){return progress;} public void progress(int v){progress=v;}
    public String owner(){return owner;} public void owner(String v){owner=v;}
    public boolean contains(UUID id){return participants.contains(id)||spectators.contains(id);}
    public void add(Player p){participants.add(p.getUniqueId());returnPositions.putIfAbsent(p.getUniqueId(),new Position(p.getX(),p.getY(),p.getZ(),p.getLevel()));}
    public void addSpectator(Player p){spectators.add(p.getUniqueId());returnPositions.putIfAbsent(p.getUniqueId(),new Position(p.getX(),p.getY(),p.getZ(),p.getLevel()));previousGamemodes.putIfAbsent(p.getUniqueId(),p.getGamemode());}
}
