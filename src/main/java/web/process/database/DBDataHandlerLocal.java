package web.process.database;

import jakarta.ejb.Local;
import java.io.IOException;
import java.sql.SQLException;
import web.process.csvData.CSVFileData;
import web.exceptions.GeneralApplicationException;

/**
 * This Interface contains method declarations that are used to handle 
 * operations with database table.
 * 
 * @author SoundlyGifted
 */
@Local
public interface DBDataHandlerLocal {
    
    /**
     * Inserts multiple records to the database table from the input Data Object.
     * 
     * @param csvFileData input CSVFileData object containing records to be 
     * inserted into the database table.
     * @return "true" if insert operation is successful, "false" otherwise.
     * @throws web.exceptions.GeneralApplicationException
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     */
    public boolean insertMultRecs(CSVFileData csvFileData) 
            throws GeneralApplicationException, IOException, SQLException;
    
    
    /**
     * Selects all records from the database table into the Data Object.
     * 
     * @return Data Object containing data records from the database table.
     * @throws web.exceptions.GeneralApplicationException
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     */
    public CSVFileData selectAll() 
            throws GeneralApplicationException, IOException, SQLException;
}
