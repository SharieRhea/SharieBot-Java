package sharierhea;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sharierhea.auth.Authenticator;
import sharierhea.commands.*;
import sharierhea.events.*;
import sharierhea.music.Jukebox;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The "main" class for SharieBot. This class launches the bot, authenticates with Twitch, and joins the chat.
 * Important: Need the following VM options to run:
 * --module-path /home/sharie/Downloads/app/openjfx-21.0.1_linux-x64_bin-sdk/javafx-sdk-21.0.1/lib --add-modules=javafx.controls --add-modules=javafx.media
 */
public class Launcher extends Application {
    public final static boolean ENABLE_OBS_WEBSOCKET = true;
    public final static boolean ENABLE_JUKEBOX = true;
    public final static String CHANNEL_NAME = "shariemakesart";
    public final static String CHANNEL_ID = "170582504";
    public final static String BOT_ID = "957074857";
    private final static Logger logger = LoggerFactory.getLogger(Launcher.class);
    public static Store store = null;
    public static OAuth2Credential botToken = null;
    public static OAuth2Credential broadcasterToken = null;
    public static TwitchClient twitchClient = null;
    public static SimpleEventHandler eventHandler = null;
    private static SocketHandler obsSocket = null;
    private static Jukebox jukebox = null;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Initialize an authenticator to receive credentials.
        Authenticator authenticator = new Authenticator();
        botToken = authenticator.getCredential();
        broadcasterToken = authenticator.getBroadcasterCredential();

        // Build the twitchClient.
        twitchClient = TwitchClientBuilder.builder()
                .withDefaultAuthToken(botToken)
                .withDefaultEventHandler(SimpleEventHandler.class)
                .withEnableChat(true)
                .withChatAccount(botToken)
                .withEnableHelix(true)
                .withChatCommandsViaHelix(true)
                .withEnableEventSocket(true)
                .build();
        // Necessary for the FollowEvent since it is chat based but not IRC
        twitchClient.getClientHelper().enableFollowEventListener(CHANNEL_ID, CHANNEL_NAME);

        store = new Store();
        if (ENABLE_OBS_WEBSOCKET) {
            obsSocket = new SocketHandler();
            updateTotalFollowersAndSubs();
        }
        if (ENABLE_JUKEBOX)
            jukebox = new Jukebox(twitchClient, store, broadcasterToken);
        twitchClient.getChat().joinChannel(CHANNEL_NAME);
        eventHandler = twitchClient.getEventManager().getEventHandler(SimpleEventHandler.class);

        List<Command> activeCommands = new ArrayList<>();
        // All active commands:
        activeCommands.add(new SleepyCommand());
        activeCommands.add(new TextCommand("!sharie", "Yes, Sharie is my actual name, it is pronounced \"shar-ee\", shar like car :)", false));
        activeCommands.add(new TextCommand("!theme", "This is the Catppuccin Mocha theme!", false));
        activeCommands.add(new TextCommand("!font", "This is the Jetbrains Mono NL (no ligatures) font!", false));
        activeCommands.add(new TextCommand("!ide", "This is IntelliJ IDEA Ultimate (students receive for free!)", false));
        activeCommands.add(new TextCommand("!faq", "https://tinyurl.com/faqshariemakesart", true));
        activeCommands.add(new TextCommand("!discord", "/me Join the Sleepy Sanctum Discord here: https://discord.com/invite/T87Qst3W64", false));
        activeCommands.add(new TextCommand("!youtube", "/me Check out Sharie's Youtube VOD channel here: https://www.youtube.com/@shariemakesart", false));
        activeCommands.add(new TextCommand("!github", "/me Check out Sharie's GitHub profile here: https://github.com/SharieRhea", false));
        activeCommands.add(new TextCommand("!socials", "/me Check out all of Sharie's socials here: https://linktr.ee/shariemakesart", false));
        activeCommands.add(new TextCommand("!school", "Sharie is a second-year (sophomore) Computer Science student at a public university.", true));
        activeCommands.add(new TextCommand("!raid", "Our raid message is \"Sharie needs to figure out a better raid message!\"", false));
        activeCommands.add(new TextCommand("!lurk", "is lurking! Thanks for tuning in and good luck on your project (if you're working on one)!", true));
        activeCommands.add(new SongCommand(jukebox));
        activeCommands.add(new RecentRequestsCommand());
        activeCommands.add(new RecentSongsCommand());
        activeCommands.add(new ShinyCommand(obsSocket));
        activeCommands.add(new InventoryCommand());
        activeCommands.add(new QuoteCommand());
        new CommandsCommand(activeCommands);
        new AddQuoteCommand();
        new AddItemCommand();
        new WhyCommand();
        new PauseCommand(jukebox);
        new ResumeCommand(jukebox);
        new RefreshSongsCommand(jukebox);
        new SkipCommand(jukebox);

        // EventListeners
        new Raid();
        new Poll(jukebox);
        new ChannelPointRedemption(jukebox);
        new AdBegin(obsSocket);
        new Follow(obsSocket);
        new Subscription(obsSocket);
    }

    /**
     * Queries the total number of followers and subscribers to update totalFollowers.txt and totalSubscribers.txt
     * upon bot startup.
     */
    private void updateTotalFollowersAndSubs() {
        var subsResponse = twitchClient.getHelix().getSubscriptions(broadcasterToken.getAccessToken(), CHANNEL_ID, null, null, null).execute();
        // Must convert to a string here, otherwise file output in non-numeric characters
        String numberOfSubscribers = subsResponse.getTotal().toString();

        try (FileWriter writer = new FileWriter("src/resources/OBSTextFiles/totalSubscribers.txt", false)) {
            writer.write(numberOfSubscribers);
            writer.flush();
        } catch (IOException ioException) {
            logger.error("Unable to write totalSubscribers to file.");
        }

        var followersResponse = twitchClient.getHelix().getChannelFollowers(broadcasterToken.getAccessToken(), CHANNEL_ID, null, null, null).execute();
        // Must convert to a string here, otherwise file output in non-numeric characters
        String numberOfFollowers = followersResponse.getTotal().toString();

        try (FileWriter writer = new FileWriter("src/resources/OBSTextFiles/totalFollowers.txt", false)) {
            writer.write(numberOfFollowers);
            writer.flush();
        } catch (IOException ioException) {
            logger.error("Unable to write totalFollowers to file.");
        }
    }
}