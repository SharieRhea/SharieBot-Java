package sharierhea.events;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.eventsub.events.ChannelAdBreakBeginEvent;
import com.github.twitch4j.eventsub.subscriptions.SubscriptionTypes;
import sharierhea.SocketHandler;

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;

public class AdBegin extends EventListener<ChannelAdBreakBeginEvent> {
    private final SocketHandler socket;

    public AdBegin(SimpleEventHandler eventHandler, TwitchClient client, SocketHandler socket, OAuth2Credential broadcasterToken) {
        super(eventHandler, client, ChannelAdBreakBeginEvent.class);
        // Manually register for the event since it requires a broadcaster token with appropriate scopes
        client.getEventSocket().register(
                broadcasterToken,
                SubscriptionTypes.CHANNEL_AD_BREAK_BEGIN.prepareSubscription(
                        builder -> builder.broadcasterUserId("170582504").build(),
                        null
                )
        );
        this.socket = socket;
    }

    @Override
    protected void handleEvent(ChannelAdBreakBeginEvent event) {
        sendMessage("/me An ad is starting! Code/Art/Game will be paused!");
        String currentScene = socket.getCurrentScene();
        logger.debug("currentScene: " + currentScene);
        socket.changeScene("ads");

        int seconds = event.getLengthSeconds();
        logger.debug("Seconds: " + seconds);
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // Only change back to original scene if on the ads scene
                // Prevents from switching back to currentScene if a manual change was made
                // during the ad
                logger.debug("currentScene: " + socket.getCurrentScene());
                if (socket.getCurrentScene().equals("ads"))
                    socket.changeScene(currentScene);
                timer.cancel();
            }
        };
        // Delay the scene to change back after the length of the ad (in ms)
        timer.schedule(task, Duration.ofSeconds(seconds).toMillis());
    }
}
