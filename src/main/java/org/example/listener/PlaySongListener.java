package org.example.listener;

import com.sedmelluq.discord.lavaplayer.player.*;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.example.audio.LavaAudioSendHandler;

// Listener for handling !play commands and managing audio playback
public class PlaySongListener extends ListenerAdapter {
    private final AudioPlayerManager playerManager;
    private final YoutubeAudioSourceManager youtubeSourceManager;

    public PlaySongListener() {
        youtubeSourceManager = new YoutubeAudioSourceManager();
        playerManager = new DefaultAudioPlayerManager();
        playerManager.registerSourceManager(youtubeSourceManager);
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    /**
     * Handles incoming messages and processes the !play command.
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String[] split = event.getMessage().getContentRaw().split(" ");
        if(split[0].equals("!play")) {
            Guild guild = event.getGuild();
            AudioManager audioManager = guild.getAudioManager();
            audioManager.openAudioConnection(event.getMember().getVoiceState().getChannel());

            AudioPlayer player = playerManager.createPlayer();
            LavaAudioSendHandler lavaHandler = new LavaAudioSendHandler(player);
            AudioSendHandler audioSendHandler = lavaHandler;

            audioManager.setSendingHandler(audioSendHandler);
            String songQuery = event.getMessage().getContentRaw().substring(6);

            if(songQuery.startsWith("http://") || songQuery.startsWith("https://")) {
                System.out.println("[+] Playing song from link: " + songQuery);
            }
            else {
                System.out.println("[+] Searching for song: " + songQuery);
                songQuery = "ytsearch:" + songQuery;
            }

            loadSong(songQuery,player);
        }

    }

    /**
     * Loads a song or playlist from the given link and plays it using the provided audio player.
     *
     * @param link The link or search query for the song/playlist.
     * @param player The audio player to play the loaded track.
     */
    private void loadSong(String link,AudioPlayer player) {
        playerManager.loadItem(link, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                System.out.println("[+] Track loaded: " + audioTrack.getInfo().title);
                player.playTrack(audioTrack);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                System.out.println("[+] Playlist loaded: " + audioPlaylist.getName());
                for(AudioTrack track : audioPlaylist.getTracks()) {
                    System.out.println("[+] Track: " + track.getInfo().title);
                }
                System.out.println("[+] Playing first track from playlist: " + audioPlaylist.getTracks().get(0).getInfo().title);
                player.playTrack(audioPlaylist.getTracks().get(0));
            }

            @Override
            public void noMatches() {
                System.out.println("[-] No matches found for: " + link);
                // Handle no matches found
            }

            @Override
            public void loadFailed(FriendlyException e) {
                System.err.println("[-] Failed to load track: " + e.getMessage());
                // Handle loading failure
            }
        });
    }
}
