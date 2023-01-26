package web.process.database;

import java.io.IOException;
import jakarta.ejb.Local;

/**
 * This interface contains method that reads SQL query from an sql-file.
 * 
 * @author SoundlyGifted
 */
@Local
public interface SQLQueryProviderLocal {
    
    /**
     * Reads SQL query from an sql-file.
     * 
     * @param path path to the sql-file
     * @return SQL query (String).
     * @throws java.io.IOException
     */
    public String getQuery(String path) throws IOException;
}
