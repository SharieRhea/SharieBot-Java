package sharierhea.commands;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.eventsub.domain.PollChoice;
import com.github.twitch4j.eventsub.domain.PollStatus;
import com.github.twitch4j.helix.domain.Poll;

import java.time.Instant;
import java.util.ArrayList;

public class PollCommand extends Command {
    private final OAuth2Credential credential;

    public PollCommand(SimpleEventHandler eventHandler, TwitchClient twitchClient, OAuth2Credential oauth) {
        super(eventHandler, twitchClient);
        trigger = "!poll";
        credential = oauth;
    }

    @Override
    protected void parseCommand(ChannelMessageEvent event) {
        if (event.getUser().getId().equals("170582504") && event.getMessage().startsWith(trigger)) {
            command(event);
        }
    }

    /**
     * The method that defines the command's behavior. Runs when parseCommand finds the command trigger.
     *
     * @param event The channel message event that triggered the command.
     */
    @Override
    protected void command(ChannelMessageEvent event) {
        PollChoice choice1 = new PollChoice(null, "Option 1", null, null, null);
        PollChoice choice2 = new PollChoice(null, "Option 2", null, null, null);
        ArrayList<PollChoice> choices = new ArrayList<>();
        choices.add(choice1);
        choices.add(choice2);
        Poll poll = new Poll(null, "170582504", "shariemakesart", "shariemakesart", "Test", choices,
                false, 0, false, 0, PollStatus.ACTIVE, 60, Instant.now(), null);
        getTwitchClient().getHelix().createPoll(credential.getAccessToken(), poll).execute();
    }
}
