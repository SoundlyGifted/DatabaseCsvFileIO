package web.process.parse;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.CSVReaderHeaderAwareBuilder;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.ejb.Stateless;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import web.process.csvData.CSVFileData;

/**
 * This Bean contains implementation of methods that are used to parse a 
 * csv-file.
 * 
 * @author SoundlyGifted
 */
@Stateless
public class AppCSVParser implements AppCSVParserLocal {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public CSVFileData parseWithCommonsCSV(Part filePart) throws IOException {
        /* Collection to keep records from csv-file.
         * Each record is a Map with a csv table values mapped to 
         * the csv table headers (Map<String, String>).
         */
        CSVFileData csvFileData = new CSVFileData();

        String charSet = "UTF-8";
        /* Using BOMInputStream class from Apache Commons IO library to deal 
         * with Byte Order Marks (BOM).
         * BOMInputStream is a wrapper class for InputStream. 
         */
        try (BOMInputStream inputStream = new BOMInputStream(filePart
                .getInputStream())) {
            if (inputStream != null) {
                try (InputStreamReader inputStreamReader
                        = new InputStreamReader(inputStream, charSet)) {
                    
                    // Parsing CSV file using Apache Commons CSV library.
                    
                    CSVFormat csvFormat = CSVFormat.EXCEL;
                    CSVFormat.Builder csvFormatBuilder = csvFormat.builder();
                    csvFormatBuilder.setDelimiter(';');
                    csvFormatBuilder.setHeader().setSkipHeaderRecord(true);
                    
                    try (org.apache.commons.csv.CSVParser csvParser 
                            = new org.apache.commons.csv
                                    .CSVParser(inputStreamReader, 
                                            csvFormatBuilder.build())) {
                        Map<String, String> rec;
                        for (CSVRecord record : csvParser) {
                            rec = record.toMap();
                            // Checking CSVFile headers on the first record only.
                            if (record.getRecordNumber() == 1) {
                                ArrayList<String> allowedHeaders
                                        = csvFileData.getAllowedCSVFileHeaders();
                                ArrayList<String> actualHeaders 
                                        = new ArrayList<>();
                                for (String actualHeader : rec.keySet()) {
                                    actualHeaders.add(actualHeader);
                                }
                                if (!actualHeaders.equals(allowedHeaders)) {
                                    return csvFileData;
                                }
                            }
                            if (!rec.isEmpty()) {
                                csvFileData.addRecord(rec);
                            }
                        }
                    }
                } catch (IOException ioex) {
                    throw new IOException("[AppCSVParser] Selected file can "
                            + "not be read or the charset '" + charSet + "' is "
                                    + "not supported. " + ioex.getMessage());                    
                }
            }
        } catch (IOException ioex) {
            throw new IOException("[AppCSVParser] Selected file can not be "
                    + "read. " + ioex.getMessage());            
        }
        return csvFileData;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public CSVFileData parseWithOpenCSV(Part filePart) 
            throws IOException, CsvValidationException {
        /* Collection to keep records from csv-file.
         * Each record is a Map with a csv table values mapped to 
         * the csv table headers (Map<String, String>).
         */
        CSVFileData csvFileData = new CSVFileData();

        String charSet = "UTF-8";
        try (InputStreamReader inputStreamReader
                = new InputStreamReader(filePart.getInputStream(), charSet)) {

            com.opencsv.ICSVParser parser
                    = new CSVParserBuilder().withSeparator(';')
                            .build();

            CSVReaderHeaderAwareBuilder readerBuilder
                    = new CSVReaderHeaderAwareBuilder(inputStreamReader);
            readerBuilder.withCSVParser(parser);

            try (CSVReaderHeaderAware reader = readerBuilder.build()) {
                try {
                    Map<String, String> rec;
                    while ((rec = reader.readMap()) != null) {
                        // Checking CSVFile headers on the first record only.
                        if (reader.getRecordsRead() == 1) {
                            ArrayList<String> allowedHeaders
                                    = csvFileData.getAllowedCSVFileHeaders();
                            ArrayList<String> actualHeaders = new ArrayList<>();
                            for (String actualHeader : rec.keySet()) {
                                actualHeaders.add(actualHeader);

                            }
                            if (!actualHeaders.equals(allowedHeaders)) {
                                return csvFileData;
                            }
                        }
                        if (!rec.isEmpty()) {
                            csvFileData.addRecord(rec);
                        }
                    }
                } catch (CsvValidationException csvvex) {
                    throw new CsvValidationException("[AppCSVParser] csv file "
                                    + "contains invalid values. "
                                    + csvvex.getMessage());
                }
            }
        } catch (IOException ioex) {
            throw new IOException("[AppCSVParser] Selected file can not be read "
                    + "or the charset '" + charSet + "' is not supported. " 
                    + ioex.getMessage());
        }
        return csvFileData;
    }
}
