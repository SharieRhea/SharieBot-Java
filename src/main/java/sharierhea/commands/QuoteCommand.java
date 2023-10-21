package sharierhea.commands;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import sharierhea.Store;

import java.sql.SQLException;

public class QuoteCommand extends Command {
    Store store;

    /**
     * Constructor to initialize the command, sets up onEvent behavior.
     * @param eventHandler The handler for all the commands.
     * @param client The twitchClient for the current session.
     */
    public QuoteCommand(SimpleEventHandler eventHandler, TwitchClient client, Store dbStore) {
        super(eventHandler, client);
        store = dbStore;
    }

    /**
     * Triggers command behavior for any message containing !quote
     * @param event The channel message event being checked.
     */
    public void parseCommand(ChannelMessageEvent event) {
        if (event.getMessage().contains("!quote"))
            command(event);
    }

    /**
     * Sends a message with a random quote.
     * @param event The channel message event that triggered the command.
     */
    public void command(ChannelMessageEvent event) {
        try {
            Store.Quote quote = store.queryRandomQuote();
            sendMessage("Quote " + quote.id() + ": " + quote.text() + " [" + quote.date() + "]");
        }
        catch (SQLException exception) {
            logger.error("Query failed" + exception);
            sendMessage("Quote not found!");
        }
    }
}
