package com.ocado.basket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BasketSplitter {
    private final Map<String, List<String>> config;
    public BasketSplitter(String configFilePath) throws IOException {
        validateConfigFile(configFilePath);
        this.config = parseConfigFile(configFilePath);
    }

    public Map<String, List<String>> getConfig() {
        return config;
    }

    public Map<String, List<String>> parseConfigFile(String configFilePath) throws IOException {
        Map<String, List<String>> config = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(new File(configFilePath));
        for (Iterator<String> it = root.fieldNames(); it.hasNext(); ) {
            String fieldName = it.next();
            JsonNode valueNode = root.get(fieldName);
            List<String> valueList = new ArrayList<>();
            if (valueNode.isArray()){
                for (JsonNode node : valueNode){
                    valueList.add(node.asText());
                }
            }
            config.put(fieldName, valueList);
        }
        return config;
    }

    private void validateConfigFile(String path) throws IOException {
        JsonNode root = getJsonNode(path);
        for (Iterator<String> it = root.fieldNames(); it.hasNext(); ) {
            String fieldName = it.next();
            JsonNode valueNode = root.get(fieldName);

            if (valueNode.isArray() && valueNode.size() > 10) {
                throw new IOException("Maximum number of delivery methods (10) exceeded for product " + fieldName + " - it has " + valueNode.size());
            }
        }
    }

    private static JsonNode getJsonNode(String path) throws IOException {
        if (path == null || path.isEmpty()) {
            throw new IOException("Config file path cannot be null or empty");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(new File(path));

        if (root.isEmpty()) {
            throw new IOException("Config file is empty");
        }
        else if (root.size() > 1000){
            throw new IOException("Maximum number of products (1000) exceeded - it has " + root.size());
        }
        return root;
    }

    public void validateBasket(List<String> items) throws IllegalArgumentException{
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Basket cannot be null or empty");
        }
        if (items.size() > 100) {
            throw new IllegalArgumentException("Maximum number of items in the basket (100) exceeded - it has " + items.size());
        }
        for (String item : items) {
            if (item == null || item.isEmpty()) {
                throw new IllegalArgumentException("Item cannot be null or empty");
            }
            if (!config.containsKey(item)) {
                throw new IllegalArgumentException("Item " + item + " is not in the config file");
            }
        }
    }

    public Map<String, List<String>> split(List<String> items){
        validateBasket(items);

        Map<String, Integer> deliveryMethodCount = new HashMap<>();
        Map<String, List<String>> result = new HashMap<>();

        for (String item : items){
            List <String> methods = config.get(item);
            for (String method : methods){
                if (deliveryMethodCount.containsKey(method)) {
                    deliveryMethodCount.put(method, deliveryMethodCount.get(method) + 1);
                } else {
                    deliveryMethodCount.put(method, 1);
                }
            }
        }

        for (String item : items){
            List<String> deliveryMethods = config.get(item);
            String maxDeliveryMethod = null;
            int maxCount = Integer.MIN_VALUE;
            for (String deliveryMethod : deliveryMethods){
                int count = deliveryMethodCount.get(deliveryMethod);
                if (count > maxCount){
                    maxCount = count;
                    maxDeliveryMethod = deliveryMethod;
                }
            }
            if (maxDeliveryMethod != null){
                List<String> products = result.getOrDefault(maxDeliveryMethod, new ArrayList<>());
                products.add(item);
                result.put(maxDeliveryMethod, products);
            }
        }
        return result;
    }

}
