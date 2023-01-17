package web.common;

import jakarta.ejb.Stateless;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import web.csvData.CSVFileData;
import web.exceptions.GeneralApplicationException;

/**
 * This Bean contains implementation of methods that are used to handle 
 * operations with database table (also contains method to establish database 
 * connection).
 * 
 * @author SoundlyGifted
 */
@Stateless
public class DatabaseHandler implements DatabaseHandlerLocal {

    private final String dbURL 
            = "jdbc:derby://localhost:1527/UploadFileToDBTestDB";
    private final String dbUser = "app";
    private final String dpPass = "app";
    
    private Connection getDatabaseConnection() throws SQLException {
        DriverManager.registerDriver(new org.apache.derby.iapi.jdbc.AutoloadedDriver());
        return DriverManager.getConnection(dbURL, dbUser, dpPass);        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean insertMultRecs(CSVFileData csvFileData) 
            throws GeneralApplicationException {
        String sql = "INSERT INTO MYDATA (TEXTDATA, DOUBLEDATA) values (?, ?)";
        Connection con;
        PreparedStatement statement = null;
        try {
            con = getDatabaseConnection();
        } catch (SQLException ex) {
            System.out.println("*** [DatabaseHandler] Error establishing "
                    + "database connection: " + ex.getMessage());
            GeneralApplicationException exception 
                    = new GeneralApplicationException("Error establishing "
                            + "database connection: " + ex.getMessage(), ex);
            throw exception;
        }
        try {
            statement = con.prepareStatement(sql);
            List<Map<String, String>> csvFileRecordList = csvFileData
                    .getRecordListWithCSVFileHeaders();
            for (Map<String, String> csvFileRecord : csvFileRecordList) {
                // values that come from the csv file
                String csvFileHeaderName;
                String inputStringValue;
                
                // variables that define a database record
                Map<String, String> databaseHeaderNames 
                        = csvFileData.getHeaderDatabaseNamesMappedToCSVFileHeaderNames();
                Map<String, String> databaseFieldTypes 
                        = csvFileData.getDataTypesMappedToCSVFileHeaderNames();
                String databaseHeaderName;
                String databaseFieldType;
                String databaseVarcharValue = "";
                Double databaseDoubleValue = (double) 0;       
                
                for (Map.Entry<String, String> entry : csvFileRecord.entrySet()) {
                    csvFileHeaderName = entry.getKey();
                    inputStringValue = entry.getValue();

                    databaseHeaderName = databaseHeaderNames
                            .get(csvFileHeaderName);
                    databaseFieldType = databaseFieldTypes
                            .get(csvFileHeaderName);

                    if ("DOUBLE".equals(databaseFieldType)) {
                        if (inputStringValue == null || inputStringValue.trim()
                                .isEmpty()) {
                            databaseDoubleValue = (double) 0;
                        } else {
                            databaseDoubleValue = stringToDouble(inputStringValue);
                            if (databaseDoubleValue == null) {
                                databaseDoubleValue = (double) 0;
                            }
                        }
                    } else if ("VARCHAR".equals(databaseFieldType)) {
                        databaseVarcharValue = inputStringValue;
                    }
                    
                    if ("TEXTDATA".equals(databaseHeaderName)) {
                        statement.setString(1, databaseVarcharValue);
                    } else if ("DOUBLEDATA".equals(databaseHeaderName)) {
                        statement.setDouble(2, databaseDoubleValue);
                    }
                }
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException ex) {
            System.out.println("*** [DatabaseHandler] Error executing prepared "
                    + "statement: " + ex.getMessage());
            GeneralApplicationException exception 
                    = new GeneralApplicationException("Error executing prepared "
                            + "statement: " + ex.getMessage(), ex);
            throw exception;
        } finally {
            csvFileData = null;
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    System.out.println("*** [DatabaseHandler] Error closing "
                            + "prepared statement: " + ex.getMessage());
                    GeneralApplicationException exception
                            = new GeneralApplicationException("Error closing "
                                    + "prepared statement: " 
                                    + ex.getMessage(), ex);
                    throw exception;                   
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    System.out.println("*** [DatabaseHandler] Error closing "
                            + "database connection: " + ex.getMessage());
                    GeneralApplicationException exception
                            = new GeneralApplicationException("Error closing "
                                    + "database connection: " 
                                    + ex.getMessage(), ex);
                    throw exception;
                }
            }
        }
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public CSVFileData selectAll() throws GeneralApplicationException {
        CSVFileData csvFileData = new CSVFileData();

        String sql = "SELECT * FROM MYDATA";
        Connection con;
        Statement statement = null;

        try {
            con = getDatabaseConnection();
        } catch (SQLException ex) {
            System.out.println("*** [DatabaseHandler] Error establishing "
                    + "database connection: " + ex.getMessage());
            GeneralApplicationException exception
                    = new GeneralApplicationException("Error establishing "
                            + "database connection: "
                            + ex.getMessage(), ex);
            throw exception;
        }

        try {
            statement = con.createStatement();
            statement.execute(sql);
            try (ResultSet resultSet = statement.getResultSet()) {
                // variables that define a database record
                Map<String, String> databaseHeaderNames 
                        = csvFileData.getHeaderDatabaseNamesMappedToCSVFileHeaderNames();
                String databaseHeaderName;
                Map<String, String> record;
                    
                // variables to receive values from the database
                String stringResultValue = "";
                Double doubleResultValue;                  
                while (resultSet.next()) {
                    record = new HashMap<>();
                    for (String csvFileDataHeader : databaseHeaderNames.keySet()) {
                        databaseHeaderName = databaseHeaderNames
                                .get(csvFileDataHeader);
                        if (databaseHeaderName.equals("TEXTDATA")) {
                            stringResultValue = resultSet
                                    .getString(databaseHeaderName);
                        } else if (databaseHeaderName.equals("DOUBLEDATA")) {
                            doubleResultValue = resultSet
                                    .getDouble(databaseHeaderName);
                            stringResultValue = doubleResultValue.toString();
                        }
                        record.put(csvFileDataHeader, stringResultValue);
                    }
                    csvFileData.addRecord(record);
                }
            }
        } catch (SQLException ex) {
            System.out.println("*** [DatabaseHandler] Error executing prepared "
                    + "statement: " + ex.getMessage());
            GeneralApplicationException exception
                    = new GeneralApplicationException(" Error executing "
                            + "prepared statement: " + ex.getMessage(), ex);
            throw exception;
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    System.out.println("*** [DatabaseHandler] Error closing "
                            + "prepared statement: " + ex.getMessage());
                    GeneralApplicationException exception
                            = new GeneralApplicationException(" Error closing "
                                    + "prepared statement: " 
                                    + ex.getMessage(), ex);
                    throw exception;                
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    System.out.println("*** [DatabaseHandler] Error closing "
                            + "database connection: " + ex.getMessage());
                    GeneralApplicationException exception
                            = new GeneralApplicationException(" Error closing "
                                    + "database connection: " 
                                    + ex.getMessage(), ex);
                    throw exception;                      
                }
            }
        }
        return csvFileData;
    }
    
    
    private Double stringToDouble(String stringVal) 
            throws GeneralApplicationException {
        if (stringVal == null || stringVal.trim().isEmpty()) {
            return (double) 0;
        }
        stringVal = stringVal.replaceAll(",", ".");
        try {
            Double doubleValue = Double.valueOf(stringVal);
            return doubleValue;
        } catch (NumberFormatException ex) {
            System.out.println("Value '" + stringVal + "' cannot be "
                    + "converted to Double");
            GeneralApplicationException exception
                    = new GeneralApplicationException("Value '" 
                            + stringVal + "' cannot be converted to Double. "
                            + ex.getMessage(), ex);
            throw exception;
        }
    }
}
