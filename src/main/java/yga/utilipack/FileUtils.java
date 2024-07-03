package yga.utilipack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility class for file operations.
 */
public class FileUtils {

    private static final Logger logger = LogManager.getLogger(FileUtils.class);

    /**
     * Appends text to the end of a specified file.
     *
     * @param filename the name of the file to append to
     * @param text     the text to append
     */
    public static void append(String filename, String text) {
        try {
            Path filePath = Paths.get(filename);
            Files.write(filePath, (text + System.lineSeparator()).getBytes(), java.nio.file.StandardOpenOption.APPEND);
        } catch (IOException e) {
            logger.error("An error occurred while appending to file: " + filename, e);
        }
    }

    /**
     * Finds all files in the specified folder path.
     *
     * @param folderPath the path to the folder to search for files
     * @return a List containing the names of all files found in the folder
     * @throws IOException if an I/O error occurs while accessing the folder
     */
    public static List<String> findFiles(String folderPath) throws IOException {
        try {
            return Files.list(Paths.get(folderPath))
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("An error occurred while listing files in folder: " + folderPath, e);
            throw e; // Propagate the exception for handling at a higher level
        }
    }
    
    /**
     * Retrieves the file extension from a file path.
     *
     * @param filePath the file path
     * @return the file extension (e.g., "png", "jpg")
     */
    public static String getFileExtension(String filePath) {
        int lastIndexOfDot = filePath.lastIndexOf(".");
        if (lastIndexOfDot == -1) {
            return ""; // empty extension
        }
        return filePath.substring(lastIndexOfDot + 1).toLowerCase();
    }
    
    /**
     * Generates the output file name based on current date/time, text, and image format.
     *
     * @param filePath the path to the original image file
     * @param currentDateTime the current date and time formatted as yyyyMMddHHmmss
     * @param text the free text 
     * @param formatName the file format (e.g., "png", "csv")
     * @return the generated output file name
     */
    public static String generateOutputFileName(String filePath, String currentDateTime, String text, String formatName) {
        String directory = new File(filePath).getParent();
        return directory + File.separator + currentDateTime + "_" + text  + formatName;
    }

      /**
     * Writes data to a file, overwriting any existing content.
     *
     * @param data     The data to write to the file.
     * @param filePath The path to the file to write to.
     */
    public static void writeToFileOverwrite(String data, String filePath) {
        FileWriter writer = null;
        try {
            // Create FileWriter with specified file path
            writer = new FileWriter(filePath);

            // Write data to file
            writer.write(data);

            // Log success message
            logger.info("Data successfully written to file (overwritten): " + filePath);
        } catch (IOException e) {
            // Log error if writing fails
            logger.error("Error writing data to file (overwritten): " + filePath, e);
            // Optionally handle the error (e.g., display a pop-up)
        } finally {
            // Ensure FileWriter is closed properly
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    // Log error if closing FileWriter fails
                    logger.error("Error closing FileWriter for file: " + filePath, e);
                }
            }
        }
    }
}
