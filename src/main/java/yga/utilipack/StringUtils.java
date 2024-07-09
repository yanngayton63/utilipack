package yga.utilipack;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A utility class for common string operations.
 */
public class StringUtils {

	static Logger logger = LogManager.getLogger(StringUtils.class);

	/**
	 * Checks if a string contains another string or any string in an array (case
	 * insensitive).
	 * 
	 * @param str    the string to check
	 * @param search the substring or array of substrings to search for
	 * @return true if the string contains the substring or any substring in the
	 *         array (ignoring case), false otherwise
	 */
	public static boolean containsIgnoreCase(String str, Object search) {
		if (str == null || search == null) {
			return false;
		}

		try {
			if (search instanceof String) {
				return containsIgnoreCaseSingle(str, (String) search);
			} else if (search instanceof String[]) {
				for (String searchStr : (String[]) search) {
					if (containsIgnoreCaseSingle(str, searchStr)) {
						return true;
					}
				}
				return false;
			} else {
				throw new IllegalArgumentException("Search parameter must be a String or an array of Strings.");
			}
		} catch (IllegalArgumentException e) {
			logger.error("Invalid search parameter type", e);
			return false;
		}

	}

	/**
	 * Checks if a string contains another string (case insensitive).
	 * 
	 * @param str       the string to check
	 * @param searchStr the substring to search for
	 * @return true if the string contains the substring (ignoring case), false
	 *         otherwise
	 */
	private static boolean containsIgnoreCaseSingle(String str, String searchStr) {
		if (str == null || searchStr == null) {
			return false;
		}
		final int length = searchStr.length();
		if (length == 0) {
			return true;
		}
		for (int i = str.length() - length; i >= 0; i--) {
			if (str.regionMatches(true, i, searchStr, 0, length)) {
				return true;
			}
		}
		return false;
	}
}
