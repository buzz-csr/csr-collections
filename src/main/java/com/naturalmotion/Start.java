package com.naturalmotion;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

import org.apache.commons.io.FilenameUtils;

public class Start {

    public static void main(String[] args) {
        File file = new File(args[0]);
        JsonObject jsonObjectBuild = parseDirectory(file, "", Json.createObjectBuilder());
        writeResult(file, jsonObjectBuild);
    }

    public static void writeResult(File file, JsonObject jsonObjectBuild) {
        StringWriter sw = new StringWriter();
        Map<String, Object> properties = new HashMap<>(1);
        properties.put(JsonGenerator.PRETTY_PRINTING, true);

        JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
        JsonWriter jsonWriter = writerFactory.createWriter(sw);

        jsonWriter.writeObject(jsonObjectBuild);
        jsonWriter.close();

        try (FileWriter myWriter = new FileWriter(file.getAbsolutePath() + "/collections.json")) {
            myWriter.write(sw.toString());
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static JsonObject parseDirectory(File file, String path, JsonObjectBuilder jsonObject) {
        jsonObject.add("directory", file.getName());
        jsonObject.add("path", path);
        JsonArrayBuilder cars = Json.createArrayBuilder();
        JsonArrayBuilder content = Json.createArrayBuilder();
        boolean hasCar = false;
        for (File actual : file.listFiles()) {
            if (actual.isDirectory()) {
                content.add(parseDirectory(actual, getNewPath(path, actual), Json.createObjectBuilder()));
            } else {
                if (actual.getName().startsWith("#")) {
                    jsonObject.add("Fusions", actual.getName());
                } else if (actual.getName().endsWith(".txt")) {
                    hasCar = true;
                    String name = FilenameUtils.removeExtension(actual.getName());
                    cars.add(name);
                }
            }
        }
        jsonObject.add("content", content.build());
        if (hasCar) {
            jsonObject.add("cars", cars);
        }
        return jsonObject.build();
    }

    public static String getNewPath(String path, File actual) {
        return path + "/" + actual.getName();
    }

}
