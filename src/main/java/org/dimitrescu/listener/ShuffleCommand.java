package org.dimitrescu.listener;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.dimitrescu.util.Config;

public class ShuffleCommand extends ListenerAdapter {

    private Config config;

    public ShuffleCommand(Config config) {
        this.config = config;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("shuffle")) {
            System.out.println("[+] Shuffle event received");
            event.deferReply().queue();
            config.getTrackQueueService().shuffle(event);
        }
    }
}
