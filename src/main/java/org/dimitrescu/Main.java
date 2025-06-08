package org.dimitrescu;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.dimitrescu.listener.PlayCommand;
import org.dimitrescu.listener.SkipCommand;
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
                        .addOption(OptionType.STRING,"song","Song"),
                Commands.slash("skip", "Skip")
        ).queue();

        PlayCommand playCommand = new PlayCommand(config);
        SkipCommand skipCommand =  new SkipCommand(config);

        api.addEventListener(playCommand);
        api.addEventListener(skipCommand);
    }
}
