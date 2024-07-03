package yga.utilipack;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

/**
 * The Class LoadFreeMarkerParameters.
 * This class is responsible for loading and configuring FreeMarker settings 
 * from a properties file.
 */
public class LoadFreeMarkerParameters {

    /** The FreeMarker configuration object. */
    protected static Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
    
    /** The resource bundle for loading properties. */
    protected static ResourceBundle rb = ResourceBundle.getBundle("param");

    /** Logger for this class. */
    private static final Logger logger = LogManager.getLogger(LoadFreeMarkerParameters.class);

    /**
     * Initializes the FreeMarker configuration.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected static void init() throws IOException {
        logger.info("Initializing FreeMarker configuration.");

        try {
            // Set FreeMarker version compatibility
            cfg.setIncompatibleImprovements(new Version(2, 3, 31));
            logger.debug("Set incompatible improvements to version 2.3.31.");

            // Set default encoding to UTF-8
            cfg.setDefaultEncoding("UTF-8");
            logger.debug("Set default encoding to UTF-8.");

            // Set the locale to US
            cfg.setLocale(Locale.US);
            logger.debug("Set locale to US.");

            // Set the template exception handler to rethrow exceptions
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            logger.debug("Set template exception handler to RETHROW_HANDLER.");

            // Set the directory for template loading from the properties file
            String templateFolder = rb.getString("free.marker.templates.folder");
            cfg.setDirectoryForTemplateLoading(new File(templateFolder));
            logger.debug("Set directory for template loading to: {}", templateFolder);

            // Set the SQL date and time time zone to the default time zone
            cfg.setSQLDateAndTimeTimeZone(TimeZone.getDefault());
            logger.debug("Set SQL date and time time zone to default time zone.");

            logger.info("FreeMarker configuration initialized successfully.");

        } catch (IOException e) {
            logger.error("Error initializing FreeMarker configuration.", e);
            throw e;
        }
    }
}
