package com.brunojaegerk.koth.listener;

import cn.nukkit.event.*;
import cn.nukkit.event.block.*;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.player.*;
import com.brunojaegerk.koth.KingOfTheHillNK;

public final class KothListener implements Listener {
    private final KingOfTheHillNK plugin;
    public KothListener(KingOfTheHillNK plugin){this.plugin=plugin;}
    @EventHandler public void onDamage(EntityDamageByEntityEvent e){plugin.events().damage(e);}
    @EventHandler public void onDeath(PlayerDeathEvent e){plugin.events().death(e);}
    @EventHandler public void onQuit(PlayerQuitEvent e){plugin.events().quit(e.getPlayer());}
    @EventHandler public void onBreak(BlockBreakEvent e){if(plugin.getConfig().getBoolean("event.block-break",true))plugin.events().protect(e.getPlayer(),e);}
    @EventHandler public void onPlace(BlockPlaceEvent e){if(plugin.getConfig().getBoolean("event.block-place",true))plugin.events().protect(e.getPlayer(),e);}
}
