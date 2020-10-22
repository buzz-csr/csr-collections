package com.naturalmotion;

import org.apache.commons.io.FilenameUtils;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParsingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StartMapping {

    private Pattern fusionGold = Pattern.compile("^#*(.*)_fusions.png");
    private Pattern fusionOthers = Pattern.compile("^(.*)_fusions.png");

    public static void main(String[] args) {
        File file = new File(args[0]);
        new StartMapping().searchInFolder(file);
    }

    public void searchInFolder(File folder) {
        if (folder.getName().startsWith("#")) {
            for (File actual : folder.listFiles()) {
                if (actual.isDirectory()) {
                    searchInFolder(actual);
                } else {
                    Matcher matcher = fusionOthers.matcher(actual.getName());
                    if (matcher.find()) {
                        String carName = matcher.group(1);
                        searchSpecialCarId(folder, carName);
                    }
                }
            }
        } else {
            String carId = null;
            String carName = null;
            for (File actual : folder.listFiles()) {
                if (actual.isDirectory()) {
                    searchInFolder(actual);
                } else {
                    Matcher matcher = fusionGold.matcher(actual.getName());
                    if (matcher.find()) {
                        carName = matcher.group(1);
                    } else if (carId == null && FilenameUtils.getExtension(actual.getName()).equals("txt")) {
                        carId = getId(actual);
                    }
                }
            }
            System.out.println(carId + ": " + carName);
        }

    }

    private void searchSpecialCarId(File folder, String carName) {
        String carId = null;
        for (File actualBis : folder.listFiles()) {
            if (actualBis.getName().equals(carName + ".txt")) {
                carId = getId(actualBis);

                System.out.println(carId + ": " + carName);
            }
        }
    }

    private static String getId(File actual) {
        String carId = null;
        try (InputStream is = new FileInputStream(actual); JsonReader reader = Json.createReader(is)) {
            JsonObject jsonObject = reader.readObject();
            if(jsonObject.containsKey("crdb")){
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


}
