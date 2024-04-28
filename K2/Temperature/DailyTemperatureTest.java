package K2.Temperature;


import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


class DailyTemperature {
    int day;
    List<Double> temps;
    String scale;

    public DailyTemperature(String line) {
        this.temps = new ArrayList<>();
        String[] parts = line.split("\\s++");
        this.day = Integer.parseInt(parts[0]);
        if (parts[1].contains("C"))
            this.scale = "C";
        else this.scale = "F";
        Arrays.stream(parts).skip(1).forEach(p -> {
            assert false;
            temps.add(Double.parseDouble(p.substring(0, p.length() - 1)));
        });
    }

    public int getDay() {
        return day;
    }

    double CelsiusToFahr(double t) {
        return t * 9 / 5 + 32;
    }

    double FahrToCelsius(double t) {
        return (t - 32) * 5 / 9;
    }


    public String toString(String scale) {
        DoubleSummaryStatistics ds;
        if (scale.equals("C") && this.scale.equals("C") || scale.equals("F") && this.scale.equals("F")) {
            ds = temps.stream().mapToDouble(i -> i).summaryStatistics();
        } else if (scale.equals("C") && this.scale.equals("F")) {
            ds = temps.stream().mapToDouble(this::FahrToCelsius).summaryStatistics();
        } else {
            ds = temps.stream().mapToDouble(this::CelsiusToFahr).summaryStatistics();
        }
        return String.format("%3d: Count: %3d Min: %6.02f%s Max: %6.02f%s Avg: %6.02f%s"
                , day, ds.getCount(), ds.getMin(), scale, ds.getMax(), scale, ds.getAverage(), scale);
    }
}


class DailyTemperatures {
    private Set<DailyTemperature> dailyTemperatures;

    public DailyTemperatures() {
        this.dailyTemperatures = new TreeSet<>(Comparator.comparing(DailyTemperature::getDay));
    }

    public void readTemperatures(InputStream in) {
        BufferedReader bf = new BufferedReader(new InputStreamReader(in));
        dailyTemperatures = bf.lines()
                .map(DailyTemperature::new)
                .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(DailyTemperature::getDay))));
    }

    public void writeDailyStats(OutputStream out, String scale) {
        PrintWriter pw = new PrintWriter(out);
        dailyTemperatures.forEach(dt -> pw.println(dt.toString(scale)));
        pw.flush();
    }

}


public class DailyTemperatureTest {
    public static void main(String[] args) {
        DailyTemperatures dailyTemperatures = new DailyTemperatures();
        dailyTemperatures.readTemperatures(System.in);
        System.out.println("=== Daily temperatures in Celsius (C) ===");
        dailyTemperatures.writeDailyStats(System.out, "C");
        System.out.println("=== Daily temperatures in Fahrenheit (F) ===");
        dailyTemperatures.writeDailyStats(System.out, "F");
    }
}

