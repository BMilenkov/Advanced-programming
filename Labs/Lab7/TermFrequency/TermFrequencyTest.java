package Labs.Lab7.TermFrequency;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;


class TermFrequency {

    private final Map<String, Long> wordCount;

    public TermFrequency() {
        this.wordCount = new TreeMap<>();
    }

    public TermFrequency(InputStream is, String[] stopWords) {
        Scanner scanner = new Scanner(is);
        List<String> words = new ArrayList<>();
        while (scanner.hasNext()) {
            String word = scanner.next();
            word = word.toLowerCase().replace(',', '\0').replace('.', '\0').trim();
            if (!word.isEmpty() &&!Arrays.asList(stopWords).contains(word))
                words.add(word);
        }

        this.wordCount = words.stream()
                .collect(Collectors.groupingBy(s -> s, TreeMap::new, Collectors.counting()));
    }

    public int countTotal() {
        return (int) wordCount.values().stream().mapToLong(i -> i).sum();
    }

    public int countDistinct() {
        return wordCount.size();
    }

    public List<String> mostOften(int k) {

        return wordCount.keySet().stream().sorted(Comparator.comparing(wordCount::get).reversed()).limit(k).collect(Collectors.toList());


//        return wordCount.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
//                .map(Entry::getKey).limit(k).collect(Collectors.toList());
    }
}


public class TermFrequencyTest {
    public static void main(String[] args) throws FileNotFoundException {
        String[] stop = new String[]{"во", "и", "се", "за", "ќе", "да", "од",
                "ги", "е", "со", "не", "тоа", "кои", "до", "го", "или", "дека",
                "што", "на", "а", "но", "кој", "ја"};
        TermFrequency tf = new TermFrequency(System.in, stop);
        System.out.println(tf.countTotal());
        System.out.println(tf.countDistinct());
        System.out.println(tf.mostOften(10));
    }
}
