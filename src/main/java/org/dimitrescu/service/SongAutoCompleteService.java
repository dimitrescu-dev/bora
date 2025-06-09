package org.dimitrescu.service;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.List;
import java.util.stream.Stream;

public class SongAutoCompleteService extends ListenerAdapter {
    private ConfigManager configManager;
    private String[] autoCompleteSongs;
    private CommandAutoCompleteInteractionEvent currentEvent;

    public SongAutoCompleteService(ConfigManager config) {
        this.configManager = config;
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if(event.getName().equals("play")) {
            currentEvent = event;
            loadSongs(event.getFocusedOption().getValue(),event.getGuild());
        }

    }

    public void updateList(List<AudioTrack> tracks) {
        autoCompleteSongs = new String[tracks.size()];
        for(int i = 0; i < tracks.size();i++) {
            autoCompleteSongs[i] = tracks.get(i).getInfo().author + " - " + tracks.get(i).getInfo().title;
            if(autoCompleteSongs[i].length() > 100) autoCompleteSongs[i] = autoCompleteSongs[i].substring(0,99);
        }
    }

    private void loadSongs(String currentString, Guild guild) {
        configManager.getConfig(guild).getPlayerManager().loadItem("ytmsearch:" + currentString, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {

            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                updateList(audioPlaylist.getTracks());
                List<Command.Choice> options = Stream.of(autoCompleteSongs)
                        .map(song -> new Command.Choice(song,song))
                        .toList();
                currentEvent.replyChoices(options).queue();
            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException e) {

            }
        });
    }
}
