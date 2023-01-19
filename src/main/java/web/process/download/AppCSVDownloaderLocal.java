package web.process.download;

import jakarta.ejb.Local;
import java.io.File;
import java.io.IOException;
import web.process.csvData.CSVFileData;

/**
 * This Interface contains method declarations that are used to write a csv data
 * into a csv-file.
 * 
 * @author SoundlyGifted
 */
@Local
public interface AppCSVDownloaderLocal {
    
    /**
     * This method writes csv data into a csv-file using Apache Commons libraries.
     * 
     * @param csvFileData csv data to be written to the file.
     * @param outputFile the output file.
     * @return true in case if the writing operation was successful, false 
     * otherwise.
     * @throws IOException 
     */
    public boolean downloadWithCommonsCSV(CSVFileData csvFileData, File outputFile) 
            throws IOException;
    
    /**
     * This method writes csv data into a csv-file using OpenCSV library.
     * 
     * @param csvFileData csv data to be written to the file.
     * @param outputFile the output file.
     * @return true in case if the writing operation was successful, false 
     * otherwise.
     * @throws IOException 
     */
    public boolean downloadWithOpenCSV(CSVFileData csvFileData, File outputFile) 
            throws IOException;
}
