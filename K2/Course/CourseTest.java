package K2.Course;

//package mk.ukim.finki.midterm;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


class Student {

    private final String index;
    private final String name;
    private int firstTerm;
    private int secondTerm;
    private int labs;

    public Student(String index, String name) {
        this.index = index;
        this.name = name;
        this.firstTerm = 0;
        this.secondTerm = 0;
        this.labs = 0;
    }

    public void addPoints(int points, String activity) {
        if (points < 0)
            throw new RuntimeException();
        if (activity.equals("midterm1"))
            this.firstTerm = points;
        else if (activity.equals("midterm2"))
            this.secondTerm = points;
        else if (activity.equals("labs"))
            this.labs = points;
    }

    public double getPoints() {
        return firstTerm * 0.45 + secondTerm * 0.45 + labs;
    }

    public int getGrade() {
        if (getPoints() >= 50)
            return (int) (getPoints() / 10) + 1;
        else return 5;
    }

    public String getID() {
        return index;
    }

    @Override
    public String toString() {
        return String.format("ID: %s Name: %s First midterm: %d" +
                        " Second midterm %d Labs: %d Summary points: %.02f Grade: %d",
                index, name, firstTerm, secondTerm, labs, getPoints(), getGrade());
    }
}

class AdvancedProgrammingCourse {
    private final Map<String, Student> students;

    public AdvancedProgrammingCourse() {
        this.students = new HashMap<>();
    }

    public void addStudent(Student student) {
        students.put(student.getID(), student);
    }

    public void updateStudent(String idNumber, String activity, int points) {
        students.get(idNumber).addPoints(points, activity);
    }

    public List<Student> getFirstNStudents(int n) {
        return students.values().stream().sorted(Comparator.comparing(Student::getPoints).reversed())
                .limit(n).collect(Collectors.toList());
    }

    public Map<Integer, Integer> getGradeDistribution() {
        Map<Integer, Integer> GDM = new HashMap<>();
        IntStream.range(5, 11)
                .forEach(i -> GDM.put(i, (int) students.values().stream()
                        .filter(student -> student.getGrade()== i)
                        .count()));
        return GDM;

//        return students.values().stream()
//                .collect(Collectors.groupingBy(Student::getGrade, TreeMap::new,
//                        Collectors.collectingAndThen(
//                                Collectors.counting(),
//                                Long::intValue)
//                ));
    }

    public void printStatistics() {
        DoubleSummaryStatistics ds = students.values()
                .stream().filter(s -> s.getPoints() >= 50)
                .mapToDouble(Student::getPoints).summaryStatistics();

        System.out.printf("Count: %d Min: %.02f Average: %.02f Max: %.02f%n",
                ds.getCount(), ds.getMin(), ds.getAverage(), ds.getMax());
    }
}


public class CourseTest {

    public static void printStudents(List<Student> students) {
        students.forEach(System.out::println);
    }

    public static void printMap(Map<Integer, Integer> map) {
        map.forEach((k, v) -> System.out.printf("%d -> %d%n", k, v));
    }

    public static void main(String[] args) {
        AdvancedProgrammingCourse advancedProgrammingCourse = new AdvancedProgrammingCourse();

        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");

            String command = parts[0];

            if (command.equals("addStudent")) {
                String id = parts[1];
                String name = parts[2];
                advancedProgrammingCourse.addStudent(new Student(id, name));
            } else if (command.equals("updateStudent")) {
                String idNumber = parts[1];
                String activity = parts[2];
                int points = Integer.parseInt(parts[3]);
                advancedProgrammingCourse.updateStudent(idNumber, activity, points);
            } else if (command.equals("getFirstNStudents")) {
                int n = Integer.parseInt(parts[1]);
                printStudents(advancedProgrammingCourse.getFirstNStudents(n));
            } else if (command.equals("getGradeDistribution")) {
                printMap(advancedProgrammingCourse.getGradeDistribution());
            } else {
                advancedProgrammingCourse.printStatistics();
            }
        }
    }
}
