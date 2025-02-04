package com.example.telecom.util;

import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

public class ResourceLoader {

    public static List<String> loadTextLines(String fileName) {
        try (InputStream inputStream = ResourceLoader.class.getResourceAsStream("/" + fileName)) {
            if (inputStream == null) {
                throw new RuntimeException("Resource not found: " + fileName);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            return reader.lines().collect(Collectors.toList());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T loadYaml(String fileName, Class<? super T> type) {
        try (InputStream inputStream = ResourceLoader.class.getResourceAsStream("/" + fileName)) {
            if (inputStream == null) {
                throw new RuntimeException("Resource not found: " + fileName);
            }

            return new Yaml().loadAs(inputStream, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}