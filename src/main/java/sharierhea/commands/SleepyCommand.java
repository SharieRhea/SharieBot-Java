package sharierhea.commands;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import java.util.Random;

/**
 * The !sleepy command generates a random number from 0 to 100 inclusive as a measure of the
 * user's sleepiness.
 */
public class SleepyCommand extends Command{

    /**
     * Constructor to initialize the command, sets up onEvent behavior.
     * @param eventHandler The handler for all the commands.
     * @param client The twitchClient for the current session.
     */
    public SleepyCommand(SimpleEventHandler eventHandler, TwitchClient client) {
        super(eventHandler, client);
    }

    /**
     * Checks to see if a message should trigger this command, if so, calls the command behavior.
     * @param event The channel message event being checked.
     */
    protected void parseCommand(ChannelMessageEvent event) {
        if (event.getMessage().contains("!sleepy"))
            command(event);
    }

    /**
     * Sends a message in chat announcing the user's sleepiness.
     * @param event The channel message event that triggered the command.
     */
    @EventSubscriber
    protected void command(ChannelMessageEvent event) {
        sendMessage("@" + event.getUser().getName() + " is " + generateRandomValue() + "% sleepy!");
    }

    /**
     * Generates a random number 0 to 100 inclusive for use in the command's method.
     * @return The generated number.
     */
    private int generateRandomValue() {
        Random generator = new Random();
        return generator.nextInt(101);
    }
}
