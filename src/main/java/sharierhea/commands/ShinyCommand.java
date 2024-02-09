package sharierhea.commands;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import sharierhea.Launcher;

import java.sql.SQLException;
import java.util.regex.Pattern;


public class ShinyCommand extends Command {
    private final Pattern pattern = Pattern.compile("^[aeiou].*", Pattern.CASE_INSENSITIVE);

    /**
     * Constructor to initialize the command, sets up onEvent behavior.
     */
    public ShinyCommand() {
        super();
        trigger = "!shiny";
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
            Launcher.STORE.getUser(event.getUser().getId(), event.getUser().getName());
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
            int rarityID = Launcher.STORE.getRarity();
            if (rarityID < 0)
                throw new SQLException("Problem finding rarity id!");

            String item = Launcher.STORE.getItem(event.getUser().getId(), rarityID);
            String article = pattern.matcher(item).matches() ? "an" : "a";
            sendMessage("@%s found %s %s!".formatted(event.getUser().getName(), article, item));

            // If a mythic item is found, play overlay effects
            if (rarityID == 5)
                Launcher.OBS_SOCKET.showAndHideSource(Launcher.OBS_SOCKET.getCurrentScene(), "BlueGlimmer", 2);
        } catch (SQLException sqlException) {
            logger.error(sqlException.getMessage());
            sendMessage("You couldn't find an item! Better luck next time.");
        }
    }
}