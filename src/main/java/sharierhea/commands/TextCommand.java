package sharierhea.commands;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class TextCommand extends Command {
    private final String message;
    private final boolean atUser;

    public TextCommand(String trigger, String response, boolean atUser) {
        this.trigger = trigger;
        message = response;
        this.atUser = atUser;
    }


    /**
     * The method that defines the command's behavior. Runs when parseCommand finds the command trigger.
     *
     * @param event The channel message event that triggered the command.
     */
    @Override
    protected void command(ChannelMessageEvent event) {
        if (atUser)
            sendMessage("@%s %s".formatted(event.getUser().getName(), message));
        else
            sendMessage(message);
    }
}
