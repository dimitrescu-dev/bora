package org.dimitrescu.listener;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.dimitrescu.service.ConfigManager;

public class QueueCommand extends ListenerAdapter {
    private ConfigManager configManager;

    public QueueCommand(ConfigManager config) {
        this.configManager = config;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("queue")) {
            System.out.println("[+] Showing queue");
            event.deferReply().queue();
            event.getHook().sendMessageEmbeds(configManager.getConfig(event.getGuild()).getEmbedSongMessageService().displayQueue()).queue();
        }
    }


}
