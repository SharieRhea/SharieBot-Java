package sharierhea.commands;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import sharierhea.Launcher;
import sharierhea.music.Jukebox;


public class ResumeCommand extends Command {
    private final Jukebox JUKEBOX;

    public ResumeCommand(Jukebox media) throws Exception {
        super();
        trigger = "!resume";
        if (media == null)
            throw new Exception("Jukebox has not been initialized.");
        else
            JUKEBOX = media;
    }

    @Override
    protected void parseCommand(ChannelMessageEvent event) {
        if (event.getUser().getId().equals(Launcher.CHANNEL_ID) && event.getMessage().startsWith(trigger)) {
            command(event);
        }
    }

    /**
     * The method that defines the command's behavior. Runs when parseCommand finds the command trigger.
     *
     * @param event The channel message event that triggered the command.
     */
    @Override
    protected void command(ChannelMessageEvent event) {
        JUKEBOX.resume();
    }
}
