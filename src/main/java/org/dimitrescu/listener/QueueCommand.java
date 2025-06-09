package org.dimitrescu.listener;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.dimitrescu.util.Config;

public class QueueCommand extends ListenerAdapter {
    private Config config;

    public QueueCommand(Config config) {
        this.config = config;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("queue")) {
            System.out.println("[+] Showing queue");
            event.deferReply().queue();
            event.getHook().sendMessageEmbeds(config.getEmbedSongMessageService().displayQueue()).queue();
        }
    }


}
