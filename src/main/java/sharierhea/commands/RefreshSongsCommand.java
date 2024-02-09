package sharierhea.commands;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import sharierhea.Launcher;

public class RefreshSongsCommand extends Command {

    public RefreshSongsCommand() {
        super();
        trigger = "!refreshSongs";
    }

    /**
     * If the streamer uses the !refreshSongs command, the songs directory will be rechecked and the music system
     * will be restarted.
     *
     * @param event The channel message event being checked.
     */
    @Override
    protected void parseCommand(ChannelMessageEvent event) {
        if (event.getMessage().contains(trigger) && event.getUser().getId().equals(Launcher.CHANNEL_ID))
            command(event);
    }

    /**
     * The method that defines the command's behavior. Runs when parseCommand finds the command trigger.
     *
     * @param event The channel message event that triggered the command.
     */
    @Override
    protected void command(ChannelMessageEvent event) {
        Launcher.jukebox.initializeSongList();
    }
}
