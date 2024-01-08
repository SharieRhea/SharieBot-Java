package sharierhea.commands;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import sharierhea.music.Jukebox;

public class RefreshSongsCommand extends Command {
    private Jukebox jukebox;

    public RefreshSongsCommand(SimpleEventHandler eventHandler, TwitchClient twitchClient, Jukebox media) {
        super(eventHandler, twitchClient);
        trigger = "!refreshSongs";
        jukebox = media;
    }

    /**
     * If the streamer uses the !refreshSongs command, the songs directory will be rechecked and the music system
     * will be restarted.
     * @param event The channel message event being checked.
     */
    @Override
    protected void parseCommand(ChannelMessageEvent event) {
        if (event.getMessage().contains(trigger) && event.getUser().getId().equals("170582504"))
                command(event);
    }

    /**
     * The method that defines the command's behavior. Runs when parseCommand finds the command trigger.
     * @param event The channel message event that triggered the command.
     */
    @Override
    protected void command(ChannelMessageEvent event) {
        jukebox.initializeSongList();
    }
}
