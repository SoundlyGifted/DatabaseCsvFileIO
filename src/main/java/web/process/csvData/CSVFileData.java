package web.process.csvData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class represents CSV File Data and used to keep data received from an
 * external CSV File or data from the database to be written to the CSV File.
 * It also contains Enum that connects Header labels of the CSV File and the
 * database field labels and the field types.
 * 
 * @author SoundlyGifted
 */
public class CSVFileData {
    
    enum CSVFileAllowedHeaderEnum {
        
        HEADER1 {
            @Override
            String getHeaderCSVFileName() {
                return "TEXTDATA";
            }
            
            @Override
            String getHeaderDatabaseName() {
                return "TEXTDATA";
            }
            
            @Override
            String getDataType() {
                return "VARCHAR";
            }
        },        
        HEADER2 {
            @Override
            String getHeaderCSVFileName() {
                return "DOUBLEDATA";
            }
            
            @Override
            String getHeaderDatabaseName() {
                return "DOUBLEDATA";
            }
            
            @Override
            String getDataType() {
                return "DOUBLE";
            }
        };
        
        abstract String getHeaderCSVFileName();
        abstract String getHeaderDatabaseName();
        abstract String getDataType();
    }    
    
    private List<Map<String, String>> recordListWithCSVFileHeaders;
    private final CSVFileAllowedHeaderEnum[] headers 
            = CSVFileAllowedHeaderEnum.values();

    
    /**
     * This constructor creates a CSVFileData object with the list of CSV File 
     * records as an empty ArrayList<>();
     */
    public CSVFileData() {
        this.recordListWithCSVFileHeaders = new ArrayList<>();
    }


    /**
     * Used to get the List of CSV File records as a mapping of values to the 
     * CSV File Headers.
     * 
     * @return List of CSV File records where each record is as a mapping of 
     * values to the CSV File Headers.
     */
    public List<Map<String, String>> getRecordListWithCSVFileHeaders() {
        return recordListWithCSVFileHeaders;
    }

    
    /**
     * Used to get the allowed CSV File Headers (most for validation of the 
     * headers in the uploaded CSV File).
     * 
     * @return allowed CSV File Headers.
     */
    public ArrayList<String> getAllowedCSVFileHeaders() {
        List<String> headerList = new LinkedList<>();
        for (CSVFileAllowedHeaderEnum header : headers) {
            headerList.add(header.getHeaderCSVFileName());
        }
        return new ArrayList<>(headerList);
    }
    
    
    /**
     * Used to get database field labels mapped to the CSV File header names.
     * 
     * @return database field labels mapped to the CSV File header names.
     */
    public Map<String, String> getHeaderDatabaseNamesMappedToCSVFileHeaderNames() {
        Map<String, String> map = new HashMap<>();
        for (CSVFileAllowedHeaderEnum header : headers) {
            map.put(header.getHeaderCSVFileName(), 
                    header.getHeaderDatabaseName());
        }
        return map;
    }
    
    
    /**
     * Used to get database field types mapped to the CSV File header names.
     * 
     * @return database field types mapped to the CSV File header names.
     */
    public Map<String, String> getDataTypesMappedToCSVFileHeaderNames() {
        Map<String, String> map = new HashMap<>();
        for (CSVFileAllowedHeaderEnum header : headers) {
            map.put(header.getHeaderCSVFileName(), 
                    header.getDataType());
        }
        return map;
    }
    
    
    /**
     * Adds a record representing a CSV File data record to the record list 
     * within the CSVFileData object.
     * 
     * @param record a record representing a CSV File data record which is a 
     * mapping of values to the corresponding CSV File headers.
     */
    public void addRecord(Map<String, String> record) {
        recordListWithCSVFileHeaders.add(record);
    }
}
