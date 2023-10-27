package sharierhea.commands;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import sharierhea.Store;

import java.sql.SQLException;

public class AddQuoteCommand extends Command {
    private Store store;
    private String quote;

    /**
     * Constructor to initialize the command, sets up onEvent behavior.
     * @param eventHandler The handler for all the commands.
     * @param client The twitchClient for the current session.
     * @param dbStore The database connection to use.
     */
    public AddQuoteCommand(SimpleEventHandler eventHandler, TwitchClient client, Store dbStore) {
        super(eventHandler, client);
        store = dbStore;
    }

    /**
     * Triggers command behavior for any message containing "!addquote" for the broadcaster only.
     * @param event The channel message event being checked.
     */
    @Override
    protected void parseCommand(ChannelMessageEvent event) {
        if (event.getUser().getName().equals("shariemakesart") && event.getMessage().startsWith("!addquote")) {
            // Split on " to get entire quote as one String
            String[] words = event.getMessage().split("\"");
            quote = words[1];
            command(event);
        }
    }

    /**
     * Attempts to add the provided quote into the quotes table. Sends a message if unsuccessful.
     * @param event The channel message event that triggered the command.
     */
    @Override
    protected void command(ChannelMessageEvent event) {
        try {
            sendMessage("Added quote number " + store.addQuote(quote));
        }
        catch (SQLException sqlException) {
            sendMessage("Unable to add quote!");
            logger.error("Unable to add quote", sqlException);
        }
    }
}
