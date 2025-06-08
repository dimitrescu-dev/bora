package org.dimitrescu.listener;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.dimitrescu.util.Config;

public class SkipCommand extends ListenerAdapter {
    private Config config;

    public SkipCommand(Config config) {
        this.config = config;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equals("skip")) {
            event.deferReply().queue();
            System.out.println("[+] Skipping current song");
            config.getTrackQueueService().skip(config.getPlayer(),event);
        }
    }

}
