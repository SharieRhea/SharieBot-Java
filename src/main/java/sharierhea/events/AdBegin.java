package sharierhea.events;


import com.github.twitch4j.eventsub.events.ChannelAdBreakBeginEvent;
import com.github.twitch4j.eventsub.subscriptions.SubscriptionTypes;
import sharierhea.SocketHandler;

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;

import static sharierhea.Launcher.*;

public class AdBegin extends EventListener<ChannelAdBreakBeginEvent> {
    private final SocketHandler OBS_SOCKET;

    public AdBegin(SocketHandler socket) throws Exception {
        super(ChannelAdBreakBeginEvent.class);
        // Manually register for the event since it requires a broadcaster token with appropriate scopes
        twitchClient.getEventSocket().register(
                broadcasterToken,
                SubscriptionTypes.CHANNEL_AD_BREAK_BEGIN.prepareSubscription(
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
    protected void handleEvent(ChannelAdBreakBeginEvent event) {
        sendMessage("/me An ad is starting! Code/Art/Game will be paused!");
        String currentScene = OBS_SOCKET.getCurrentScene();
        logger.debug("currentScene: " + currentScene);
        OBS_SOCKET.changeScene("ads");

        int seconds = event.getLengthSeconds();
        logger.debug("Seconds: " + seconds);
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // Only change back to original scene if on the ads scene
                // Prevents from switching back to currentScene if a manual change was made
                // during the ad
                logger.debug("currentScene: " + OBS_SOCKET.getCurrentScene());
                if (OBS_SOCKET.getCurrentScene().equals("ads"))
                    OBS_SOCKET.changeScene(currentScene);
                timer.cancel();
            }
        };
        // Delay the scene to change back after the length of the ad (in ms)
        timer.schedule(task, Duration.ofSeconds(seconds).toMillis());
    }
}
