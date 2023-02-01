package web.process.database;

import jakarta.ejb.Local;
import java.io.IOException;
import java.sql.SQLException;
import web.process.csvdata.CSVFileData;

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
     * @throws java.io.IOException if the file containing SQL query can not be
     * found in the resource folder or can not be read.
     * @throws java.sql.SQLException if the connection to the database can not
     * be established or the SQL query can not be executed.
     * @throws java.lang.NumberFormatException if the csv data contains a value
     * that can not be converted to the proper database field type.
     */
    public void insertMultRecs(CSVFileData csvFileData) 
            throws IOException, SQLException, NumberFormatException;
    
    
    /**
     * Selects all records from the database table into the Data Object.
     * 
     * @return Data Object containing data records from the database table.
     * @throws java.io.IOException if the file containing SQL query can not be
     * found in the resource folder or can not be read.
     * @throws java.sql.SQLException if the connection to the database can not
     * be established or the SQL query can not be executed.
     */
    public CSVFileData selectAll() throws IOException, SQLException;
    
    /**
     * Deletes all records from the database table.
     *  
     * @throws java.io.IOException if the file containing SQL query can not be
     * found in the resource folder or can not be read.
     * @throws java.sql.SQLException if the connection to the database can not
     * be established or the SQL query can not be executed.
     */
    public void deleteAll() throws IOException, SQLException;
}
