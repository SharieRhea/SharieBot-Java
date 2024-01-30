import javafx.application.Application;
import javafx.stage.Stage;
import sharierhea.SocketHandler;
import sharierhea.Store;
import sharierhea.auth.Authenticator;
import sharierhea.commands.*;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import sharierhea.events.*;
import sharierhea.music.Jukebox;

import java.util.ArrayList;
import java.util.List;

/**
 * The "main" class for SharieBot. This class launches the bot, authenticates with Twitch, and joins the chat.
 * Important: Need the following VM options to run:
 * --module-path /home/sharie/Downloads/app/openjfx-21.0.1_linux-x64_bin-sdk/javafx-sdk-21.0.1/lib --add-modules=javafx.controls --add-modules=javafx.media
 */
public class Launcher extends Application {
    // Singleton for the database
    private final static Store store = new Store();
    private final static SocketHandler socket = new SocketHandler();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Initialize an authenticator to receive credentials.
        Authenticator authenticator = new Authenticator();
        OAuth2Credential credential = authenticator.getCredential();
        OAuth2Credential broadcasterToken = authenticator.getBroadcasterCredential();

        // Build the twitchClient.
        TwitchClient twitchClient = TwitchClientBuilder.builder()
                .withDefaultAuthToken(credential)
                .withDefaultEventHandler(SimpleEventHandler.class)
                .withEnableChat(true)
                .withChatAccount(credential)
                .withEnableHelix(true)
                .withChatCommandsViaHelix(true)
                .withEnablePubSub(true)
                .withEnableEventSocket(true)
                .build();

        // Necessary for media player
        Jukebox jukebox = new Jukebox(twitchClient, store, authenticator.getBroadcasterCredential());

        twitchClient.getChat().joinChannel("shariemakesart");

        // Initializes the eventHandler that will be used for all commands.
        SimpleEventHandler eventHandler = twitchClient.getEventManager().getEventHandler(SimpleEventHandler.class);

        List<Command> activeCommands = new ArrayList<>();

        // All active commands:
        activeCommands.add(new SleepyCommand(eventHandler, twitchClient));
        activeCommands.add(new SharieCommand(eventHandler, twitchClient));
        activeCommands.add(new ThemeCommand(eventHandler, twitchClient));
        activeCommands.add(new FontCommand(eventHandler, twitchClient));
        activeCommands.add(new IDECommand(eventHandler, twitchClient));
        activeCommands.add(new FAQCommand(eventHandler, twitchClient));
        activeCommands.add(new DiscordCommand(eventHandler, twitchClient));
        activeCommands.add(new YoutubeCommand(eventHandler, twitchClient));
        activeCommands.add(new GitHubCommand(eventHandler, twitchClient));
        activeCommands.add(new SocialsCommand(eventHandler, twitchClient));
        activeCommands.add(new ShinyCommand(eventHandler, twitchClient, store, socket));
        activeCommands.add(new InventoryCommand(eventHandler, twitchClient, store));
        activeCommands.add(new SchoolCommand(eventHandler, twitchClient));
        activeCommands.add(new QuoteCommand(eventHandler, twitchClient, store));
        activeCommands.add(new RaidCommand(eventHandler, twitchClient));
        activeCommands.add(new LurkCommand(eventHandler, twitchClient));
        activeCommands.add(new SongCommand(eventHandler, twitchClient, jukebox));
        activeCommands.add(new RecentRequestsCommand(eventHandler, twitchClient, store));
        activeCommands.add(new RecentSongsCommand(eventHandler, twitchClient, store));
        new AddQuoteCommand(eventHandler, twitchClient, store);
        new AddItemCommand(eventHandler, twitchClient, store);
        new WhyCommand(eventHandler, twitchClient);
        new CommandsCommand(eventHandler,twitchClient, activeCommands);
        new PauseCommand(eventHandler, twitchClient, jukebox);
        new ResumeCommand(eventHandler, twitchClient, jukebox);
        new RefreshSongsCommand(eventHandler, twitchClient, jukebox);
        new SkipCommand(eventHandler, twitchClient, jukebox, credential);
        new TestCommand(eventHandler, twitchClient, socket);

        // EventListeners
        new Raid(eventHandler, twitchClient, credential);
        new Poll(eventHandler, twitchClient, credential, jukebox);
        new ChannelPointRedemption(eventHandler, twitchClient, credential, jukebox);
        new AdBegin(eventHandler, twitchClient, socket, broadcasterToken);
        new Follow(eventHandler, twitchClient, socket);
        new Subscription(eventHandler, twitchClient, socket, broadcasterToken);
    }
}