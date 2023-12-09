import sharierhea.Store;
import sharierhea.auth.Authenticator;
import sharierhea.commands.*;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import sharierhea.events.Raid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
                .withChatCommandsViaHelix(true)
                .build();

        Store store = new Store();
        twitchClient.getChat().joinChannel("shariemakesart");

        // Initializes the eventHandler that will be used for all commands.
        SimpleEventHandler eventHandler = twitchClient.getEventManager().getEventHandler(SimpleEventHandler.class);

        List<Command> activeCommands = new ArrayList<>();

        // All active commands:
        activeCommands.add(new SleepyCommand(eventHandler, twitchClient));
        activeCommands.add(new SharieCommand(eventHandler, twitchClient));
        activeCommands.add(new ThemeCommand(eventHandler, twitchClient));
        activeCommands.add(new FontCommand(eventHandler, twitchClient));
        activeCommands.add(new FAQCommand(eventHandler, twitchClient));
        activeCommands.add(new DiscordCommand(eventHandler, twitchClient));
        activeCommands.add(new YoutubeCommand(eventHandler, twitchClient));
        activeCommands.add(new GitHubCommand(eventHandler, twitchClient));
        activeCommands.add(new SocialsCommand(eventHandler, twitchClient));
        activeCommands.add(new ShinyCommand(eventHandler, twitchClient, store));
        activeCommands.add(new InventoryCommand(eventHandler, twitchClient, store));
        activeCommands.add(new SchoolCommand(eventHandler, twitchClient));
        activeCommands.add(new QuoteCommand(eventHandler, twitchClient, store));
        activeCommands.add(new RaidCommand(eventHandler, twitchClient));
        activeCommands.add(new LurkCommand(eventHandler, twitchClient));
        new AddQuoteCommand(eventHandler, twitchClient, store);
        new AddItemCommand(eventHandler, twitchClient, store);
        new WhyCommand(eventHandler, twitchClient);
        new CommandsCommand(eventHandler,twitchClient, activeCommands);

        // EventListeners
        new Raid(eventHandler, twitchClient, credential);
    }
}