package sharierhea.commands;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class FAQCommand extends Command {
    /**
     * Constructor to initialize the command, sets up onEvent behavior.
     * @param eventHandler The handler for all the commands.
     * @param client The twitchClient for the current session.
     */
    public FAQCommand(SimpleEventHandler eventHandler, TwitchClient client) {
        super(eventHandler, client);
    }

    /**
     * Triggers command behavior for any message containing "!faq".
     * @param event The channel message event being checked.
     */
    @Override
    protected void parseCommand(ChannelMessageEvent event) {
        if (event.getMessage().contains("!faq") || event.getMessage().contains("!FAQ"))
            command(event);
    }

    /**
     * Sends a message with the link to Sharie's faq.
     * @param event The channel message event that triggered the command.
     */
    @Override
    protected void command(ChannelMessageEvent event) {
        sendMessage("https://tinyurl.com/faqshariemakesart");
    }
}
