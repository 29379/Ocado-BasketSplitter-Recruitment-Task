package com.ocado.basket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class BasketSplitterTest {
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private BasketSplitter basketSplitter;

    @Test
    public void testValidateConfigFile() throws IOException {
        String filePath = "src/main/resources/config.json";
        int maxSize = 10;
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(new File(filePath));

        Iterator<String> fieldNames = root.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode valueNode = root.get(fieldName);
            assertTrue(valueNode.isArray());
            assertTrue(valueNode.size() <= maxSize && !valueNode.isEmpty());
        }
    }

    @Test
    public void testConstructor() throws IOException {
        basketSplitter = new BasketSplitter("src/main/resources/config.json");
        assertEquals(100, basketSplitter.getConfig().size());

        assertEquals(2, basketSplitter.getConfig().get("Cookies Oatmeal Raisin").size());   // random value from the beginning of the file
        assertEquals(8, basketSplitter.getConfig().get("Beans - Green").size());            // random value from the end of the file
        assertEquals(5, basketSplitter.getConfig().get("Flower - Daisies").size());         // random value from the middle of the file

        assertThrows(IOException.class, () -> new BasketSplitter("src/main/resources/empty.json"));
        assertThrows(IOException.class, () -> new BasketSplitter(null));
    }

    @Test
    public void testValidateBasket() throws IOException {
        basketSplitter = new BasketSplitter("src/main/resources/config.json");
        objectMapper = new ObjectMapper();
        String jsonArray = "[\"Cocoa Butter\", \"Tart - Raisin And Pecan\", \"Table Cloth 54x72 White\", \"Flower - Daisies\", \"Fond - Chocolate\", \"Cookies - Englishbay Wht\"]\n";
        jsonArray = jsonArray.replace("\" ", "\", \""); // Add missing comma between elements
        List<String> items = List.of(objectMapper.readValue(jsonArray, String[].class));

        assertThrows(IllegalArgumentException.class, () -> basketSplitter.validateBasket(null));        //  null not allowed
        assertThrows(IllegalArgumentException.class, () -> basketSplitter.validateBasket(List.of("")));   // empty list not allowed
        assertThrows(IllegalArgumentException.class,
                () -> basketSplitter.validateBasket(List.of(
                        "Cocoa Butter",
                        "Tart - Raisin And Pecan",
                        "",
                        "Table Cloth 54x72 White")));       // empty element not allowed
        assertThrows(IllegalArgumentException.class,
                () -> basketSplitter.validateBasket(List.of(
                        "Cocoa Butter",
                        "Tart - Raisin And Pecan",
                        "aaaaaaaa",
                        "Table Cloth 54x72 White")));      // element that does not appear in config file not allowed
        basketSplitter.validateBasket(items);              // valid - should go through
    }

    @Test
    public void testSplit() throws IOException {
            basketSplitter = new BasketSplitter("src/main/resources/config.json");
            List<String> items = Main.readItems("src/main/resources/basket-1.json");
            Map<String, List<String>> solution = basketSplitter.split(items);

            assertEquals(2, solution.size());
            assertTrue(solution.containsKey("Courier"));
            assertTrue(solution.containsKey("Express Collection"));
            assertEquals(5, solution.get("Courier").size());
            assertEquals(1, solution.get("Express Collection").size());

            assertThrows(IllegalArgumentException.class, () -> basketSplitter.split(null));
            assertThrows(IllegalArgumentException.class, () -> basketSplitter.split(List.of()));
            assertThrows(IllegalArgumentException.class, () -> basketSplitter.split(List.of("")));
            assertThrows(IllegalArgumentException.class,
                    () -> basketSplitter.split(List.of(
                            "Cocoa Butter",
                            "aaa",
                            "Table Cloth 54x72 White",
                            "Flower - Daisies",
                            "Fond - Chocolate",
                            "Cookies - Englishbay Wht")));
    }

}
