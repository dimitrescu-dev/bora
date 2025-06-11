package org.dimitrescu.service;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import org.dimitrescu.audio.SongRequest;
import org.dimitrescu.util.Config;

import java.util.ArrayList;
import java.util.Random;

public class TrackQueueService extends AudioEventAdapter {
    private ArrayList<SongRequest> queue;
    private SongRequest currentSong = null;
    private AIRadioService radioService;
    private Config config;

    private boolean isLooping = false;

    public TrackQueueService(Config config) {
        radioService = new AIRadioService();
        this.config = config;
        queue = new ArrayList<>();
    }

    public void queue(SongRequest request, AudioPlayer player, SlashCommandInteractionEvent event) {
        queue.add(request);

        if(queue.size() == 1 && player.getPlayingTrack() == null && currentSong == null) playFirstTrack(player,event);
        else event.getHook().sendMessageEmbeds(config.getEmbedSongMessageService().sendAddToQueue(request, queue.size())).queue();
    }

    public void clearQueue() {
        currentSong = null;
        config.getPlayer().stopTrack();
        queue.clear();
    }

    public void skip(AudioPlayer player,SlashCommandInteractionEvent event) {
        if(isLooping) currentSong = getNextTrack();
        playFirstTrack(player,event);
    }

    public void buttonSkip(AudioPlayer player) {
        if(isLooping) currentSong = getNextTrack();
        playNextTrack(player);
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

    public void buttonShuffle() {
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
            config.lastPlayMessage.getChannel().sendMessageEmbeds(config.getEmbedSongMessageService().shuffleSongs()).queue();
        }

        else config.lastPlayMessage.getChannel().sendMessageEmbeds(config.getEmbedSongMessageService().noMoreSongs()).queue();
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

    public void buttonGetAi() {
        for(String song : radioService.getAiSongRecommendations(queue,currentSong)) {
            loadSong(song);
        }
        currentSong.getSongChannel().sendMessageEmbeds(config.getEmbedSongMessageService().addedAiSongs(radioService.getAiSongRecommendations(queue,currentSong))).queue();
    }

    public void getAi(SlashCommandInteractionEvent event) {
        for(String song : radioService.getAiSongRecommendations(queue,currentSong)) {
            loadSong(song);
        }
        event.getHook().sendMessageEmbeds(config.getEmbedSongMessageService().addedAiSongs(radioService.getAiSongRecommendations(queue,currentSong))).queue();
    }



    public void togglePause(SlashCommandInteractionEvent event) {
        System.out.println("[+] Setting player pause to : " + !config.getPlayer().isPaused());
        config.getPlayer().setPaused(!config.getPlayer().isPaused());
        event.getHook().sendMessageEmbeds(config.getEmbedSongMessageService().pause(!config.getPlayer().isPaused())).queue();
    }

    public void buttonTogglePause() {
        System.out.println("[+] Setting player pause to : " + !config.getPlayer().isPaused());
        config.getPlayer().setPaused(!config.getPlayer().isPaused());
        config.lastPlayMessage.getChannel().sendMessageEmbeds(config.getEmbedSongMessageService().pause(!config.getPlayer().isPaused())).queue();
    }

    public void buttonToggleLoop() {
        isLooping = !isLooping;
        config.lastPlayMessage.getChannel().sendMessageEmbeds(config.getEmbedSongMessageService().loopStatus()).queue();
    }

    public void playFirstTrack(AudioPlayer player,SlashCommandInteractionEvent event) {
        if(config.lastPlayMessage != null) config.lastPlayMessage.editMessageComponents().queue();
        if(!isLooping) {
            SongRequest nextTrack = getNextTrack();
            if (nextTrack != null) {
                currentSong = nextTrack;
                player.playTrack(nextTrack.getTrack());
                addPlaybackButtons(event.getHook().sendMessageEmbeds(config.getEmbedSongMessageService().sendFirstSong(nextTrack, queue.size()))).queue(message -> {
                    config.lastPlayMessage = message;
                });
            } else {
                event.getHook().sendMessageEmbeds(config.getEmbedSongMessageService().noMoreSongs()).queue();
                currentSong.getGuild().getAudioManager().closeAudioConnection();
                currentSong = null;
                player.stopTrack();
            }
        } else {
            if(currentSong == null) currentSong = getNextTrack();
            addPlaybackButtons(event.getHook().sendMessageEmbeds(config.getEmbedSongMessageService().sendFirstSong(currentSong,queue.size()))).queue(message -> {
                config.lastPlayMessage = message;
            });
            player.playTrack(currentSong.getTrack().makeClone());
        }
    }

    public WebhookMessageCreateAction<Message> addPlaybackButtons(WebhookMessageCreateAction<Message> embed) {
        embed.addActionRow(Button.secondary("skip","⏭️"),Button.secondary("shuffle","🔀"),Button.secondary("pause","⏯️"));
        embed.addActionRow(Button.secondary("aiplaylist","🤖"),Button.secondary("loop","🔁"),Button.secondary("queue","📃"));
        return embed;
    }


    public void playNextTrack(AudioPlayer player) {
        if(config.lastPlayMessage != null) config.lastPlayMessage.editMessageComponents().queue();
        if(!isLooping) {
        SongRequest nextTrack = getNextTrack();
            if (nextTrack != null) {
                currentSong = nextTrack;
                player.playTrack(nextTrack.getTrack());
                 config.getEmbedSongMessageService().sendNowPlayingSongEmbed(nextTrack, queue.size());
            } else {
                if(currentSong == null) currentSong = getNextTrack();
                currentSong.getSongChannel().sendMessageEmbeds(config.getEmbedSongMessageService().noMoreSongs()).queue();
                currentSong.getGuild().getAudioManager().closeAudioConnection();
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

    private void loadSong(String songName) {
        config.getPlayerManager().loadItem("ytmsearch:" + songName, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {

            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                queue.add(new SongRequest(currentSong.getGuild().getJDA().getSelfUser(),audioPlaylist.getTracks().get(0),currentSong.getSongChannel(),currentSong.getGuild()));
            }

            @Override
            public void noMatches() {
            }

            @Override
            public void loadFailed(FriendlyException e) {
            }
        });
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
