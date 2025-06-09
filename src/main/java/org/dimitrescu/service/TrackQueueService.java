package org.dimitrescu.service;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.dimitrescu.audio.SongRequest;
import org.dimitrescu.util.Config;

import java.util.ArrayList;
import java.util.Random;

public class TrackQueueService extends AudioEventAdapter {
    private ArrayList<SongRequest> queue;
    private SongRequest currentSong = null;
    private Config config;

    private boolean isLooping = false;

    public TrackQueueService(Config config) {
        this.config = config;
        queue = new ArrayList<>();
    }

    public void queue(SongRequest request, AudioPlayer player, SlashCommandInteractionEvent event) {
        queue.add(request);

        if(queue.size() == 1 && player.getPlayingTrack() == null && currentSong == null) playFirstTrack(player,event);
        else event.getHook().sendMessageEmbeds(config.getEmbedSongMessageService().sendAddToQueue(request, queue.size())).queue();
    }

    public void skip(AudioPlayer player,SlashCommandInteractionEvent event) {
        if(isLooping) currentSong = getNextTrack();
        playFirstTrack(player,event);
    }

    public void shuffle(SlashCommandInteractionEvent event) {
        int s = queue.size();
        if(s > 0) {
            ArrayList<SongRequest> nq = new ArrayList<>();
            Random random = new Random();


            for (int i = 0; i < s; i++) {
                int r = random.nextInt(queue.size());
                nq.add(queue.get(r));
                queue.remove(r);
            }

            queue = nq;
            event.getHook().sendMessageEmbeds(config.getEmbedSongMessageService().shuffleSongs()).queue();
        }

        else event.getHook().sendMessageEmbeds(config.getEmbedSongMessageService().noMoreSongs()).queue();
    }

    public SongRequest getNextTrack() {
        if (queue.isEmpty()) {
            return null;
        }
        return queue.remove(0);
    }

    public void toggleLoop(SlashCommandInteractionEvent event) {
        isLooping = !isLooping;
        event.getHook().sendMessageEmbeds(config.getEmbedSongMessageService().loopStatus()).queue();
    }

    public void playFirstTrack(AudioPlayer player,SlashCommandInteractionEvent event) {
        if(!isLooping) {
            SongRequest nextTrack = getNextTrack();
            if (nextTrack != null) {
                currentSong = nextTrack;
                player.playTrack(nextTrack.getTrack());
                event.getHook().sendMessageEmbeds(config.getEmbedSongMessageService().sendFirstSong(nextTrack, queue.size())).queue();
            } else {
                event.getHook().sendMessageEmbeds(config.getEmbedSongMessageService().noMoreSongs()).queue();
                currentSong = null;
                player.stopTrack();
            }
        } else {
            if(currentSong == null) currentSong = getNextTrack();
            event.getHook().sendMessageEmbeds(config.getEmbedSongMessageService().sendFirstSong(currentSong, queue.size())).queue();
            player.playTrack(currentSong.getTrack().makeClone());
        }
    }


    public void playNextTrack(AudioPlayer player) {
        if(!isLooping) {
        SongRequest nextTrack = getNextTrack();
            if (nextTrack != null) {
                currentSong = nextTrack;
                player.playTrack(nextTrack.getTrack());
                config.getEmbedSongMessageService().sendNowPlayingSongEmbed(nextTrack, queue.size());
            } else {
                if(currentSong == null) currentSong = getNextTrack();
                currentSong.getSongChannel().sendMessageEmbeds(config.getEmbedSongMessageService().noMoreSongs()).queue();
                currentSong = null;
                player.stopTrack();
            }
        }
        else {
            config.getEmbedSongMessageService().sendNowPlayingSongEmbed(currentSong, queue.size());
            player.playTrack(currentSong.getTrack().makeClone());
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason reason) {
        System.out.println("[+] Track ended: " + track.getInfo().title + " | Reason: " + reason.name());
        if(reason.name().equals("FINISHED")) playNextTrack(player);
    }

    public ArrayList<SongRequest> getQueue() {
        return queue;
    }

    public SongRequest getCurrentSong() {
        return currentSong;
    }

    public boolean isLooping() {
        return isLooping;
    }
}
