package yga.utilipack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility class for handling CSV file operations such as reading, writing, and
 * adding headers.
 */
public class CsvUtils {

    static Logger logger = LogManager.getLogger(CsvUtils.class);
	
    private static char separator = getRegionalSeparator();

    /**
     * Retrieves the regional CSV separator used in the system.
     *
     * @return the regional CSV separator (',' or ';')
     */
    public static char getSeparator() {
        return separator;
    }

    /**
     * Reads a CSV file and splits its content into provided lists.
     *
     * @param csvFile the path to the CSV file
     * @param lists   the lists to store the values from each column
     */
    @SafeVarargs
    public static void readInputFile(String csvFile, List<String>... lists) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(String.valueOf(separator));
                for (int i = 0; i < Math.min(values.length, lists.length); i++) {
                    lists[i].add(values[i].trim());
                }
            }
        } catch (IOException e) {
            logger.error("An error occurred while reading the CSV file: {}", csvFile, e);
        }
    }

    /**
     * Adds headers to the provided lists and shifts the current values down.
     *
     * @param headers the headers to add
     * @param lists   the lists to add the headers to
     */
    @SafeVarargs
    public static void addHeadersAndShift(List<String> headers, List<String>... lists) {
        for (List<String> list : lists) {
            list.add(0, "");
        }
        for (int i = 0; i < headers.size(); i++) {
            lists[i].set(0, headers.get(i));
        }
    }

    /**
     * Writes the provided lists to a CSV file with the specified name. The file
     * name is suffixed with the current timestamp.
     *
     * @param outputFileName the base name of the output file
     * @param lists          the lists containing the data to write
     */
    @SafeVarargs
    public static void writeOutput(String outputFileName, List<String>... lists) {
        File file = new File(outputFileName + ".csv");

        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            int size = lists[0].size();
            for (int i = 0; i < size; i++) {
                StringBuilder row = new StringBuilder();
                for (int j = 0; j < lists.length; j++) {
                    if (j > 0) {
                        row.append(CsvUtils.separator);
                    }
                    row.append(lists[j].get(i));
                }
                pw.println(row.toString());
            }
        } catch (IOException e) {
            logger.error("An error occurred in CsvUtils", e);
        }
    }

    /**
     * Private method to determine the regional separator used in the system.
     *
     * @return the regional separator (',' or ';')
     */
    private static char getRegionalSeparator() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        return symbols.getGroupingSeparator() == '.' ? ';' : ',';
    }
}
