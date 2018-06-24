package ru.star.parser.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.Arrays;
import java.util.List;

/**
 * Interface with json parsing methods.
 */
public interface JParser {
    Gson gson = new Gson();
    JsonParser jsonParser = new JsonParser();

    /**
     * Convert json string to the object.
     *
     * @param jsonString - source json string
     * @param clazz      - result of the result object
     * @param <T>        - parameter class
     * @return result object
     */
    default <T> T parse(String jsonString, Class<T> clazz) {
        return gson.fromJson(jsonString, clazz);
    }

    /**
     * Convert json string to List of objects.
     *
     * @param jsonString - source json string
     * @param arrayClazz - class of the array
     * @param <T>        - parameter class
     * @return result list
     */
    default <T> List<T> parseArray(String jsonString, Class<T[]> arrayClazz) {
        return Arrays.asList(gson.fromJson(jsonString, arrayClazz));
    }

    /**
     * Get inner json from json string.
     *
     * @param jsonString  - external json
     * @param innerFields - path to inner json
     * @return inner json string
     */
    default String getInnerJson(String jsonString, String... innerFields) {
        JsonElement element = jsonParser.parse(jsonString);
        for (String field : innerFields) {
            element = element.getAsJsonObject().get(field);
        }
        return element.toString();
    }
}
