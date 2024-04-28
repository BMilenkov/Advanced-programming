package K2.MapOps;


import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


interface MergeStrategy<V> {
    V execute(V a, V b);
}


class MapOps<K, V> {


    public static <K extends Comparable<K>, V extends Comparable<V>> Map<K, V> merge(Map<K, V> mapLeft, Map<K, V> mapRight, MergeStrategy<V> mergeStrategy) {
        Map<K, V> resultMap = new TreeMap<>();

        mapRight.keySet()
                .stream().filter(mapLeft::containsKey)
                .forEach(k -> resultMap.put(k, mergeStrategy.execute(mapLeft.get(k), mapRight.get(k))));

        mapLeft.keySet().stream().
                filter(k -> !mapRight.containsKey(k))
                .forEach(k -> resultMap.put(k, mapLeft.get(k)));

        mapRight.keySet().stream().
                filter(k -> !mapLeft.containsKey(k))
                .forEach(k -> resultMap.put(k, mapRight.get(k)));

        return resultMap;
    }

}


public class GenericMapTest {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int testCase = Integer.parseInt(sc.nextLine());

        if (testCase == 1) { //Mergeable integers
            Map<Integer, Integer> mapLeft = new HashMap<>();
            Map<Integer, Integer> mapRight = new HashMap<>();
            readIntMap(sc, mapLeft);
            readIntMap(sc, mapRight);

            //TODO Create an object of type MergeStrategy that will enable merging of two Integer objects into a new Integer object which is their sum
            MergeStrategy<Integer> mergeStrategy = Integer::sum;

            printMap(MapOps.merge(mapLeft, mapRight, mergeStrategy));
        } else if (testCase == 2) { // Mergeable strings
            Map<String, String> mapLeft = new HashMap<>();
            Map<String, String> mapRight = new HashMap<>();
            readStrMap(sc, mapLeft);
            readStrMap(sc, mapRight);

            //TODO Create an object of type MergeStrategy that will enable merging of two String objects into a new String object which is their concatenation
            MergeStrategy<String> mergeStrategy = String::concat;

            printMap(MapOps.merge(mapLeft, mapRight, mergeStrategy));
        } else if (testCase == 3) {
            Map<Integer, Integer> mapLeft = new HashMap<>();
            Map<Integer, Integer> mapRight = new HashMap<>();
            readIntMap(sc, mapLeft);
            readIntMap(sc, mapRight);

            //TODO Create an object of type MergeStrategy that will enable merging of two Integer objects into a new Integer object which will be the max of the two objects
            MergeStrategy<Integer> mergeStrategy = (n1, n2) -> n1 > n2 ? n1 : n2;

            printMap(MapOps.merge(mapLeft, mapRight, mergeStrategy));
        } else if (testCase == 4) {
            Map<String, String> mapLeft = new HashMap<>();
            Map<String, String> mapRight = new HashMap<>();
            readStrMap(sc, mapLeft);
            readStrMap(sc, mapRight);

            //TODO Create an object of type MergeStrategy that will enable merging of two String objects into a new String object which will mask the occurrences of the second string in the first string

            MergeStrategy<String> mergeStrategy = (s1, s2) -> {
                String result = IntStream.range(0, s2.length()).mapToObj(i -> "*").collect(Collectors.joining(""));
                return s1.replaceAll(s2, result);
            };
            printMap(MapOps.merge(mapLeft, mapRight, mergeStrategy));
        }
    }

    private static void readIntMap(Scanner sc, Map<Integer, Integer> map) {
        int n = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < n; i++) {
            String input = sc.nextLine();
            String[] parts = input.split("\\s+");
            int k = Integer.parseInt(parts[0]);
            int v = Integer.parseInt(parts[1]);
            map.put(k, v);
        }
    }

    private static void readStrMap(Scanner sc, Map<String, String> map) {
        int n = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < n; i++) {
            String input = sc.nextLine();
            String[] parts = input.split("\\s+");
            map.put(parts[0], parts[1]);
        }
    }

    private static void printMap(Map<?, ?> map) {
        map.forEach((k, v) -> System.out.printf("%s -> %s%n", k.toString(), v.toString()));
    }
}
