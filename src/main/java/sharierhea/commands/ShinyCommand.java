package sharierhea.commands;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.stream.Stream;

public class ShinyCommand extends Command {

    Random generator;
    String[] commonItems;
    String[] rareItems;
    String[] epicItems;
    String[] legendaryItems;
    String[] mythicItems;
    FileWriter writer;

    /**
     * Constructor to initialize the command, sets up onEvent behavior.
     * @param eventHandler The handler for all the commands.
     * @param client The twitchClient for the current session.
     */
    public ShinyCommand(SimpleEventHandler eventHandler, TwitchClient client) {
        super(eventHandler, client);
        generator = new Random();

        // For each rarity category, populate appropriate array
        // idea: make it so that array is only repopulated when necessary (on change?)
        try (Stream<String> stream = Files.lines(Path.of("src/resources/shiny_objects_common.txt"))) {
            commonItems = stream.toArray(String[]::new);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (Stream<String> stream = Files.lines(Path.of("src/resources/shiny_objects_rare.txt"))) {
            rareItems = stream.toArray(String[]::new);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (Stream<String> stream = Files.lines(Path.of("src/resources/shiny_objects_epic.txt"))) {
            epicItems = stream.toArray(String[]::new);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (Stream<String> stream = Files.lines(Path.of("src/resources/shiny_objects_legendary.txt"))) {
            legendaryItems = stream.toArray(String[]::new);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (Stream<String> stream = Files.lines(Path.of("src/resources/shiny_objects_mythic.txt"))) {
            mythicItems = stream.toArray(String[]::new);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Triggers command behavior for any message containing "!shiny"
     * @param event The channel message event being checked.
     */
    @Override
    protected void parseCommand(ChannelMessageEvent event) {
        if (event.getMessage().contains("!shiny")) {
            File file = new File("src/resources/users/" + event.getUser().getName());
            try {
                file.createNewFile();
                writer = new FileWriter(file, true);

            }
            catch (IOException ioException) {
                logger.error(ioException.getMessage());
            }
            command(event);
        }
    }

    /**
     * Sends a message in the format: "@user found a [object]!"
     * @param event The channel message event that triggered the command.
     */
    @Override
    protected void command(ChannelMessageEvent event) {
        sendMessage("@" + event.getUser().getName() + " found a " + getRarityAndItem() + "!");
    }

    /**
     * Generates a random number to determine rarity, then returns result of
     * appropriate getRarityItem() method.
     * @return String of the item.
     */
    private String getRarityAndItem() {
        int number = generator.nextInt(100);
        String item;
        String rarity;

        if (number < 50) {
            item = getItem(commonItems);
            rarity = " (common)";
        }
        else if (number < 75) {
            item = getItem(rareItems);
            rarity = " (rare)";
        }
        else if (number < 90) {
            item = getItem(epicItems);
            rarity = " (epic)";
        }
        else if (number < 97) {
            item = getItem(legendaryItems);
            rarity = " (legendary)";
        }
        else {
            item = getItem(mythicItems);
            rarity = " (mythic)";
        }
        addToInventory(item);
        return item + rarity;
    }

    /**
     * Returns an item from the specified array.
     * @param array The array to grab an item from.
     * @return String of the item.
     */
    private String getItem(String[] array) {
        return array[generator.nextInt(array.length)];
    }

    /**
     * Adds the specified item to the user's "inventory", which is
     * a text file.
     * @param item The item to add.
     */
    private void addToInventory(String item) {
        try {
            writer.append(item).append("\n");
            writer.flush();
        }
        catch (IOException ioException) {
            logger.error(ioException.getMessage());
        }
    }
}

/*
    Current thoughts:
     - text files suck, replace with a lightweight database?
        - sqlite, maybe H2
     - deployment (maybe at some point in the future?)
        - AWS
        - render
     - make item a class?
     - use hashmap to keep track of repeated items?
     - how to integrate with !inventory?
 */
