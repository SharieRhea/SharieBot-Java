package sharierhea.commands;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import sharierhea.Store;

import java.sql.SQLException;

public class AddItemCommand extends Command {
    private Store store;
    private String itemName;
    private String rarity;

    /**
     * Constructor to initialize the command, sets up onEvent behavior.
     * @param eventHandler The handler for all the commands.
     * @param client The twitchClient for the current session.
     * @param dbStore The database connection to use.
     */
    public AddItemCommand(SimpleEventHandler eventHandler, TwitchClient client, Store dbStore) {
        super(eventHandler, client);
        store = dbStore;
    }


    @Override
    protected void parseCommand(ChannelMessageEvent event) {
        if (event.getUser().getId().equals("170582504") && event.getMessage().startsWith("!additem")) {
            String[] words = event.getMessage().split(" ");

            if (words.length != 3) {
                sendMessage("Expected 2 arguments for '!additem'!");
                return;
            }

            itemName = words[1];
            rarity = words[2];
            command(event);
        }
    }

    @Override
    protected void command(ChannelMessageEvent event) {
        try {
            sendMessage("Added item number " + store.addItem(itemName, rarity) + "!");
        }
        catch (SQLException sqlException) {
            sendMessage("Unable to add item!");
            logger.error("Unable to add item", sqlException);
        }
    }
}
