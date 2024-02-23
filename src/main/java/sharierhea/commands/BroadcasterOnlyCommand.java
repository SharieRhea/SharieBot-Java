package sharierhea.commands;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import static sharierhea.Launcher.CHANNEL_ID;

/**
 * Abstract base class for commands that only the broadcaster can trigger.
 */
public abstract class BroadcasterOnlyCommand extends Command {

    protected BroadcasterOnlyCommand() {
        super();
    }

    /**
     * Check for the broadcaster, then normal behavior.
     * @param event The channel message event being checked.
     */
    @Override
    protected void parseCommand(ChannelMessageEvent event) {
        if (event.getUser().getId().equals(CHANNEL_ID))
            super.parseCommand(event);
    }
}
