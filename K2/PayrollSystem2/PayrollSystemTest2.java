package K2.PayrollSystem2;

import java.util.*;
import java.util.stream.Collectors;


class BonusNotAllowedException extends Exception {
    public BonusNotAllowedException(String line) {
        super(line);
    }
}


abstract class Employee {
    private final String type;
    private final String name;
    private final String level;


    public Employee(String line) {
        String[] infoBonusPart = line.split("\\s+");
        String[] infoPart = infoBonusPart[0].split(";");
        this.type = infoPart[0];
        this.name = infoPart[1];
        this.level = infoPart[2];
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getLevel() {
        return level;
    }

    public abstract double salary();

    public abstract double overtimeSalary();

    public abstract int points();

    public abstract double getBonus();

}

class HourlyEmployee extends Employee {
    private final double hours;
    private double bonus;

    // HourlyEmployee: H;ID;level;hours + bonus;
    public HourlyEmployee(String line)
            throws BonusNotAllowedException {
        super(line);
        String[] infoBonus = line.split("\\s++");
        String[] info = infoBonus[0].split(";");
        this.hours = Double.parseDouble(info[info.length - 1]);
        if (infoBonus.length > 1) {
            setBonus(infoBonus[1]);
        } else this.bonus = 0;
    }

    public void setBonus(String bonus)
            throws BonusNotAllowedException {
        if (bonus.contains("%")) {
            double Bonus = Double.parseDouble(bonus.replace("%", ""));
            if (Bonus > 20)
                throw new BonusNotAllowedException(String.format("Bonus of %s is not allowed", bonus));
            this.bonus = Bonus / 100 * salary();
        } else {
            double Bonus = Double.parseDouble(bonus);
            if (Bonus > 1000)
                throw new BonusNotAllowedException(String.format("Bonus of %s$ is not allowed", bonus));
            this.bonus = Bonus;
        }
    }

    public double getBonus() {
        return bonus;
    }

    @Override
    public double salary() {
        if (hours <= 40)
            return hours * PayrollSystem.getHourlyRateByLevel().get(getLevel()) + bonus;
        return 40 * PayrollSystem.getHourlyRateByLevel().get(getLevel()) + (hours - 40) * (PayrollSystem.getHourlyRateByLevel().get(getLevel()) * 1.5) + bonus;
    }

    @Override
    public double overtimeSalary() {
        if (hours <= 40)
            return 0;
        return (hours - 40) * (PayrollSystem.getHourlyRateByLevel().get(getLevel()) * 1.5);
    }

    @Override
    public int points() {
        return 0;
    }

    @Override
    public String toString() {
        if (hours > 40)
            return String.format("Employee ID: %s Level: %s Salary: %.02f Regular hours: %.02f Overtime hours: %.02f Bonus: %.02f"
                    , getName(), getLevel(), salary(), 40.00, hours - 40, bonus);
        return String.format("Employee ID: %s Level: %s Salary: %.02f Regular hours: %.02f Overtime hours: %.02f Bonus: %.02f"
                , getName(), getLevel(), salary(), hours, 0.00, bonus);
    }
}

class FreelanceEmployee extends Employee {
    private final List<Integer> ticketPoints;
    private double bonus;

    //F;ID;level;ticketPoints1;ticketPoints2;...;ticketPointsN + bonus;
    public FreelanceEmployee(String line)
            throws BonusNotAllowedException {
        super(line);
        this.ticketPoints = new ArrayList<>();
        String[] infoBonus = line.split("\\s+");
        String[] info = infoBonus[0].split(";");
        for (int i = 3; i < info.length; i++)
            ticketPoints.add(Integer.parseInt(info[i]));
        if (infoBonus.length > 1) {
            setBonus(infoBonus[1]);
        } else this.bonus = 0;
    }

    public void setBonus(String bonus)
            throws BonusNotAllowedException {
        if (bonus.contains("%")) {
            double Bonus = Double.parseDouble(bonus.replace("%", ""));
            if (Bonus > 20)
                throw new BonusNotAllowedException(String.format("Bonus of %s is not allowed", bonus));
            this.bonus = Bonus / 100 * salary();
        } else {
            double Bonus = Double.parseDouble(bonus);
            if (Bonus > 1000)
                throw new BonusNotAllowedException(String.format("Bonus of %s$ is not allowed", bonus));
            this.bonus = Bonus;
        }
    }

    public double getBonus() {
        return bonus;
    }

    @Override
    public double salary() {
        return ticketPoints.stream().mapToInt(i -> i).sum()
                * PayrollSystem.getTicketRateByLevel().get(getLevel()) + bonus;
    }

    public int points() {
        return (int) ticketPoints.stream().mapToInt(i -> i).count();
    }

    @Override
    public double overtimeSalary() {
        return -1;
    }


    @Override
    public String toString() {
        if (bonus == 0)
            return String.format("Employee ID: %s Level: %s Salary: %.02f Tickets count: %d Tickets points: %d"
                    , getName(), getLevel(), salary(), ticketPoints.size(), ticketPoints.stream().mapToInt(i -> i).sum());
        return String.format("Employee ID: %s Level: %s Salary: %.02f Tickets count: %d Tickets points: %d Bonus: %.02f"
                , getName(), getLevel(), salary(), ticketPoints.size(), ticketPoints.stream().mapToInt(i -> i).sum(), bonus);
    }
}

class PayrollSystem {

    private final Set<Employee> employees;
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

    public Employee createEmployee(String line) throws BonusNotAllowedException {
        if (line.split(";")[0].equals("H")) {
            employees.add(new HourlyEmployee(line));
            return new HourlyEmployee(line);
        }
        employees.add(new FreelanceEmployee(line));
        return new FreelanceEmployee(line);
    }


    public Map<String, Double> getOvertimeSalaryForLevels() {
        Map<String, Double> overtimeByLevel = employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getLevel,
                        Collectors.summingDouble(Employee::overtimeSalary)
                ));

        overtimeByLevel.entrySet().removeIf(entry -> entry.getValue().equals(-1.0));
        return overtimeByLevel;
    }



    //TODO
    //Statistics for overtime salary: Min: 0.00 Average: 408.55 Max: 1735.47 Sum: 7762.52
    public void printStatisticsForOvertimeSalary() {
        DoubleSummaryStatistics ds = employees.stream().filter(employee -> employee instanceof HourlyEmployee).mapToDouble(Employee::overtimeSalary).summaryStatistics();
        System.out.printf("Statistics for overtime salary: Min: %.02f Average: %.02f Max: %.02f Sum: %.02f%n"
                , ds.getMin(), ds.getAverage(), ds.getMax(), ds.getSum());
    }

    public Map<String, Integer> ticketsDoneByLevel() {
        return employees.stream().filter(employee -> employee instanceof FreelanceEmployee)
                .collect(Collectors.groupingBy(Employee::getLevel, Collectors.summingInt(Employee::points)));
    }

    public Collection<Employee> getFirstNEmployeesByBonus(int n) {
        return employees.stream().sorted(Comparator.comparing(Employee::getBonus).reversed()).limit(n).collect(Collectors.toList());
    }
}


public class PayrollSystemTest2 {

    public static void main(String[] args) {

        Map<String, Double> hourlyRateByLevel = new LinkedHashMap<>();
        Map<String, Double> ticketRateByLevel = new LinkedHashMap<>();
        for (int i = 1; i <= 10; i++) {
            hourlyRateByLevel.put("level" + i, 11 + i * 2.2);
            ticketRateByLevel.put("level" + i, 5.5 + i * 2.5);
        }

        Scanner sc = new Scanner(System.in);

        int employeesCount = Integer.parseInt(sc.nextLine());

        PayrollSystem ps = new PayrollSystem(hourlyRateByLevel, ticketRateByLevel);
        Employee emp = null;
        for (int i = 0; i < employeesCount; i++) {
            try {
                emp = ps.createEmployee(sc.nextLine());
            } catch (BonusNotAllowedException e) {
                System.out.println(e.getMessage());
            }
        }

        int testCase = Integer.parseInt(sc.nextLine());

        switch (testCase) {
            case 1: //Testing createEmployee
                if (emp != null)
                    System.out.println(emp);
                break;
            case 2: //Testing getOvertimeSalaryForLevels()
                ps.getOvertimeSalaryForLevels().forEach((level, overtimeSalary) -> {
                    System.out.printf("Level: %s Overtime salary: %.2f\n", level, overtimeSalary);
                });
                break;
            case 3: //Testing printStatisticsForOvertimeSalary()
                ps.printStatisticsForOvertimeSalary();
                break;
            case 4: //Testing ticketsDoneByLevel
                ps.ticketsDoneByLevel().forEach((level, overtimeSalary) -> {
                    System.out.printf("Level: %s Tickets by level: %d\n", level, overtimeSalary);
                });
                break;
            case 5: //Testing getFirstNEmployeesByBonus (int n)
                ps.getFirstNEmployeesByBonus(Integer.parseInt(sc.nextLine())).forEach(System.out::println);
                break;
        }

    }
}