package ch.andef4.datascience.hamlet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    private static List<String> readFile() {
        try {
            return Files.readAllLines(Paths.get("src/main/resources/hamlet.txt"));
        } catch (IOException e) {
            throw new RuntimeException("Cannot open file");
        }
    }

    private static List<String> getWords(List<String> lines) {
        List<String> words = new ArrayList<>();

        Pattern pattern = Pattern.compile("\\w+", Pattern.UNICODE_CHARACTER_CLASS);
        for (String line : lines) {

            Matcher m = pattern.matcher(line);
            while (m.find()) {
                words.add(m.group().toLowerCase());
            }
        }
        return words;
    }

    private static Map<String, Integer> countWords(List<String> words) {
        HashMap<String, Integer> wordCount = new HashMap<>();
        for (String word : words) {
            Integer count = wordCount.getOrDefault(word, 0);
            count += 1;
            wordCount.put(word, count);
        }
        return wordCount;
    }

    private static void exportGnuplot(Map<String, Integer> wordCount) {
        try {
            Files.write(Paths.get("words.dat"),
                    wordCount.entrySet()
                            .stream()
                            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                            .map(e1 -> String.format("%s\t%d", e1.getKey(), e1.getValue()))
                            .limit(20)
                            .collect(Collectors.toList()));
        } catch (IOException e) {
            throw new RuntimeException("Cannot write gnuplot file");
        }

    }

    public static void main(String[] args) {
        List<String> lines = readFile();
        List<String> words = getWords(lines);
        Map<String, Integer> wordCount = countWords(words);
        exportGnuplot(wordCount);
    }
}
