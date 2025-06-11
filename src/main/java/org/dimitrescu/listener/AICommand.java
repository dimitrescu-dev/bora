package org.dimitrescu.listener;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.dimitrescu.service.ConfigManager;

public class AICommand extends ListenerAdapter {
    private ConfigManager configManager;

    public AICommand(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equals("aiplaylist")) {
            event.deferReply().queue();
            if(configManager.getConfig(event.getGuild()).getTrackQueueService().getCurrentSong() != null) {
                configManager.getConfig(event.getGuild()).getTrackQueueService()
                        .getAi(event);
            }
            else event.getHook().sendMessageEmbeds(configManager.getConfig(event.getGuild()).getEmbedSongMessageService()
                    .noMoreSongs()).queue();

        }
    }
}
