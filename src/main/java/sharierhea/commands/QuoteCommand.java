package sharierhea.commands;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import sharierhea.Launcher;
import sharierhea.Store;

import java.sql.SQLException;
import java.util.Optional;

public class QuoteCommand extends Command {

    /**
     * Constructor to initialize the command, sets up onEvent behavior.
     */
    public QuoteCommand() {
        super();
        trigger = "!quote";
    }

    /**
     * Triggers command behavior for any message containing !quote. Checks to see
     * if there is an integer argument provided for quote number.
     *
     * @param event The channel message event being checked.
     */
    @Override
    public void parseCommand(ChannelMessageEvent event) {
        Optional<String> argument = parseCommandArgument(event);
        if (argument.isPresent()) {
            try {
                int quoteNumber = Integer.parseInt(argument.get());
                command(event, quoteNumber);
            } catch (NumberFormatException numberFormatException) {
                command(event);
            }
        }
    }

    /**
     * Sends a message with a random quote.
     *
     * @param event The channel message event that triggered the command.
     */
    public void command(ChannelMessageEvent event) {
        try {
            Store.Quote quote = Launcher.store.queryRandomQuote();
            sendMessage("Quote " + quote.id() + ": \"" + quote.text() + "\" [" + quote.date() + "]");
        } catch (SQLException exception) {
            logger.error("Query failed" + exception);
            sendMessage("Quote not found!");
        }
    }

    /**
     * Sends a message with the quote based on the given number.
     *
     * @param event The channel message event that triggered the command.
     */
    public void command(ChannelMessageEvent event, int quoteNumber) {
        try {
            Store.Quote quote = Launcher.store.queryQuote(quoteNumber);
            if (quote.id() == null)
                sendMessage("Quote not found!");
            else
                sendMessage("Quote " + quote.id() + ": \"" + quote.text() + "\" [" + quote.date() + "]");
        } catch (SQLException exception) {
            logger.error("Query failed" + exception);
            sendMessage("Quote not found!");
        }
    }
}
