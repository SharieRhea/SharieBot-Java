package sharierhea.commands;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import sharierhea.music.Jukebox;

public class SongCommand extends Command {
    private final Jukebox JUKEBOX;

    public SongCommand(Jukebox media) throws Exception {
        super();
        trigger = "!song";
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
        sendMessage("@%s the currently playing song is %s!".formatted(event.getUser().getName(), JUKEBOX.getCurrentSong()));
    }
}
