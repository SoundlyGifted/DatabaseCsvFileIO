package web.process.database;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * This EJB is used to handle database Connection.
 *
 * @author SoundlyGifted
 */
@Startup
@Singleton
public class DBConnectionHandler implements DBConnectionHandlerLocal {

    private static final String CONFIGS = "/resources/config.properties";
    private final Properties configs = new Properties();
    
    private String dbURL;
    private String dbUser;
    private String dbPass;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getDBConnection() throws SQLException {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(dbURL, dbUser, dbPass);
        } catch (SQLException sqlex) {
            throw new SQLException("[DBConnectionHandler] could not connect to "
                    + "the database using URL '" + dbURL + "', user '" + dbUser 
                    + "', password '" + dbPass + "; " + sqlex.getMessage());
        }
        return connection;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void closeDBConnection(Connection connection) throws SQLException {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException sqlex) {
            throw new SQLException("[DBConnectionHandler] DBConnection closing "
                    + "error :" + sqlex.getMessage());
        }
    }    
    
    
    private void setDefaultConfigs(String databaseName, 
            String user, String pass) {
        configs.setProperty("database.driver", "jdbc:derby");
        configs.setProperty("database.host", "localhost");
        configs.setProperty("database.port", "1527");
        configs.setProperty("database.name", databaseName);
        configs.setProperty("database.name", user);
        configs.setProperty("database.password", pass);
    }
    
    
    private void setDBConnectionParameters() {
        dbURL = configs.getProperty("database.driver") + "://"
                + configs.getProperty("database.host") + ":"
                + configs.getProperty("database.port") + "/"
                + configs.getProperty("database.name");
        dbUser = configs.getProperty("database.user");
        dbPass = configs.getProperty("database.password");
    }
    
    
    @PostConstruct
    public void postConstruct() {
        /* Resource package "resources" (containing config.properties and 
         * package with SQL files) was placed in "/src/main/resources", so that 
         * Maven will properly pack them into right folder in "war" archive:
         * "[project-name].war -> WEB-INF/classes"
         * This is the classpath of "war" archive, where the ClassLoader loads 
         * resources from.
         */
        ClassLoader classLoader = this.getClass().getClassLoader();
        try (InputStream stream = classLoader.getResourceAsStream(CONFIGS)) {
            configs.load(stream);
            setDBConnectionParameters();
        } catch (IOException ioex) {
            System.out.println("[DBConnectionHandler]: Database connection "
                    + "Properties file loading failure for the configuration "
                    + "file '" + CONFIGS + "': " + ioex.getMessage());
            System.out.println("[DBConnectionHandler]: Default Apache Derby "
                    + "database configuration will be used instead.");
            
            String databaseName = "DatabaseCsvFileIOAppDB";
            String user = "app";
            String pass = "app";
            setDefaultConfigs(databaseName, user, pass);
            setDBConnectionParameters();
        }
    }
}
