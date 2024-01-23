package sharierhea.commands;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import sharierhea.SocketHandler;

public class TestCommand extends Command {
    private SocketHandler socket;

    public TestCommand(SimpleEventHandler eventHandler, TwitchClient twitchClient, SocketHandler socketHandler) {
        super(eventHandler, twitchClient);
        socket = socketHandler;
        trigger = "!test";
    }

    @Override
    public void parseCommand(ChannelMessageEvent event) {
        if (event.getUser().getId().equals("170582504") && event.getMessage().contains(trigger))
            command(event);
    }

    /**
     * The method that defines the command's behavior. Runs when parseCommand finds the command trigger.
     * @param event The channel message event that triggered the command.
     */
    @Override
    protected void command(ChannelMessageEvent event) {
        socket.setSceneItemVisible("main", "FaceCam");
    }
}
