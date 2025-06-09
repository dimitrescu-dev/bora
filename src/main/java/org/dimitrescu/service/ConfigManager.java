package org.dimitrescu.service;

import net.dv8tion.jda.api.entities.Guild;
import org.dimitrescu.util.Config;

import java.util.HashMap;

public class ConfigManager {
    HashMap<Guild, Config> guildConfigMap = new HashMap<>();

    public ConfigManager() {
    }

    public Config getConfig(Guild guild) {
        if(!guildConfigMap.containsKey(guild)) guildConfigMap.put(guild,new Config());
        return guildConfigMap.get(guild);
    }
}
