package web.process.download;

import jakarta.ejb.Local;
import java.io.File;
import java.io.IOException;
import web.process.csvData.CSVFileData;

/**
 *
 * @author SoundlyGifted
 */
@Local
public interface AppCSVDownloaderLocal {
    
    public boolean downloadWithCommonsCSV(CSVFileData csvFileData, File outputFile) 
            throws IOException;
    
    public boolean downloadWithOpenCSV(CSVFileData csvFileData, File outputFile) 
            throws IOException;
}
