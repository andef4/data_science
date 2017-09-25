package ch.andef4.datascience.hamlet2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    private static Stream<String> readFile() {
        try {
            return Files.readAllLines(Paths.get("src/main/resources/hamlet.txt")).stream();
        } catch (IOException e) {
            throw new RuntimeException("Cannot open file");
        }
    }

    private static List<String> tokenize(Stream<String> lines) {
        List<String> words = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\w+", Pattern.UNICODE_CHARACTER_CLASS);
        lines.forEach(line -> {
            Matcher m = pattern.matcher(line);
            while (m.find()) {
                words.add(m.group());
            }
        });
        return words;
    }

    private static Stream<String> lowerCase(Stream<String> stream) {
        return stream.map(String::toLowerCase);
    }

    private static Pair<Stream<String>, Double> removeStopwords(Stream<String> stream) {
        // TODO
        return new Pair<>(stream, 0.0);
    }

    private static Stream<String> stemm(Stream<String> stream) {
        // TODO
        return stream;
    }

    private static Map<String, Integer> countWords(Stream<String> words) {
        HashMap<String, Integer> wordCount = new HashMap<>();
        words.forEach(word -> {
            Integer count = wordCount.getOrDefault(word, 0);
            count += 1;
            wordCount.put(word, count);
        });
        return wordCount;
    }

    private static void exportGnuplot(Map<String, Integer> wordCount, String name) {
        try {
            Stream<String> preamble = Files.readAllLines(
                    Paths.get("src/main/resources/words.plt")).stream();
            Files.write(Paths.get(String.format("%s.plt", name)),
                    Stream.concat(preamble,
                    wordCount.entrySet()
                            .stream()
                            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                            .map(e1 -> String.format("%s\t%d", e1.getKey(), e1.getValue()))
                            .limit(20))
                            .collect(Collectors.toList()));
        } catch (IOException e) {
            throw new RuntimeException("Cannot write gnuplot file");
        }
    }

    public static void main(String[] args) {
        Stream<String> lines = readFile();
        List<String> words = tokenize(lines);

        // the unmodified tokenized text
        exportGnuplot(countWords(words.stream()), "unmodified");

        // the lower-cased text
        exportGnuplot(countWords(lowerCase(words.stream())), "lowercase");

        // the text without stopwords (what is the percentage of stopwords)
        Pair<Stream<String>, Double> stopwords = removeStopwords(words.stream());
        System.out.printf("Percentage of stopwords (no lowercase): %f", stopwords.getSecond() * 100.0);
        exportGnuplot(countWords(stopwords.getFirst()), "stopwords");

        stopwords = removeStopwords(lowerCase(words.stream()));
        System.out.printf("Percentage of stopwords (with lowercase): %f", stopwords.getSecond() * 100.0);
        exportGnuplot(countWords(stopwords.getFirst()), "stopwords_lowercase");

        // the stemmed text
        exportGnuplot(countWords(stemm(words.stream())), "stemmed");
        exportGnuplot(countWords(stemm(lowerCase(words.stream()))), "lowercase_stemmed");
        exportGnuplot(countWords(removeStopwords(stemm(lowerCase(words.stream()))).getFirst()), "lowercase_stemmed_stopwords");
    }
}


class Pair<A, B> {
    private A first;
    private B second;

    public Pair(A first, B second) {
        super();
        this.first = first;
        this.second = second;
    }

    public int hashCode() {
        int hashFirst = first != null ? first.hashCode() : 0;
        int hashSecond = second != null ? second.hashCode() : 0;

        return (hashFirst + hashSecond) * hashSecond + hashFirst;
    }

    public boolean equals(Object other) {
        if (other instanceof Pair) {
            Pair otherPair = (Pair) other;
            return
                    ((  this.first == otherPair.first ||
                            ( this.first != null && otherPair.first != null &&
                                    this.first.equals(otherPair.first))) &&
                            (  this.second == otherPair.second ||
                                    ( this.second != null && otherPair.second != null &&
                                            this.second.equals(otherPair.second))) );
        }

        return false;
    }

    public String toString()
    {
        return "(" + first + ", " + second + ")";
    }

    public A getFirst() {
        return first;
    }

    public void setFirst(A first) {
        this.first = first;
    }

    public B getSecond() {
        return second;
    }

    public void setSecond(B second) {
        this.second = second;
    }
}