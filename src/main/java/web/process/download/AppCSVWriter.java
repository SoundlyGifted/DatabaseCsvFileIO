package web.process.download;

import com.opencsv.CSVWriter;
import jakarta.ejb.Stateless;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import web.process.csvdata.CSVFileData;

/**
 * This Bean contains implementation of methods that are used to write a 
 * csv-data.
 * 
 * @author SoundlyGifted
 */
@Stateless
public class AppCSVWriter implements AppCSVWriterLocal {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void writeWithCommonsCSV(CSVFileData csvFileData, 
            HttpServletResponse response) throws IOException {
        ArrayList<String> csvFileHeaders = csvFileData
                .getAllowedCSVFileHeaders();
        List<Map<String, String>> recordList = csvFileData
                .getRecordListWithCSVFileHeaders();

        CSVFormat csvFormat = CSVFormat.EXCEL;
        CSVFormat.Builder csvFormatBuilder = csvFormat.builder();
        csvFormatBuilder.setDelimiter(';');
        csvFormatBuilder.setHeader(csvFileHeaders.get(0),
                csvFileHeaders.get(1));
        
        try (PrintWriter printWriter = response.getWriter();
                CSVPrinter printer 
                        = new CSVPrinter(printWriter, csvFormatBuilder.build())) {
            for (Map<String, String> record : recordList) {
                printer.printRecord(record.get(csvFileHeaders.get(0)),
                        record.get(csvFileHeaders.get(1)));
            }
            printer.flush();
        } catch (IOException ioex) {
            throw new IOException("[AppCSVWriter] Data writing error: "
                    + ioex.getMessage());
        }
    }
    
    /**
     * {@inheritDoc}
     */    
    @Override
    public void writeWithOpenCSV(CSVFileData csvFileData, 
            HttpServletResponse response) throws IOException {
        try (PrintWriter printWriter = response.getWriter();
                CSVWriter writer = new CSVWriter(printWriter, ';',
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END)) {

            ArrayList<String> csvFileHeaders = csvFileData
                    .getAllowedCSVFileHeaders();
            List<Map<String, String>> recordList = csvFileData
                    .getRecordListWithCSVFileHeaders();         
            
            // writing headers first.
            String[] headers = {csvFileHeaders.get(0),
                csvFileHeaders.get(1)};
            writer.writeNext(headers);

            // then writing values of each record one by one.
            for (Map<String, String> record : recordList) {
                String[] values = {record.get(csvFileHeaders.get(0)),
                    record.get(csvFileHeaders.get(1))};
                writer.writeNext(values);
            }
            writer.flush();

        } catch (IOException ioex) {
            throw new IOException("[AppCSVWriter] Data writing error: "
                    + ioex.getMessage());
        }
    }
}
