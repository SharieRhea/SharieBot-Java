package sharierhea.events;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.eventsub.events.ChannelSubscribeEvent;
import com.github.twitch4j.eventsub.subscriptions.SubscriptionTypes;
import sharierhea.SocketHandler;

public class Subscription extends EventListener<ChannelSubscribeEvent> {
    private final SocketHandler socket;

    public Subscription(SimpleEventHandler eventHandler, TwitchClient twitchClient, SocketHandler socketHandler, OAuth2Credential broadcasterToken) {
        super(eventHandler, twitchClient, ChannelSubscribeEvent.class);
        socket = socketHandler;
        twitchClient.getEventSocket().register(
                broadcasterToken,
                SubscriptionTypes.CHANNEL_SUBSCRIBE.prepareSubscription(
                        builder -> builder.broadcasterUserId("170582504").build(),
                        null
                )
        );
    }

    @Override
    protected void handleEvent(ChannelSubscribeEvent event) {
        socket.showAndHideSource(socket.getCurrentScene(), "BlueGlimmer", 2);
    }
}
