package K2.PayrollSystem1;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;


abstract class Employee {
    private final String type;
    private final String id;
    private final String level;

    public Employee(String line) {
        String[] parts = line.split(";");
        this.type = parts[0];
        this.id = parts[1];
        this.level = parts[2];
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getLevel() {
        return level;
    }

    public abstract double salary();
}

class HourlyEmployee extends Employee {
    private final double hours;

    // HourlyEmployee: H;ID;level;hours;
    public HourlyEmployee(String line) {
        super(line);
        String[] parts = line.split(";");
        this.hours = Double.parseDouble(parts[3]);
    }

    @Override
    public double salary() {
        if (hours <= 40)
            return hours * PayrollSystem.getHourlyRateByLevel().get(getLevel());
        return 40 * PayrollSystem.getHourlyRateByLevel().get(getLevel()) + (hours - 40) * (PayrollSystem.getHourlyRateByLevel().get(getLevel()) * 1.5);
    }

    @Override
    public String toString() {
        if (hours > 40)
            return String.format("Employee ID: %s Level: %s Salary: %.02f Regular hours: %.02f Overtime hours: %.02f"
                    , getId(), getLevel(), salary(), 40.00, hours - 40);
        return String.format("Employee ID: %s Level: %s Salary: %.02f Regular hours: %.02f Overtime hours: %.02f"
                , getId(), getLevel(), salary(), hours, 0.00);
    }
}

class FreelanceEmployee extends Employee {
    private final List<Integer> ticketPoints;

    //F;ID;level;ticketPoints1;ticketPoints2;...;ticketPointsN;
    public FreelanceEmployee(String line) {
        super(line);
        this.ticketPoints = new ArrayList<>();
        String[] parts = line.split(";");
        for (int i = 3; i < parts.length; i++) {
            ticketPoints.add(Integer.parseInt(parts[i]));
        }
    }

    @Override
    public double salary() {
        return ticketPoints.stream().mapToInt(i -> i).sum()
                * PayrollSystem.getTicketRateByLevel().get(getLevel());
    }

    @Override
    public String toString() {
        return String.format("Employee ID: %s Level: %s Salary: %.02f Tickets count: %d Tickets points: %d"
                , getId(), getLevel(), salary(), ticketPoints.size(), ticketPoints.stream().mapToInt(i -> i).sum());
    }
}

class PayrollSystem {

    private Set<Employee> employees;
    private static Map<String, Double> hourlyRateByLevel;
    private static Map<String, Double> ticketRateByLevel;

    public Comparator<Employee> comparator = Comparator.comparing(Employee::salary).thenComparing(Employee::getLevel).reversed();

    public PayrollSystem(Map<String, Double> hourlyRateByLevel, Map<String, Double> ticketRateByLevel) {
        this.employees = new HashSet<>();
        PayrollSystem.hourlyRateByLevel = hourlyRateByLevel;
        PayrollSystem.ticketRateByLevel = ticketRateByLevel;
    }

    public static Map<String, Double> getHourlyRateByLevel() {
        return hourlyRateByLevel;
    }

    public static Map<String, Double> getTicketRateByLevel() {
        return ticketRateByLevel;
    }

    public void readEmployees(InputStream in) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

        this.employees = bufferedReader.lines().map(line -> {
            if (line.split(";")[0].equals("F"))
                return new FreelanceEmployee(line);
            else return new HourlyEmployee(line);
        }).collect(Collectors.toSet());
    }

    public Map<String, Set<Employee>> printEmployeesByLevels(PrintStream out, Set<String> levels) {

        return employees.stream()
                .filter(employee -> levels.contains(employee.getLevel()))
                .collect(Collectors.groupingBy(Employee::getLevel,
                        TreeMap::new,
                        Collectors.toCollection(() -> new TreeSet<>(comparator))));

    }
}


public class PayrollSystemTest {

    public static void main(String[] args) {

        Map<String, Double> hourlyRateByLevel = new LinkedHashMap<>();
        Map<String, Double> ticketRateByLevel = new LinkedHashMap<>();
        for (int i = 1; i <= 10; i++) {
            hourlyRateByLevel.put("level" + i, 10 + i * 2.2);
            ticketRateByLevel.put("level" + i, 5 + i * 2.5);
        }

        PayrollSystem payrollSystem = new PayrollSystem(hourlyRateByLevel, ticketRateByLevel);

        System.out.println("READING OF THE EMPLOYEES DATA");
        payrollSystem.readEmployees(System.in);

        System.out.println("PRINTING EMPLOYEES BY LEVEL");
        Set<String> levels = new LinkedHashSet<>();
        for (int i = 5; i <= 10; i++) {
            levels.add("level" + i);
        }
        Map<String, Set<Employee>> result = payrollSystem.printEmployeesByLevels(System.out, levels);
        result.forEach((level, employees) -> {
            System.out.println("LEVEL: " + level);
            System.out.println("Employees: ");
            employees.forEach(System.out::println);
            System.out.println("------------");
        });


    }
}