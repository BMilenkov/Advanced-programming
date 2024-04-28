package K2.Faculty;

import java.util.*;
import java.util.stream.IntStream;

class OperationNotAllowedException extends Exception {
    public OperationNotAllowedException(String message) {
        super(message);
    }
}

class Course {
    private final String courseName;
    private final List<Integer> grades;

    public Course(String courseName) {
        this.courseName = courseName;
        this.grades = new ArrayList<>();
    }

    public void addGrade(int grade) {
        grades.add(grade);
    }

    public double getCourseAverageGrade() {
        return grades.stream().mapToDouble(g -> g).average().orElse(0);
    }

    public int listeners() {
        return grades.size();
    }

    public String getCourseName() {
        return courseName;
    }

    @Override
    public String toString() {
        return String.format("%s %d %.02f", courseName, listeners(), getCourseAverageGrade());
    }
}

class Student {
    private final String id;
    private final int yearsOfStudies;
    private final Map<Integer, List<Integer>> gradesByTerm;
    private final Set<String> courses;

    public Student(String id, int yearsOfStudies) {
        this.id = id;
        this.yearsOfStudies = yearsOfStudies;
        this.courses = new TreeSet<>();
        this.gradesByTerm = new TreeMap<>();
        IntStream.range(1, yearsOfStudies * 2 + 1).forEach(i -> gradesByTerm.put(i, new ArrayList<>()));
    }

    public int passedExams() {
        return gradesByTerm.values().stream().mapToInt(List::size).sum();
    }

    public boolean hasGraduated() {
        return passedExams() == 18 && yearsOfStudies == 3 || passedExams() == 24 && yearsOfStudies == 4;
    }

    public boolean addMyGrade(int term, String courseName, int grade)
            throws OperationNotAllowedException {
        if (!gradesByTerm.containsKey(term))
            throw new OperationNotAllowedException(String.format("Term %d is not possible for student with ID %s", term, id));
        if (gradesByTerm.get(term).size() == 3)
            throw new OperationNotAllowedException(String.format("Student %s already has 3 grades in term %d", id, term));

        gradesByTerm.get(term).add(grade);
        courses.add(courseName);
        return hasGraduated();
    }

    public double getAverageGrade() {
        return gradesByTerm.values().stream().flatMap(Collection::stream)
                .mapToInt(i -> i).average().orElse(5.00);
    }

    public double getAverageGradeForTerm(int term) {
        return gradesByTerm.get(term).stream().mapToInt(i -> i).average().orElse(5.00);
    }

    public int getYearsOfStudies() {
        return yearsOfStudies;
    }

    public String getId() {
        return id;
    }

    public String shortReport() {
        return String.format("Student: %s Courses passed: %d Average grade: %.02f"
                , id, passedExams(), getAverageGrade());
    }

    public String termReport(int term) {
        return String.format("Term %d\nCourses: %d\nAverage grade for term: %.02f\n"
                , term, gradesByTerm.get(term).size(), getAverageGradeForTerm(term));
    }

    public String detailedReport() {
        StringBuilder sb = new StringBuilder(String.format("Student: %s\n", id));
        gradesByTerm.keySet().forEach(t -> sb.append(termReport(t)));
        sb.append(String.format("Average grade: %.02f\nCourses attended: %s"
                , getAverageGrade(), String.join(",", courses)));

        return sb.toString();
    }
}


class Faculty {
    private final Map<String, Student> students;
    private final Map<String, Course> courses;
    private final List<String> logs;


    public Faculty() {
        this.students = new HashMap<>();
        this.courses = new HashMap<>();
        this.logs = new ArrayList<>();
    }

    void addStudent(String id, int yearsOfStudies) {
        students.put(id, new Student(id, yearsOfStudies));
    }

    void addGradeToStudent(String studentId, int term, String courseName, int grade) throws OperationNotAllowedException {
        boolean hasGraduated = students.get(studentId).addMyGrade(term, courseName, grade);
        courses.computeIfAbsent(courseName, k -> new Course(courseName)).addGrade(grade);

        if (hasGraduated) {
            logs.add(String.format("Student with ID %s graduated with " +
                            "average grade %.02f in %d years.", studentId,
                    students.get(studentId).getAverageGrade(),
                    students.get(studentId).getYearsOfStudies()));
            students.remove(studentId);
        }
    }

    String getFacultyLogs() {
        return String.join("\n", logs);
    }

    String getDetailedReportForStudent(String id) {
        return students.get(id).detailedReport();
    }

    void printFirstNStudents(int n) {
        Comparator<Student> comparator = Comparator.comparing(Student::passedExams)
                .thenComparing(Student::getAverageGrade).thenComparing(Student::getId).reversed();

        TreeSet<Student> sortedStudents = new TreeSet<>(comparator);
        sortedStudents.addAll(students.values());
        sortedStudents.stream().limit(n)
                .forEach(s -> System.out.println(s.shortReport()));
    }

    void printCourses() {
        Comparator<Course> comparator = Comparator.comparing(Course::listeners).thenComparing(Course::getCourseAverageGrade)
                .thenComparing(Course::getCourseName);

        TreeSet<Course> Courses = new TreeSet<>(comparator);
        Courses.addAll(courses.values());
        Courses.forEach(course -> System.out.println(course.toString()));
    }
}

public class FacultyTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int testCase = sc.nextInt();

        if (testCase == 1) {
            System.out.println("TESTING addStudent AND printFirstNStudents");
            Faculty faculty = new Faculty();
            for (int i = 0; i < 10; i++) {
                faculty.addStudent("student" + i, (i % 2 == 0) ? 3 : 4);
            }
            faculty.printFirstNStudents(10);

        } else if (testCase == 2) {
            System.out.println("TESTING addGrade and exception");
            Faculty faculty = new Faculty();
            faculty.addStudent("123", 3);
            faculty.addStudent("1234", 4);
            try {
                faculty.addGradeToStudent("123", 7, "NP", 10);
            } catch (OperationNotAllowedException e) {
                System.out.println(e.getMessage());
            }
            try {
                faculty.addGradeToStudent("1234", 9, "NP", 8);
            } catch (OperationNotAllowedException e) {
                System.out.println(e.getMessage());
            }
        } else if (testCase == 3) {
            System.out.println("TESTING addGrade and exception");
            Faculty faculty = new Faculty();
            faculty.addStudent("123", 3);
            faculty.addStudent("1234", 4);
            for (int i = 0; i < 4; i++) {
                try {
                    faculty.addGradeToStudent("123", 1, "course" + i, 10);
                } catch (OperationNotAllowedException e) {
                    System.out.println(e.getMessage());
                }
            }
            for (int i = 0; i < 4; i++) {
                try {
                    faculty.addGradeToStudent("1234", 1, "course" + i, 10);
                } catch (OperationNotAllowedException e) {
                    System.out.println(e.getMessage());
                }
            }
        } else if (testCase == 4) {
            System.out.println("Testing addGrade for graduation");
            Faculty faculty = new Faculty();
            faculty.addStudent("123", 3);
            faculty.addStudent("1234", 4);
            int counter = 1;
            for (int i = 1; i <= 6; i++) {
                for (int j = 1; j <= 3; j++) {
                    try {
                        faculty.addGradeToStudent("123", i, "course" + counter, (i % 2 == 0) ? 7 : 8);
                    } catch (OperationNotAllowedException e) {
                        System.out.println(e.getMessage());
                    }
                    ++counter;
                }
            }
            counter = 1;
            for (int i = 1; i <= 8; i++) {
                for (int j = 1; j <= 3; j++) {
                    try {
                        faculty.addGradeToStudent("1234", i, "course" + counter, (j % 2 == 0) ? 7 : 10);
                    } catch (OperationNotAllowedException e) {
                        System.out.println(e.getMessage());
                    }
                    ++counter;
                }
            }
            System.out.println("LOGS");
            System.out.println(faculty.getFacultyLogs());
            System.out.println("PRINT STUDENTS (there shouldn't be anything after this line!");
            faculty.printFirstNStudents(2);
        } else if (testCase == 5 || testCase == 6 || testCase == 7) {
            System.out.println("Testing addGrade and printFirstNStudents (not graduated student)");
            Faculty faculty = new Faculty();
            for (int i = 1; i <= 10; i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j < ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= ((j % 2 == 1) ? 3 : 2); k++) {
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), i % 5 + 6);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }
            }
            if (testCase == 5)
                faculty.printFirstNStudents(10);
            else if (testCase == 6)
                faculty.printFirstNStudents(3);
            else
                faculty.printFirstNStudents(20);
        } else if (testCase == 8 || testCase == 9) {
            System.out.println("TESTING DETAILED REPORT");
            Faculty faculty = new Faculty();
            faculty.addStudent("student1", ((testCase == 8) ? 3 : 4));
            int grade = 6;
            int counterCounter = 1;
            for (int i = 1; i < ((testCase == 8) ? 6 : 8); i++) {
                for (int j = 1; j < 3; j++) {
                    try {
                        faculty.addGradeToStudent("student1", i, "course" + counterCounter, grade);
                    } catch (OperationNotAllowedException e) {
                        e.printStackTrace();
                    }
                    grade++;
                    if (grade == 10)
                        grade = 5;
                    ++counterCounter;
                }
            }
            System.out.println(faculty.getDetailedReportForStudent("student1"));
        } else if (testCase == 10) {
            System.out.println("TESTING PRINT COURSES");
            Faculty faculty = new Faculty();
            for (int i = 1; i <= 10; i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j < ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= ((j % 2 == 1) ? 3 : 2); k++) {
                        int grade = sc.nextInt();
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), grade);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }
            }
            faculty.printCourses();
        } else if (testCase == 11) {
            System.out.println("INTEGRATION TEST");
            Faculty faculty = new Faculty();
            for (int i = 1; i <= 10; i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j <= ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= ((j % 2 == 1) ? 2 : 3); k++) {
                        int grade = sc.nextInt();
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), grade);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }

            }

            for (int i = 11; i < 15; i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j <= ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= 3; k++) {
                        int grade = sc.nextInt();
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), grade);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }
            }
            System.out.println("LOGS");
            System.out.println(faculty.getFacultyLogs());
            System.out.println("DETAILED REPORT FOR STUDENT");
            System.out.println(faculty.getDetailedReportForStudent("student2"));
            try {
                System.out.println(faculty.getDetailedReportForStudent("student11"));
                System.out.println("The graduated students should be deleted!!!");
            } catch (NullPointerException e) {
                System.out.println("The graduated students are really deleted");
            }
            System.out.println("FIRST N STUDENTS");
            faculty.printFirstNStudents(10);
            System.out.println("COURSES");
            faculty.printCourses();
        }
    }
}
