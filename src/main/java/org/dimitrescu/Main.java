package org.dimitrescu;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.dimitrescu.listener.*;
import org.dimitrescu.service.SongAutoCompleteService;
import org.dimitrescu.util.Config;

public class Main {
    public static void main(String[] args) {
        Config config = new Config();

        // Build the JDA (Java Discord API) instance with the bot token and enable message content intent
        JDA api = JDABuilder.createDefault(System.getenv("DISCORD_BOT_TOKEN"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build();

        api.updateCommands().addCommands(
                Commands.slash("play","Play")
                        .addOption(OptionType.STRING,"song","Song",true,true),
                Commands.slash("skip", "Skip"),
                Commands.slash("queue","Queue"),
                Commands.slash("loop","Loop"),
                Commands.slash("shuffle", "Shuffle")
        ).queue();

        PlayCommand playCommand = new PlayCommand(config);
        SkipCommand skipCommand =  new SkipCommand(config);
        QueueCommand queueCommand = new QueueCommand(config);
        LoopCommand loopCommand = new LoopCommand(config);
        ShuffleCommand shuffleCommand = new ShuffleCommand(config);
        SongAutoCompleteService service = new SongAutoCompleteService(config);

        api.addEventListener(playCommand);
        api.addEventListener(shuffleCommand);
        api.addEventListener(skipCommand);
        api.addEventListener(loopCommand);
        api.addEventListener(queueCommand);
        api.addEventListener(service);
    }
}
