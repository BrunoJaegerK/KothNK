package com.brunojaegerk.koth.clan;

import com.brunojaegerk.koth.KingOfTheHillNK;

/**
 * Integración opcional aislada. El API.md compartido no publica el paquete
 * Java ni el acceso documentado al ID/tag del clan desde ClanPlayer; no se
 * inventan imports o getters. Los equipos internos funcionan sin este plugin.
 */
public final class ClanSystemMxBridge {
 private final KingOfTheHillNK plugin;
 public ClanSystemMxBridge(KingOfTheHillNK plugin){this.plugin=plugin;}
 public void inspectAvailability(){
  if(!plugin.getConfig().getBoolean("clans.enabled",false))return;
  if(plugin.getServer().getPluginManager().getPlugin("ClanSystemMX")==null)
   plugin.getLogger().warning("clans.enabled está activo, pero ClanSystemMX no está instalado.");
  else plugin.getLogger().warning("ClanSystemMX detectado, pero la agrupación automática está desactivada: el API.md no documenta el identificador/tag del clan de ClanPlayer. Usa los equipos internos /koth team.");
 }
}