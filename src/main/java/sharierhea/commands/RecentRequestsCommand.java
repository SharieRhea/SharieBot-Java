package sharierhea.commands;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import sharierhea.Launcher;

import java.sql.SQLException;
import java.util.List;

public class RecentRequestsCommand extends Command {

    public RecentRequestsCommand() {
        super();
        trigger = "!recentrequests";
    }

    @Override
    protected void command(ChannelMessageEvent event) {
        try {
            List<String> requests = Launcher.store.getRecentSongRequests(event.getUser().getId());
            if (requests.isEmpty())
                sendMessage("@%s you haven't requested any songs yet!".formatted(event.getUser().getName()));
            else
                sendMessage("@%s your recent requests: %s".formatted(event.getUser().getName(), String.join(", ", requests)));

        } catch (SQLException sqlException) {
            logger.error("Error retrieving recently requested songs", sqlException);
            sendMessage("@%s your recent songs have been hacked!".formatted(event.getUser().getName()));
        }
    }
}
