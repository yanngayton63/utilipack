package yga.utilipack;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility class for file operations.
 */
public class FileUtils {
	
	static Logger logger = LogManager.getLogger(FileUtils.class);
	
    /**
     * Appends text to the end of a specified file.
     *
     * @param filename the name of the file to append to
     * @param text     the text to append
     */
    public static void append(String filename, String text) {
        BufferedWriter bufWriter = null;
        FileWriter fileWriter = null;
        try {
            // Open the file for appending
            fileWriter = new FileWriter(filename, true);
            bufWriter = new BufferedWriter(fileWriter);
            // Write a new line
            bufWriter.newLine();
            // Write the text to the file
            bufWriter.write(text);
        } catch (IOException e) {
        	logger.error("An error occurred in FileUtils", e);
        } finally {
            try {
                // Close the writers
                if (bufWriter != null) {
                    bufWriter.close();
                }
                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException e) {
            	logger.error("An error occurred in FileUtils", e);
            }
        }
    }
}
