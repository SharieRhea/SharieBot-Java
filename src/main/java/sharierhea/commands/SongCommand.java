package sharierhea.commands;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import sharierhea.music.Jukebox;

public class SongCommand extends Command {
    private Jukebox jukebox;

    public SongCommand(SimpleEventHandler eventHandler, TwitchClient twitchClient, Jukebox jukebox) {
        super(eventHandler, twitchClient);
        this.jukebox = jukebox;
        trigger = "!song";
    }

    /**
     * The method that defines the command's behavior. Runs when parseCommand finds the command trigger.
     * @param event The channel message event that triggered the command.
     */
    @Override
    protected void command(ChannelMessageEvent event) {
        sendMessage("@%s the currently playing song is %s".formatted(event.getUser().getName(), jukebox.getCurrentSong()));
    }
}
