package yga.utilipack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The HttpLoader class provides functionality to load and read text data from a URL, 
 * such as an HTML file. It supports optional Basic Authentication.
 * http://www.fobec.com/java/908/ouvrir-une-url-charger-son-contenu-format-texte.html
 * 
 * @version 1.0
 */
public class HttpLoader {

    /** Logger instance for logging events */
	private static final Logger logger = LogManager.getLogger(FileUtils.class);

    /**
     * Opens a URL and reads the text data, for example, an HTML file.
     * Supports optional Basic Authentication.
     *
     * @param _url The URL to open
     * @param user The username for Basic Authentication
     * @param password The password for Basic Authentication
     * @param isBasicAuth Boolean indicating if Basic Authentication is required
     * @return String containing the content of the file
     */
    public static String getTextFile(String _url, String user, String password, Boolean isBasicAuth) {
        // Use try-with-resources to ensure resources are closed automatically
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                    new URL(_url.replaceAll(" ", "%20")).openConnection().getInputStream(), StandardCharsets.UTF_8))) {

            URL url = new URL(_url.replaceAll(" ", "%20"));
            URLConnection urlConnection = url.openConnection();

            // If Basic Authentication is required, set the Authorization header
            if (isBasicAuth) {
                String basicAuth = "Basic " + Base64.getEncoder().encodeToString((user + ":" + password).getBytes(StandardCharsets.UTF_8));
                urlConnection.setRequestProperty("Authorization", basicAuth);
            }

            // Initialize BufferedReader to read the URL's content
            StringBuilder sb = new StringBuilder();
            String line;

            // Read the content line by line
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();

        } catch (IOException ex) {
            // Log the exception
            logger.error("Error reading from URL: " + _url, ex);
            return "";
        }
    }
}