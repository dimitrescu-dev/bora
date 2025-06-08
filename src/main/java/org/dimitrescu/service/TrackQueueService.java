package org.dimitrescu.service;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.dimitrescu.audio.SongRequest;
import org.dimitrescu.util.Config;

import java.util.ArrayList;

public class TrackQueueService extends AudioEventAdapter {
    private ArrayList<SongRequest> queue;
    private SongRequest currentSong = null;
    private Config config;

    public TrackQueueService(Config config) {
        this.config = config;
        queue = new ArrayList<>();
    }

    public void queue(SongRequest request, AudioPlayer player, SlashCommandInteractionEvent event) {
        queue.add(request);

        if(queue.size() == 1 && player.getPlayingTrack() == null) playFirstTrack(player,event);
        else event.getHook().sendMessageEmbeds(config.getEmbedSongMessageService().sendAddToQueue(request, queue.size())).queue();
    }

    public void skip(AudioPlayer player,SlashCommandInteractionEvent event) {
        playFirstTrack(player,event);
    }

    public SongRequest getNextTrack() {
        if (queue.isEmpty()) {
            return null;
        }
        return queue.remove(0);
    }

    public void playFirstTrack(AudioPlayer player,SlashCommandInteractionEvent event) {
        SongRequest nextTrack = getNextTrack();
        if (nextTrack != null) {
            currentSong = nextTrack;
            player.playTrack(nextTrack.getTrack());
            event.getHook().sendMessageEmbeds(config.getEmbedSongMessageService().sendFirstSong(nextTrack, queue.size())).queue();
        } else {
            event.getHook().sendMessageEmbeds(config.getEmbedSongMessageService().noMoreSongs(currentSong)).queue();
            currentSong = null;
            player.stopTrack();
        }
    }


    public void playNextTrack(AudioPlayer player) {
        SongRequest nextTrack = getNextTrack();
        if (nextTrack != null) {
            currentSong = nextTrack;
            player.playTrack(nextTrack.getTrack());
            config.getEmbedSongMessageService().sendNowPlayingSongEmbed(nextTrack, queue.size());
        } else {
            currentSong.getSongChannel().sendMessageEmbeds(config.getEmbedSongMessageService().noMoreSongs(currentSong)).queue();
            currentSong = null;
            player.stopTrack();
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason reason) {
        System.out.println("[+] Track ended: " + track.getInfo().title + " | Reason: " + reason.name());
        playNextTrack(player);
    }

    public ArrayList<SongRequest> getQueue() {
        return queue;
    }

    public SongRequest getCurrentSong() {
        return currentSong;
    }
}
