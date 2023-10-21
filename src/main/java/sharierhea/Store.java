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
     * Queries the quotes table and retrieves a random tuple.
     * @return Quote object with id, text, and date from the retrieved tuple.
     * @throws SQLException Nonexistent table.
     */
    public Quote queryRandomQuote() throws SQLException {
        String sql = "SELECT id, text, date FROM quotes ORDER BY RANDOM() LIMIT 1";
        PreparedStatement statement = connection.prepareStatement(sql);
    ResultSet resultSet = statement.executeQuery();
        return new Quote(resultSet.getString("id"), resultSet.getString("text"), resultSet.getString("date"));
    }
}
