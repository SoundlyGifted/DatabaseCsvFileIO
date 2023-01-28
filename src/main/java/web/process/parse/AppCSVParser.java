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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import web.process.csvData.CSVFileData;
import web.process.parse.exceptions.FileValidationException;

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
    public CSVFileData parseWithCommonsCSV(Part filePart) 
            throws FileValidationException, IOException {
        validateFile(filePart);
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
                        /* Checking that:
                         * 1) csv-file is not empty.
                         * 2) actual csv-file headers are the allowed headers.
                         */
                        List<String> allowedHeaders 
                                = csvFileData.getAllowedCSVFileHeaders();
                        List<String> actualHeaders 
                                = new ArrayList<>(csvParser.getHeaderNames());
                        if (actualHeaders.isEmpty() 
                                || (actualHeaders.stream().allMatch(x -> x.isEmpty()))) {
                            throw new FileValidationException("[AppCSVParser] "
                                    + "Provided csv-file is empty.");
                        }
                        if (!actualHeaders.equals(allowedHeaders)) {
                            throw new FileValidationException("[AppCSVParser] "
                                    + "Selected file has invalid "
                                    + "headers.");
                        }
                        // Writing CSV data into the CSVFileData object.
                        for (CSVRecord record : csvParser) {
                            rec = record.toMap();
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
            throws FileValidationException, IOException, CsvValidationException {
        validateFile(filePart);
        /* Collection to keep records from csv-file.
         * Each record is a Map with a csv table values mapped to 
         * the csv table headers (Map<String, String>).
         */
        CSVFileData csvFileData = new CSVFileData();

        String charSet = "UTF-8";
        try (InputStreamReader inputStreamReader
                = new InputStreamReader(filePart.getInputStream(), charSet)) {

            com.opencsv.ICSVParser parser
                    = new CSVParserBuilder().withSeparator(';').build();

            CSVReaderHeaderAwareBuilder readerBuilder
                    = new CSVReaderHeaderAwareBuilder(inputStreamReader);
            readerBuilder.withCSVParser(parser);

            try (CSVReaderHeaderAware reader = readerBuilder.build()) {
                Map<String, String> rec;
                while ((rec = reader.readMap()) != null) {
                    /* Checking CSVFile headers on the second record only
                     * (first record is the header record which is skipped).
                     */
                    if (reader.getRecordsRead() == 2) {
                        /* Checking that actual csv-file headers are the 
                         * allowed headers.
                         */
                        ArrayList<String> allowedHeaders
                                = csvFileData.getAllowedCSVFileHeaders();
                        List<String> actualHeaders = new LinkedList<>();
                        for (String actualHeader : rec.keySet()) {
                            actualHeaders.add(actualHeader);
                        }
                        actualHeaders = new ArrayList<>(actualHeaders);
                        if (!actualHeaders.equals(allowedHeaders)) {
                            throw new FileValidationException("[AppCSVParser] "
                                    + "Selected file has invalid "
                                    + "headers.");
                        }
                    }
                    if (!rec.isEmpty()) {
                        csvFileData.addRecord(rec);
                    }
                }
                if (csvFileData.getRecordListWithCSVFileHeaders().isEmpty()) {
                    throw new FileValidationException("[AppCSVParser] Provided "
                            + "csv-file is empty or has no data except headers.");
                }
            } catch (CsvValidationException csvvex) {
                throw new CsvValidationException("[AppCSVParser] csv file "
                        + "contains invalid values. "
                        + csvvex.getMessage());
            }
        } catch (IOException ioex) {
            throw new IOException("[AppCSVParser] Selected file can not be read "
                    + "or the charset '" + charSet + "' is not supported. " 
                    + ioex.getMessage());
        }
        return csvFileData;
    }
    
    
    private void validateFile(Part filePart) throws FileValidationException {
        String filePartContentType = null;
        if (filePart != null) {
            filePartContentType = filePart.getContentType();
        }
        if (filePart == null || filePartContentType == null 
                || !filePart.getContentType().equals("text/csv")) {
            throw new FileValidationException("[AppCSVParser] No proper csv-file "
                    + "selected");
        }
    }
}
