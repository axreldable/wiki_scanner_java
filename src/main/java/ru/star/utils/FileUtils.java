package ru.star.utils;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtils {
    private final static Logger logger = Logger.getLogger(FileUtils.class);

    public static void saveToFile(String text, String fileName) {
        try {
            Files.write(Paths.get(fileName), text.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.info("Exception during writing to file - " + fileName, e);
        }
    }

    public static Boolean createDir(String dirName) {
        File file = new File(dirName);
        if (file.mkdir()) {
            logger.debug("Create dir - " + dirName);
            return true;
        }
        logger.debug("Couldn't create dir - " + dirName);
        return false;
    }
}