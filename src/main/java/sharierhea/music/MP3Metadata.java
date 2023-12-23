package sharierhea.music;

import java.io.File;

/**
 * Abstraction for getting MP3 metadata, such as title, artist, and album.
 */
public interface MP3Metadata {
    String[] getMetadata(File file);
}
