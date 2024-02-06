package sharierhea.events;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.eventsub.events.ChannelPollEndEvent;
import com.github.twitch4j.eventsub.subscriptions.SubscriptionTypes;
import sharierhea.music.Jukebox;

public class Poll extends EventListener<ChannelPollEndEvent> {
    private final Jukebox jukebox;

    public Poll(SimpleEventHandler eventHandler, TwitchClient client, OAuth2Credential broadcasterToken, Jukebox jukebox) {
        super(eventHandler, client, ChannelPollEndEvent.class);
        client.getEventSocket().register(
                broadcasterToken,
                SubscriptionTypes.POLL_END.prepareSubscription(
                        builder -> builder.broadcasterUserId("170582504").build(),
                        null
                )
        );
       // twitchClient.getPubSub().listenForPollEvents(credential, "170582504");
        this.jukebox = jukebox;
    }

    @Override
    protected void handleEvent(ChannelPollEndEvent event) {
        /*if (event.getType() != PollsEvent.EventType.POLL_COMPLETE && event.getType() != PollsEvent.EventType.POLL_TERMINATE)
            return;

        if (event.getData().getTitle().equals("Vote for Upcoming Songs!"))
            jukebox.handlePollResults(event.getData().getChoices());*/

        jukebox.handlePollResults(event.getChoices());
    }
}
