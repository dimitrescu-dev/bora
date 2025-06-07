package org.example;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.example.listener.PlaySongListener;

public class Main {
    public static void main(String[] args) {
        // Build the JDA (Java Discord API) instance with the bot token and enable message content intent
        JDA api = JDABuilder.createDefault(System.getenv("DISCORD_BOT_TOKEN"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build();

        // Register the PlaySongListener to handle song-related commands/events
        PlaySongListener playSongListener = new PlaySongListener();
        api.addEventListener(playSongListener);
    }
}
