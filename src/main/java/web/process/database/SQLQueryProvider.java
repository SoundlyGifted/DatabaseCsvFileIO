package web.process.database;

import jakarta.ejb.DependsOn;
import jakarta.ejb.Singleton;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * This EJB is used to provide SQL queries red from sql-files.
 * 
 * @author SoundlyGifted
 */
@Singleton
@DependsOn("DBConnectionHandler")
public class SQLQueryProvider implements SQLQueryProviderLocal {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getQuery(String path) throws IOException {
        path = "/resources/sql/" + path + ".sql";
        StringBuilder builder;
        ClassLoader classLoader = this.getClass().getClassLoader();
        try (InputStream stream = classLoader.getResourceAsStream(path)) {
            try(Reader reader = new InputStreamReader(stream)) {
                try(BufferedReader in = new BufferedReader(reader)) {
                    builder = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        builder.append(line).append(System.lineSeparator());
                    }
                    return builder.toString();
                }
            }
        } catch (IOException ioex) {
            throw new IOException("[SQLQueryProvider] could not read the '"
                    + path + "' query: " + ioex.getMessage());
        }
    }
}
