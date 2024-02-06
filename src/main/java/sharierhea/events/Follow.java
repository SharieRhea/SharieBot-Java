package sharierhea.events;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.FollowEvent;
import sharierhea.SocketHandler;

import java.io.FileWriter;
import java.io.IOException;

public class Follow extends EventListener<FollowEvent> {
    private final SocketHandler socket;
    private final OAuth2Credential token;

    public Follow(SimpleEventHandler eventHandler, TwitchClient twitchClient, OAuth2Credential broadcasterToken, SocketHandler socket) {
        super(eventHandler, twitchClient, FollowEvent.class);
        this.socket = socket;
        this.token = broadcasterToken;
    }

    @Override
    protected void handleEvent(FollowEvent event) {
        socket.showAndHideSource(socket.getCurrentScene(), "WhiteGlimmer", 2);

        // Grab the most recent follower to populate text file for OBS
        String follower = event.getUser().getName();
        logger.debug("Follower:" + follower);
        try (FileWriter writer = new FileWriter("src/resources/OBSTextFiles/recentFollower.txt", false)) {
            writer.write(follower);
            writer.flush();
        }
        catch (IOException ioException) {
            logger.error("Failed to write recentFollower to file", ioException);
        }

        var response = twitchClient.getHelix().getChannelFollowers(token.getAccessToken(), "170582504", null, null, null).execute();
        String numberOfFollowers = response.getTotal().toString();
        logger.debug("Followers: " + numberOfFollowers);

        try (FileWriter writer = new FileWriter("src/resources/OBSTextFiles/totalFollowers.txt", false)) {
            writer.write(numberOfFollowers);
            writer.flush();
        }
        catch (IOException ioException) {
            logger.error("Failed to write total followers to file", ioException);
        }
    }
}
