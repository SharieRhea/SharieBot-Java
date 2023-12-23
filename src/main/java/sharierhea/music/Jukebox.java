package sharierhea.music;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sharierhea.Store;

public class Jukebox {
    private final String directoryPath = "/home/sharie/Music/Stream Music";
    private final HashMap<String, Path> filepaths;
    private final Store store;
    private final Logger logger = LoggerFactory.getLogger(Jukebox.class);
    private final Queue<String> songQueue;

    public Jukebox(Store database) throws Exception {
        store = database;
        filepaths = new HashMap<>();
        MP3agic mp3agic = new MP3agic();

        songQueue = new ArrayDeque<>();
        initializeSongList(mp3agic);
    }

    /**
     * Initializes the song list and adds new songs to the database if necessary.
     * Populates a hashmap for hash -> filepath
     * @param mp3Metadata The object used to retrieve metadata
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws SQLException
     */
    private void initializeSongList(MP3Metadata mp3Metadata) throws IOException, NoSuchAlgorithmException, SQLException {
        // Clear the map to refresh this play session
        filepaths.clear();

        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        if (files == null)
            throw new FileNotFoundException("Invalid path to song directory.");
        for (File file : files) {
            String hexString = getFileHash(file);

            // add to hashmap<hash, filepath>
            filepaths.put(hexString, file.toPath());

            boolean bool = store.songExists(hexString);
            // Song already exists, move on to the next one
            if (bool)
                continue;

            String[] metadata = mp3Metadata.getMetadata(file);
            if (metadata[0] == null || metadata[1] == null || metadata[2] == null) {
                logger.error("Invalid or missing metadata for file %s".formatted(file.toPath()));
                continue;
            }

            String title = metadata[0];
            int artistID = store.tryAddArtistOrAlbum(metadata[1], "artist");
            int albumID = store.tryAddArtistOrAlbum(metadata[2], "album");
            store.addSong(hexString, title, artistID, albumID);
        }

        // shuffle the list
        songQueue.addAll(shuffleSongs());
        System.out.println(songQueue);
    }

    /**
     * Returns a string of the file's hash using SHA-256
     * @param file The file to hash
     * @return Hexadecimal string of the hash
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    private String getFileHash(File file) throws NoSuchAlgorithmException, IOException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = Files.readAllBytes(file.toPath());
        var hash = messageDigest.digest(bytes);
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private List<String> shuffleSongs() {
        ArrayList<String> list = new ArrayList<>(filepaths.keySet().stream().toList());
        Collections.shuffle(list);
        return list;
    }

    public static void main(String[] args) throws Exception {
        var jukebox = new Jukebox(new Store());
    }

    // shuffle?
    // skip
    // pause/play
    // send poll warning
    // launches poll
    // point redemption: request a specific song
}
