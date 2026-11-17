package com.brunojaegerk.koth;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import com.brunojaegerk.koth.arena.ArenaManager;
import com.brunojaegerk.koth.clan.ClanSystemMxBridge;
import com.brunojaegerk.koth.command.KothCommand;
import com.brunojaegerk.koth.event.KothEventManager;
import com.brunojaegerk.koth.listener.KothListener;
import com.brunojaegerk.koth.reward.RewardManager;
import com.brunojaegerk.koth.scheduler.KothScheduler;
import com.brunojaegerk.koth.statistics.StatisticsManager;
import com.brunojaegerk.koth.team.TeamManager;
import java.io.File;

public final class KingOfTheHillNK extends PluginBase {
    private Config arenas,rewards,schedule,messages;
    private ArenaManager arenaManager;
    private TeamManager teamManager;
    private StatisticsManager statistics;
    private RewardManager rewardManager;
    private KothEventManager events;
    private KothScheduler scheduler;

    @Override public void onEnable() {
        if(!getDataFolder().exists())getDataFolder().mkdirs();
        for(String file:new String[]{"config.yml","arenas.yml","rewards.yml","schedule.yml","messages.yml","stats.yml"})saveResource(file,false);
        loadFiles();createServices();
        getServer().getPluginManager().registerEvents(new KothListener(this),this);
        getServer().getScheduler().scheduleRepeatingTask(this,()->{if(events!=null){events.tick();scheduler.tick();}},20);
        new ClanSystemMxBridge(this).inspectAvailability();
        getLogger().info("KingOfTheHillNK habilitado. Arenas cargadas: "+arenaManager.size());
    }
    private void loadFiles(){
        arenas=new Config(new File(getDataFolder(),"arenas.yml"),Config.YAML);
        rewards=new Config(new File(getDataFolder(),"rewards.yml"),Config.YAML);
        schedule=new Config(new File(getDataFolder(),"schedule.yml"),Config.YAML);
        messages=new Config(new File(getDataFolder(),"messages.yml"),Config.YAML);
    }
    private void createServices(){
        arenaManager=new ArenaManager(arenas,getConfig());teamManager=new TeamManager();
        statistics=new StatisticsManager(new Config(new File(getDataFolder(),"stats.yml"),Config.YAML));
        rewardManager=new RewardManager(this,rewards);
        events=new KothEventManager(this,arenaManager,teamManager,statistics,rewardManager);
        scheduler=new KothScheduler(schedule,events);
    }
    /** Detiene sesiones antes de sustituir servicios para no dejar jugadores atrapados. */
    public void reloadAll(){
        if(events!=null)events.shutdown();
        getConfig().reload();loadFiles();createServices();
    }
    @Override public boolean onCommand(CommandSender sender,Command command,String label,String[] args){return KothCommand.execute(this,sender,args);}
    @Override public void onDisable(){if(events!=null)events.shutdown();if(statistics!=null)statistics.save();}
    public String message(String key){
        String prefix=getConfig().getString("prefix","&8[&6KOTH&8] ");
        return color(prefix+messages.getString(key,"&cMensaje no configurado: "+key));
    }
    public static String color(String s){return s==null?"":s.replace('&','§');}
    public ArenaManager arenas(){return arenaManager;} public TeamManager teams(){return teamManager;}
    public StatisticsManager stats(){return statistics;} public RewardManager rewards(){return rewardManager;}
    public KothEventManager events(){return events;} public Config scheduleConfig(){return schedule;}
}
