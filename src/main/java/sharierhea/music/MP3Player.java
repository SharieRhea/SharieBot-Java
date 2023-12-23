package sharierhea.music;

import javafx.application.Application;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

public class MP3Player extends Application {

    @Override
    public void start(Stage primaryStage) {
        String mp3FilePath = "src/resources/1AM.mp3";
        AtomicReference<Media> media = new AtomicReference<>(new Media(new File(mp3FilePath).toURI().toString()));
        MediaPlayer mediaPlayer = new MediaPlayer(media.get());

        // Optional: You can add event handlers to handle playback events
        mediaPlayer.setOnReady(() -> {
            System.out.println("Media is ready to play");
        });

        mediaPlayer.setOnEndOfMedia(() -> {
            System.out.println("Playback finished");
        });

        // Start playing the media
        mediaPlayer.play();

       /* mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayer.dispose();
            media.set(new Media(new File("/home/sharie/Music/Stream Music/Mipha's Theme").toURI().toString()));
            mediaPlayer = new MediaPlayer(media)
            mediaPlayer.play();
        });*/
    }

    public static void main(String[] args) {
        launch(args);
    }
}

