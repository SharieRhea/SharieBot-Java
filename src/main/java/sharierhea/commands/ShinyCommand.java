package sharierhea.commands;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import sharierhea.Launcher;
import sharierhea.SocketHandler;

import java.sql.SQLException;
import java.util.regex.Pattern;


public class ShinyCommand extends Command {
    private final Pattern pattern = Pattern.compile("^[aeiou].*", Pattern.CASE_INSENSITIVE);
    private final SocketHandler OBS_SOCKET;

    /**
     * Constructor to initialize the command, sets up onEvent behavior.
     */
    public ShinyCommand(SocketHandler socket) throws Exception {
        super();
        trigger = "!shiny";
        if (socket == null)
            throw new Exception("Websocket has not been initialized.");
        else
            OBS_SOCKET = socket;
    }

    /**
     * Ensures the user who triggered the command is in the user table and
     * retrieves an item to insert into their inventory.
     *
     * @param event The channel message event that triggered the command.
     */
    @Override
    protected void command(ChannelMessageEvent event) {
        getUser(event);
        getItem(event);
    }

    /**
     * Attempts to add the user to the user table, does nothing if the user
     * already exists.
     *
     * @param event The message that triggered the command.
     */
    private void getUser(ChannelMessageEvent event) {
        try {
            Launcher.store.getUser(event.getUser().getId(), event.getUser().getName());
        } catch (SQLException sqlException) {
            logger.error("SQLException", sqlException);
            sendMessage("Problem inserting into user table.");
        }
    }

    /**
     * Retrieves an item for the user to "find" and sends a message in the format
     * "@user found a(n) item (rarity)!"
     *
     * @param event The message event that triggered the command.
     */
    private void getItem(ChannelMessageEvent event) {
        try {
            int rarityID = Launcher.store.getRarity();
            if (rarityID < 0)
                throw new SQLException("Problem finding rarity id!");

            String item = Launcher.store.getItem(event.getUser().getId(), rarityID);
            String article = pattern.matcher(item).matches() ? "an" : "a";
            sendMessage("@%s found %s %s!".formatted(event.getUser().getName(), article, item));

            // If a mythic item is found, play overlay effects
            if (rarityID == 5)
                OBS_SOCKET.showAndHideSource(OBS_SOCKET.getCurrentScene(), "BlueGlimmer", 2);
        } catch (SQLException sqlException) {
            logger.error(sqlException.getMessage());
            sendMessage("You couldn't find an item! Better luck next time.");
        }
    }
}