package sharierhea.events;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.pubsub.domain.PollData;
import com.github.twitch4j.pubsub.events.PollsEvent;
import sharierhea.music.Jukebox;

public class Poll extends EventListener<PollsEvent> {
    private final Jukebox jukebox;

    public Poll(SimpleEventHandler eventHandler, TwitchClient client, OAuth2Credential credential, Jukebox jukebox) {
        super(eventHandler, client, PollsEvent.class);
        twitchClient.getPubSub().listenForPollEvents(credential, "170582504");
        this.jukebox = jukebox;
    }

    @Override
    protected void handleEvent(PollsEvent event) {
        if (event.getType() != PollsEvent.EventType.POLL_COMPLETE && event.getType() != PollsEvent.EventType.POLL_TERMINATE)
            return;

        if (event.getData().getTitle().equals("Vote for Upcoming Songs!"))
            jukebox.handlePollResults(event.getData().getChoices());
    }
}
