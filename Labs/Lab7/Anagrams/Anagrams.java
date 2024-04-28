package Labs.Lab7.Anagrams;

import java.io.InputStream;
import java.util.*;

public class Anagrams {

    public static void main(String[] args) {
        findAll(System.in);
    }

    public static void findAll(InputStream inputStream) {
        Map<String, Set<String>> words = new LinkedHashMap<>();

        Scanner scanner = new Scanner(inputStream);
        while ((scanner.hasNext())) {
            String word = scanner.nextLine();
            String key = sortedWord(word);
            words.computeIfAbsent(key, k -> new TreeSet<>()).add(word);
        }

        words.values().stream().filter(set -> set.size() >= 5)
                .forEach(set -> System.out.println(String.join(" ", set)));
    }

    public static String sortedWord(String s) {
        char[] chars = s.toCharArray();
        Arrays.sort(chars);
        return Arrays.toString(chars);
    }
}
