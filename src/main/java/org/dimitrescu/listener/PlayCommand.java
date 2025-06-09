package org.dimitrescu.listener;

import com.sedmelluq.discord.lavaplayer.player.*;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.dimitrescu.audio.LavaAudioSendHandler;
import org.dimitrescu.audio.SongRequest;
import org.dimitrescu.service.ConfigManager;
import org.dimitrescu.service.TrackQueueService;

// Listener for handling !play commands and managing audio playback
public class PlayCommand extends ListenerAdapter {
    private ConfigManager configManager;


    public PlayCommand(ConfigManager config) {
        this.configManager = config;
    }


    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equals("play")) {
            event.deferReply().queue();
            Guild guild = event.getGuild();
            AudioManager audioManager = guild.getAudioManager();
            if (event.getMember().getVoiceState().getChannel() != null) {
                audioManager.openAudioConnection(event.getMember().getVoiceState().getChannel());
                configManager.getConfig(event.getGuild()).currentChannel = event.getMember().getVoiceState().getChannel();


                LavaAudioSendHandler lavaHandler = new LavaAudioSendHandler(configManager.getConfig(event.getGuild()).getPlayer());
                AudioSendHandler audioSendHandler = lavaHandler;
                audioManager.setSendingHandler(audioSendHandler);

                String songQuery = event.getOption("song").getAsString();

                if (songQuery.startsWith("http://") || songQuery.startsWith("https://")) {
                    System.out.println("[+] Playing song from link: " + songQuery);
                } else {
                    System.out.println("[+] Searching for song: " + songQuery);
                    songQuery = "ytmsearch:" + songQuery;
                }
                loadSong(songQuery, configManager.getConfig(event.getGuild()).getPlayer(), event);
                guild.getSelfMember().deafen(true).queue();
            }
            else {
                //user who called /play not in voice
                event.getHook().sendMessageEmbeds(configManager.getConfig(event.getGuild()).getEmbedSongMessageService().userNotInVoice()).queue();
            }
        }
    }

    private void playTrack(AudioPlayer player, AudioTrack track, SlashCommandInteractionEvent event) {
        configManager.getConfig(event.getGuild()).getTrackQueueService().queue(new SongRequest(event.getUser(), track, event.getChannel(), event.getGuild()), player, event);
    }

    /**
     * Loads a song or playlist from the given link and plays it using the provided audio player.
     *
     * @param link   The link or search query for the song/playlist.
     * @param player The audio player to play the loaded track.
     */
    private void loadSong(String link, AudioPlayer player, SlashCommandInteractionEvent event) {
        configManager.getConfig(event.getGuild()).getPlayerManager().loadItem(link, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                System.out.println("[+] Track loaded: " + audioTrack.getInfo().title);
                playTrack(player, audioTrack, event);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                System.out.println("[+] Playlist loaded: " + audioPlaylist.getName());
                System.out.println("[+] Playing first track from playlist: " + audioPlaylist.getTracks().get(0).getInfo().title);
                playTrack(player, audioPlaylist.getTracks().get(0), event);
            }

            @Override
            public void noMatches() {
                System.out.println("[-] No matches found for: " + link);
                event.getHook().sendMessageEmbeds(configManager.getConfig(event.getGuild()).getEmbedSongMessageService().songNotFound()).queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                System.err.println("[-] Failed to load track: " + e.getMessage());
                event.getHook().sendMessageEmbeds(configManager.getConfig(event.getGuild()).getEmbedSongMessageService().songNotFound()).queue();
            }
        });
    }

    public TrackQueueService getTrackQueueService(Guild g) {
        return configManager.getConfig(g).getTrackQueueService();
    }
}
