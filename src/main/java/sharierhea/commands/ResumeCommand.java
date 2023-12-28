package sharierhea.commands;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import sharierhea.music.Jukebox;


public class ResumeCommand extends Command {
    private Jukebox jukebox;

    public ResumeCommand(SimpleEventHandler eventHandler, TwitchClient twitchClient, Jukebox media) {
        super(eventHandler, twitchClient);
        trigger = "!resume";
        jukebox = media;
    }

    @Override
    protected void parseCommand(ChannelMessageEvent event) {
        if (event.getUser().getId().equals("170582504") && event.getMessage().startsWith(trigger)) {
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
        jukebox.resume();
    }
}
