package sharierhea;

import io.obswebsocket.community.client.OBSRemoteController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;

import static sharierhea.Launcher.ENABLE_OBS_WEBSOCKET;

public class SocketHandler {
    OBSRemoteController obsRemoteController;
    private final Logger logger = LoggerFactory.getLogger(Store.class);

    public SocketHandler() throws IOException {
        if (!ENABLE_OBS_WEBSOCKET)
            return;

        String pass;
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/java/sharierhea/auth/socketInfo.txt"))) {
            pass = reader.readLine();
        } catch (IOException exception) {
            logger.error("Unable to read credentials for OBS websocket", exception);
            throw new IOException(exception);
        }

        obsRemoteController = OBSRemoteController.builder()
            .autoConnect(true)
            .host("127.0.0.1")
            .port(4455)
            .password(pass)
            .build();

        obsRemoteController.connect();
        // todo: implement on ready callback to prevent requests before connection has succeeded
        // todo: figure out how to throw an exception if connection fails
            // if websocket is enabled and can't connect, program should fail
    }

    /**
     * Changes the current scene in OBS to the specified scene name.
     * @param sceneName The scene to switch to.
     */
    public void changeScene(String sceneName){
        obsRemoteController.setCurrentProgramScene(sceneName, 100);
    }

    /**
     * Returns the ID of a source from a given scene.
     * @param sceneName The scene the source is in.
     * @param sourceName The source name to retrieve the ID from.
     * @return The integer ID of the source.
     */
    public int getSourceID(String sceneName, String sourceName) {
        var response = obsRemoteController.getSceneItemId(sceneName, sourceName, 0, 100);
        return response.getSceneItemId().intValue();
    }

    /**
     * Sets a scene item to be visible.
     * @param sceneName The scene the item is in.
     * @param sourceName The name of the item to set visible.
     */
    public void setSceneItemVisible(String sceneName, String sourceName) {
        int sourceID = getSourceID(sceneName, sourceName);
        obsRemoteController.setSceneItemEnabled(sceneName, sourceID, true, 100);
    }

    /**
     * Sets a scene item to be hidden.
     * @param sceneName The scene the item is in.
     * @param sourceName The name of the item to hide.
     */
    public void setSceneItemHidden(String sceneName, String sourceName) {
        int sourceID = getSourceID(sceneName, sourceName);
        obsRemoteController.setSceneItemEnabled(sceneName, sourceID, false, 100);
    }

    /**
     * Returns the name of the current scene in OBS.
     * @return String name of the current scene.
     */
    public String getCurrentScene() {
        var response = obsRemoteController.getCurrentProgramScene(100);
        return response.getCurrentProgramSceneName();
    }

    /**
     * Toggles the visibility of the given source, showing the source for the given number of seconds.
     * @param sceneName The scene OR group in which the source resides.
     * @param sourceName The name of the source to show.
     * @param seconds The number of seconds for the source to remain visible.
     */
    public void showAndHideSource(String sceneName, String sourceName, int seconds) {
        setSceneItemVisible(sceneName, sourceName);
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                setSceneItemHidden(sceneName, sourceName);
                timer.cancel();
            }
        };
        timer.schedule(task, Duration.ofSeconds(seconds).toMillis());
    }

}
