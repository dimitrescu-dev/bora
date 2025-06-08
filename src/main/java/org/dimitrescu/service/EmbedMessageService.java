package org.dimitrescu.service;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.dimitrescu.audio.SongRequest;
import org.dimitrescu.util.Config;

import java.awt.*;

public class EmbedMessageService {

    private Config config;

    public EmbedMessageService(Config config) {
        this.config = config;
    }

    public void sendNowPlayingSongEmbed(SongRequest request, int queueSize) {
        AudioTrack track = request.getTrack();
        User user = request.getRequester();
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setColor(Color.decode("#EAE2CE"));
        embedBuilder.setTitle("⏯️  Now playing in " + request.getGuild().getName());
        embedBuilder.setThumbnail(config.getAlbumCoverService().getAlbumCoverUrl(track.getInfo().title,track.getInfo().author));

        embedBuilder.addField(track.getInfo().title, "by " + track.getInfo().author, false);
        embedBuilder.addField("Played by ",user.getAsMention(),true);
        embedBuilder.addField("Track Duration",String.format("``%d : %02d``", track.getInfo().length / 60000,
                (track.getInfo().length / 1000) % 60), true);
        embedBuilder.addField("Songs in Queue", "``" + queueSize + "``", true);

        System.out.println("[+] Sending embed");
        request.getSongChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }

    public MessageEmbed sendFirstSong(SongRequest request, int queueSize) {
        AudioTrack track = request.getTrack();
        User user = request.getRequester();
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setColor(Color.decode("#EAE2CE"));
        embedBuilder.setTitle("⏯️  Now playing in " + request.getGuild().getName());
        embedBuilder.setThumbnail(config.getAlbumCoverService().getAlbumCoverUrl(track.getInfo().title,track.getInfo().author));

        embedBuilder.addField(track.getInfo().title, "by " + track.getInfo().author, false);
        embedBuilder.addField("Played by ",user.getAsMention(),true);
        embedBuilder.addField("Track Duration",String.format("``%d : %02d``", track.getInfo().length / 60000,
                (track.getInfo().length / 1000) % 60), true);
        embedBuilder.addField("Songs in Queue", "``" + queueSize + "``", true);

        System.out.println("[+] Sending embed");
        return embedBuilder.build();
    }

    public MessageEmbed sendAddToQueue(SongRequest request, int positionInQueue) {
        AudioTrack track = request.getTrack();
        User user = request.getRequester();
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setColor(Color.decode("#EAE2CE"));
        embedBuilder.setTitle("⏯️  Song added to queue in " + request.getGuild().getName());
        embedBuilder.setThumbnail(config.getAlbumCoverService().getAlbumCoverUrl(track.getInfo().title,track.getInfo().author));

        embedBuilder.addField(track.getInfo().title, "by " + track.getInfo().author, false);
        embedBuilder.addField("Requested by ",user.getAsMention(),true);
        embedBuilder.addField("Track Duration",String.format("``%d : %02d``", track.getInfo().length / 60000,
                (track.getInfo().length / 1000) % 60), true);
        embedBuilder.addField("Position in Queue", "``" + positionInQueue + "``", true);

        System.out.println("[+] Sending embed");
        return embedBuilder.build();
    }

    public MessageEmbed skipSong(SongRequest request, SlashCommandInteractionEvent event) {
        AudioTrack track = request.getTrack();
        User user = event.getUser();
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setColor(Color.decode("#EAE2CE"));
        embedBuilder.setTitle("⏯️  Song skipped in " + request.getGuild().getName());
        embedBuilder.setThumbnail(config.getAlbumCoverService().getAlbumCoverUrl(track.getInfo().title,track.getInfo().author));

        embedBuilder.addField(track.getInfo().title, "by " + track.getInfo().author, false);
        embedBuilder.addField("Skipped by ",user.getAsMention(),true);

        System.out.println("[+] Sending embed");
        return embedBuilder.build();
    }

    public MessageEmbed displayQueue(SlashCommandInteractionEvent event) {
            EmbedBuilder embedBuilder = new EmbedBuilder();

            embedBuilder.setColor(Color.decode("#EAE2CE"));
            embedBuilder.setTitle("Song queue, requested by " + event.getMember().getAsMention());

            if (config.getTrackQueueService().getCurrentSong() != null)
                embedBuilder.addField("1 - " + config.getTrackQueueService().getCurrentSong().getTrack().getInfo().title,
                        "by " + config.getTrackQueueService().getCurrentSong().getTrack().getInfo().author, false);

            for (int i = 0; i < config.getTrackQueueService().getQueue().size(); i++) {
                SongRequest track = config.getTrackQueueService().getQueue().get(i);
                embedBuilder.addField((i + 2) + " - " + track.getTrack().getInfo().title, "by " + track.getTrack().getInfo().author, false);
            }

            return embedBuilder.build();
    }

    public MessageEmbed noMoreSongs(SongRequest lastSongs) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setColor(Color.decode("#EAE2CE"));
        embedBuilder.setTitle("❌ No more songs in the queue.");
        embedBuilder.setDescription("All the songs in the queue have already been played.");

        return embedBuilder.build();
    }
}
