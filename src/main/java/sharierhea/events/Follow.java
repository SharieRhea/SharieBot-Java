package sharierhea.events;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.eventsub.events.ChannelFollowEvent;
import sharierhea.SocketHandler;

public class Follow extends EventListener<ChannelFollowEvent> {
    private final SocketHandler socket;

    public Follow(SimpleEventHandler eventHandler, TwitchClient twitchClient, SocketHandler socket) {
        super(eventHandler, twitchClient, ChannelFollowEvent.class);
        this.socket = socket;
    }

    @Override
    protected void handleEvent(ChannelFollowEvent event) {
        socket.showAndHideSource(socket.getCurrentScene(), "WhiteGlimmer", 2);
    }
}
