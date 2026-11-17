package com.brunojaegerk.koth.clan;

import com.brunojaegerk.koth.KingOfTheHillNK;

/**
 * Adaptador aislado. API.md no publica el paquete de ClanSystemAPI ni un método
 * tipado/documentado para obtener el clan desde ClanPlayer; no se inventan
 * imports ni getters. Los equipos internos siguen funcionando sin ese plugin.
 */
public final class ClanSystemMxBridge {
    private final KingOfTheHillNK plugin;
    public ClanSystemMxBridge(KingOfTheHillNK plugin){this.plugin=plugin;}
    public void inspectAvailability(){
        if(plugin.getServer().getPluginManager().getPlugin("ClanSystemMX")!=null)
            plugin.getLogger().warning("ClanSystemMX detectado. Falta documentar paquete Java y acceso al ID/tag del clan para activar el proveedor automático. Usa /koth team mientras tanto.");
    }
}
