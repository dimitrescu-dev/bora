package org.dimitrescu.listener;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.dimitrescu.service.ConfigManager;
import org.dimitrescu.util.Config;

public class LoopCommand extends ListenerAdapter {
    private ConfigManager configManager;

    public LoopCommand(ConfigManager config) {
        this.configManager = config;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equals("loop")) {
            event.deferReply().queue();
            configManager.getConfig(event.getGuild()).getTrackQueueService().toggleLoop(event);
        }
    }

}
