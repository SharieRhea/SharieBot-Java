package sharierhea.commands;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract base class for a Command. Ensures that at the bare minimum, there is a method to check for
 * whether the command should be triggered and there is a method that defines the command's behavior.
 */
public abstract class Command {
    // Every command must have access to the twitchClient and the channel's name.
    TwitchClient twitchClient;
    // todo: move channel name into a text file for re-usability
    // note: check to see if channel name has been hardcoded anywhere else
    final String CHANNEL_NAME = "shariemakesart";
    Logger logger = LoggerFactory.getLogger(Command.class);

    /**
     * Base constructor for all Commands.
     * @param eventHandler The main eventHandler that listens for events.
     * @param client The active and connected TwitchClient.
     */
    protected Command(SimpleEventHandler eventHandler, TwitchClient client) {
        twitchClient = client;
        eventHandler.onEvent(ChannelMessageEvent.class, this::parseCommand);
    }

    /**
     * The method where each message is "parsed" to see if the applicable command is present.
     * @param event The channel message event being checked.
     */
    protected abstract void parseCommand(ChannelMessageEvent event);

    /**
     * The method that defines the command's behavior. Runs when parseCommand finds the command trigger.
     * @param event The channel message event that triggered the command.
     */
    @EventSubscriber
    protected abstract void command(ChannelMessageEvent event);

    protected void sendMessage(String message) {
        twitchClient.getChat().sendMessage(CHANNEL_NAME, message);
    }
}
