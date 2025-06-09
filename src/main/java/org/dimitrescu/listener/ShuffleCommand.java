package org.dimitrescu.listener;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.dimitrescu.service.ConfigManager;

public class ShuffleCommand extends ListenerAdapter {

    private ConfigManager configManager;

    public ShuffleCommand(ConfigManager config) {
        this.configManager = config;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("shuffle")) {
            System.out.println("[+] Shuffle event received");
            event.deferReply().queue();
            configManager.getConfig(event.getGuild()).getTrackQueueService().shuffle(event);
        }
    }
}
