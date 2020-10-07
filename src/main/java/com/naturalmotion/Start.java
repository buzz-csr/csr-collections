package com.naturalmotion;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.apache.commons.io.FilenameUtils;

public class Start {

    public static void main(String[] args) {
        File file = new File(args[0]);
        JsonObject jsonObjectBuild = parseDirectory(file, "", Json.createObjectBuilder());
        writeResult(file, jsonObjectBuild);
    }

    public static void writeResult(File file, JsonObject jsonObjectBuild) {
        try (Writer out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file.getAbsolutePath() + "/collections.json"), StandardCharsets.UTF_8));) {
            out.write(jsonObjectBuild.toString());
        } catch (Exception e) {
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
            } else if (actual.getName().endsWith(".txt")) {
                hasCar = true;
                String name = FilenameUtils.removeExtension(actual.getName());
                cars.add(name);
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
