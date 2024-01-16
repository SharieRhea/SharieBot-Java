package sharierhea.commands;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.helix.domain.StreamList;
import sharierhea.music.Jukebox;

import java.util.ArrayList;


public class SkipCommand extends Command {
    private static final double VIEWER_PERCENTAGE = 0.25;
    private Jukebox jukebox;
    private OAuth2Credential credential;

    public SkipCommand(SimpleEventHandler eventHandler, TwitchClient twitchClient, Jukebox media, OAuth2Credential credential) {
        super(eventHandler, twitchClient);
        trigger = "!skip";
        jukebox = media;
        this.credential = credential;
    }

    /**
     * If the streamer uses the !skip command, the song is immediately skipped. Otherwise, retrieve the current
     * number of viewers and calculate a threshold for number of chatters needed to skip the song.
     * @param event The channel message event being checked.
     */
    @Override
    protected void parseCommand(ChannelMessageEvent event) {
        if (event.getMessage().contains(trigger)) {
            if (event.getUser().getId().equals("170582504"))
                command(event);
            // Note: Skip functionality has been moved to channel point redemptions
            /*
            else {
                // Retrieve the number of current viewers and calculate threshold
                var userIds = new ArrayList<String>();
                userIds.add("170582504");
                StreamList streamlist = getTwitchClient().getHelix().getStreams(credential.getAccessToken(), null, null, null, null, null, userIds, null).execute();
                int skipThreshold = (int) Math.max(streamlist.getStreams().get(0).getViewerCount() * VIEWER_PERCENTAGE, 3);

                int currentSkipCounter = jukebox.addSkipUser(event.getUser().getId());
                if (currentSkipCounter >= skipThreshold){
                    sendMessage("%d/%d chatters reached, song is being skipped...".formatted(currentSkipCounter, skipThreshold));
                    command(event);
                }
                else
                    sendMessage("Don't like this song? %d/%d chatters want to skip it!".formatted(currentSkipCounter, skipThreshold));
            }*/
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
