# Ocado Technology recruitment task: BasketSplitter
The packed archive contains:
- `BasketSplitter.java` - the main code of my library located in the `src/main/java/com/ocado/basket` folder
- `Main.java` - a file containing an example of using my library located in the `src/main/java/com/ocado/basket` folder
- `BasketSplitterTest.java` - a file containing unit test for my library, located in the `src/test/java/com/ocado/basket` folder
- `basket-1.json`, `basket-2.json`, `config.json` - files that I received as an attachment to the email informing me about this recruitment task. They are located in the `src/main/resources` folder and can be used for testing purposes.
- `pom.xml` - a file containing Maven configuration
- `ocado-basket-splitter-1.0-SNAPSHOT.jar` and `ocado-basket-splitter-1.0-SNAPSHOT-jar-with-dependencies.jar` - files containing the compiled library, located in the source folder

# How to run the program
In order to run the program using the `.jar` file with the built library (fat-jar, which contains all required dependencies), you need to run the terminal in the source folder of the project and enter the command: `java -jar ocado-basketsplitter-1.0-SNAPSHOT-jar-with-dependencies.jar $arg1 $arg2`, where:
- `$arg1` - an absolute path to the configuration file - you can also use the attached file, e.g. `src/main/resources/config.json`
- `$arg2` - an absolute path to the file describing the basket - you can also use the attached files, e.g. `src/main/resources/basket-1.json` or `src/main/resources/basket-2.json`

# Testing
I have tested my program with the use of dependencies - `junit` and `mockito`. I have created a few test cases, that can be foung in the `src/test/java/com/ocado/basket/BasketSplitterTest.java` folder. The tests can be run by entering the command `mvn test` in the terminal in the source folder of the project.  The tested methods are: the main `split` method, the `BasketSplitter` constructor, and the two methods used for validation of the input that my program is provided with - `validateConfigFile` and `validateBasket`.

# Solution description
My program was written in Java 21 and built using the `maven-assembly-plugin` plugin, which allowed me to create a fat-jar containing all the necessary dependencies. It also uses the `jackson-databind` library, which is responsible for parsing JSON files, since the program reads both the configuration file and the basket file in this format.
<br /> <br />
The main part of the solution is the `split` method inside my `BasketSplitter` class, which is responsible for finding the optimal way to split the basket in a way that was described in the task description. The method accepts a list of strings as a parameter, validates whether the item list is valid based on the task requirements, and then solves the issue with an iterative approach - first it goes over the list of strings and creates a `Map<String, Integer> - deliveryMethodCount`, which keeps track of the information which delivery method appears the most across the item list from the basket based on the configuration file, and then it goes over the item list again, keeping track on the cardinality of each delivery method and choosing the ones that appear the most. The output of the method is represented with a `Map<String, List<String>>`, where the key is the delivery method, and the value is a list of items, that should be delivered using this method.
<br /> <br />
The constructor to the `BasketSplitter` class accepts a `String` parameter, which is the path to the `.json` configuration file, which then goes through a process of being validated with the use of `validateConfigFile` method (which checks whether the file is valid and meets the requirements, that were presented in the task description), and parsed into a `Map<String, List<String>>` object by a `parseConfigFile` method.
