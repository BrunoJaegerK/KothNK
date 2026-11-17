package com.brunojaegerk.koth.arena;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;

public final class Arena {
    private final String name;
    private String world="";
    private double x1,y1,z1,x2,y2,z2,cx,cy,cz,sx,sy,sz,tx,ty,tz,radius=6;
    private boolean p1,p2,center,spawn,spectator;
    private int captureSeconds=30,maxPlayers=50;
    private String mode="individual",captureMethod="individual";
    public Arena(String name){this.name=name;}
    public String name(){return name;} public String world(){return world;}
    public int captureSeconds(){return captureSeconds;} public int maxPlayers(){return maxPlayers;} public double radius(){return radius;}
    public String mode(){return mode;} public String captureMethod(){return captureMethod;}
    public boolean ready(){return !world.isEmpty()&&p1&&p2&&center;} public boolean hasCenter(){return center;}
    public void setMode(String v){mode=v;} public void setCaptureMethod(String v){captureMethod=v;}
    public void setCaptureSeconds(int v){captureSeconds=Math.max(1,v);}
    private void useWorld(Player p){String next=p.getLevel().getName();if(!world.isEmpty()&&!world.equalsIgnoreCase(next))throw new IllegalArgumentException("Todas las posiciones de una arena deben estar en el mismo mundo.");world=next;}
    public void corner(Player p,boolean first){useWorld(p);if(first){x1=p.getX();y1=p.getY();z1=p.getZ();p1=true;}else{x2=p.getX();y2=p.getY();z2=p.getZ();p2=true;}}
    public void center(Player p){useWorld(p);cx=p.getX();cy=p.getY();cz=p.getZ();center=true;}
    public void spawn(Player p){useWorld(p);sx=p.getX();sy=p.getY();sz=p.getZ();spawn=true;}
    public void spectator(Player p){useWorld(p);tx=p.getX();ty=p.getY();tz=p.getZ();spectator=true;}
    public boolean hasSpawn(){return spawn;} public boolean hasSpectator(){return spectator;}
    public double spawnX(){return sx;} public double spawnY(){return sy;} public double spawnZ(){return sz;}
    public double specX(){return tx;} public double specY(){return ty;} public double specZ(){return tz;}
    public double centerX(){return cx;} public double centerY(){return cy;} public double centerZ(){return cz;}
    public boolean inside(Player p){
        if(p.getLevel()==null||!world.equalsIgnoreCase(p.getLevel().getName()))return false;
        double x=p.getX(),y=p.getY(),z=p.getZ();
        if(p1&&p2&&(x<Math.min(x1,x2)||x>Math.max(x1,x2)||y<Math.min(y1,y2)||y>Math.max(y1,y2)||z<Math.min(z1,z2)||z>Math.max(z1,z2)))return false;
        if(!center)return p1&&p2;
        double dx=x-cx,dz=z-cz;
        return dx*dx+dz*dz<=radius*radius&&y>=cy-4&&y<=cy+8;
    }
    public void load(Config c,String path){
        world=c.getString(path+".world","");p1=c.getBoolean(path+".region.p1",false);p2=c.getBoolean(path+".region.p2",false);
        x1=c.getDouble(path+".region.x1",0);y1=c.getDouble(path+".region.y1",0);z1=c.getDouble(path+".region.z1",0);
        x2=c.getDouble(path+".region.x2",0);y2=c.getDouble(path+".region.y2",0);z2=c.getDouble(path+".region.z2",0);
        center=c.getBoolean(path+".center.set",false);cx=c.getDouble(path+".center.x",0);cy=c.getDouble(path+".center.y",0);cz=c.getDouble(path+".center.z",0);
        spawn=c.getBoolean(path+".spawn.set",false);sx=c.getDouble(path+".spawn.x",0);sy=c.getDouble(path+".spawn.y",0);sz=c.getDouble(path+".spawn.z",0);
        spectator=c.getBoolean(path+".spectator.set",false);tx=c.getDouble(path+".spectator.x",0);ty=c.getDouble(path+".spectator.y",0);tz=c.getDouble(path+".spectator.z",0);
        radius=c.getDouble(path+".radius",6);captureSeconds=c.getInt(path+".capture-seconds",30);maxPlayers=c.getInt(path+".max-players",50);
        mode=c.getString(path+".mode","individual");captureMethod=c.getString(path+".capture-method","individual");
    }
    public void save(Config c,String path){
        c.set(path+".world",world);c.set(path+".region.p1",p1);c.set(path+".region.p2",p2);
        c.set(path+".region.x1",x1);c.set(path+".region.y1",y1);c.set(path+".region.z1",z1);c.set(path+".region.x2",x2);c.set(path+".region.y2",y2);c.set(path+".region.z2",z2);
        c.set(path+".center.set",center);c.set(path+".center.x",cx);c.set(path+".center.y",cy);c.set(path+".center.z",cz);
        c.set(path+".spawn.set",spawn);c.set(path+".spawn.x",sx);c.set(path+".spawn.y",sy);c.set(path+".spawn.z",sz);
        c.set(path+".spectator.set",spectator);c.set(path+".spectator.x",tx);c.set(path+".spectator.y",ty);c.set(path+".spectator.z",tz);
        c.set(path+".radius",radius);c.set(path+".capture-seconds",captureSeconds);c.set(path+".max-players",maxPlayers);c.set(path+".mode",mode);c.set(path+".capture-method",captureMethod);c.save();
    }
}
