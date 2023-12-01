package sharierhea.commands;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * An abstract base class for a Command. Ensures that at the bare minimum, there is a method to check for
 * whether the command should be triggered and there is a method that defines the command's behavior.
 */
public abstract class Command {
    // Every command must have access to the twitchClient and the channel's name.
    private TwitchClient twitchClient;
    // todo: move channel name into a text file for re-usability
    // note: check to see if channel name has been hardcoded anywhere else
    private final String CHANNEL_NAME = "shariemakesart";
    protected Logger logger = LoggerFactory.getLogger(Command.class);
    protected String trigger;

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
    protected void parseCommand(ChannelMessageEvent event) {
        if (event.getMessage().contains(trigger))
            command(event);
    }

    /**
     * Parses a command that may have one argument after its trigger. If an argument is present, return it.
     * @param event The message being checked.
     * @return String argument or Optional.empty()
     */
    protected Optional<String> parseCommandArgument(ChannelMessageEvent event) {
        if (!event.getMessage().contains(trigger))
            return Optional.empty();

        String[] words = event.getMessage().split(" ");
        if (words.length < 2) {
            command(event);
            return Optional.empty();
        }

        for (int i = 0; i < words.length; i ++) {
            // if trigger is found and there is another word after it, return that word
            if (words[i].equals(trigger) && i + 1 < words.length)
                return Optional.of(words[i + 1]);
        }
        command(event);
        return Optional.empty();
    }

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
