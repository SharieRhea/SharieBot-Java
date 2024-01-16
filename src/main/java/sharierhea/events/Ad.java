package sharierhea.events;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.eventsub.events.ChannelAdBreakBeginEvent;

public class Ad extends EventListener<ChannelAdBreakBeginEvent> {

    public Ad(SimpleEventHandler eventHandler, TwitchClient client) {
        super(eventHandler, client, ChannelAdBreakBeginEvent.class);
    }

    @Override
    protected void handleEvent(ChannelAdBreakBeginEvent event) {
        sendMessage("/me An ad is starting! Code/Art/Game will be paused!");
        // todo: OBS websocket library for automatically changing the scene in OBS?
    }
}
