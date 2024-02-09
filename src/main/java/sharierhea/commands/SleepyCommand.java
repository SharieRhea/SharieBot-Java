package sharierhea.commands;

import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import java.util.Random;

/**
 * The !sleepy command generates a random number from 0 to 100 inclusive as a measure of the
 * user's sleepiness.
 */
public class SleepyCommand extends Command {

    /**
     * Constructor to initialize the command, sets up onEvent behavior.
     */
    public SleepyCommand() {
        super();
        trigger = "!sleepy";
    }

    /**
     * Sends a message in chat announcing the user's sleepiness.
     *
     * @param event The channel message event that triggered the command.
     */
    @EventSubscriber
    protected void command(ChannelMessageEvent event) {
        sendMessage("@%s is %d%% sleepy!".formatted(event.getUser().getName(), generateRandomValue()));
    }

    /**
     * Generates a random number 0 to 100 inclusive for use in the command's method.
     *
     * @return The generated number.
     */
    private int generateRandomValue() {
        Random generator = new Random();
        return generator.nextInt(101);
    }
}
