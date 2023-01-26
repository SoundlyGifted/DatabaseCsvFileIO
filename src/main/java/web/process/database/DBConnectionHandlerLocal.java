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
     * @throws java.sql.SQLException
     */
    public Connection getDBConnection() throws SQLException;
    
    /**
     * Closes database Connection.
     * 
     * @param connection database Connection
     * @throws java.sql.SQLException
     */
    public void closeDBConnection(Connection connection) throws SQLException;
}
