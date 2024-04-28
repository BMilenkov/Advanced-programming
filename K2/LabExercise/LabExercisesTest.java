package K2.LabExercise;


import java.util.*;
import java.util.stream.Collectors;


class Student {
    private final String index;
    private final List<Integer> points;

    public Student(String index, List<Integer> points) {
        this.index = index;
        this.points = points;
    }

    public String getIndex() {
        return index;
    }

    public double AveragePointsFromLabs() {
        return points.stream().mapToDouble(i -> i).sum() / 10;
    }

    public boolean hasSignature() {
        return points.size() >= 8;
    }

    public Integer getYearStudy() {
        return (20 - Integer.parseInt(index.substring(0, 2)));
    }

    @Override
    public String toString() {
        return String.format("%s %s %.02f", index, hasSignature() ? "YES" : "NO", AveragePointsFromLabs());

    }
}

class LabExercises {
    private final Set<Student> students;

    Comparator<Student> c1 = Comparator.comparing(Student::AveragePointsFromLabs).thenComparing(Student::getIndex);
    Comparator<Student> c2 = Comparator.comparing(Student::getIndex).thenComparing(Student::AveragePointsFromLabs);

    public LabExercises() {
        this.students = new HashSet<>();
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public void printByAveragePoints(boolean ascending, int n) {
        if (ascending)
            students.stream().sorted(c1).limit(n).forEach(System.out::println);
        else students.stream().sorted(c1.reversed()).limit(n).forEach(System.out::println);
    }

    public List<Student> failedStudents() {
        return students.stream().filter(student -> !student.hasSignature()).sorted(c2).collect(Collectors.toList());
    }

    public Map<Integer, Double> getStatisticsByYear() {

        return students.stream().filter(Student::hasSignature)
                .collect(Collectors.groupingBy(Student::getYearStudy, Collectors.averagingDouble(Student::AveragePointsFromLabs)));
    }
}


public class LabExercisesTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        LabExercises labExercises = new LabExercises();
        while (sc.hasNextLine()) {
            String input = sc.nextLine();
            String[] parts = input.split("\\s+");
            String index = parts[0];
            List<Integer> points = Arrays.stream(parts).skip(1)
                    .mapToInt(Integer::parseInt)
                    .boxed()
                    .collect(Collectors.toList());

            labExercises.addStudent(new Student(index, points));
        }

        System.out.println("===printByAveragePoints (ascending)===");
        labExercises.printByAveragePoints(true, 100);
        System.out.println("===printByAveragePoints (descending)===");
        labExercises.printByAveragePoints(false, 100);
        System.out.println("===failed students===");
        labExercises.failedStudents().forEach(System.out::println);
        System.out.println("===statistics by year");
        labExercises.getStatisticsByYear().entrySet().stream()
                .map(entry -> String.format("%d : %.2f", entry.getKey(), entry.getValue()))
                .forEach(System.out::println);

    }
}