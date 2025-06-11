package org.dimitrescu.listener;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.dimitrescu.service.ConfigManager;

public class ButtonManager extends ListenerAdapter {
    private ConfigManager configManager;

    public ButtonManager(ConfigManager config) {
        this.configManager = config;
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        event.deferEdit().queue();
        if(event.getComponentId().equals("skip")) configManager.getConfig(event.getGuild()).getTrackQueueService().buttonSkip(configManager.getConfig(event.getGuild()).getPlayer());
        else if(event.getComponentId().equals("shuffle")) configManager.getConfig(event.getGuild()).getTrackQueueService().buttonShuffle();
        else if(event.getComponentId().equals("loop")) configManager.getConfig(event.getGuild()).getTrackQueueService().buttonToggleLoop();
        else if(event.getComponentId().equals("queue")) event.getChannel().sendMessageEmbeds(configManager.getConfig(event.getGuild()).getEmbedSongMessageService().displayQueue()).queue();
        else if(event.getComponentId().equals("pause")) configManager.getConfig(event.getGuild()).getTrackQueueService().buttonTogglePause();
        else if(event.getComponentId().equals("aiplaylist")) configManager.getConfig(event.getGuild()).getTrackQueueService().buttonGetAi();
    }
}
