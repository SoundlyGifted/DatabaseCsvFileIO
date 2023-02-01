package web.process.database;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import web.process.csvdata.CSVFileData;

/**
 * This Bean contains implementation of methods that are used to handle 
 * operations with database table (also contains method to establish database 
 * connection).
 * 
 * @author SoundlyGifted
 */
@Stateless
public class DBDataHandler implements DBDataHandlerLocal {

    @EJB
    private DBConnectionHandlerLocal connectionHandler;
    
    @EJB
    private SQLQueryProviderLocal sqlQueryProvider;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void insertMultRecs(CSVFileData csvFileData) 
            throws IOException, SQLException, NumberFormatException {
        String sqlFileName = "insert.mydata";
        String sql;
        try {
            sql = sqlQueryProvider.getQuery(sqlFileName);
        } catch (IOException ioex) {
            throw new IOException("[DBDataHandler] Could not read SQL query "
                    + "from '" + sqlFileName + ".sql' file. " + ioex.getMessage());
        }

        try (Connection connection = connectionHandler.getDBConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
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
        } catch (SQLException sqlex) {
            throw new SQLException("[DBDataHandler] Error connecting to the "
                    + "database or executing SQL query: " 
                    + sqlex.getMessage());
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public CSVFileData selectAll() throws IOException, SQLException {
        CSVFileData csvFileData = new CSVFileData();

        String sqlFileName = "select.all.mydata";
        String sql;
        try {
            sql = sqlQueryProvider.getQuery(sqlFileName);
        } catch (IOException ioex) {
            throw new IOException("[DBDataHandler] Could not read SQL query "
                    + "from '" + sqlFileName + ".sql' file. " + ioex.getMessage());
        }

        try (Connection connection = connectionHandler.getDBConnection();
                Statement statement = connection.createStatement()) {
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
        } catch (SQLException sqlex) {
            throw new SQLException("[DBDataHandler] Error connecting to the "
                    + "database or executing SQL query: " 
                    + sqlex.getMessage());
        }
        return csvFileData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll() throws IOException, SQLException {
        String sqlFileName = "delete.all.mydata";
        String sql;
        try {
            sql = sqlQueryProvider.getQuery(sqlFileName);
        } catch (IOException ioex) {
            throw new IOException("[DBDataHandler] Could not read SQL query "
                    + "from '" + sqlFileName + ".sql' file. " + ioex.getMessage());
        }
        
        try (Connection connection = connectionHandler.getDBConnection();
                Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException sqlex) {
            throw new SQLException("[DBDataHandler] Error connecting to the "
                    + "database or executing SQL query: " 
                    + sqlex.getMessage());
        }
    }
    

    private Double stringToDouble(String stringVal) 
            throws NumberFormatException {
        if (stringVal == null || stringVal.trim().isEmpty()) {
            return (double) 0;
        }
        stringVal = stringVal.replaceAll(",", ".");
        try {
            Double doubleValue = Double.valueOf(stringVal);
            return doubleValue;
        } catch (NumberFormatException nfex) {
            throw new NumberFormatException("[DBDataHandler] Value '" 
                            + stringVal + "' cannot be converted to Double. "
                            + nfex.getMessage());
        }
    }
}
