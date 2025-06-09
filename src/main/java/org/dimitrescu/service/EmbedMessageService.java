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
        embedBuilder.setThumbnail(config.getAlbumCoverService().getAlbumCoverUrl(track.getInfo().title,track.getInfo().author,track.getInfo().uri));

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
        embedBuilder.setThumbnail(config.getAlbumCoverService().getAlbumCoverUrl(track.getInfo().title,track.getInfo().author,track.getInfo().uri));

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
        embedBuilder.setThumbnail(config.getAlbumCoverService().getAlbumCoverUrl(track.getInfo().title,track.getInfo().author,track.getInfo().uri));

        embedBuilder.addField(track.getInfo().title, "by " + track.getInfo().author, false);
        embedBuilder.addField("Requested by ",user.getAsMention(),true);
        embedBuilder.addField("Track Duration",String.format("``%d : %02d``", track.getInfo().length / 60000,
                (track.getInfo().length / 1000) % 60), true);
        embedBuilder.addField("Position in Queue", "``" + positionInQueue + "``", true);

        System.out.println("[+] Sending embed");
        return embedBuilder.build();
    }

    public MessageEmbed displayQueue(SlashCommandInteractionEvent event) {
            EmbedBuilder embedBuilder = new EmbedBuilder();

            embedBuilder.setColor(Color.decode("#EAE2CE"));
            embedBuilder.setTitle("🎸  Song queue");

            if (config.getTrackQueueService().getCurrentSong() != null)
                embedBuilder.addField("▶️ - " + config.getTrackQueueService().getCurrentSong().getTrack().getInfo().title,
                        "by " + config.getTrackQueueService().getCurrentSong().getTrack().getInfo().author, false);


            for (int i = 0; i < config.getTrackQueueService().getQueue().size(); i++) {
                SongRequest track = config.getTrackQueueService().getQueue().get(i);
                embedBuilder.addField((i + 1) + " - " + track.getTrack().getInfo().title, "by " + track.getTrack().getInfo().author, false);
            }

            return embedBuilder.build();
    }

    public MessageEmbed noMoreSongs() {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setColor(Color.decode("#EAE2CE"));
        embedBuilder.setTitle("❌ No more songs in the queue.");
        embedBuilder.setDescription("All the songs in the queue have already been played.");

        return embedBuilder.build();
    }

    public MessageEmbed shuffleSongs() {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setColor(Color.decode("#EAE2CE"));
        embedBuilder.setTitle("🔀 Current queue shuffled.");
        embedBuilder.setDescription("All the songs in the current queue have been mixed.");

        return embedBuilder.build();
    }

    public MessageEmbed loopStatus() {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setColor(Color.decode("#EAE2CE"));
        embedBuilder.setTitle(config.getTrackQueueService().isLooping() ? "✅ Started looping the current song." : "❌ Stopped looping the current song.");
        embedBuilder.setDescription(config.getTrackQueueService().isLooping() ? "The current song will be repeatedly played, indefinetly."
                : "The next song played will be from the queue, not the loop.");

        return embedBuilder.build();
    }
}
