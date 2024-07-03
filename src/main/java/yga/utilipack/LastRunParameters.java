package yga.utilipack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The `LastRun` class handles loading and saving of configuration data from a file.
 * It provides methods to load configuration data into memory and save updated
 * configuration data back to the file.
 */
public class LastRunParameters {

    private static final Logger logger = LogManager.getLogger(LastRun.class);

    /** The file path where configuration data is loaded from and saved to. */
    private static String file;

    /** Array to store loaded configuration data. */
    public static String[] words;

    /**
     * Sets the file path for loading and saving configuration data.
     *
     * @param file the file path to set
     */
    public static void setFile(String file) {
        LastRun.file = file;
    }

    /**
     * Loads configuration data from the file specified by `file` into memory.
     * Data is expected to be semicolon-separated.
     */
    public static void loadLastRun() {
        Path path = Paths.get(file);
        try {
            InputStream input = Files.newInputStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            // Read the first line from the file
            String line = reader.readLine();

            // Split the line into an array using semicolon as delimiter
            words = line.split(";");

            // Close resources
            input.close();
            reader.close();

            // Log successful loading of last run data
            logger.info("Last run data loaded successfully.");
        } catch (IOException e) {
            // Log error and handle exception
            logger.error("Failed to load last run data: " + e.getMessage(), e);

        }
    }

    /**
     * Saves run information to the file specified by `file`.
     *
     * @param keys the list of keys to fetch data from ConfigManagerWindow
     * @param separator the separator to use between data elements
     * @throws FileNotFoundException if the specified file path does not exist
     * @throws UnsupportedEncodingException if UTF-8 encoding is not supported
     */
    public static void saveRunInfo(List<String> keys, String separator) throws FileNotFoundException, UnsupportedEncodingException {
        // Define the file to write data to
        File outputFile = new File(file);

        try (PrintWriter writer = new PrintWriter(outputFile, "UTF-8")) {
            // Build the string to save based on keys and separator
            StringBuilder sb = new StringBuilder();
            for (String key : keys) {
                // Get data associated with each key from ConfigManagerWindow
                String data = ConfigManagerWindow.getDataFromWindow(key);

                // Append data to the string builder with separator
                sb.append(data).append(separator);
            }

            // Remove the last separator if string builder is not empty
            if (sb.length() > 0) {
                sb.setLength(sb.length() - separator.length());
            }

            // Write the string to the file
            writer.println(sb.toString());

            // Log successful saving of run info
            logger.info("Run info saved successfully.");
        } catch (IOException e) {
            // Log error and handle exception
            logger.error("Failed to save run info: " + e.getMessage(), e);
            throw e; // Rethrow the exception to indicate failure
        }
    }

    /**
     * Gets a specific word from the loaded configuration data.
     *
     * @param i the index of the word to retrieve
     * @return the word at the specified index
     */
    public static String getWords(int i) {
        return words[i];
    }

}
