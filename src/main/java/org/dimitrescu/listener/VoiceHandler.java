package org.dimitrescu.listener;

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.dimitrescu.util.Config;

public class VoiceHandler extends ListenerAdapter {
    private Config config;

    public VoiceHandler(Config config) {
        this.config = config;
    }

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        try {
            if (event.getChannelLeft().getMembers().size() == 1 && event.getChannelLeft().equals(config.currentChannel)) {
                System.out.println("[+] Leaving empty voice");
                event.getGuild().getAudioManager().closeAudioConnection();
                config.getTrackQueueService().clearQueue();
            }
        } catch (NullPointerException ignored) {}
    }
}
