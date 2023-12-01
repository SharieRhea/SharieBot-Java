package sharierhea.commands;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import sharierhea.Store;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InventoryCommand extends Command {
    private final Store store;
    private final HashMap<Integer, String> rarityMap;

    /**
     * Constructor for InventoryCommand.
     * @param eventHandler The handler for all message events.
     * @param twitchClient The TwitchClient for the current session.
     */
    public InventoryCommand(SimpleEventHandler eventHandler, TwitchClient twitchClient, Store database) {
        super(eventHandler, twitchClient);
        store = database;
        rarityMap = store.getRarityMap();
        trigger = "!inventory";
    }

    /**
     * Triggers command behavior for any message containing !inventory. Checks to see
     * if there is an integer argument provided for quote number.
     * @param event The channel message event being checked.
     */
    @Override
    public void parseCommand(ChannelMessageEvent event) {
        Optional<String> argument = parseCommandArgument(event);

        if (argument.isPresent()) {
            if (rarityMap.containsValue(argument.get()))
                command(event, argument.get());
            else
                command(event);
        }
    }

    /**
     * Displays a message showing the user's inventory. On error displays a message stating that
     * their inventory is "lost" or "empty".
     * @param event The channel message event that triggered the command.
     */
    @Override
    protected void command(ChannelMessageEvent event) {
        Function<Map.Entry<Integer, Integer>, String> mapper = (entry) -> "%dx %s".formatted(entry.getValue(), rarityMap.get(entry.getKey()));

        try {
            HashMap<Integer, Integer> map = store.getInventory(event.getUser().getId());
            if (map.isEmpty())
                sendMessage("@%s seems like don't have anything in your inventory!".formatted(event.getUser().getName()));
            else {
                String string = map.entrySet().stream().map(mapper).filter(Objects::nonNull).collect(Collectors.joining(", "));
                sendMessage("@%s you have found: %s!".formatted(event.getUser().getName(), string));
            }
        }
        catch (SQLException sqlException) {
            logger.error(sqlException.getMessage());
            sendMessage("Seems like you lost your inventory!");
        }
    }

    /**
     * Displays a message showing distinct items found for one rarity for the given user.
     * @param event The channel message event that triggered the command.
     */
    @EventSubscriber
    private void command(ChannelMessageEvent event, String argument) {
        try {
            ArrayList<String> names = (ArrayList<String>) store.getItemNames(event.getUser().getId(), argument);
            if (names.isEmpty())
                sendMessage("@%s seems like you haven't found any %s items!".formatted(event.getUser().getName(), argument));
            else
                sendMessage("@%s's %s inventory: %s.".formatted(event.getUser().getName(), argument, String.join( ", ", names)));
        }
        catch (SQLException sqlException) {
            logger.error(sqlException.getMessage());
            sendMessage("Seems like you lost your inventory!");
        }
    }
}
