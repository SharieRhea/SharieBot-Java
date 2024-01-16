package sharierhea.commands;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class IDECommand extends Command {

    public IDECommand(SimpleEventHandler eventHandler, TwitchClient twitchClient) {
        super(eventHandler, twitchClient);
        trigger = "!ide";
    }

    /**
     * The method that defines the command's behavior. Runs when parseCommand finds the command trigger.
     * @param event The channel message event that triggered the command.
     */
    @Override
    protected void command(ChannelMessageEvent event) {
        sendMessage("@%s IntelliJ IDEA Ultimate (students receive for free)!".formatted(event.getUser().getName()));
    }
}
