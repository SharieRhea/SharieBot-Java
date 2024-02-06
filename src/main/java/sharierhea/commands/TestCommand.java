package sharierhea.commands;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class TestCommand extends Command {

    public TestCommand(SimpleEventHandler eventHandler, TwitchClient twitchClient, OAuth2Credential token) {
        super(eventHandler, twitchClient);
        trigger = "!test";
    }

    @Override
    public void parseCommand(ChannelMessageEvent event) {
        if (event.getUser().getId().equals("170582504") && event.getMessage().contains(trigger))
            command(event);
    }

    /**
     * The method that defines the command's behavior. Runs when parseCommand finds the command trigger.
     * @param event The channel message event that triggered the command.
     */
    @Override
    protected void command(ChannelMessageEvent event) {

    }
}
