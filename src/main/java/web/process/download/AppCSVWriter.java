package web.process.download;

import com.opencsv.CSVWriter;
import jakarta.ejb.Stateless;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import web.process.csvData.CSVFileData;

/**
 * This Bean contains implementation of methods that are used to write a csv 
 * data into a csv-file.
 * 
 * @author SoundlyGifted
 */
@Stateless
public class AppCSVWriter implements AppCSVWriterLocal {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void writeWithCommonsCSV(CSVFileData csvFileData, File outputFile) 
            throws IOException {
        try {
            FileWriter fileWriter = new FileWriter(outputFile);

            ArrayList<String> csvFileHeaders = csvFileData
                    .getAllowedCSVFileHeaders();
            List<Map<String, String>> recordList = csvFileData
                    .getRecordListWithCSVFileHeaders();

            CSVFormat csvFormat = CSVFormat.EXCEL;
            CSVFormat.Builder csvFormatBuilder = csvFormat.builder();
            csvFormatBuilder.setDelimiter(';');
            csvFormatBuilder.setHeader(csvFileHeaders.get(0),
                    csvFileHeaders.get(1));

            try (CSVPrinter printer 
                    = new CSVPrinter(fileWriter, csvFormatBuilder.build())) {
                for (Map<String, String> record : recordList) {
                    printer.printRecord(record.get(csvFileHeaders.get(0)),
                            record.get(csvFileHeaders.get(1)));
                }
                printer.flush();
            }
        } catch (IOException ioex) {
            throw new IOException("[AppCSVWriter] File writing error for the "
                    + "file '" + outputFile.getAbsolutePath() + "'. " 
                    + ioex.getMessage());
        }
    }
    
    /**
     * {@inheritDoc}
     */    
    @Override
    public void writeWithOpenCSV(CSVFileData csvFileData, File outputFile) 
            throws IOException {
        try {
            FileWriter fileWriter = new FileWriter(outputFile);

            ArrayList<String> csvFileHeaders = csvFileData
                    .getAllowedCSVFileHeaders();
            List<Map<String, String>> recordList = csvFileData
                    .getRecordListWithCSVFileHeaders();

            try (CSVWriter writer = new CSVWriter(fileWriter, ';',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END)) {
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
            }
        } catch (IOException ioex) {
            throw new IOException("[AppCSVWriter] File writing error for the "
                    + "file '" + outputFile.getAbsolutePath() + "'. " 
                    + ioex.getMessage());            
        }
    }
}
