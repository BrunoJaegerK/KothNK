package com.brunojaegerk.koth.command;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import com.brunojaegerk.koth.KingOfTheHillNK;
import com.brunojaegerk.koth.arena.Arena;
import java.util.Locale;

public final class KothCommand {
    private KothCommand(){}
    public static boolean execute(KingOfTheHillNK plugin,CommandSender sender,String[] args){
        if(args.length==0||args[0].equalsIgnoreCase("help")){help(sender);return true;}
        String sub=args[0].toLowerCase(Locale.ROOT);
        if(sub.equals("list")){sender.sendMessage("§6Arenas KOTH:");for(Arena a:plugin.arenas().all())sender.sendMessage("§e- "+a.name()+" §7["+a.mode()+"/"+a.captureMethod()+"]");return true;}
        if(sub.equals("stats")){if(sender instanceof Player p)sender.sendMessage(plugin.stats().summary(p));else sender.sendMessage("Solo jugadores.");return true;}
        if(sub.equals("top")){sender.sendMessage("§6Clasificación KOTH");for(String line:plugin.stats().top(10))sender.sendMessage("§e"+line);return true;}
        if(sub.equals("join")||sub.equals("leave")||sub.equals("spectate")||sub.equals("teamjoin")||sub.equals("teamleave")){
            if(!(sender instanceof Player p)){sender.sendMessage("§cEste comando requiere un jugador.");return true;}
            if(sub.equals("leave")){plugin.events().leave(p);return true;}
            if(sub.equals("teamjoin")){if(args.length<3){sender.sendMessage("§cUso: /koth teamjoin <arena> <equipo>");return true;}var event=plugin.events().get(args[1]);if(event==null||!event.participants().contains(p.getUniqueId())){sender.sendMessage("§cDebes participar en ese KOTH para elegir equipo.");return true;}plugin.teams().assign(args[1],p,args[2]);sender.sendMessage("§aTe uniste al equipo "+args[2]+".");return true;}
            if(sub.equals("teamleave")){if(args.length<2){sender.sendMessage("§cUso: /koth teamleave <arena>");return true;}plugin.teams().unassign(args[1],p);sender.sendMessage("§eSaliste de tu equipo en esa arena.");return true;}
            if(args.length<2){sender.sendMessage("§cUso: /koth "+sub+" <arena>");return true;}
            boolean ok=sub.equals("join")?plugin.events().join(args[1],p):plugin.events().spectate(args[1],p);
            sender.sendMessage(ok?"§aAcción completada.":"§cNo se pudo entrar; verifica el evento y tu participación actual.");return true;
        }
        if(!sender.hasPermission("koth.admin")){sender.sendMessage("§cNo tienes permiso.");return true;}
        try{
            switch(sub){
                case "create" -> {need(args,2);sender.sendMessage(plugin.arenas().create(args[1])?"§aArena creada.":"§cEsa arena ya existe.");}
                case "delete" -> {need(args,2);plugin.events().stop(args[1]);sender.sendMessage(plugin.arenas().delete(args[1])?"§aArena eliminada.":"§cNo existe esa arena.");}
                case "start" -> {need(args,2);sender.sendMessage(plugin.events().start(args[1])?"§aEvento iniciado/preparándose.":"§cNo se pudo iniciar; configura pos1, pos2 y centro.");}
                case "stop","forceend" -> {need(args,2);sender.sendMessage(plugin.events().stop(args[1])?"§aEvento detenido.":"§cNo hay un evento activo.");}
                case "pos1","pos2","setcenter","setspawn","setspectator" -> {
                    need(args,2);if(!(sender instanceof Player p))throw new IllegalArgumentException("Ejecuta este comando dentro del juego.");
                    switch(sub){case "pos1"->plugin.arenas().corner(args[1],p,true);case "pos2"->plugin.arenas().corner(args[1],p,false);case "setcenter"->plugin.arenas().center(args[1],p);case "setspawn"->plugin.arenas().spawn(args[1],p);case "setspectator"->plugin.arenas().spectator(args[1],p);}
                    sender.sendMessage("§aPosición guardada para "+args[1]+".");
                }
                case "setmode" -> {need(args,3);String v=args[2].toLowerCase(Locale.ROOT);if(!v.equals("individual")&&!v.equals("teams"))throw new IllegalArgumentException("Usa individual o teams.");plugin.arenas().mode(args[1],v);sender.sendMessage("§aModalidad actualizada.");}
                case "setcapture" -> {need(args,3);String v=args[2].toLowerCase(Locale.ROOT);if(!v.equals("individual")&&!v.equals("majority"))throw new IllegalArgumentException("Usa individual o majority.");plugin.arenas().capture(args[1],v);sender.sendMessage("§aMétodo de captura actualizado.");}
                case "settime" -> {need(args,3);plugin.arenas().time(args[1],Integer.parseInt(args[2]));sender.sendMessage("§aTiempo de captura actualizado.");}
                case "setradius" -> {need(args,3);plugin.arenas().radius(args[1],Double.parseDouble(args[2]));sender.sendMessage("§aRadio de captura actualizado.");}
                case "setmaxplayers" -> {need(args,3);plugin.arenas().maxPlayers(args[1],Integer.parseInt(args[2]));sender.sendMessage("§aLímite de jugadores actualizado.");}
                case "team" -> {need(args,4);Player target=null;for(Player p:plugin.getServer().getOnlinePlayersList())if(p.getName().equalsIgnoreCase(args[2]))target=p;if(target==null)throw new IllegalArgumentException("El jugador debe estar conectado.");plugin.teams().assign(args[1],target,args[3]);sender.sendMessage("§aEquipo asignado.");}
                case "autobalance" -> {need(args,2);var event=plugin.events().get(args[1]);if(event==null)throw new IllegalArgumentException("El evento no está activo.");int index=0;for(Player target:plugin.getServer().getOnlinePlayersList())if(event.participants().contains(target.getUniqueId()))plugin.teams().assign(args[1],target,(index++%2==0)?"red":"blue");sender.sendMessage("§aEquipos balanceados: red / blue.");}
                case "schedule" -> {need(args,4);plugin.scheduleConfig().set("enabled",true);plugin.scheduleConfig().set("events."+args[1]+".time",args[2]);plugin.scheduleConfig().set("events."+args[1]+".days",java.util.Arrays.asList(args[3].toUpperCase(Locale.ROOT).split(",")));plugin.scheduleConfig().save();sender.sendMessage("§aHorario guardado.");}
                case "unschedule" -> {need(args,2);plugin.scheduleConfig().getSection("events").remove(args[1]);plugin.scheduleConfig().save();sender.sendMessage("§aHorario eliminado.");}
                case "reload" -> {plugin.reloadAll();sender.sendMessage("§aConfiguración recargada.");}
                case "setreward" -> sender.sendMessage("§eEdita plugins/KingOfTheHillNK/rewards.yml y ejecuta /koth reload.");
                default -> help(sender);
            }
        }catch(Exception ex){sender.sendMessage("§c"+ex.getMessage());}
        return true;
    }
    private static void need(String[] args,int count){if(args.length<count)throw new IllegalArgumentException("Faltan argumentos. Usa /koth help.");}
    private static void help(CommandSender s){
        s.sendMessage("§6§lKingOfTheHillNK §7- Comandos");
        s.sendMessage("§e/koth list, join <arena>, leave, spectate <arena>, stats, top");
        s.sendMessage("§c/koth create|delete|start|stop <arena>");
        s.sendMessage("§c/koth pos1|pos2|setcenter|setspawn|setspectator <arena>");
        s.sendMessage("§c/koth setmode <arena> <individual|teams> §7| §c/koth setcapture <arena> <individual|majority>");
        s.sendMessage("§c/koth settime <arena> <segundos> §7| §c/koth setradius <arena> <bloques> §7| §c/koth setmaxplayers <arena> <cantidad>");
        s.sendMessage("§c/koth team <arena> <jugador> <equipo> §7| §c/koth autobalance <arena>");
        s.sendMessage("§e/koth teamjoin <arena> <equipo> §7| §e/koth teamleave <arena>");
        s.sendMessage("§c/koth schedule <arena> <HH:mm> <MONDAY,TUESDAY,...> §7| §c/koth unschedule <arena>");
        s.sendMessage("§c/koth setreward <arena> §7| §c/koth reload");
    }
}
