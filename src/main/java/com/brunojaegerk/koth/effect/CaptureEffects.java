package com.brunojaegerk.koth.effect;

import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.GenericParticle;
import cn.nukkit.level.particle.Particle;
import com.brunojaegerk.koth.KingOfTheHillNK;
import com.brunojaegerk.koth.arena.Arena;

/** Efectos ligeros: un anillo de ocho partículas por tick de evento y sonidos puntuales. */
public final class CaptureEffects {
    private final KingOfTheHillNK plugin;
    public CaptureEffects(KingOfTheHillNK plugin){this.plugin=plugin;}
    public void ring(Arena arena,int particleType){
        if(!plugin.getConfig().getBoolean("effects.particles",true))return;
        Level level=plugin.getServer().getLevelByName(arena.world());if(level==null||!arena.hasCenter())return;
        double radius=arena.radius();
        for(int i=0;i<8;i++){
            double angle=(Math.PI*2*i)/8.0;
            Position point=new Position(arena.centerX()+Math.cos(angle)*radius,arena.centerY()+1,arena.centerZ()+Math.sin(angle)*radius,level);
            level.addParticle(new GenericParticle(point,particleType));
        }
    }
    public void sound(Arena arena,Sound sound){
        if(!plugin.getConfig().getBoolean("effects.sounds",true))return;
        Level level=plugin.getServer().getLevelByName(arena.world());if(level!=null&&arena.hasCenter())
            level.addSound(new Position(arena.centerX(),arena.centerY(),arena.centerZ(),level),sound);
    }
}
