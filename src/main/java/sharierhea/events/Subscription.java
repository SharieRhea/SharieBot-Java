package sharierhea.events;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.eventsub.events.ChannelSubscribeEvent;
import com.github.twitch4j.eventsub.subscriptions.SubscriptionTypes;
import sharierhea.SocketHandler;

import java.io.FileWriter;
import java.io.IOException;

public class Subscription extends EventListener<ChannelSubscribeEvent> {
    private final SocketHandler socket;
    private final OAuth2Credential broadcasterToken;

    public Subscription(SimpleEventHandler eventHandler, TwitchClient twitchClient, SocketHandler socketHandler, OAuth2Credential broadcasterToken) {
        super(eventHandler, twitchClient, ChannelSubscribeEvent.class);
        socket = socketHandler;
        this.broadcasterToken = broadcasterToken;
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

        String subscriber = event.getUserName();
        try (FileWriter writer = new FileWriter("src/resources/OBSTextFiles/recentSubscriber.txt", false)) {
            writer.write(subscriber);
            writer.flush();
        }
        catch (IOException ioException) {
            logger.error("Failed to write recentSubscriber to file", ioException);
        }

        var response = twitchClient.getHelix().getSubscriptions(broadcasterToken.getAccessToken(), "170582504", null, null, null).execute();
        String numberOfSubscribers = response.getTotal().toString();

        try (FileWriter writer = new FileWriter("src/resources/OBSTextFiles/totalSubscribers.txt", false)) {
            writer.write(numberOfSubscribers);
            writer.flush();
        }
        catch (IOException ioException) {
            logger.error("Failed to write total subscribers to file", ioException);
        }
    }
}
