package org.dimitrescu.listener;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.dimitrescu.util.Config;

public class LeaveCommand extends ListenerAdapter {
    private Config config;

    public LeaveCommand(Config config) {
        this.config = config;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("leave")) {
            event.deferReply().queue();
            if(event.getMember().getVoiceState().getChannel() != null) {
                event.getGuild().getAudioManager().closeAudioConnection();
                config.lastPlayMessage.editMessageComponents().queue();
                config.getTrackQueueService().clearQueue();

                event.getHook().sendMessageEmbeds(config.getEmbedSongMessageService().noMoreSongs()).queue();
            }
            else event.getHook().sendMessageEmbeds(config.getEmbedSongMessageService().userNotInVoice()).queue();
        }
    }
}
