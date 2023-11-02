package sharierhea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * Database layer to deal with connection to a sqlite database.
 */
public class Store {
    private String path = "jdbc:sqlite:src/resources/identifier.sqlite";
    private Connection connection;
    private Logger logger = LoggerFactory.getLogger(Store.class);

    public record Quote(String id, String text, String date) { }

    /**
     * Constructor for store, attempts to establish a connection to the database.
     */
    public Store() {
        try {
            connection = DriverManager.getConnection(path);
            logger.debug("Connection succeeded.");
        }
        catch (SQLException exception) {
            logger.error("Failed to connect to database", exception);
        }
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
        String sqlGetNumber = "SELECT COUNT(*) FROM item";
        PreparedStatement statementGetNumber = connection.prepareStatement(sqlGetNumber);
        return statementGetNumber.executeQuery().getInt(1);
    }

    public int addItem(String itemName, String rarity) throws SQLException {
        String sqlGetID = "SELECT id FROM rarity WHERE title LIKE ?";
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
}
