import com.auth.Authenticator;
import com.commands.SharieCommand;
import com.commands.ShinyCommand;
import com.commands.SleepyCommand;
import com.commands.ThemeCommand;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;

import java.io.IOException;

/**
 * The "main" class for SharieBot. This class launches the bot, authenticates with Twitch, and joins the chat.
 */
public class Launcher {
    public static void main(String[] args) throws IOException {
        // Initialize an authenticator to receive credentials.
        Authenticator authenticator = new Authenticator();
        OAuth2Credential credential = authenticator.getCredential();

        // Build the twitchClient.
        TwitchClient twitchClient = TwitchClientBuilder.builder()
                .withDefaultAuthToken(credential)
                .withDefaultEventHandler(SimpleEventHandler.class)
                .withEnableChat(true)
                .withChatAccount(credential)
                .withEnableHelix(true)
                .build();

        twitchClient.getChat().joinChannel("shariemakesart");

        // Initializes the eventHandler that will be used for all commands.
        SimpleEventHandler eventHandler = twitchClient.getEventManager().getEventHandler(SimpleEventHandler.class);

        // All active commands:
        new SleepyCommand(eventHandler, twitchClient);
        new SharieCommand(eventHandler, twitchClient);
        new ThemeCommand(eventHandler, twitchClient);
        new ShinyCommand(eventHandler, twitchClient);
    }
}