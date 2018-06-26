package ru.star.utils;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Utility class for file working.
 */
public class FileUtils {
    private final static Logger logger = Logger.getLogger(FileUtils.class);

    private FileUtils() {
        throw new UnsupportedOperationException("Utility classes are not supposed to be instantiated");
    }

    /**
     * Saves the text to file in UTF-8 encoding.
     * Create the file if the path to the file exists.
     * Rewrite file context if the file exists.
     *
     * @param text     - text for save
     * @param fileName - path to file
     */
    public static void saveToFile(String text, String fileName) {
        try {
            Files.write(Paths.get(fileName), text.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.info("Exception during writing to file - " + fileName, e);
        }
    }

    /**
     * Creates dirs.
     *
     * @param dirName - path to the dir
     * @return creation result
     */
    public static Boolean createDirs(String dirName) {
        File file = new File(dirName);
        if (file.mkdirs()) {
            logger.debug("Create dir - " + dirName);
            return true;
        }
        logger.debug("Couldn't create dir - " + dirName);
        return false;
    }

    /**
     * Reads file to String from file in UTF-8 encoding.
     *
     * @param fileName - path to the file
     * @return the file contents
     */
    public static String readFromFile(String fileName) {
        try {
            return new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.info("Exception during reading from file - " + fileName, e);
        }
        return null;
    }
}
