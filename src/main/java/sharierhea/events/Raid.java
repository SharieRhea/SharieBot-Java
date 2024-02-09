package sharierhea.events;

import com.github.twitch4j.chat.events.channel.RaidEvent;

import static sharierhea.Launcher.*;

public class Raid extends EventListener<RaidEvent> {

    public Raid() {
        super(RaidEvent.class);
    }

    @Override
    public void handleEvent(RaidEvent event) {
        sendMessage("Thanks for the raid @%s!".formatted(event.getRaider().getName()));
        twitchClient.getHelix().sendShoutout(botToken.getAccessToken(), CHANNEL_ID, event.getRaider().getId(), BOT_ID).execute();
    }
}
