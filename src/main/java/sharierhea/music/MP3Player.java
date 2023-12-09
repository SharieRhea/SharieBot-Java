package sharierhea.music;

import javafx.application.Application;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;

public class MP3Player extends Application {

    @Override
    public void start(Stage primaryStage) {
        String mp3FilePath = "src/resources/1AM.mp3";
        Media media = new Media(new File(mp3FilePath).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);

        // Optional: You can add event handlers to handle playback events
        mediaPlayer.setOnReady(() -> {
            System.out.println("Media is ready to play");
        });

        mediaPlayer.setOnEndOfMedia(() -> {
            System.out.println("Playback finished");
        });

        // Start playing the media
        mediaPlayer.play();

        primaryStage.setTitle("MP3 Player");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

