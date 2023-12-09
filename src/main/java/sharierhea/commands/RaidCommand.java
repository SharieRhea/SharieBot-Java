package sharierhea.commands;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class RaidCommand extends Command {
    /**
     * Constructor to initialize the command, sets up onEvent behavior.
     * @param eventHandler The handler for all the commands.
     * @param client The twitchClient for the current session.
     */
    public RaidCommand(SimpleEventHandler eventHandler, TwitchClient client) {
        super(eventHandler, client);
        trigger = "!raid";
    }

    /**
     * Triggers command behavior for any message containing "!raid".
     * @param event The channel message event being checked.
     */
    @Override
    protected void parseCommand(ChannelMessageEvent event) {
        if (event.getMessage().contains(trigger))
            command(event);
    }

    /**
     * Sends a message explaining what the raid message
     * @param event The channel message event that triggered the command.
     */
    @Override
    protected void command(ChannelMessageEvent event) {
        // TODO: come up with a better message
        sendMessage("Our raid message is \"Sharie needs to figure out a better raid message!\"");
    }
}
