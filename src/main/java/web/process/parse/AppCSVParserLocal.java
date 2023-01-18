package web.process.parse;

import jakarta.ejb.Local;
import jakarta.servlet.http.Part;
import java.io.IOException;
import web.exceptions.GeneralApplicationException;
import web.process.csvData.CSVFileData;

/**
 * This Interface contains method declarations that are used to parse a csv-file.
 * 
 * @author SoundlyGifted
 */
@Local
public interface AppCSVParserLocal {
    
    /**
     * This method parses csv-file using Apache Commons libraries.
     * 
     * @param filePart file part received within a multipart/form-data POST request.
     * @return CSVFileData object that contains parsed csv-file data.
     * @throws IOException 
     */
    public CSVFileData parseWithCommonsCSV(Part filePart) throws IOException;
    
    /**
     * This method parses csv-file using OpenCSV library.
     * 
     * @param filePart file part received within a multipart/form-data POST request.
     * @return CSVFileData object that contains parsed csv-file data.
     * @throws IOException
     * @throws GeneralApplicationException 
     */
    public CSVFileData parseWithOpenCSV(Part filePart)
            throws IOException, GeneralApplicationException;
}
