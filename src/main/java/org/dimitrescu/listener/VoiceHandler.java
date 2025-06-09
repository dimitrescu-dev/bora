package org.dimitrescu.listener;

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildDeafenEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildMuteEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.dimitrescu.service.ConfigManager;

public class VoiceHandler extends ListenerAdapter {
    private ConfigManager configManager;

    public VoiceHandler(ConfigManager config) {
        this.configManager = config;
    }

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        try {
            if (event.getChannelLeft().getMembers().size() == 1 && event.getChannelLeft().equals(configManager.getConfig(event.getGuild()).currentChannel)) {
                System.out.println("[+] Leaving empty voice");
                event.getGuild().getAudioManager().closeAudioConnection();
                configManager.getConfig(event.getGuild()).getTrackQueueService().clearQueue();
            }
        } catch (NullPointerException ignored) {}
    }

    @Override
    public void onGuildVoiceGuildDeafen(GuildVoiceGuildDeafenEvent event) {
        if(event.getMember().getId().equals(event.getJDA().getSelfUser().getId())) {
            System.out.println("[+] Bot mute/unmute event");
            if(event.getMember().getVoiceState() != null) {
                if(!event.getMember().getVoiceState().isDeafened()) {
                    event.getMember().deafen(true).queue();
                    configManager.getConfig(event.getGuild()).getEmbedSongMessageService().pleaseDontUnmute();
                }
            }
        }
    }


}
