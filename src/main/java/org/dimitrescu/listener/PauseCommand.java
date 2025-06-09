package org.dimitrescu.listener;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.dimitrescu.util.Config;

public class PauseCommand extends ListenerAdapter {
    private Config config;

    public PauseCommand(Config config) {
        this.config = config;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("pause")) {
            event.deferReply().queue();
            config.getTrackQueueService().togglePause(event);
        }
    }
}
