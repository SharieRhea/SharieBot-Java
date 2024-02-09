package sharierhea.commands;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import sharierhea.Launcher;

import java.sql.SQLException;

public class AddQuoteCommand extends Command {
    private String quote;

    /**
     * Constructor to initialize the command, sets up onEvent behavior.
     */
    public AddQuoteCommand() {
        super();
        trigger = "!addquote";
    }

    /**
     * Triggers command behavior for any message containing "!addquote" for the broadcaster only.
     *
     * @param event The channel message event being checked.
     */
    @Override
    protected void parseCommand(ChannelMessageEvent event) {
        if (event.getUser().getId().equals(Launcher.CHANNEL_NAME) && event.getMessage().startsWith(trigger)) {
            // Split on " to get entire quote as one String
            String[] words = event.getMessage().split("\"");
            quote = words[1];
            command(event);
        }
    }

    /**
     * Attempts to add the provided quote into the quotes table. Sends a message if unsuccessful.
     *
     * @param event The channel message event that triggered the command.
     */
    @Override
    protected void command(ChannelMessageEvent event) {
        try {
            sendMessage("Added quote number %d!".formatted(Launcher.STORE.addQuote(quote)));
        } catch (SQLException sqlException) {
            sendMessage("Unable to add quote!");
            logger.error("Unable to add quote", sqlException);
        }
    }
}
