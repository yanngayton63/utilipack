package yga.utilipack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class to explore and list the contents of a directory. Provides functionality
 * to list all files and directories, optionally including subdirectories.
 * 
 * <p>
 * Original source:
 * http://www.fobec.com/java/964/lister-fichiers-dossiers-repertoire.html
 * </p>
 * 
 * <p>
 * Example usage:
 * 
 * <pre>
 * DiskFileExplorer explorer = new DiskFileExplorer("/path/to/directory", true);
 * List<String> files = explorer.listDirectory("/path/to/directory");
 * for (String file : files) {
 * 	System.out.println(file);
 * }
 * </pre>
 * </p>
 * 
 * @version 1.1
 * @since 2010
 */
public class DiskFileExplorer {

	// Logger for logging important events
	static Logger logger = LogManager.getLogger(DiskFileExplorer.class);

	// The initial path of the directory to be explored.
	private String initialPath;

	// Flag to determine if subdirectories should be included.
	private Boolean recursivePath;

	// Counter for the number of files found.
	public int fileCount = 0;

	// Counter for the number of directories found.
	public int dirCount = 0;

	/**
	 * Constructor to initialize DiskFileExplorer.
	 * 
	 * @param path      The path of the directory to be explored.
	 * @param subFolder Boolean flag to indicate if subdirectories should be
	 *                  included.
	 */
	public DiskFileExplorer(String path, Boolean subFolder) {
		this.initialPath = path;
		this.recursivePath = subFolder;
		logger.info("DiskFileExplorer initialized with path: " + path + " and recursivePath: " + subFolder);
	}

	/**
	 * Method to initiate the listing of the directory from the initial path.
	 */
	public void list() {
		this.listDirectory(this.initialPath);
	}

	/**
	 * Recursively lists the contents of a directory.
	 * 
	 * @param dir The directory to be listed.
	 * @return A list of strings representing the absolute paths of the files.
	 */
	public List<String> listDirectory(String dir) {
		File file = new File(dir);
		File[] files = file.listFiles();
		List<String> fileList = new ArrayList<>();

		// Check if the directory is not empty
		if (files != null) {
			for (File f : files) {
				if (f.isDirectory()) {
					dirCount++;
					logger.debug("Directory found: " + f.getAbsolutePath());
					if (this.recursivePath) {
						fileList.addAll(this.listDirectory(f.getAbsolutePath()));
					}
				} else {
					fileCount++;
					logger.debug("File found: " + f.getAbsolutePath());
					fileList.add(f.getAbsolutePath());
				}
			}
		} else {
			logger.warn("The directory " + dir + " is empty or does not exist.");
		}

		return fileList;
	}

	/**
	 * Get all the paths of the java files in a given directory.
	 * 
	 * @param inputDirectory The root directory containing the files.
	 * @return A list of all the files path in the directory given in parameter and
	 *         its subdirectory.
	 */
	public static List<String> getAllFilesPath(final File inputDirectory, String extension) {
		List<String> filesPath = new ArrayList<String>();
		for (final File fileEntry : inputDirectory.listFiles()) {
			if (fileEntry.isDirectory()) {
				filesPath.addAll(getAllFilesPath(fileEntry, extension));
			} else {
				String file = fileEntry.getAbsolutePath();
				if (FilenameUtils.getExtension(file).equals(extension)) {
					filesPath.add(file);
				}
			}
		}
		return filesPath;
	}
}
