package sharierhea.events;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;
import sharierhea.music.Jukebox;

public class ChannelPointRedemption extends EventListener<RewardRedeemedEvent> {
    private final OAuth2Credential credential;
    private Jukebox jukebox;

    public ChannelPointRedemption(SimpleEventHandler eventHandler, TwitchClient client, OAuth2Credential credential, Jukebox jukebox) {
        super(eventHandler, client, RewardRedeemedEvent.class);
        twitchClient.getPubSub().listenForChannelPointsRedemptionEvents(credential, "170582504");
        this.credential = credential;
        this.jukebox = jukebox;
    }

    @Override
    public void handleEvent(RewardRedeemedEvent event) {
        // todo: see if there's a way to refund from code
        String eventTitle = event.getRedemption().getReward().getTitle();

        if (eventTitle.equals("Start a Music Poll"))
            jukebox.initiatePoll();

        if (eventTitle.equals("Skip Song"))
            jukebox.skip();


        if (eventTitle.equals("Request a Song")) {
            String userID = event.getRedemption().getUser().getId();
            String username = event.getRedemption().getUser().getDisplayName();
            String userInput = event.getRedemption().getUserInput();
            if (!userInput.contains(" - ")) {
                sendMessage("@%s your request was formatted incorrectly (missing ' - '). Your points will be refunded.".formatted(username));
                return;
            }

            String[] info = userInput.strip().split(" - ");

            if (jukebox.handleRequest(info, userID, username))
                sendMessage("@%s your request was successful! Stay tuned...".formatted(username));
            else
                sendMessage("@%s your request was formatted incorrectly (invalid title or artist). Your points will be refunded.".formatted(username));
        }
    }
}
