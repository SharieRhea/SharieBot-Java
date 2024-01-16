package sharierhea.commands;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class ThemeCommand extends Command {

    /**
     * Constructor to initialize the command, sets up onEvent behavior.
     * @param eventHandler The handler for all the commands.
     * @param client The twitchClient for the current session.
     */
    public ThemeCommand(SimpleEventHandler eventHandler, TwitchClient client) {
        super(eventHandler, client);
        trigger = "!theme";
    }

    /**
     * Sends a message with the current theme.
     * @param event The channel message event that triggered the command.
     */
    @Override
    protected void command(ChannelMessageEvent event) {
        sendMessage("This is the Catppucchin Mocha theme!");
    }
}
