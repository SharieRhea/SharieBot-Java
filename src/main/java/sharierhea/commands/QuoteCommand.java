package sharierhea.commands;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import sharierhea.Store;

import java.sql.SQLException;

public class QuoteCommand extends Command {
    private Store store;

    /**
     * Constructor to initialize the command, sets up onEvent behavior.
     * @param eventHandler The handler for all the commands.
     * @param client The twitchClient for the current session.
     */
    public QuoteCommand(SimpleEventHandler eventHandler, TwitchClient client, Store dbStore) {
        super(eventHandler, client);
        store = dbStore;
        trigger = "!quote";
    }

    /**
     * Triggers command behavior for any message containing !quote. Checks to see
     * if there is an integer argument provided for quote number.
     * @param event The channel message event being checked.
     */
    @Override
    public void parseCommand(ChannelMessageEvent event) {
        if (!event.getMessage().contains(trigger))
            return;

        String[] words = event.getMessage().split(" ");
        if (words.length < 2) {
            command(event);
            return;
        }

        for (int i = 0; i < words.length; i ++) {
            // if !quote is found and there is another word after it
            if (words[i].equals("!quote") && i + 1 < words.length) {
                try {
                    int quoteNumber = Integer.parseInt(words[i + 1]);
                    command(event, quoteNumber);
                    return;
                }
                catch (NumberFormatException ignored) {
                }
            }
        }
        command(event);
    }

    /**
     * Sends a message with a random quote.
     * @param event The channel message event that triggered the command.
     */
    public void command(ChannelMessageEvent event) {
        try {
            Store.Quote quote = store.queryRandomQuote();
            sendMessage("Quote " + quote.id() + ": \"" + quote.text() + "\" [" + quote.date() + "]");
        }
        catch (SQLException exception) {
            logger.error("Query failed" + exception);
            sendMessage("Quote not found!");
        }
    }

    /**
     * Sends a message with the quote based on the given number.
     * @param event The channel message event that triggered the command.
     */
    public void command(ChannelMessageEvent event, int quoteNumber) {
        try {
            Store.Quote quote = store.queryQuote(quoteNumber);
            if (quote.id() == null)
                sendMessage("Quote not found!");
            else
                sendMessage("Quote " + quote.id() + ": \"" + quote.text() + "\" [" + quote.date() + "]");
        }
        catch (SQLException exception) {
            logger.error("Query failed" + exception);
            sendMessage("Quote not found!");
        }
    }
}
