package sharierhea.commands;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import sharierhea.Launcher;

import java.sql.SQLException;

public class AddItemCommand extends Command {
    private String itemName;
    private String rarity;

    /**
     * Constructor to initialize the command, sets up onEvent behavior.
     */
    public AddItemCommand() {
        super();
        trigger = "!additem";
    }


    @Override
    protected void parseCommand(ChannelMessageEvent event) {
        if (event.getUser().getId().equals("170582504") && event.getMessage().startsWith(trigger)) {
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
            sendMessage("Added item number %d!".formatted(Launcher.store.addItem(itemName, rarity)));
        } catch (SQLException sqlException) {
            sendMessage("Unable to add item!");
            logger.error("Unable to add item", sqlException);
        }
    }
}
