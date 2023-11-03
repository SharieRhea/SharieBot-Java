package sharierhea.commands;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import sharierhea.Store;
import java.sql.SQLException;


public class ShinyCommand extends Command {
    private final Store store;

    /**
     * Constructor to initialize the command, sets up onEvent behavior.
     * @param eventHandler The handler for all the commands.
     * @param client The twitchClient for the current session.
     */
    public ShinyCommand(SimpleEventHandler eventHandler, TwitchClient client, Store dbstore) {
        super(eventHandler, client);
        store = dbstore;
    }

    /**
     * Triggers command behavior for any message containing "!shiny"
     * @param event The channel message event being checked.
     */
    @Override
    protected void parseCommand(ChannelMessageEvent event) {
        if (event.getMessage().contains("!shiny"))
            command(event);
    }

    /**
     * Ensures the user who triggered the command is in the user table and
     * retrieves an item to insert into their inventory.
     * @param event The channel message event that triggered the command.
     */
    @Override
    protected void command(ChannelMessageEvent event) {
        getUser(event);
        getItem(event);
    }

    /**
     * Attempts to add the user to the user table, does nothing if the user
     * already exists.
     * @param event The message that triggered the command.
     */
    private void getUser(ChannelMessageEvent event) {
        try {
            store.getUser(event.getUser().getId(), event.getUser().getName());
        }
        catch (SQLException sqlException) {
            logger.error("SQLException", sqlException);
            sendMessage("Problem inserting into user table.");
        }
    }

    /**
     * Retrieves an item for the user to "find" and sends a message in the format
     * "@user found a(n) item (rarity)!"
     * @param event The message event that triggered the command.
     */
    private void getItem(ChannelMessageEvent event) {
        try {
            int rarityID = store.getRarity();
            if (rarityID < 0)
                throw new SQLException("Problem finding rarity id!");

            sendMessage("@" + event.getUser().getName() + " found a(n) " +
                    store.getItem(event.getUser().getId(), rarityID) + "!");
        }
        catch (SQLException sqlException) {
            logger.error(sqlException.getMessage());
            sendMessage("You couldn't find an item! Better luck next time.");
        }
    }
}