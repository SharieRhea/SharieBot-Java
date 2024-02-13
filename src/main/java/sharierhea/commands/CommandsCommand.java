package sharierhea.commands;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import java.util.List;
import java.util.stream.Collectors;

public class CommandsCommand extends Command {
    private final String listOfTriggers;

    /**
     * Constructor to initialize the command, sets up onEvent behavior.
     */
    public CommandsCommand(List<Command> commands) {
        super();
        trigger = "!commands";
        listOfTriggers = commands.stream().map(command -> command.trigger).collect(Collectors.joining(", "));
    }

    /**
     * Sends a message with all the active command triggers.
     *
     * @param event The channel message event that triggered the command.
     */
    @Override
    protected void command(ChannelMessageEvent event) {
        sendMessage("Here is a list of all the current commands: %s".formatted(listOfTriggers));
    }
}
