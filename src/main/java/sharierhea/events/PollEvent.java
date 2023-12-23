package sharierhea.events;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.pubsub.domain.PollData;
import com.github.twitch4j.pubsub.events.PollsEvent;

public class PollEvent extends EventListener<PollsEvent> {

    public PollEvent(SimpleEventHandler eventHandler, TwitchClient client, OAuth2Credential credential) {
        super(eventHandler, client, PollsEvent.class);
        twitchClient.getPubSub().listenForPollEvents(credential, "170582504");
    }

    @Override
    protected void handleEvent(PollsEvent event) {
        if (event.getType() != PollsEvent.EventType.POLL_COMPLETE)
            return;

        var choices = event.getData().getChoices();
        for (PollData.PollChoice choice : choices) {
            logger.debug("%s: %d".formatted(choice.getTitle(), choice.getVotes().getTotal()));
        }
    }
}
