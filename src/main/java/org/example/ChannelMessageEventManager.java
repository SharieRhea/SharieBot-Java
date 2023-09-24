package org.example;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class ChannelMessageEventManager {
    private TwitchClient twitchClient;
    private final String CHANNEL_NAME = "shariemakesart";

    public ChannelMessageEventManager(SimpleEventHandler eventHandler, TwitchClient client) {
        twitchClient = client;
        eventHandler.onEvent(ChannelMessageEvent.class, event -> parseCommand(event));
    }

    private void parseCommand(ChannelMessageEvent event) {
        if (event.getMessage().contains("!sleepy"))
            sleepyCommand(event);
        else if (event.getMessage().contains("!shiny"))
            shinyCommand(event);
    }
    @EventSubscriber
    public void printChannelMessage(ChannelMessageEvent event) {
        twitchClient.getChat().sendMessage("shariemakesart", "Test received.");
    }

    @EventSubscriber
    public void sleepyCommand(ChannelMessageEvent event) {
        twitchClient.getChat().sendMessage(CHANNEL_NAME, "Sleepy command triggered.");
    }

    @EventSubscriber
    public void shinyCommand(ChannelMessageEvent event) {
        twitchClient.getChat().sendMessage(CHANNEL_NAME, "Shiny command triggered.");
    }
}
