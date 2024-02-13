package sharierhea.events;

import com.github.twitch4j.eventsub.events.ChannelSubscribeEvent;
import com.github.twitch4j.eventsub.subscriptions.SubscriptionTypes;
import sharierhea.SocketHandler;

import java.io.FileWriter;
import java.io.IOException;

import static sharierhea.Launcher.*;

public class Subscription extends EventListener<ChannelSubscribeEvent> {
    private final SocketHandler OBS_SOCKET;

    public Subscription(SocketHandler socket) throws Exception {
        super(ChannelSubscribeEvent.class);
        twitchClient.getEventSocket().register(
                broadcasterToken,
                SubscriptionTypes.CHANNEL_SUBSCRIBE.prepareSubscription(
                        builder -> builder.broadcasterUserId(CHANNEL_ID).build(),
                        null
                )
        );
        if (socket == null)
            throw new Exception("Websocket has not been initialized.");
        else
            OBS_SOCKET = socket;
    }

    @Override
    protected void handleEvent(ChannelSubscribeEvent event) {
        OBS_SOCKET.showAndHideSource(OBS_SOCKET.getCurrentScene(), "BlueGlimmer", 2);

        String subscriber = event.getUserName();
        try (FileWriter writer = new FileWriter("src/resources/OBSTextFiles/recentSubscriber.txt", false)) {
            writer.write(subscriber);
            writer.flush();
        } catch (IOException ioException) {
            logger.error("Failed to write recentSubscriber to file", ioException);
        }

        var response = twitchClient.getHelix().getSubscriptions(broadcasterToken.getAccessToken(), CHANNEL_ID, null, null, null).execute();
        String numberOfSubscribers = response.getTotal().toString();

        try (FileWriter writer = new FileWriter("src/resources/OBSTextFiles/totalSubscribers.txt", false)) {
            writer.write(numberOfSubscribers);
            writer.flush();
        } catch (IOException ioException) {
            logger.error("Failed to write total subscribers to file", ioException);
        }
    }
}
