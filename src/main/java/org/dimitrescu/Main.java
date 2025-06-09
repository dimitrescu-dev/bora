package org.dimitrescu;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.dimitrescu.listener.*;
import org.dimitrescu.service.ConfigManager;
import org.dimitrescu.service.SongAutoCompleteService;
import org.dimitrescu.util.Config;

public class Main {
    public static void main(String[] args) {
        ConfigManager config = new ConfigManager();

        // Build the JDA (Java Discord API) instance with the bot token and enable message content intent
        JDA api = JDABuilder.createDefault(System.getenv("DISCORD_BOT_TOKEN"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build();


        api.updateCommands().addCommands(
                Commands.slash("play", "Play a song or add it to the queue")
                        .addOption(OptionType.STRING, "song", "The name or URL of the song to play", true, true),
                Commands.slash("skip", "Skip the current song and play the next one in the queue"),
                Commands.slash("queue", "Show the current song queue"),
                Commands.slash("loop", "Toggle looping for the current song"),
                Commands.slash("shuffle", "Shuffle the songs in the queue"),
                Commands.slash("leave","Leave the current channel and stop playing"),
                Commands.slash("pause", "Pause/Unpause the current song")
        ).queue();


        PlayCommand playCommand = new PlayCommand(config);
        SkipCommand skipCommand =  new SkipCommand(config);
        QueueCommand queueCommand = new QueueCommand(config);
        LoopCommand loopCommand = new LoopCommand(config);
        PauseCommand pauseCommand = new PauseCommand(config);
        LeaveCommand leaveCommand = new LeaveCommand(config);
        ShuffleCommand shuffleCommand = new ShuffleCommand(config);

        ButtonManager buttonManager = new ButtonManager(config);
        VoiceHandler voiceHandler = new VoiceHandler(config);
        SongAutoCompleteService service = new SongAutoCompleteService(config);

        api.addEventListener(playCommand);
        api.addEventListener(shuffleCommand);
        api.addEventListener(voiceHandler);
        api.addEventListener(skipCommand);
        api.addEventListener(loopCommand);
        api.addEventListener(queueCommand);
        api.addEventListener(leaveCommand);
        api.addEventListener(pauseCommand);
        api.addEventListener(buttonManager);
        api.addEventListener(service);
    }
}
