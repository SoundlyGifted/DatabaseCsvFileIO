
package web.UploadFileToDBTest.common;

import jakarta.ejb.Local;
import web.UploadFileToDBTest.csvData.CSVFileData;

/**
 * This Interface contains method declarations that are used to handle 
 * operations with database table.
 * 
 * @author SoundlyGifted
 */
@Local
public interface DatabaseHandlerLocal {
    
    /**
     * Inserts multiple records to the database table from the input Data Object.
     * 
     * @param csvFileData input CSVFileData object containing records to be 
     * inserted into the database table.
     * @return "true" if insert operation is successful, "false" otherwise.
     */
    public boolean insertMultRecs(CSVFileData csvFileData);
    
    
    /**
     * Selects all records from the database table into the Data Object.
     * 
     * @return Data Object containing data records from the database table.
     */
    public CSVFileData selectAll();
}
