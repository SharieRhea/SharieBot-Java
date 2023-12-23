package sharierhea.music;

import java.io.File;

public class Jukebox {
    private final String directoryPath = "/home/sharie/Music/Stream Music";

    public Jukebox() throws Exception {
        MP3agic mp3agic = new MP3agic();
        initializeSongList(mp3agic);
    }

    private void initializeSongList(MP3Metadata mp3Metadata) throws Exception {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        if (files == null)
            throw new Exception("Invalid path to song directory.");
        for (File file : files) {
            var metadata = mp3Metadata.getMetadata(file);
            if (metadata[0] == null || metadata[1] == null || metadata[2] == null)
                // Todo: change this: If any files have invalid (missing) metadata, stop iterating over song list
                return;
        }

    }

    public static void main(String[] args) throws Exception {
        var jukebox = new Jukebox();
    }

    // shuffle?
    // skip
    // pause/play
    // send poll warning
    // launches poll
    // point redemption: request a specific song
}
