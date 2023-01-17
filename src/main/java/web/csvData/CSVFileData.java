package web.csvData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
     * This constructor creates a CSVFileData object based on the record list
     * received as the input parameter.
     * 
     * @param recordList input record list.
     */
    public CSVFileData(List<Map<String, String>> recordList) {
        if (recordList == null || recordList.isEmpty()) {
            this.recordListWithCSVFileHeaders = new ArrayList<>();
        } else {
            Map<String, String> firstRecord = recordList.get(0);
            ArrayList<String> actualHeaders = new ArrayList<>();
            for (String actualHeader : firstRecord.keySet()) {
                actualHeaders.add(actualHeader);
            }
            if (headers.equals(actualHeaders)) {
                this.recordListWithCSVFileHeaders = recordList;
            } else {
                this.recordListWithCSVFileHeaders = new ArrayList<>();
            }
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.recordListWithCSVFileHeaders);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CSVFileData other = (CSVFileData) obj;
        return Objects.equals(this.recordListWithCSVFileHeaders, other
                .recordListWithCSVFileHeaders);
    }

    @Override
    public String toString() {
        return "csvFileData{" + "recordList=" 
                + recordListWithCSVFileHeaders + '}';
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
     * Used to get the allowed CSV File Headers (most frequently for the purpose 
     * of validation of the headers in the uploaded CSV File).
     * 
     * @return allowed CSV File Headers.
     */
    public ArrayList<String> getAllowedCSVFileHeaders() {
        ArrayList<String> headerList = new ArrayList<>();
        for (CSVFileAllowedHeaderEnum header : headers) {
            headerList.add(header.getHeaderCSVFileName());
        }
        return headerList;
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
