package sharierhea.music;

import java.io.*;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.eventsub.domain.PollChoice;
import com.github.twitch4j.eventsub.domain.PollStatus;
import com.github.twitch4j.helix.domain.Poll;
import com.github.twitch4j.pubsub.domain.PollData;
import javafx.event.EventHandler;
import javafx.scene.media.Media;
import javafx.scene.media.MediaMarkerEvent;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sharierhea.Store;

public class Jukebox {
    private final static int AUTO_SONG_NUMBER = 5;
    private final static int POLL_OPTIONS = 5;
    private final static int POLL_ACCEPTED = 4;
    private final TwitchClient twitchClient;
    private final OAuth2Credential credential;

    private final static String DIRECTORY_PATH = "/home/sharie/Music/Stream Music";
    private final static String CHANNEL_NAME = "shariemakesart";
    private final HashMap<String, Path> filepaths;
    private final Store store;
    private final Logger logger = LoggerFactory.getLogger(Jukebox.class);

    private final Queue<SongRequest> songQueue;
    private final Queue<String> songDump;
    private MediaPlayer mediaPlayer;
    private Media media;

    private FileWriter fileWriter;
    private List<String> currentPollSongs = new ArrayList<>();

    private Set<String> skipUsers = new HashSet<>();

    private enum SongCategories {
        AUTO, POLL, USER
    }

    private record SongRequest(String hash, SongCategories category, int id) {}

    public int addSkipUser(String userID) {
        skipUsers.add(userID);
        return skipUsers.size();
    }

    /**
     *
     * @param twitchClient
     * @param database
     * @param credential
     * @throws Exception
     */
    public Jukebox(TwitchClient twitchClient, Store database, OAuth2Credential credential) throws Exception {
        this.twitchClient = twitchClient;
        this.credential = credential;
        store = database;
        filepaths = new HashMap<>();
        MP3agic mp3agic = new MP3agic();

        songQueue = new ArrayDeque<>();
        songDump = new ArrayDeque<>();
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
    public void initializeSongList(MP3Metadata mp3Metadata) throws IOException, NoSuchAlgorithmException, SQLException {
        // Clear the map to refresh this play session
        filepaths.clear();

        File directory = new File(DIRECTORY_PATH);
        File[] files = directory.listFiles();
        if (files == null)
            throw new FileNotFoundException("Invalid path to song directory.");
        for (File file : files) {
            String hexString = getFileHash(file);

            // add to hashmap<hash, filepath>
            filepaths.put(hexString, file.toPath());

            // Song already exists, move on to the next one
            if (store.songExists(hexString))
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

        songDump.addAll(shuffleSongs());
        // Move first 5 songs to songQueue to start with
        for (int i = 0; i < AUTO_SONG_NUMBER; i++) {
            songQueue.add(new SongRequest(songDump.remove(), SongCategories.AUTO, -1));
        }

        play();
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

    /**
     * Shuffles and returns the list.
     * @return List of shuffled songs.
     */
    private List<String> shuffleSongs() {
        ArrayList<String> list = new ArrayList<>(filepaths.keySet().stream().toList());
        Collections.shuffle(list);
        return list;
    }

    /**
     * Sets up and begins playing the next song in the queue. Eventhandlers for starting polls, playing next song,
     * and adding play information to the database.
     * @throws IOException
     */
    private void play() {
        // If the queue is empty, just grab one from the songDump instead
        if (songQueue.isEmpty())
            songQueue.add(new SongRequest(songDump.remove(), SongCategories.AUTO, -1));

        skipUsers.clear();

        SongRequest request = songQueue.remove();
        // URI normalization for spaces, manually replace 's
        String path = filepaths.get(request.hash).toUri().normalize().toString().replace("'", "%27");
        media = new Media(path);

        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.setOnReady(() -> {
            Duration marker = media.getDuration()
                    .subtract(Duration.seconds(90));
            if (marker.compareTo(Duration.ZERO) < 0)
                marker = Duration.ZERO;
            media.getMarkers().put("1:30 Remaining", marker);
        });

        mediaPlayer.setOnMarker(new EventHandler<>() {
            @Override
            public void handle(MediaMarkerEvent mediaMarkerEvent) {
                // When playback reaches 1:30 remaining or less, check to see if a poll should be started
                logger.debug("OnMarkerEvent reached");
                if (songQueue.isEmpty())
                    initiatePoll();
            }
        });

        try {
            String[] metadata = getMetadata(request.hash);
            fileWriter = new FileWriter("src/resources/currentSong.txt", false);
            // Update text file for OBS
            fileWriter.write("Music from Gamechops.com      %s - %s      ".formatted(metadata[0], metadata[1]));
            fileWriter.close();
        }
        catch (IOException e) {
            logger.error("IO failure for writing to currentSong.txt", e);
        }

        if (songQueue.isEmpty())
            // Last song is starting, send an announcement in chat about poll
            twitchClient.getChat().sendMessage(CHANNEL_NAME, "/me A poll to choose the next round of songs will be starting soon, remember to vote!");

        mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayer.dispose();
            try {
                // Update the database with song information after entire song has been played (not skipped)
                store.addPlay(request.category.name(), request.hash, request.id);
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
            play();
        });
    }

    /**
     * Attempts to retrieve song metadata from the database.
     * @param hash The hash identifying the song.
     * @return String[3] with title, artist, album
     */
    private String[] getMetadata(String hash) {
        String[] metadata;
        try {
            metadata = store.getSongMetadata(hash);
        } catch (SQLException e) {
            logger.error("Unable to retrieve metadata", e);
            throw new RuntimeException(e);
        }
        return metadata;
    }

    /**
     * Functionality for the !skip command, stop current song and start the next song.
     */
    public void skip() {
        mediaPlayer.dispose();
        play();
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public void resume() {
        mediaPlayer.play();
    }

    /**
     * Creates a sends a poll with 5 song options.
     */
    private void initiatePoll() {
        currentPollSongs.clear();
        // Ensure songDump has enough songs
        if (songDump.size() < POLL_OPTIONS)
            songDump.addAll(shuffleSongs());

        ArrayList<PollChoice> choices = new ArrayList<>();
        for (int i = 0; i < POLL_OPTIONS; i++) {
            String hash = songDump.remove();
            String[] metadata = getMetadata(hash);

            String option = "%d. %s".formatted(i + 1, metadata[0]);
            String title = (option.length() > 25) ? option.substring(0, 25) : option;
            choices.add((new PollChoice(null, title, null, null, null)));
            currentPollSongs.add(hash);
        }
        Poll poll = new Poll(null, "170582504", "shariemakesart",
                "shariemakesart", "Vote for Upcoming Songs!", choices, false,
                0, false, 0, PollStatus.ACTIVE, 60,
                Instant.now(), null);

        twitchClient.getHelix().createPoll(credential.getAccessToken(), poll).execute();
    }

    /**
     * Retrieves poll results and stores information in the database. Adds top songs to the queue and discards
     * other songs.
     * @param results The results object from the eventhandler.
     */
    public void handlePollResults(List<PollData.PollChoice> results) {
        // add poll results to the database
        ArrayList<String> hashes = new ArrayList<>();
        ArrayList<Integer> votes = new ArrayList<>();
        HashMap<String, Integer> map = new HashMap<>();
        int pollID;
        int index = 0;
        for (PollData.PollChoice choice : results) {
            String hash = currentPollSongs.get(index);
            int vote = choice.getTotalVoters();
            hashes.add(hash);
            votes.add(vote);
            map.put(hash, vote);
            index++;
        }
        try {
            pollID = store.addPollResults(hashes, votes);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        HashSet<Integer> set = new HashSet<>(votes);
        ArrayList<Integer> numbers = new ArrayList<>(set);
        numbers.sort(null);

        int counter = 0;
        for (int i = numbers.size() - 1; i >= 0; i--) {
            if (counter == POLL_ACCEPTED)
                break;
            for (String hash : hashes) {
                if (counter == POLL_ACCEPTED)
                    break;
                if (Objects.equals(numbers.get(i), map.get(hash))) {
                    songQueue.add(new SongRequest(hash, SongCategories.POLL, pollID));
                    counter++;
                }
            }
        }
    }

    // todo: point redemption: user requested
}