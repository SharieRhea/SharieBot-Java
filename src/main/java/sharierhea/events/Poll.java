package sharierhea.events;

import com.github.twitch4j.eventsub.events.ChannelPollEndEvent;
import com.github.twitch4j.eventsub.subscriptions.SubscriptionTypes;

import static sharierhea.Launcher.*;

public class Poll extends EventListener<ChannelPollEndEvent> {

    public Poll() {
        super(ChannelPollEndEvent.class);
        twitchClient.getEventSocket().register(
                broadcasterToken,
                SubscriptionTypes.POLL_END.prepareSubscription(
                        builder -> builder.broadcasterUserId(CHANNEL_ID).build(),
                        null
                )
        );
    }

    @Override
    protected void handleEvent(ChannelPollEndEvent event) {
        if (event.getTitle().equals("Vote for Upcoming Songs!"))
            jukebox.handlePollResults(event.getChoices());
    }
}
