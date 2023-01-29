package web.process.download;

import jakarta.ejb.Local;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import web.process.csvData.CSVFileData;

/**
 * This Interface contains method declarations that are used to write a 
 * csv-data.
 * 
 * @author SoundlyGifted
 */
@Local
public interface AppCSVWriterLocal {
    
    /**
     * This method uses Apache Commons libraries to write csv-data into the 
     * text-output stream associated with the HTTP Servlet response (for further
     * sending character text from that stream to the client with the response).
     * 
     * @param csvFileData csv-data to be written to the output stream of the 
     * HTTP Servlet response.
     * @param response HTTP Servlet response to write the output stream.
     * @throws IOException if the I/O data writing error occured.
     */
    public void writeWithCommonsCSV(CSVFileData csvFileData, 
            HttpServletResponse response) throws IOException;
    
    /**
     * This method uses Open CSV library to write csv-data into the text-output 
     * stream associated with the HTTP Servlet response (for further sending 
     * character text from that stream to the client with the response).
     * 
     * @param csvFileData csv-data to be written to the output stream of the 
     * HTTP Servlet response.
     * @param response HTTP Servlet response to write the output stream.
     * @throws java.io.IOException if the I/O data writing error occured.
     */
    public void writeWithOpenCSV(CSVFileData csvFileData, 
            HttpServletResponse response) throws IOException;
}
