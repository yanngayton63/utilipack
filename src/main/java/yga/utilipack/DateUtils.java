import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility class for date-related operations.
 */
public class DateUtils {

    private static final Logger logger = LogManager.getLogger(DateUtils.class);

    /**
     * Converts a date string from the format "ddyyyyHHmmss" to the specified output format.
     *
     * @param dateStr      the date string in the format "ddyyyyHHmmss"
     * @param outputFormat the desired output date format
     * @return the formatted date string in the desired output format, or null if parsing fails
     */
    public static String convertDateFormat(String dateStr, String outputFormat) {
        // Input format: ddyyyyHHmmss
        SimpleDateFormat inputFormat = new SimpleDateFormat("ddyyyyHHmmss");

        // Output format specified by outputFormat
        SimpleDateFormat outputFormatter = new SimpleDateFormat(outputFormat);

        Date date;
        try {
            // Parsing the date in the input format
            date = inputFormat.parse(dateStr);
        } catch (ParseException e) {
            logger.error("Error parsing date: {}", dateStr, e);
            return null; // Return null if parsing fails
        }

        // Formatting the date to the desired output format
        return outputFormatter.format(date);
    }
}