package org.dimitrescu.listener;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.dimitrescu.service.ConfigManager;
import org.dimitrescu.util.Config;

public class PauseCommand extends ListenerAdapter {
    private ConfigManager configManager;

    public PauseCommand(ConfigManager config) {
        this.configManager = config;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("pause")) {
            event.deferReply().queue();
            configManager.getConfig(event.getGuild()).getTrackQueueService().togglePause(event);
        }
    }
}
