package org.dimitrescu.listener;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.dimitrescu.service.ConfigManager;

public class LeaveCommand extends ListenerAdapter {
    private ConfigManager configManager;

    public LeaveCommand(ConfigManager config) {
        this.configManager = config;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("leave")) {
            event.deferReply().queue();
            if(event.getMember().getVoiceState().getChannel() != null) {
                event.getGuild().getAudioManager().closeAudioConnection();
                configManager.getConfig(event.getGuild()).lastPlayMessage.editMessageComponents().queue();
                configManager.getConfig(event.getGuild()).getTrackQueueService().clearQueue();

                event.getHook().sendMessageEmbeds(configManager.getConfig(event.getGuild()).getEmbedSongMessageService().noMoreSongs()).queue();
            }
            else event.getHook().sendMessageEmbeds(configManager.getConfig(event.getGuild()).getEmbedSongMessageService().userNotInVoice()).queue();
        }
    }
}
