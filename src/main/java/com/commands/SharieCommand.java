package com.commands;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class SharieCommand extends Command {

    /**
     * Constructor to initialize the command, sets up onEvent behavior.
     * @param eventHandler The handler for all the commands.
     * @param client The twitchClient for the current session.
     */
    public SharieCommand(SimpleEventHandler eventHandler, TwitchClient client) {
        super(eventHandler, client);
    }

    /**
     * Triggers command behavior if a message contains "!sharie"
     * @param event The channel message event being checked.
     */
    @Override
    protected void parseCommand(ChannelMessageEvent event) {
        if (event.getMessage().contains("!sharie"))
            command(event);
    }

    /**
     * Sends a message explaining "Sharie".
     * @param event The channel message event that triggered the command.
     */
    @Override
    protected void command(ChannelMessageEvent event) {
        sendMessage("Yes, Sharie is my actual name, it is pronounced \"shar-ee\", shar like car :)");
    }
}
