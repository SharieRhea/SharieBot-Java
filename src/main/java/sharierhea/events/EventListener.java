package sharierhea.events;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.TwitchClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class EventListener<T> {
    // Every command must have access to the twitchClient and the channel's name.
    protected TwitchClient twitchClient;
    // todo: move channel name into a text file for re-usability
    // note: check to see if channel name has been hardcoded anywhere else
    private final String CHANNEL_NAME = "shariemakesart";
    protected Logger logger = LoggerFactory.getLogger(EventListener.class);

    protected EventListener(SimpleEventHandler eventHandler, TwitchClient client, Class<T> eventClass) {
        twitchClient = client;
        eventHandler.onEvent(eventClass, this::handleEvent);
    }

    @EventSubscriber
    protected abstract void handleEvent(T event);

    protected void sendMessage(String message) {
        twitchClient.getChat().sendMessage(CHANNEL_NAME, message);
    }
}
