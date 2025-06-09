package org.dimitrescu.audio;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

public class SongRequest {
    private User requester;
    private AudioTrack track;
    private MessageChannelUnion songChannel;
    private Guild guild;

    public SongRequest(User requester, AudioTrack track, MessageChannelUnion songChannel, Guild guild) {
        this.requester = requester;
        this.track = track;
        this.songChannel = songChannel;
        this.guild = guild;
    }

    public User getRequester() {
        return requester;
    }

    public AudioTrack getTrack() {
        return track;
    }

    public MessageChannelUnion getSongChannel() {
        return songChannel;
    }

    public Guild getGuild() {
        return guild;
    }

    @Override
    public String toString() {
        return track.getInfo().author + " - " + track.getInfo().title;
    }
}
