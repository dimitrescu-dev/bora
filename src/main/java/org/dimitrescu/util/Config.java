package org.dimitrescu.util;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import org.dimitrescu.service.AlbumCoverService;
import org.dimitrescu.service.EmbedSongMessageService;
import org.dimitrescu.service.TrackQueueService;

public class Config {
    private final AudioPlayerManager playerManager;
    private final AlbumCoverService albumCoverService;
    private final TrackQueueService trackQueueService;
    private final EmbedSongMessageService embedSongMessageService;
    private final AudioPlayer player;

    public Config() {
        playerManager = new DefaultAudioPlayerManager();
        albumCoverService = new AlbumCoverService();
        embedSongMessageService = new EmbedSongMessageService();
        trackQueueService = new TrackQueueService(this);

        YoutubeAudioSourceManager youtubeSource = new YoutubeAudioSourceManager();
        playerManager.registerSourceManager(youtubeSource);
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);

        player = playerManager.createPlayer();
        player.addListener(trackQueueService);
    }

    public AudioPlayerManager getPlayerManager() {
        return playerManager;
    }

    public AlbumCoverService getAlbumCoverService() {
        return albumCoverService;
    }

    public TrackQueueService getTrackQueueService() {
        return trackQueueService;
    }

    public EmbedSongMessageService getEmbedSongMessageService() {
        return embedSongMessageService;
    }

    public AudioPlayer getPlayer() {
        return player;
    }
}
