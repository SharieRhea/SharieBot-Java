package sharierhea.events;

import com.github.twitch4j.eventsub.events.ChannelPointsCustomRewardRedemptionEvent;
import com.github.twitch4j.eventsub.subscriptions.SubscriptionTypes;
import sharierhea.music.Jukebox;

import static sharierhea.Launcher.*;

public class ChannelPointRedemption extends EventListener<ChannelPointsCustomRewardRedemptionEvent> {
    private final Jukebox JUKEBOX;

    public ChannelPointRedemption(Jukebox media) throws Exception {
        super(ChannelPointsCustomRewardRedemptionEvent.class);
        twitchClient.getEventSocket().register(
                broadcasterToken,
                SubscriptionTypes.CHANNEL_POINTS_CUSTOM_REWARD_REDEMPTION_ADD.prepareSubscription(
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
    public void handleEvent(ChannelPointsCustomRewardRedemptionEvent event) {
        String eventTitle = event.getReward().getTitle();

        if (eventTitle.equals("Start a Music Poll"))
            JUKEBOX.initiatePoll();

        if (eventTitle.equals("Skip Song")) {
            if (!JUKEBOX.skip())
                sendMessage("This song cannot be skipped because it was requested by a chatter! (Points will be refunded)");
        }

        if (eventTitle.equals("Request a Song")) {
            // todo: see if there's a way to refund from code
            String userID = event.getUserId();
            String username = event.getUserName();
            String userInput = event.getUserInput();
            if (!userInput.contains(" - ")) {
                sendMessage("@%s your request was formatted incorrectly (missing ' - '). Your points will be refunded.".formatted(username));
                return;
            }

            String[] info = userInput.strip().split(" - ");

            if (JUKEBOX.handleRequest(info, userID, username))
                sendMessage("@%s your request was successful! Stay tuned...".formatted(username));
            else
                sendMessage("@%s your request was formatted incorrectly (invalid title or artist). Your points will be refunded.".formatted(username));
        }
    }
}
