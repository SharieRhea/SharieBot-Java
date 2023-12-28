package sharierhea;

import com.github.twitch4j.pubsub.domain.PollData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Database layer to deal with connection to a sqlite database.
 */
public class Store {
    private Connection connection;
    private HashMap<Integer, String> rarityMap;
    private final Logger logger;

    public record Quote(String id, String text, String date) { }

    /**
     * Constructor for store, attempts to establish a connection to the database.
     */
    public Store() {
        logger = LoggerFactory.getLogger(Store.class);
        try {
            String path = "jdbc:sqlite:src/resources/identifier.sqlite";
            connection = DriverManager.getConnection(path);
            logger.debug("Connection succeeded.");
        }
        catch (SQLException exception) {
            logger.error("Failed to connect to database", exception);
        }

        try{
            rarityMap = populateRarityMap();
        }
        catch (SQLException exception) {
            logger.error("Failed to populate rarityMap", exception);
        }
    }

    public HashMap<Integer, String> getRarityMap() {
        return rarityMap;
    }

    /**
     * Queries the quote table and retrieves a random tuple.
     * @return Quote object with id, text, and date from the retrieved tuple.
     * @throws SQLException Nonexistent table.
     */
    public Quote queryRandomQuote() throws SQLException {
        String sql = "SELECT id, text, date FROM quote ORDER BY RANDOM() LIMIT 1";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        return new Quote(resultSet.getString("id"), resultSet.getString("text"), resultSet.getString("date"));
    }

    /**
     * Queries the quote table and retrieves the quote associated with the given quoteNumber.
     * @param quoteNumber The id of the quote to get.
     * @return Quote object with id, text, and date from the retrieved tuple.
     * @throws SQLException Nonexistent table.
     */
    public Quote queryQuote(int quoteNumber) throws SQLException {
        String sql = "SELECT id, text, date FROM quote WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, quoteNumber);
        ResultSet resultSet = statement.executeQuery();
        return new Quote(resultSet.getString("id"), resultSet.getString("text"), resultSet.getString("date"));
    }

    /**
     * Adds a new quote to the quote table with the given text. Id and date are set automatically.
     * @param text The actual quote.
     * @throws SQLException Nonexistent.
     */
    public int addQuote(String text) throws SQLException {
        String sql = "INSERT INTO quote(text) VALUES(?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, text);
        statement.executeUpdate();
        String sqlGetNumber = "SELECT COUNT(*) FROM quote";
        PreparedStatement statementGetNumber = connection.prepareStatement(sqlGetNumber);
        return statementGetNumber.executeQuery().getInt(1);
    }

    /**
     * Adds a new item to the item table with the provided name and foreign key reference
     * to its rarity level.
     * @param itemName The item name.
     * @param rarity The item's rarity as plain text
     * @return The number corresponding to the added item.
     * @throws SQLException Nonexistent table or duplicate item.
     */
    public int addItem(String itemName, String rarity) throws SQLException {
        String sqlGetID = "SELECT id FROM rarity WHERE title = ?";
        PreparedStatement statementGetID = connection.prepareStatement(sqlGetID);
        statementGetID.setString(1, rarity);
        ResultSet resultSet = statementGetID.executeQuery();
        int rarityID = resultSet.getInt("id");

        String sql = "INSERT INTO item(rarityID, name) VALUES(?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, rarityID);
        statement.setString(2, itemName);

        statement.executeUpdate();
        String sqlGetNumber = "SELECT COUNT(*) FROM item";
        PreparedStatement statementGetNumber = connection.prepareStatement(sqlGetNumber);
        return statementGetNumber.executeQuery().getInt(1);
    }

    /**
     * Attempts to insert a new userID and username into the user table, does nothing on an id
     * conflict (user already exists)
     * @param userID The userID
     * @param username The username
     * @throws SQLException Nonexistent table or other exception
     */
    public void getUser(String userID, String username) throws SQLException {
        String sqlQueryUser = "INSERT INTO user(userID, username) VALUES(?, ?) ON CONFLICT(userID) DO NOTHING";
        PreparedStatement statement = connection.prepareStatement(sqlQueryUser);
        statement.setString(1, userID);
        statement.setString(2, username);
        statement.executeUpdate();
    }

    /**
     * Determines a random item based on weighted probabilities.
     * @return The rarityID or -1 for an error.
     * @throws SQLException Nonexistent table or other.
     */
    public int getRarity() throws SQLException {
        String sql = "SELECT probability FROM rarity";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        LinkedList<Integer> probabilities = new LinkedList<>();

        int current;
        int previous = 0;
        while (resultSet.next()) {
            current = resultSet.getInt("probability");
            // Cumulative probabilities
            probabilities.add(current + previous);
            previous = probabilities.getLast();
        }

        Random generator = new Random();
        int number = generator.nextInt(previous) + 1;
        for (int i = 1; i < probabilities.size() + 1; i++) {
            // Rarities are 1 indexed
            if (number <= probabilities.get(i - 1))
                return i;
        }
        return -1;
    }

    /**
     * Gets a random item based off the given rarityID for the userID provided.
     * @param userID The user who "found" this item.
     * @param rarityID The rarityID for the item.
     * @return A formatted string: <itemName> (<rarityTitle>)
     * @throws SQLException Nonexistent table or other.
     */
    public String getItem(String userID, int rarityID) throws SQLException {
        String sql = "SELECT id, name FROM item WHERE rarityID = ? ORDER BY RANDOM() LIMIT 1";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, rarityID);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        int itemID = resultSet.getInt("id");
        insertItem(userID, itemID);

        return resultSet.getString("name") + " (" + rarityMap.get(rarityID) + ")";
    }

    /**
     * Inserts the item given by itemID into the inventory table for the user given by
     * userID. If the item already exists, increment its count by 1.
     * @param userID The userID to insert
     * @param itemID The itemID to insert
     * @throws SQLException Nonexistent table or other
     */
    private void insertItem(String userID, int itemID) throws SQLException {
        String sql = "INSERT INTO inventory(userID, itemID) VALUES(?, ?) ON CONFLICT(userID, itemID) DO UPDATE SET count = count + 1";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, userID);
        statement.setInt(2, itemID);
        statement.executeUpdate();
    }

    /**
     * Gets a hashmap from rarityId -> number of items found for the given user.
     * @param userID The id for the user whose inventory will be found.
     * @return HashMap<Integer, Integer>
     * @throws SQLException Nonexistent table or other.
     */
    public HashMap<Integer, Integer> getInventory(String userID) throws SQLException {
        String sql = """
                SELECT item.rarityID, SUM(count)
                FROM (inventory JOIN item ON (inventory.itemID = item.id))
                WHERE inventory.userID = ?
                GROUP BY item.rarityID
                ORDER BY item.rarityID""";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, userID);
        ResultSet resultSet = statement.executeQuery();

        HashMap<Integer, Integer> map = new HashMap<>();
        while (resultSet.next()) {
            map.put(resultSet.getInt("rarityID"), resultSet.getInt("SUM(count)"));
        }
        return map;
    }

    /**
     * Create the rarityMap based on the rarity table for repeated use.
     * @return HashMap<Integer, String> rarityMap
     * @throws SQLException Nonexistent table or other
     */
    private HashMap<Integer, String> populateRarityMap() throws SQLException {
        String sql = "SELECT id, title FROM rarity";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        HashMap<Integer, String> map = new HashMap<>();
        while (resultSet.next()) {
            map.put(resultSet.getInt("id"), resultSet.getString("title"));
        }
        return map;
    }

    /**
     * Returns a list of item names for a given user and rarityTitle.
     * @param userID The userID to retrieve items for.
     * @param rarityTitle The rarity to filter by.
     * @return A list of names.
     * @throws SQLException Nonexistent table or other.
     */
    public List<String> getItemNames(String userID, String rarityTitle) throws SQLException {
        int rarityID;
        var resultEntry = rarityMap.entrySet().stream().filter(entry ->
            entry.getValue().equals(rarityTitle)).findFirst();
        if (resultEntry.isPresent())
            rarityID = resultEntry.get().getKey();
        else {
            logger.error("Could not find rarityID for given title.");
            return new ArrayList<>();
        }

        String sql = """
                SELECT DISTINCT item.name
                FROM (inventory JOIN item ON (inventory.itemID = item.id))
                WHERE inventory.userID = ? AND item.rarityID = ?""";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, userID);
        statement.setInt(2, rarityID);
        ResultSet resultSet = statement.executeQuery();

        ArrayList<String> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(resultSet.getString("name"));
        }
        return list;
    }

    /**
     * Returns true if this song (identified by its hash) already exists in the
     * table song
     * @param hash The hash to check
     * @return True if exists, false otherwise
     * @throws SQLException
     */
    public boolean songExists(String hash) throws SQLException {
        String sql = "SELECT id FROM song WHERE id LIKE ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, hash);
        ResultSet resultSet = statement.executeQuery();
        // If there are no rows in the result set, "isBeforeFirst" is false, otherwise true
        return resultSet.isBeforeFirst();
    }

    /**
     * First, queries the database for the artist/album id, if found returns it. Otherwise,
     * add the new artist/album, re-query and return the id.
     * @param data The artist/album name to add.
     * @param attribute artist or album
     * @return The id of the given artist/album name
     * @throws SQLException
     */
    public int tryAddArtistOrAlbum(String data, String attribute) throws SQLException {
        String query = "SELECT id FROM %s WHERE name LIKE ?".formatted(attribute);
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, data);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.isBeforeFirst()) {
            resultSet.next();
            return resultSet.getInt("id");
        }

        // Add the data
        String sql = "INSERT INTO %s(name) VALUES(?)".formatted(attribute);
        PreparedStatement insertStatement = connection.prepareStatement(sql);
        insertStatement.setString(1, data);
        insertStatement.executeUpdate();

        resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt("id");
    }

    /**
     * Adds a new song to the song table.
     * @param hash The hash of the file (primary key)
     * @param title The title of the song
     * @param artistID Foreign key reference to the artist
     * @param albumID Foreign key reference to the album
     * @throws SQLException
     */
    public void addSong(String hash, String title, int artistID, int albumID) throws SQLException {
        String sql = "INSERT INTO song(id, title, artistID, albumID) VALUES(?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, hash);
        statement.setString(2, title);
        statement.setInt(3, artistID);
        statement.setInt(4, albumID);
        statement.executeUpdate();
    }

    public String[] getSongMetadata(String hash) throws SQLException {
        String[] returnValue = new String[3];
        String songSQL = """
        SELECT song.title, artist.name AS artist, album.name AS album
        FROM song JOIN artist ON (song.artistID = artist.id)
        JOIN album ON (song.albumID = album.id)
        WHERE song.id LIKE ?""";
        PreparedStatement statement = connection.prepareStatement(songSQL);
        statement.setString(1, hash);
        ResultSet songResultSet = statement.executeQuery();
        songResultSet.next();
        returnValue[0] = songResultSet.getString("title");
        returnValue[1] = songResultSet.getString("artist");
        returnValue[2] = songResultSet.getString("album");
        return returnValue;
    }

    public int addPollResults(List<String> hashes, List<Integer> votes) throws SQLException {
        if (hashes.size() != 5 || votes.size() != 5) {
            logger.error("Invalid number of choices for poll results!");
            return -1;
        }

        String sql = """
        INSERT INTO poll(choice1, choice2, choice3, choice4, choice5,
        votes1, votes2, votes3, votes4, votes5)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        int index = 1;
        for (String hash : hashes) {
            preparedStatement.setString(index, hash);
            index++;
        }
        for (int vote: votes) {
            preparedStatement.setInt(index, vote);
            index++;
        }
        preparedStatement.executeUpdate();

        String query = "SELECT MAX(id) FROM poll";
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet set = statement.executeQuery();
        set.next();
        return set.getInt("MAX(id)");
    }

    public void addPlay(String category, String hash, int id) throws SQLException {
        String sql;
        PreparedStatement statement;
        switch (category) {
            case "POLL":
                sql = """
                INSERT INTO play(songID, pollID) VALUES(?, ?)""";
                statement = connection.prepareStatement(sql);
                statement.setString(1, hash);
                statement.setInt(2, id);
                break;
            case "USER":
                sql = """
                INSERT INTO play(songID, userID) VALUES(?, ?)""";
                statement = connection.prepareStatement(sql);
                statement.setString(1, hash);
                statement.setInt(2, id);
                break;
            default:
                sql = """
                INSERT INTO play(songID) VALUES(?)""";
                statement = connection.prepareStatement(sql);
                statement.setString(1, hash);
                break;
        }

        statement.executeUpdate();
    }
}