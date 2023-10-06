package com.commands;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import java.io.FileNotFoundException;
import java.io.IOException;
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
    /**
     * Constructor to initialize the command, sets up onEvent behavior.
     * @param eventHandler The handler for all the commands.
     * @param client The twitchClient for the current session.
     */
    public ShinyCommand(SimpleEventHandler eventHandler, TwitchClient client) throws FileNotFoundException {
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
        if (event.getMessage().contains("!shiny"))
            command(event);
    }

    /**
     * Sends a message in the format: "@user found a [object]!
     * @param event The channel message event that triggered the command.
     */
    @Override
    protected void command(ChannelMessageEvent event) {
        sendMessage("@" + event.getUser().getName() + " found a " + getItemRarity() + "!");
    }

    /**
     * Generates a random number to determine rarity, then returns result of
     * appropriate getRarityItem() method.
     * @return String of the item.
     */
    private String getItemRarity() {
        int number = generator.nextInt(100);
        if (number < 50)
            return getItem(commonItems);
        else if (number < 75)
            return getItem(rareItems);
        else if (number < 90)
            return getItem(epicItems);
        else if (number < 97)
            return getItem(legendaryItems);
        else
            return getItem(mythicItems);
    }

    /**
     * Returns an item from the specified array.
     * @param array The array to grab an item from.
     * @return String of the item.
     */
    private String getItem(String[] array) {
        return array[generator.nextInt(array.length)];
    }


    /*
        - categories of rarity
          - common, rare, epic, legendary, mythic
          - 50%, 25%, 15%, 7%, 3%
        - categories of things? NullPointerException
        -
     */
}
