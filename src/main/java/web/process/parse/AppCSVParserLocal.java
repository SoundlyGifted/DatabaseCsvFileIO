package web.process.parse;

import com.opencsv.exceptions.CsvValidationException;
import jakarta.ejb.Local;
import jakarta.servlet.http.Part;
import java.io.IOException;
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
     * @param filePart file part received within a multipart/form-data POST 
     * request.
     * @return CSVFileData object that contains parsed csv-file data.
     * @throws IOException if the selected file can not be read or used charset
     * is not supported.
     */
    public CSVFileData parseWithCommonsCSV(Part filePart) throws IOException;
    
    /**
     * This method parses csv-file using OpenCSV library.
     * 
     * @param filePart file part received within a multipart/form-data POST 
     * request.
     * @return CSVFileData object that contains parsed csv-file data.
     * @throws IOException if the selected file can not be read or used charset
     * is not supported.
     * @throws com.opencsv.exceptions.CsvValidationException if the file 
     * contains invalid values.
     */
    public CSVFileData parseWithOpenCSV(Part filePart)
            throws IOException, CsvValidationException;
}
