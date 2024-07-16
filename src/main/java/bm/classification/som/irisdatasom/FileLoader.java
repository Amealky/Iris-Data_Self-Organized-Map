package bm.classification.som.irisdatasom;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FileLoader {

    static public Map<String, Object> readSimpleJsonFile(String filePath) {
        Map<String, Object> jsonContent = new HashMap<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File jsonFile = new File(filePath);

            jsonContent = objectMapper.readValue(jsonFile, Map.class);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonContent;
    }

    static public ArrayList<IrisVector> loadIrisDatasFile(String filePath) {
        ArrayList<IrisVector> irisVectors = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                ArrayList<Double> values = new ArrayList<>();
                String label = "unknown";
                String[] lineValues = line.split(",");
                for(String value : lineValues) {
                    try {
                        values.add(Double.parseDouble(value));
                    } catch (NumberFormatException e) {
                        label = value.toLowerCase();
                    }
                }
                IrisVector irisVector = new IrisVector(values, label);
                irisVectors.add(irisVector);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return irisVectors;
    }
}