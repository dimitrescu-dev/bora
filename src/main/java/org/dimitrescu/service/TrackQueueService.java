package org.dimitrescu.service;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import org.dimitrescu.audio.SongRequest;
import org.dimitrescu.util.Config;

import java.util.ArrayList;

public class TrackQueueService extends AudioEventAdapter {
    private ArrayList<SongRequest> queue;
    private Config config;

    public TrackQueueService(Config config) {
        this.config = config;
        queue = new ArrayList<>();
    }

    public void queue(SongRequest request, AudioPlayer player) {
        queue.add(request);

        if(queue.size() == 1 && player.getPlayingTrack() == null) playNextTrack(player);
        else config.getEmbedSongMessageService().sendAddToQueue(request, queue.size());
    }

    public void skip(AudioPlayer player) {
        playNextTrack(player);
    }

    public SongRequest getNextTrack() {
        if (queue.isEmpty()) {
            return null;
        }
        return queue.remove(0);
    }

    public void playNextTrack(AudioPlayer player) {
        SongRequest nextTrack = getNextTrack();
        if (nextTrack != null) {
            player.playTrack(nextTrack.getTrack());
            config.getEmbedSongMessageService().sendNowPlayingSongEmbed(nextTrack, queue.size());
        } else {
            System.out.println("[!] No more tracks in the queue.");
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason reason) {
        System.out.println("[+] Track ended: " + track.getInfo().title + " | Reason: " + reason.name());
        playNextTrack(player);
    }
}
