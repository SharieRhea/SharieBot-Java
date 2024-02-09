package sharierhea.commands;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import sharierhea.Launcher;

public class SongCommand extends Command {

    public SongCommand() {
        super();
        trigger = "!song";
    }

    /**
     * The method that defines the command's behavior. Runs when parseCommand finds the command trigger.
     *
     * @param event The channel message event that triggered the command.
     */
    @Override
    protected void command(ChannelMessageEvent event) {
        sendMessage("@%s the currently playing song is %s!".formatted(event.getUser().getName(), Launcher.jukebox.getCurrentSong()));
    }
}
