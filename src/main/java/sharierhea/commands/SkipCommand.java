package sharierhea.commands;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import sharierhea.music.Jukebox;


public class SkipCommand extends Command {
    private Jukebox jukebox;

    public SkipCommand(SimpleEventHandler eventHandler, TwitchClient twitchClient, Jukebox media) {
        super(eventHandler, twitchClient);
        trigger = "!skip";
        jukebox = media;
    }

    @Override
    protected void parseCommand(ChannelMessageEvent event) {
        if (event.getMessage().contains(trigger)) {
            if (event.getUser().getId().equals("170582504"))
                command(event);
            else {
                // todo: fix this hardcoded value
                int currentSkipCounter = jukebox.addSkipUser(event.getUser().getId());
                if (currentSkipCounter > 4){
                    sendMessage("%d/5 chatters reached, song is being skipped...".formatted(currentSkipCounter));
                    command(event);
                }
                else
                    sendMessage("Don't like this song? %d/5 chatters needed to skip it!".formatted(currentSkipCounter));
            }
        }
    }

    /**
     * The method that defines the command's behavior. Runs when parseCommand finds the command trigger.
     * @param event The channel message event that triggered the command.
     */
    @Override
    protected void command(ChannelMessageEvent event) {
        jukebox.skip();
    }
}
