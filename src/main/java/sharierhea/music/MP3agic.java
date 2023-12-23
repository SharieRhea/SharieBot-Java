package sharierhea.music;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.File;
import java.io.IOException;

/**
 * Handles retrieving metadata for the given file using the MP3agic dependency.
 */
public class MP3agic implements MP3Metadata {

    public MP3agic() {
        // empty constructor
    }

    /**
     * Retrieves relevant metadata for the given file.
     * @param file The file to retrieve metadata from.
     * @return The song's title, artist, and album as a string array.
     */
    @Override
    public String[] getMetadata(File file) {
        Mp3File mp3File = null;
        try {
            mp3File = new Mp3File(file);
        } catch (IOException | UnsupportedTagException | InvalidDataException e) {
            throw new RuntimeException("Error creating MP3 file", e);
        }
        String[] returnValue = new String[3];
        if (mp3File.hasId3v1Tag()) {
            var tag = mp3File.getId3v1Tag();
            returnValue[0] = tag.getTitle();
            returnValue[1] = tag.getArtist();
            returnValue[2] = tag.getAlbum();
        }
        else if (mp3File.hasId3v2Tag()) {
            var tag = mp3File.getId3v2Tag();
            returnValue[0] = tag.getTitle();
            returnValue[1] = tag.getArtist();
            returnValue[2] = tag.getAlbum();
        }
        else
            throw new RuntimeException("Invalid metadata for file: " + file.toPath());
        return returnValue;
    }
}
