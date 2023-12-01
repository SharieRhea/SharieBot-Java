package sharierhea.commands;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import java.util.List;
import java.util.stream.Collectors;

public class CommandsCommand extends Command {
    private final List<Command> activeCommands;

    /**
     * Constructor to initialize the command, sets up onEvent behavior.
     * @param eventHandler The handler for all the commands.
     * @param client The twitchClient for the current session.
     */
    public CommandsCommand(SimpleEventHandler eventHandler, TwitchClient client, List<Command> commands) {
        super(eventHandler, client);
        activeCommands = commands;
        trigger = "!commands";
    }

    /**
     * Sends a message with all the active command triggers.
     * @param event The channel message event that triggered the command.
     */
    @Override
    protected void command(ChannelMessageEvent event) {
        String message = activeCommands.stream().map(command -> command.trigger).collect(Collectors.joining(", "));
        sendMessage("Here is a list of all the current commands: %s".formatted(message));
    }
}
