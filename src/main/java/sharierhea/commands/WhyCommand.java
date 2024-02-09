package sharierhea.commands;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class WhyCommand extends Command {
    /**
     * Constructor to initialize the command, sets up onEvent behavior.
     */
    public WhyCommand() {
        super();
        trigger = "why";
    }

    @Override
    protected void parseCommand(ChannelMessageEvent event) {
        if (event.getMessage().toLowerCase().contains(trigger)) {
            command(event);
        }
    }

    /**
     * Sends a snarky message.
     *
     * @param event The channel message event that triggered the command.
     */
    @Override
    protected void command(ChannelMessageEvent event) {
        sendMessage("@" + event.getUser().getName() + " why not?");
    }
}
