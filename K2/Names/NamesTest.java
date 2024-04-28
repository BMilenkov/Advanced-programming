package K2.Names;


import java.util.*;
import java.util.stream.Collectors;


class Names {
    List<String> names;
    Map<String,Long> uniqueNames;

    public Names() {
        this.names = new ArrayList<>();
        this.uniqueNames = new HashMap<>();
    }

    public void addName(String name) {
        names.add(name);
    }

    public void printN(int n) {
        this.uniqueNames =
                names.stream().collect(Collectors
                        .groupingBy(name -> name, TreeMap::new, Collectors.counting()));

        uniqueNames.entrySet().stream()
                .filter(e -> e.getValue() >= n)
                .forEach(e -> System.out.printf("%s (%d) %d\n", e.getKey(), e.getValue(), uniqueChars(e.getKey())));
    }

    public int uniqueChars(String name) {
        Set<Character> unique = new HashSet<>();
        for (int i = 0; i < name.length(); i++) {
            unique.add(Character.toLowerCase(name.charAt(i)));
        }
        return unique.size();
    }

    public String findName(int len, int x) {

        List<String> result = uniqueNames.keySet().stream().filter(name -> name.length() < len)
                .collect(Collectors.toList());

        int index = x % result.size();
        return result.get(index);
    }
}


public class NamesTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        Names names = new Names();
        for (int i = 0; i < n; ++i) {
            String name = scanner.nextLine();
            names.addName(name);
        }
        n = scanner.nextInt();
        System.out.printf("===== PRINT NAMES APPEARING AT LEAST %d TIMES =====\n", n);
        names.printN(n);
        System.out.println("===== FIND NAME =====");
        int len = scanner.nextInt();
        int index = scanner.nextInt();
        System.out.println(names.findName(len, index));
        scanner.close();

    }
}
