package com.brunojaegerk.koth.arena;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.utils.Config;

public final class Arena {
 private final String name; private String world="";
 private double x1,y1,z1,x2,y2,z2,cx,cy,cz,sx,sy,sz,tx,ty,tz,radius=6;
 private boolean p1,p2,center,spawn,spectator; private int captureSeconds=30,maxPlayers=50;
 private String mode="individual",captureMethod="individual";
 public Arena(String name){this.name=name;}
 public String name(){return name;} public String world(){return world;} public int captureSeconds(){return captureSeconds;}
 public int maxPlayers(){return maxPlayers;} public double radius(){return radius;} public String mode(){return mode;}
 public String captureMethod(){return captureMethod;} public boolean ready(){return !world.isEmpty()&&p1&&p2&&center;}
 public boolean hasCenter(){return center;} public void setMode(String v){mode=v;} public void setCaptureMethod(String v){captureMethod=v;}
 public void setCaptureSeconds(int v){captureSeconds=Math.max(1,v);} public void setRadius(double v){radius=Math.max(1,Math.min(64,v));}
 public void setMaxPlayers(int v){maxPlayers=Math.max(1,Math.min(500,v));}
 private void useWorld(Player p){String next=p.getLevel().getName();if(!world.isEmpty()&&!world.equalsIgnoreCase(next))throw new IllegalArgumentException("Todas las posiciones de una arena deben estar en el mismo mundo.");world=next;}
 public void corner(Player p,boolean first){useWorld(p);if(first){x1=p.getX();y1=p.getY();z1=p.getZ();p1=true;}else{x2=p.getX();y2=p.getY();z2=p.getZ();p2=true;}}
 public void center(Player p){useWorld(p);cx=p.getX();cy=p.getY();cz=p.getZ();center=true;}
 public void spawn(Player p){useWorld(p);sx=p.getX();sy=p.getY();sz=p.getZ();spawn=true;}
 public void spectator(Player p){useWorld(p);tx=p.getX();ty=p.getY();tz=p.getZ();spectator=true;}
 public boolean hasSpawn(){return spawn;} public boolean hasSpectator(){return spectator;}
 public double spawnX(){return sx;} public double spawnY(){return sy;} public double spawnZ(){return sz;}
 public double specX(){return tx;} public double specY(){return ty;} public double specZ(){return tz;}
 public double centerX(){return cx;} public double centerY(){return cy;} public double centerZ(){return cz;}
 public boolean inside(Player p){return p.getLevel()!=null&&insideCapture(p.getLevel(),p.getX(),p.getY(),p.getZ());}
 public boolean insideCapture(Level level,double x,double y,double z){
  if(!insideRegion(level,x,y,z))return false;if(!center)return true;
  double dx=x-cx,dz=z-cz;return dx*dx+dz*dz<=radius*radius&&y>=cy-4&&y<=cy+8;
 }
 /** Cuboide completo: se usa para protección y límites de teletransporte. */
 public boolean insideRegion(Level level,double x,double y,double z){
  return level!=null&&world.equalsIgnoreCase(level.getName())&&p1&&p2
   &&x>=Math.min(x1,x2)&&x<=Math.max(x1,x2)&&y>=Math.min(y1,y2)&&y<=Math.max(y1,y2)
   &&z>=Math.min(z1,z2)&&z<=Math.max(z1,z2);
 }
 public boolean containsBlock(Block b){return b!=null&&insideRegion(b.getLevel(),b.getX(),b.getY(),b.getZ());}
 public void load(Config c,String p){
  world=c.getString(p+".world","");p1=c.getBoolean(p+".region.p1",false);p2=c.getBoolean(p+".region.p2",false);
  x1=c.getDouble(p+".region.x1",0);y1=c.getDouble(p+".region.y1",0);z1=c.getDouble(p+".region.z1",0);
  x2=c.getDouble(p+".region.x2",0);y2=c.getDouble(p+".region.y2",0);z2=c.getDouble(p+".region.z2",0);
  center=c.getBoolean(p+".center.set",false);cx=c.getDouble(p+".center.x",0);cy=c.getDouble(p+".center.y",0);cz=c.getDouble(p+".center.z",0);
  spawn=c.getBoolean(p+".spawn.set",false);sx=c.getDouble(p+".spawn.x",0);sy=c.getDouble(p+".spawn.y",0);sz=c.getDouble(p+".spawn.z",0);
  spectator=c.getBoolean(p+".spectator.set",false);tx=c.getDouble(p+".spectator.x",0);ty=c.getDouble(p+".spectator.y",0);tz=c.getDouble(p+".spectator.z",0);
  radius=c.getDouble(p+".radius",6);captureSeconds=c.getInt(p+".capture-seconds",30);maxPlayers=c.getInt(p+".max-players",50);
  mode=c.getString(p+".mode","individual");captureMethod=c.getString(p+".capture-method","individual");
 }
 public void save(Config c,String p){
  c.set(p+".world",world);c.set(p+".region.p1",p1);c.set(p+".region.p2",p2);
  c.set(p+".region.x1",x1);c.set(p+".region.y1",y1);c.set(p+".region.z1",z1);c.set(p+".region.x2",x2);c.set(p+".region.y2",y2);c.set(p+".region.z2",z2);
  c.set(p+".center.set",center);c.set(p+".center.x",cx);c.set(p+".center.y",cy);c.set(p+".center.z",cz);
  c.set(p+".spawn.set",spawn);c.set(p+".spawn.x",sx);c.set(p+".spawn.y",sy);c.set(p+".spawn.z",sz);
  c.set(p+".spectator.set",spectator);c.set(p+".spectator.x",tx);c.set(p+".spectator.y",ty);c.set(p+".spectator.z",tz);
  c.set(p+".radius",radius);c.set(p+".capture-seconds",captureSeconds);c.set(p+".max-players",maxPlayers);c.set(p+".mode",mode);c.set(p+".capture-method",captureMethod);c.save();
 }
}