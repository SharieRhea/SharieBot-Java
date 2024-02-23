package sharierhea.commands;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import sharierhea.music.Jukebox;


public class PauseCommand extends BroadcasterOnlyCommand {
    private final Jukebox JUKEBOX;

    public PauseCommand(Jukebox media) throws Exception {
        super();
        trigger = "!pause";

        if (media == null)
            throw new Exception("Jukebox has not been initialized.");
        else
            JUKEBOX = media;
    }

    /**
     * The method that defines the command's behavior. Runs when parseCommand finds the command trigger.
     *
     * @param event The channel message event that triggered the command.
     */
    @Override
    protected void command(ChannelMessageEvent event) {
        JUKEBOX.pause();
    }
}
