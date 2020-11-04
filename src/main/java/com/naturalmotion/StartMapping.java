package com.naturalmotion;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParsingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StartMapping {

    private Pattern fusionWithDash = Pattern.compile("^#*(.*)_fusions.png");
    private Pattern fusion = Pattern.compile("^(.*)_fusions.png");

    public static void main(String[] args) {
        File file = new File(args[0]);
        try (FileWriter writer = new FileWriter("carNames.json")) {
            writer.write("{\n");
            new StartMapping().searchFusionInFolder(file, writer);
            writer.write("}");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void searchFusionInFolder(File folder, FileWriter writer) throws IOException {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                searchFusionInFolder(file, writer);
            } else {
                String fileName = file.getName();
                Matcher matcherWithDash = fusionWithDash.matcher(fileName);
                String carName = getCarName(fileName, matcherWithDash);
                if (carName != null) {
                    searchSpecialCarId(folder, carName, writer);
                }
            }
        }
    }

    private String getCarName(String fileName, Matcher matcherWithDash) {
        String carName = null;
        if (matcherWithDash.find()) {
            carName = matcherWithDash.group(1);
        } else {
            Matcher matcher = fusion.matcher(fileName);
            if (matcher.find()) {
                carName = matcher.group(1);
            }
        }
        return carName;
    }

    private void searchSpecialCarId(File folder, String carName, FileWriter writer) throws IOException {
        String carId = null;
        for (File actualBis : folder.listFiles()) {
            if (actualBis.getName().equals(carName + ".txt")) {
                carId = getId(actualBis);
                write(writer, carId, carName);
            }
        }
        if (carId == null) {
            int index = 0;
            String[] fileNames = folder.list();
            while (carId == null && index < fileNames.length) {
                if (fileNames[index].endsWith(".txt")) {
                    carId = getId(folder.listFiles()[index]);
                    write(writer, carId, carName);
                }
                index++;
            }
        }
    }

    private static String getId(File actual) {
        String carId = null;
        try (InputStream is = new FileInputStream(actual); JsonReader reader = Json.createReader(is)) {
            JsonObject jsonObject = reader.readObject();
            if (jsonObject.containsKey("crdb")) {
                carId = jsonObject.getString("crdb");
            }
        } catch (FileNotFoundException | JsonParsingException e) {
            System.out.println(actual.getName());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println(actual.getName());
            e.printStackTrace();
        }
        return carId;
    }

    private void write(FileWriter writer, String carId, String carName) throws IOException {
        System.out.println(carId + ": " + carName);
        if (carId != null && carName != null) {
            writer.write("\"" + carId + "\": \"" + carName + "\",\n");
        }
    }


}
