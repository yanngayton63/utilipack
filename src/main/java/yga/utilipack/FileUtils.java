package yga.utilipack;

import java.io.File;
import java.io.FileWriter;
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
			return Files.list(Paths.get(folderPath)).filter(Files::isRegularFile).map(Path::getFileName)
					.map(Path::toString).collect(Collectors.toList());
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
	 * Generates the output file name based on current date/time, text, and image
	 * format.
	 *
	 * @param filePath        the path to the original image file
	 * @param currentDateTime the current date and time formatted as yyyyMMddHHmmss
	 * @param text            the free text
	 * @param formatName      the file format (e.g., "png", "csv")
	 * @return the generated output file name
	 */
	public static String generateOutputFileName(String filePath, String currentDateTime, String text,
			String formatName) {
		String directory = new File(filePath).getParent();
		return directory + File.separator + currentDateTime + "_" + text + formatName;
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

		/**
	 * Check if the directory located at the path given in output already exists. If
	 * it does : do nothing and just return the input. If it doesn't : create the
	 * directory.
	 * 
	 * @param outputDirectoryPath the directory.
	 * @return the outputDirectoryPath.
	 */
	public static String generateDirectory(String outputDirectoryPath) {
		File directory = new File(outputDirectoryPath);

		if (!directory.exists()) {
			directory.mkdir();
			logger.info("Creation of output directory : " + outputDirectoryPath);
		}

		return outputDirectoryPath;
	}

	/**
	 * Check if the directory located at the path given in output already exists. If
	 * it does : do nothing and just return the input. If it doesn't : create the
	 * directory. Use the default output directory instead of a given one.
	 * 
	 * @param inputFile used to get the path of the inputFile and generate a new
	 *                  directory from this path.
	 * @return a string containing the path of the output directory.
	 */
	public static String generateDefaultDirectory(File inputFile) {
		String outputDirectoryPath = DEFAULT_OUTPUT_DIRECTORY;
		File directory = new File(outputDirectoryPath);

		if (!directory.exists()) {
			directory.mkdir();
			logger.info("Creation of default output directory : " + outputDirectoryPath);
		}

		return outputDirectoryPath;
	}

	/**
	 * Used in the log.
	 * 
	 * @return the output directory.
	 */
	public static final String getOutputDirectory() {
		return OUTPUT_DIRECTORY;
	}

	/**
	 * Reset static field to default value.
	 */
	public static void cleanup() {
		OUTPUT_DIRECTORY = DEFAULT_OUTPUT_DIRECTORY;
	}

		/**
	 * Generate and write into a file with the given content.
	 * 
	 * @param inputFile       The given file from which we will generate another
	 *                        file.
	 * @param funcID			The identifier of the function, for naming purpose
	 * @param fileContent     The content of the result file.
	 * @param extension       The extension of the result file.
	 * @param prefix          The result file name will be a concatenation with the prefix
	 * @param suffix          The result file name will be a concatenation with the suffix
	 *                        one of the File given in parameter and this parameter.
	 * @param outputDirectory The output directory to generate the result file.
	 */
	public static void generateFile(File inputFile, String funcID, String fileContent, String extension, String prefix, String suffix,
			String outputDirectory) {
		String outputFileName = generateFileName(funcID, extension, prefix, suffix);

		// If there's a specified outputDirectory than use this one, otherwise use
		// default outputDirectory
		String finalOutputDirectory = outputDirectory != null ? generateDirectory(outputDirectory)
				: generateDefaultDirectory(inputFile);

		BufferedWriter writer;

		try {
			writer = new BufferedWriter(new FileWriter(new File(finalOutputDirectory, outputFileName)));
			writer.write(fileContent);
			writer.close();
		} catch (IOException e) {
			logger.error("Could not generate a new file called " + outputFileName + " at the location "
					+ finalOutputDirectory);
		}
		OUTPUT_DIRECTORY = finalOutputDirectory;
	}

	
	/**
	 * Generate and write into a file with the given content.
	 * 
	 * @param inputFile       The given file from which we will generate another
	 *                        file.
	 * @param fileContent     The content of the result file.
	 * @param extension       The extension of the result file.
	 * @param suffix          The result file name will be a concatenation of the
	 *                        one of the File given in parameter and this parameter.
	 * @param outputDirectory The output directory to generate the result file.
	 */
	public static void generateFile(File inputFile, String fileContent, String extension, String suffix,
			String outputDirectory) {
		String outputFileName = generateFileName(inputFile, extension, suffix);

		// If there's a specified outputDirectory than use this one, otherwise use
		// default outputDirectory
		String finalOutputDirectory = outputDirectory != null ? generateDirectory(outputDirectory)
				: generateDefaultDirectory(inputFile);

		BufferedWriter writer;

		try {
			writer = new BufferedWriter(new FileWriter(new File(finalOutputDirectory, outputFileName)));
			writer.write(fileContent);
			writer.close();
		} catch (IOException e) {
			logger.error("Could not generate a new file called " + outputFileName + " at the location "
					+ finalOutputDirectory);
		}
		OUTPUT_DIRECTORY = finalOutputDirectory;
	}

	/**
	 * Generate and write into a file with the given content.
	 * 
	 * @param inputFile       The given file from which we will generate another
	 *                        file.
	 * @param fileContent     The content of the result file.
	 * @param extension       The extension of the result file.
	 * @param prefix          The result file name will be a concatenation with the prefix
	 * @param suffix          The result file name will be a concatenation with the suffix
	 *                        one of the File given in parameter and this parameter.
	 * @param outputDirectory The output directory to generate the result file.
	 */
	public static void generateFile(File inputFile, String fileContent, String extension, String prefix, String suffix,
			String outputDirectory) {
		String outputFileName = generateFileName(inputFile, extension, prefix, suffix);

		// If there's a specified outputDirectory than use this one, otherwise use
		// default outputDirectory
		String finalOutputDirectory = outputDirectory != null ? generateDirectory(outputDirectory)
				: generateDefaultDirectory(inputFile);

		BufferedWriter writer;

		try {
			writer = new BufferedWriter(new FileWriter(new File(finalOutputDirectory, outputFileName)));
			writer.write(fileContent);
			writer.close();
		} catch (IOException e) {
			logger.error("Could not generate a new file called " + outputFileName + " at the location "
					+ finalOutputDirectory);
		}
		OUTPUT_DIRECTORY = finalOutputDirectory;
	}

}
