package org.example.listener;

import com.sedmelluq.discord.lavaplayer.player.*;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import dev.lavalink.youtube.clients.Music;
import dev.lavalink.youtube.clients.Web;
import dev.lavalink.youtube.clients.skeleton.Client;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.example.audio.LavaAudioSendHandler;
import org.example.service.AlbumCoverService;

import java.awt.*;

// Listener for handling !play commands and managing audio playback
public class PlaySongListener extends ListenerAdapter {
    private final AudioPlayerManager playerManager;
    private final AlbumCoverService albumCoverService;

    public PlaySongListener() {
        playerManager = new DefaultAudioPlayerManager();
        albumCoverService = new AlbumCoverService();
        YoutubeAudioSourceManager youtubeSource = new YoutubeAudioSourceManager();
        playerManager.registerSourceManager(youtubeSource);
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
                songQuery = "ytmsearch:" + songQuery;
            }

            loadSong(songQuery,player,event);
        }

    }

    private void playTrack(AudioPlayer player, AudioTrack track, MessageReceivedEvent event) {
        player.playTrack(track);
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setColor(Color.decode("#EAE2CE"));
        embedBuilder.setTitle("⏯️ Now playing in " + event.getGuild().getName());
        embedBuilder.setThumbnail(albumCoverService.getAlbumCoverUrl(track.getInfo().title,track.getInfo().author));

        embedBuilder.addField(track.getInfo().title, "by " + track.getInfo().author, false);
        embedBuilder.addBlankField(false);
        embedBuilder.addField("Played by ",event.getAuthor().getAsMention(),true);
        embedBuilder.addField("Track Duration",String.format("``%d : %02d``", track.getInfo().length / 60000,
                (track.getInfo().length / 1000) % 60), true);
        embedBuilder.addField("Position in Queue", "``" + 0 + "``", true);

        System.out.println("[+] Sending embed");
        event.getMessage().getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }

    /**
     * Loads a song or playlist from the given link and plays it using the provided audio player.
     *
     * @param link The link or search query for the song/playlist.
     * @param player The audio player to play the loaded track.
     */
    private void loadSong(String link,AudioPlayer player,MessageReceivedEvent event) {
        playerManager.loadItem(link, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                System.out.println("[+] Track loaded: " + audioTrack.getInfo().title);
                playTrack(player, audioTrack,event);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                System.out.println("[+] Playlist loaded: " + audioPlaylist.getName());
                for(AudioTrack track : audioPlaylist.getTracks()) {
                    System.out.println("[+] Track: " + track.getInfo().title);
                }
                System.out.println("[+] Playing first track from playlist: " + audioPlaylist.getTracks().get(0).getInfo().title);
                playTrack(player,audioPlaylist.getTracks().get(0),event);
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
