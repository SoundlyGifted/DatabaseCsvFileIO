package web.process.database;

import jakarta.ejb.Local;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * This interface contains methods to handle database connection.
 * 
 * @author SoundlyGifted
 */
@Local
public interface DBConnectionHandlerLocal {
    
    /**
     * Returns database Connection
     * 
     * @return database Connection
     * @throws java.sql.SQLException if the connection to the database can not
     * be established.
     */
    public Connection getDBConnection() throws SQLException;
    
    /**
     * Closes database Connection.
     * 
     * @param connection database Connection
     * @throws java.sql.SQLException if some error occured while closing the 
     * database connection.
     */
    public void closeDBConnection(Connection connection) throws SQLException;
}
