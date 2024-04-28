package K2.FileSystem;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Partial exam II 2016/2017
 */


class File implements Comparable<File> {
    private final String name;
    private final Integer size;
    private final LocalDateTime lct;

    public File(String name, Integer size, LocalDateTime lct) {
        this.name = name;
        this.size = size;
        this.lct = lct;
    }

    public String getName() {
        return name;
    }

    public Integer getSize() {
        return size;
    }

    public int getYear() {
        return lct.getYear();
    }

    public LocalDateTime getLct() {
        return lct;
    }


    public String monthAndDay() {
        return lct.getMonth() + "-" + lct.getDayOfMonth();
    }


    @Override
    public String toString() {
        return String.format("%-10s %5dB %s", name, size, lct);
    }


    @Override
    public int compareTo(File o) {
        return Comparator.comparing(File::getLct).thenComparing(File::getName).thenComparing(File::getSize).compare(this, o);
    }
}

class FileSystem {
    Map<Character, Set<File>> filesInFolder;
    Comparator<File> comparator = Comparator.comparing(File::getLct).thenComparing(File::getName).thenComparing(File::getSize);

    public FileSystem() {
        this.filesInFolder = new HashMap<>();
    }

    public void addFile(char c, String part, int i, LocalDateTime localDateTime) {
        filesInFolder.putIfAbsent(c, new TreeSet<>(comparator));
        filesInFolder.get(c).add(new File(part, i, localDateTime));
    }

    public List<File> findAllHiddenFilesWithSizeLessThen(int size) {
        return filesInFolder.values().stream()
                .flatMap(Collection::stream)
                .filter(file -> file.getName().startsWith(".") && file.getSize() < size)
                .collect(Collectors.toList());
    }

    public int totalSizeOfFilesFromFolders(List<Character> folders) {
        return folders.stream()
                .flatMap(folder -> filesInFolder.getOrDefault(folder, Collections.emptySet()).stream())
                .mapToInt(File::getSize)
                .sum();
    }

    public Map<Integer, Set<File>> byYear() {
        return filesInFolder.values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(File::getYear, Collectors.toSet()));
    }

    public Map<String, Long> sizeByMonthAndDay() {

        return filesInFolder.values()
                .stream().flatMap(Collection::stream)
                .collect(Collectors.groupingBy(File::monthAndDay
                        , Collectors.summingLong(File::getSize)));

    }
}


public class FileSystemTest {
    public static void main(String[] args) {
        FileSystem fileSystem = new FileSystem();
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; i++) {
            String line = scanner.nextLine();
            String[] parts = line.split(":");
            fileSystem.addFile(parts[0].charAt(0), parts[1],
                    Integer.parseInt(parts[2]),
                    LocalDateTime.of(2016, 12, 29, 0, 0, 0).minusDays(Integer.parseInt(parts[3]))
            );
        }
        int action = scanner.nextInt();
        if (action == 0) {
            scanner.nextLine();
            int size = scanner.nextInt();
            System.out.println("== Find all hidden files with size less then " + size);
            List<File> files = fileSystem.findAllHiddenFilesWithSizeLessThen(size);
            files.forEach(System.out::println);
        } else if (action == 1) {
            scanner.nextLine();
            String[] parts = scanner.nextLine().split(":");
            System.out.println("== Total size of files from folders: " + Arrays.toString(parts));
            int totalSize = fileSystem.totalSizeOfFilesFromFolders(Arrays.stream(parts)
                    .map(s -> s.charAt(0))
                    .collect(Collectors.toList()));
            System.out.println(totalSize);
        } else if (action == 2) {
            System.out.println("== Files by year");
            Map<Integer, Set<File>> byYear = fileSystem.byYear();
            byYear.keySet().stream().sorted()
                    .forEach(key -> {
                        System.out.printf("Year: %d\n", key);
                        Set<File> files = byYear.get(key);
                        files.stream()
                                .sorted()
                                .forEach(System.out::println);
                    });
        } else if (action == 3) {
            System.out.println("== Size by month and day");
            Map<String, Long> byMonthAndDay = fileSystem.sizeByMonthAndDay();
            byMonthAndDay.keySet().stream().sorted()
                    .forEach(key -> System.out.printf("%s -> %d\n", key, byMonthAndDay.get(key)));
        }
        scanner.close();
    }
}
