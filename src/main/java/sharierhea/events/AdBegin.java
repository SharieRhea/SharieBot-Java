package sharierhea.events;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.eventsub.events.ChannelAdBreakBeginEvent;
import sharierhea.SocketHandler;

import java.util.Timer;
import java.util.TimerTask;

// Todo: see why event is never registered, could be a eventsub issue from Twitch4J?

public class AdBegin extends EventListener<ChannelAdBreakBeginEvent> {
    private SocketHandler socket;

    public AdBegin(SimpleEventHandler eventHandler, TwitchClient client, SocketHandler socket) {
        super(eventHandler, client, ChannelAdBreakBeginEvent.class);
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
            }
        };
        // Delay the scene to change back after the length of the ad (in ms)
        timer.schedule(task, seconds * 1000L);
    }
}
