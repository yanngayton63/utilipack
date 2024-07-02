package yga.utilipack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility class for mathematical operations.
 */
public class MathUtils {

    private static final Logger logger = LogManager.getLogger(MathUtils.class);

    /**
     * Calculates the Greatest Common Divisor (GCD) of two integers using the Euclidean algorithm.
     *
     * @param a the first integer (must be positive)
     * @param b the second integer (must be positive)
     * @return the GCD of a and b
     * @throws IllegalArgumentException if either a or b is non-positive
     */
    public static int calculateGCD(int a, int b) {
        if (a <= 0 || b <= 0) {
            String errorMessage = "Both numbers must be positive: a=" + a + ", b=" + b;
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        while (b != 0) {
            int remainder = a % b;
            a = b;
            b = remainder;
        }
        return a;
    }

}
