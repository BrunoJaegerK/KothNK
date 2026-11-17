package com.brunojaegerk.koth.listener;
import cn.nukkit.event.*;
import cn.nukkit.event.block.*;
import cn.nukkit.event.entity.*;
import cn.nukkit.event.player.*;
import com.brunojaegerk.koth.KingOfTheHillNK;

public final class KothListener implements Listener {
 private final KingOfTheHillNK plugin;
 public KothListener(KingOfTheHillNK plugin){this.plugin=plugin;}
 @EventHandler public void onDamage(EntityDamageByEntityEvent e){plugin.events().damage(e);}
 @EventHandler public void onDeath(PlayerDeathEvent e){plugin.events().death(e);}
 @EventHandler public void onQuit(PlayerQuitEvent e){plugin.events().quit(e.getPlayer());}
 @EventHandler public void onTeleport(PlayerTeleportEvent e){plugin.events().teleport(e);}
 @EventHandler public void onBreak(BlockBreakEvent e){if(plugin.getConfig().getBoolean("event.block-break",true))plugin.events().protectBlock(e.getBlock(),e);}
 @EventHandler public void onPlace(BlockPlaceEvent e){if(plugin.getConfig().getBoolean("event.block-place",true))plugin.events().protectBlock(e.getBlock(),e);}
 @EventHandler public void onInteract(PlayerInteractEvent e){
  if(plugin.getConfig().getBoolean("event.protect-interactions",true)&&e.getAction()!=PlayerInteractEvent.Action.LEFT_CLICK_AIR&&e.getAction()!=PlayerInteractEvent.Action.RIGHT_CLICK_AIR)plugin.events().protectBlock(e.getBlock(),e);
 }
 @EventHandler public void onBucketEmpty(PlayerBucketEmptyEvent e){if(plugin.getConfig().getBoolean("event.protect-liquids",true))plugin.events().protectBlock(e.getBlockClicked(),e);}
 @EventHandler public void onBucketFill(PlayerBucketFillEvent e){if(plugin.getConfig().getBoolean("event.protect-liquids",true))plugin.events().protectBlock(e.getBlockClicked(),e);}
 @EventHandler public void onFlow(BlockFromToEvent e){plugin.events().protectFlow(e.getFrom(),e.getTo(),e);}
 @EventHandler public void onEntityExplosion(EntityExplodeEvent e){plugin.events().protectExplosion(e.getPosition(),e);plugin.events().filterExplosion(e.getBlockList());}
 @EventHandler public void onBlockExplosion(BlockExplodeEvent e){plugin.events().protectExplosion(e.getPosition(),e);plugin.events().filterExplosion(e.getBlockList());}
}