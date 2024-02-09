package sharierhea.events;

import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static sharierhea.Launcher.*;

public abstract class EventListener<T> {
    // Every command must have access to the twitchClient and the channel's name.
    protected Logger logger = LoggerFactory.getLogger(EventListener.class);

    protected EventListener(Class<T> eventClass) {
        eventHandler.onEvent(eventClass, this::handleEvent);
    }

    @EventSubscriber
    protected abstract void handleEvent(T event);

    protected void sendMessage(String message) {
        twitchClient.getChat().sendMessage(CHANNEL_NAME, message);
    }
}
