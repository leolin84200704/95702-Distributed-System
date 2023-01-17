import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import java.util.Arrays;
import java.util.Scanner;


/**
 * Distributed systems
 * Analyzing a txt file.
 *
 * Andrew ID: hungfanl
 * @author  Leo Lin
 */

public class TempestAnalytics {
    static SparkConf sparkConf = new SparkConf().setMaster("local").setAppName("JD Word Counter");
    static JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);

    /**
     * Calculating the number of lines in the txt file.
     * @param fileName The filename of the txt file
     */
    private static void lineCount(String fileName) {

        JavaRDD<String> inputFile = sparkContext.textFile(fileName);

        JavaRDD<String> wordsFromFile = inputFile.flatMap(content -> Arrays.asList(content.split("\n")));

        System.out.println("Number of lines: " + wordsFromFile.count());
    }

    /**
     * Calculating the number of words in the txt file.
     * @param fileName The filename of the txt file
     */
    private static void wordCount(String fileName) {

        JavaRDD<String> inputFile = sparkContext.textFile(fileName);

        JavaRDD<String> wordsFromFile = inputFile.flatMap(content -> Arrays.asList(content.split("[^a-zA-Z]+")));

        Function<String, Boolean> filter = k -> (!k.isEmpty());
        wordsFromFile = wordsFromFile.filter(filter);

        System.out.println("Number of words: " + wordsFromFile.count());
    }

    /**
     * Calculating the number of distinct words in the txt file.
     * @param fileName The filename of the txt file
     */
    private static void distinctWordCount(String fileName) {

        JavaRDD<String> inputFile = sparkContext.textFile(fileName);

        JavaRDD<String> wordsFromFile = inputFile.flatMap(content -> Arrays.asList(content.split("[^a-zA-Z]+")));

        Function<String, Boolean> filter = k -> (!k.isEmpty());
        wordsFromFile = wordsFromFile.filter(filter);

        System.out.println("Number of distinct words: " + wordsFromFile.distinct().count());
    }

    /**
     * Calculating the number of symbols in the txt file.
     * @param fileName The filename of the txt file
     */
    private static void symbols(String fileName) {

        JavaRDD<String> inputFile = sparkContext.textFile(fileName);

        JavaRDD<String> wordsFromFile = inputFile.flatMap(content -> Arrays.asList(content.split("")));

        Function<String, Boolean> filter = k -> (!k.isEmpty());
        wordsFromFile = wordsFromFile.filter(filter);

        System.out.println("Number of symbols: " + wordsFromFile.count());
    }

    /**
     * Calculating the number of distinct symbols in the txt file.
     * @param fileName The filename of the txt file
     */
    private static void distinctSymbols(String fileName) {

        JavaRDD<String> inputFile = sparkContext.textFile(fileName);

        JavaRDD<String> wordsFromFile = inputFile.flatMap(content -> Arrays.asList(content.split("")));

        Function<String, Boolean> filter = k -> (!k.isEmpty());
        wordsFromFile = wordsFromFile.filter(filter);

        System.out.println("Number of symbols: " + wordsFromFile.distinct().count());
    }

    /**
     * Calculating the number of distinct english letters in the txt file.
     * @param fileName The filename of the txt file
     */
    private static void distinctLetters(String fileName) {

        JavaRDD<String> inputFile = sparkContext.textFile(fileName);

        JavaRDD<String> wordsFromFile = inputFile.flatMap(content -> Arrays.asList(content.split("")));

        Function<String, Boolean> filter = k -> (!k.isEmpty());
        wordsFromFile = wordsFromFile.filter(filter);
        Function<String, Boolean> f = k -> ((k.charAt(0) >= 'a' && k.charAt(0) <= 'z') || (k.charAt(0) >= 'A' && k.charAt(0) <= 'Z'));

        wordsFromFile = wordsFromFile.filter(f);

        System.out.println("Number of distinct Letters: " + wordsFromFile.distinct().count());
    }

    /**
     * Have the user insert a word and print out all the lines that include that particular word.
     * @param fileName The filename of the txt file
     */
    private static void search(String fileName) {
        System.out.println("Insert the word you want to search");
        Scanner readInput = new Scanner(System.in);
        String key = readInput.nextLine();

        JavaRDD<String> inputFile = sparkContext.textFile(fileName);

        JavaRDD<String> wordsFromFile = inputFile.flatMap(content -> Arrays.asList(content.split("\n")));

        Function<String, Boolean> filter = k -> (k.contains(key));
        wordsFromFile = wordsFromFile.filter(filter);
        for(int i = 0; i < wordsFromFile.count(); i++) {
            System.out.println(wordsFromFile.collect().get(i));
        }
    }

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("No files provided.");
            System.exit(0);
        }
        lineCount(args[0]);
        wordCount(args[0]);
        distinctWordCount(args[0]);
        symbols(args[0]);
        distinctSymbols(args[0]);
        distinctLetters(args[0]);
        search(args[0]);
    }
}
