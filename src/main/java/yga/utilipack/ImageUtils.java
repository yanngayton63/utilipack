package yga.utilipack;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility class for image operations.
 */
public class ImageUtils {

    private static final Logger logger = LogManager.getLogger(ImageUtils.class);

    /**
     * Adds text to an image at the specified coordinates.
     *
     * @param imagePath the path to the image file
     * @param fontSize the size of the font
     * @param fontColor the color of the font
     * @param text the text to add to the image
     * @param x the x coordinate where the text will be placed
     * @param y the y coordinate where the text will be placed
     * @throws IOException if there is an error reading or writing the image
     * @throws IllegalArgumentException if the coordinates (x, y) are out of bounds
     */
    public static void addTextToImage(String imagePath, float fontSize, Color fontColor, String text, int x, int y) throws IOException {
        logger.info("Starting addTextToImage with imagePath: {}, fontSize: {}, fontColor: {}, text: {}, x: {}, y: {}",
                    imagePath, fontSize, fontColor, text, x, y);

        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(imagePath));
            if (image == null) {
                throw new IOException("Unsupported image format or image is corrupted.");
            }
        } catch (IOException e) {
            logger.error("Error reading the image file: {}", imagePath, e);
            throw e;
        }

        if (x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight()) {
            String errorMsg = "Coordinates (x, y) are out of image bounds.";
            logger.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }

        Graphics g = image.getGraphics();
        g.setFont(g.getFont().deriveFont(fontSize));
        g.setColor(fontColor);
        g.drawString(text, x, y);
        g.dispose();

        String currentDateTime = StringUtils.getCurrentDateTimeString();
        String formatName = FileUtils.getFileExtension(imagePath);

        if (!isSupportedFormat(formatName)) {
            String errorMsg = "Unsupported image format: " + formatName;
            logger.error(errorMsg);
            throw new IOException(errorMsg);
        }

        String outputFileName = FileUtils.generateOutputFileName(imagePath, currentDateTime, text, formatName);
        try {
            ImageIO.write(image, formatName, new File(outputFileName));
            logger.info("Image written successfully to {}", outputFileName);
        } catch (IOException e) {
            logger.error("Error writing the image file: {}", outputFileName, e);
            throw e;
        }
    }

    /**
     * Checks if the provided image format is supported.
     *
     * @param formatName the image format (e.g., "png", "jpg")
     * @return true if the format is supported, false otherwise
     */
    private static boolean isSupportedFormat(String formatName) {
        String[] supportedFormats = ImageIO.getWriterFormatNames();
        for (String format : supportedFormats) {
            if (format.equalsIgnoreCase(formatName)) {
                return true;
            }
        }
        return false;
    }
}
