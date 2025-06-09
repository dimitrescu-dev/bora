package org.dimitrescu.listener;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.dimitrescu.util.Config;

public class LoopCommand extends ListenerAdapter {
    private Config config;

    public LoopCommand(Config config) {
        this.config = config;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equals("loop")) {
            event.deferReply().queue();
            config.getTrackQueueService().toggleLoop(event);
        }
    }

}
