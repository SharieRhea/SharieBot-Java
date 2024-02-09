package sharierhea.commands;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import sharierhea.Launcher;

import java.sql.SQLException;
import java.util.List;

public class RecentSongsCommand extends Command {

    public RecentSongsCommand() {
        super();
        trigger = "!recentsongs";
    }

    @Override
    protected void command(ChannelMessageEvent event) {
        try {
            List<String> songs = Launcher.STORE.getRecentSongs();
            sendMessage("The 5 most recently played songs are: %s".formatted(String.join(", ", songs)));
        } catch (SQLException sqlException) {
            logger.error("Could not retrieve recent songs", sqlException);
            sendMessage("The recent songs have been hacked!");
        }
    }
}
