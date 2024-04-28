package K2.StudentRecords;



import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


class STUDENT {
    private final String code;
    private final String program;
    private final List<Integer> grades;

    public STUDENT(String line) {
        this.grades = new ArrayList<>();
        String[] parts = line.split("\\s+");
        this.code = parts[0];
        this.program = parts[1];
        for (int i = 2; i < parts.length; i++) {
            grades.add(Integer.parseInt(parts[i]));
        }
    }

    public String getCode() {
        return code;
    }

    public String getProgram() {
        return program;
    }

    public double averageGrade() {
        return grades.stream().mapToDouble(g -> g).sum() / grades.size();
    }

    public int studentCountGrade(int grade) {
        return (int) grades.stream().filter(g -> g == grade).count();
    }

    @Override
    public String toString() {
        return String.format("%s %.02f", code, averageGrade());
    }
}

class StudentRecords {
    private Map<String, Set<STUDENT>> studentsByProgram;

    Comparator<STUDENT> comparator = Comparator.comparing(STUDENT::averageGrade).reversed().thenComparing(STUDENT::getCode);
    Comparator<Map.Entry<String, Set<STUDENT>>> comparator1 = (e1, e2) ->
            Long.compare(e2.getValue().stream().mapToInt(s -> s.studentCountGrade(10)).sum(),
                    e1.getValue().stream().mapToInt(s -> s.studentCountGrade(10)).sum());

    public StudentRecords() {
        this.studentsByProgram = new TreeMap<>();
    }

    // ioqmx7 MT 10 8 10 8 10 7 6 9 9 9 6 8 6 6 9 9 8
    public int readRecords(InputStream in) {
        BufferedReader bf = new BufferedReader(new InputStreamReader(in));

        this.studentsByProgram = bf.lines()
                .map(STUDENT::new)
                .collect(Collectors.groupingBy(STUDENT::getProgram, Collectors.toCollection(() -> new TreeSet<>(comparator))));

        return studentsByProgram.values().stream().mapToInt(Set::size).sum();
    }

    public void writeTable(OutputStream out) {
        PrintWriter pw = new PrintWriter(out);

        studentsByProgram.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .forEach(e -> {
                    pw.println(e.getKey());
                    e.getValue().forEach(pw::println);
                });

        pw.flush();
    }

    public void writeDistribution(PrintStream out) {
        PrintWriter pw = new PrintWriter(out);
        studentsByProgram.entrySet().stream().sorted(comparator1)
                .forEach(entry -> {
                    pw.println(entry.getKey());
                    for (int i = 6; i <= 10; i++) {
                        int finalI = i;
                        int gradeCount = entry.getValue().stream().mapToInt(student -> student.studentCountGrade(finalI)).sum();
                        int stars = (int) Math.ceil((double) gradeCount / 10);
                        pw.printf("%2d | ", i);
                        IntStream.range(0, stars).forEach(a -> pw.print("*"));
                        pw.printf("(%d)\n", gradeCount);
                    }
                });
        pw.flush();
    }
}

public class StudentRecordsTest {
    public static void main(String[] args) {
        System.out.println("=== READING RECORDS ===");
        StudentRecords studentRecords = new StudentRecords();
        int total = studentRecords.readRecords(System.in);
        System.out.printf("Total records: %d\n", total);
        System.out.println("=== WRITING TABLE ===");
        studentRecords.writeTable(System.out);
        System.out.println("=== WRITING DISTRIBUTION ===");
        studentRecords.writeDistribution(System.out);
    }
}

