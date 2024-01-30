package sharierhea.commands;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import sharierhea.Store;

import java.sql.SQLException;
import java.util.List;

public class RecentSongsCommand extends Command {
    private final Store store;

    public RecentSongsCommand(SimpleEventHandler eventHandler, TwitchClient twitchClient, Store store) {
        super(eventHandler, twitchClient);
        this.store = store;
        trigger = "!recentsongs";
    }

    @Override
    protected void command(ChannelMessageEvent event) {
        try {
            List<String> songs = store.getRecentSongs();
            sendMessage("The 5 most recently played songs are: %s".formatted(String.join(", ", songs)));
        }
        catch (SQLException sqlException) {
            logger.error("Could not retrieve recent songs", sqlException);
            sendMessage("The recent songs have been hacked!");
        }
    }
}
