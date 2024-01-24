package sharierhea.events;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.pubsub.domain.VideoPlaybackData;
import com.github.twitch4j.pubsub.events.VideoPlaybackEvent;
import sharierhea.SocketHandler;

import java.util.Timer;
import java.util.TimerTask;

public class AdBegin extends EventListener<VideoPlaybackEvent> {
    private final SocketHandler socket;

    public AdBegin(SimpleEventHandler eventHandler, TwitchClient client, SocketHandler socket) {
        super(eventHandler, client, VideoPlaybackEvent.class);
        this.socket = socket;
        client.getPubSub().listenForVideoPlaybackEvents(null, "170582504");
    }

    @Override
    protected void handleEvent(VideoPlaybackEvent event) {
        if (event.getData().getType() != VideoPlaybackData.Type.COMMERCIAL)
            return;

        sendMessage("/me An ad is starting! Code/Art/Game will be paused!");
        String currentScene = socket.getCurrentScene();
        logger.debug("currentScene: " + currentScene);
        socket.changeScene("ads");

        int seconds = event.getData().getLength();
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
            }
        };
        // Delay the scene to change back after the length of the ad (in ms)
        timer.schedule(task, seconds * 1000L);
    }
}
