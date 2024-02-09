package sharierhea.commands;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import sharierhea.Launcher;


public class SkipCommand extends Command {
    private static final double VIEWER_PERCENTAGE = 0.25;

    public SkipCommand() {
        super();
        trigger = "!skip";
    }

    /**
     * If the streamer uses the !skip command, the song is immediately skipped. Otherwise, retrieve the current
     * number of viewers and calculate a threshold for number of chatters needed to skip the song.
     *
     * @param event The channel message event being checked.
     */
    @Override
    protected void parseCommand(ChannelMessageEvent event) {
        // TODO: add an enum switch for what type of skipping behavior to use
        if (event.getMessage().contains(trigger)) {
            if (event.getUser().getId().equals(Launcher.CHANNEL_ID))
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
     *
     * @param event The channel message event that triggered the command.
     */
    @Override
    protected void command(ChannelMessageEvent event) {
        if (!Launcher.jukebox.skip())
            sendMessage("This song cannot be skipped because it was requested by a chatter!");
    }
}
