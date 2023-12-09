package sharierhea.commands;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class LurkCommand extends Command {
    /**
     * Constructor to initialize the command, sets up onEvent behavior.
     * @param eventHandler The handler for all the commands.
     * @param client The twitchClient for the current session.
     */
    public LurkCommand(SimpleEventHandler eventHandler, TwitchClient client) {
        super(eventHandler, client);
        trigger = "!lurk";
    }

    /**
     * Triggers command behavior for any message containing "!lurk".
     * @param event The channel message event being checked.
     */
    @Override
    protected void parseCommand(ChannelMessageEvent event) {
        if (event.getMessage().contains(trigger))
            command(event);
    }

    /**
     * Sends a message letting chat know that this user is lurking.
     * @param event The channel message event that triggered the command.
     */
    @Override
    protected void command(ChannelMessageEvent event) {
        sendMessage("@%s is lurking! Thanks for tuning in and good luck on your project (if you're working on one)!".formatted(event.getUser().getName()));
    }
}
