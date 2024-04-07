package com.ocado.basket;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {

    public static List<String> readItems(String path){
        try{
            File jsonFile = new File(path);
            ObjectMapper mapper = new ObjectMapper();
            String[] array = mapper.readValue(jsonFile, String[].class);
            return Arrays.asList(array);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void printSolution(Map<String, List<String>> solution){
        System.out.println("\nSolution: ");
        for (Map.Entry<String, List<String>> entry : solution.entrySet()){
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }


    public static void main(String[] args) throws IOException {
        if (args.length < 2){
            System.out.println("Please provide two json files as arguments");
            return;
        }
        String configFilePath = args[0];
        String itemsFilePath = args[1];

        BasketSplitter basketSplitLib = new BasketSplitter(configFilePath);
        List<String> items = readItems(itemsFilePath);

        Map<String, List<String>> solution = basketSplitLib.split(items);
        printSolution(solution);
    }
}
