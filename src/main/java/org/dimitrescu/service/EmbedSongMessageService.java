package org.dimitrescu.service;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import org.dimitrescu.audio.SongRequest;

import java.awt.*;

public class EmbedSongMessageService {

    AlbumCoverService albumCoverService;

    public EmbedSongMessageService() {
        albumCoverService = new AlbumCoverService();
    }

    public void sendNowPlayingSongEmbed(SongRequest request, int queueSize) {
        AudioTrack track = request.getTrack();
        User user = request.getRequester();
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setColor(Color.decode("#EAE2CE"));
        embedBuilder.setTitle("⏯️  Now playing in " + request.getGuild().getName());
        embedBuilder.setThumbnail(albumCoverService.getAlbumCoverUrl(track.getInfo().title,track.getInfo().author));

        embedBuilder.addField(track.getInfo().title, "by " + track.getInfo().author, false);
        embedBuilder.addField("Played by ",user.getAsMention(),true);
        embedBuilder.addField("Track Duration",String.format("``%d : %02d``", track.getInfo().length / 60000,
                (track.getInfo().length / 1000) % 60), true);
        embedBuilder.addField("Songs in Queue", "``" + queueSize + "``", true);

        System.out.println("[+] Sending embed");
        request.getSongChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }

    public void sendAddToQueue(SongRequest request, int positionInQueue) {
        AudioTrack track = request.getTrack();
        User user = request.getRequester();
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setColor(Color.decode("#EAE2CE"));
        embedBuilder.setTitle("⏯️  Song added to queue in " + request.getGuild().getName());
        embedBuilder.setThumbnail(albumCoverService.getAlbumCoverUrl(track.getInfo().title,track.getInfo().author));

        embedBuilder.addField(track.getInfo().title, "by " + track.getInfo().author, false);
        embedBuilder.addField("Requested by ",user.getAsMention(),true);
        embedBuilder.addField("Track Duration",String.format("``%d : %02d``", track.getInfo().length / 60000,
                (track.getInfo().length / 1000) % 60), true);
        embedBuilder.addField("Position in Queue", "``" + positionInQueue + "``", true);

        System.out.println("[+] Sending embed");
        request.getSongChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }
}
