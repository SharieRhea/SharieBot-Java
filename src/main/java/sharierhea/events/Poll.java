package sharierhea.events;

import com.github.twitch4j.eventsub.events.ChannelPollEndEvent;
import com.github.twitch4j.eventsub.subscriptions.SubscriptionTypes;
import sharierhea.music.Jukebox;

import static sharierhea.Launcher.*;

public class Poll extends EventListener<ChannelPollEndEvent> {
    private final Jukebox JUKEBOX;

    public Poll(Jukebox media) throws Exception {
        super(ChannelPollEndEvent.class);
        twitchClient.getEventSocket().register(
                broadcasterToken,
                SubscriptionTypes.POLL_END.prepareSubscription(
                        builder -> builder.broadcasterUserId(CHANNEL_ID).build(),
                        null
                )
        );
        if (media == null)
            throw new Exception("Jukebox has not been initialized.");
        else
            JUKEBOX = media;
    }

    @Override
    protected void handleEvent(ChannelPollEndEvent event) {
        if (event.getTitle().equals("Vote for Upcoming Songs!"))
            JUKEBOX.handlePollResults(event.getChoices());
    }
}
