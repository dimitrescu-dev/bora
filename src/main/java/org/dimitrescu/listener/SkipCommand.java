package org.dimitrescu.listener;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.dimitrescu.service.ConfigManager;

public class SkipCommand extends ListenerAdapter {
    private ConfigManager configManager;

    public SkipCommand(ConfigManager config) {
        this.configManager = config;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equals("skip")) {
            event.deferReply().queue();
            System.out.println("[+] Skipping current song");
            configManager.getConfig(event.getGuild()).getTrackQueueService().skip(configManager.getConfig(event.getGuild()).getPlayer(),event);
        }
    }

}
