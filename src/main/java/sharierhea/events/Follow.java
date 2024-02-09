package sharierhea.events;

import com.github.twitch4j.chat.events.channel.FollowEvent;
import sharierhea.Launcher;

import java.io.FileWriter;
import java.io.IOException;

import static sharierhea.Launcher.*;

public class Follow extends EventListener<FollowEvent> {

    public Follow() {
        super(FollowEvent.class);
    }

    @Override
    protected void handleEvent(FollowEvent event) {
        OBS_SOCKET.showAndHideSource(OBS_SOCKET.getCurrentScene(), "WhiteGlimmer", 2);

        // Grab the most recent follower to populate text file for OBS
        String follower = event.getUser().getName();
        logger.debug("Follower:" + follower);
        try (FileWriter writer = new FileWriter("src/resources/OBSTextFiles/recentFollower.txt", false)) {
            writer.write(follower);
            writer.flush();
        } catch (IOException ioException) {
            logger.error("Failed to write recentFollower to file", ioException);
        }

        var response = twitchClient.getHelix().getChannelFollowers(broadcasterToken.getAccessToken(), Launcher.CHANNEL_ID, null, null, null).execute();
        String numberOfFollowers = response.getTotal().toString();
        logger.debug("Followers: " + numberOfFollowers);

        try (FileWriter writer = new FileWriter("src/resources/OBSTextFiles/totalFollowers.txt", false)) {
            writer.write(numberOfFollowers);
            writer.flush();
        } catch (IOException ioException) {
            logger.error("Failed to write total followers to file", ioException);
        }
    }
}
