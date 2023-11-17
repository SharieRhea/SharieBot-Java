package sharierhea.commands;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import sharierhea.Store;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InventoryCommand extends Command {
    private Store store;

    /**
     * Constructor for InventoryCommand.
     * @param eventHandler The handler for all message events.
     * @param twitchClient The TwitchClient for the current session.
     */
    public InventoryCommand(SimpleEventHandler eventHandler, TwitchClient twitchClient, Store database) {
        super(eventHandler, twitchClient);
        store = database;
        trigger = "!inventory";
    }

    /**
     * Displays a message showing the user's inventory. On error displays a message stating that
     * their inventory is "lost" or "empty".
     * @param event The channel message event that triggered the command.
     */
    @Override
    protected void command(ChannelMessageEvent event) {
        Function<Map.Entry<Integer, Integer>, String> mapper = (entry) -> {
            try {
                return String.format("%dx %s", entry.getValue(), store.getRarityTitle(entry.getKey()));
            } catch (SQLException sqlException) {
                return null;
            }
        };

        try {
            HashMap<Integer, Integer> map = store.getInventory(event.getUser().getId());
            if (map.isEmpty())
                sendMessage("@" + event.getUser().getName() + " seems like don't have anything in your inventory!");
            else {
                String string = map.entrySet().stream().map(mapper).filter(Objects::nonNull).collect(Collectors.joining(", "));
                sendMessage("@" + event.getUser().getName() + " you have found: " + string + "!");
            }
        }
        catch (SQLException sqlException) {
            logger.error(sqlException.getMessage());
            sendMessage("Seems like you lost your inventory!");
        }
    }


}
