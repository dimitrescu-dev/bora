package org.dimitrescu.listener;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.dimitrescu.util.Config;

public class ButtonManager extends ListenerAdapter {
    private Config config;

    public ButtonManager(Config config) {
        this.config = config;
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        event.deferEdit().queue();
        if(event.getComponentId().equals("skip")) config.getTrackQueueService().buttonSkip(config.getPlayer());
        else if(event.getComponentId().equals("shuffle")) config.getTrackQueueService().buttonShuffle();
        else if(event.getComponentId().equals("loop")) config.getTrackQueueService().buttonToggleLoop();
        else if(event.getComponentId().equals("queue")) event.getChannel().sendMessageEmbeds(config.getEmbedSongMessageService().displayQueue()).queue();
    }
}
