package com.brunojaegerk.koth.scheduler;

import cn.nukkit.utils.Config;
import com.brunojaegerk.koth.event.KothEventManager;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public final class KothScheduler {
    private final Config config;private final KothEventManager events;private String lastMinute="";private final Map<String,LocalDate> fired=new HashMap<>();
    public KothScheduler(Config config,KothEventManager events){this.config=config;this.events=events;}
    /** Ejecuta horarios locales una sola vez por minuto y por fecha. */
    public void tick(){
        if(!config.getBoolean("enabled",false))return;
        ZoneId zone;try{zone=ZoneId.of(config.getString("timezone","America/Guayaquil"));}catch(DateTimeException ex){zone=ZoneId.of("UTC");}
        ZonedDateTime now=ZonedDateTime.now(zone);String minute=now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));if(minute.equals(lastMinute))return;lastMinute=minute;
        String clock=now.format(DateTimeFormatter.ofPattern("HH:mm"));
        for(String arena:config.getSection("events").keySet()){
            if(!clock.equals(config.getString("events."+arena+".time",""))||now.toLocalDate().equals(fired.get(arena)))continue;
            Object raw=config.get("events."+arena+".days",List.of());
            if(raw instanceof List<?> days&&!days.isEmpty()&&!days.contains(now.getDayOfWeek().name()))continue;
            if(events.start(arena))fired.put(arena,now.toLocalDate());
        }
    }
}
