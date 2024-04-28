package K1.WeatherStation;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


class Measurment implements Comparable<Measurment> {

    private double temp;
    private double hum;
    private double wind;
    private double visibility;
    private Date date;

    public Measurment() {}

    public Measurment(double temp, double hum, double wind, double visibility, Date date) {
        this.temp = temp;
        this.hum = hum;
        this.wind = wind;
        this.visibility = visibility;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return String.format("%.1f %.1f km/h %.1f%% %.1f km ",
                temp, hum, wind, visibility) + df.format(date);
    }


    @Override
    public int compareTo(Measurment o) {
        return this.date.compareTo(o.date);
    }

    public double getTemp() {
        return temp;
    }
}

class WeatherStation {

    private int Days;
    private List<Measurment> measurments;


    public WeatherStation(int days) {
        Days = days;
        this.measurments = new ArrayList<>();
    }

    public void addMeasurment(float temperature, float wind, float humidity,
                              float visibility, Date date) {


        Measurment measurment = new Measurment(temperature, wind, humidity, visibility, date);

        if (measurments.isEmpty()) {
            measurments.add(measurment);
            return;
        }

        if (Math.abs(measurments.get(measurments.size() - 1).getDate().getTime()
                - measurment.getDate().getTime()) < 2.5 * 60 * 1000)
            return;
        measurments.add(measurment);


        List<Measurment> toRemove = measurments.stream()
                .filter(mes -> (Math.abs(mes.getDate().getTime() -
                        measurment.getDate().getTime())) > (long) getDays() * 24 * 60 * 60 * 1000)
                .collect(Collectors.toList());

        measurments.removeAll(toRemove);
    }

    public void status(Date from, Date to)
            throws RuntimeException {

        List<Measurment> ms = measurments.stream()
                .filter(measurment -> (measurment.getDate().after(from) || measurment.getDate().equals(from))
                        && (measurment.getDate().before(to) || measurment.getDate().equals(to)))
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());

        if (ms.isEmpty())
            throw new RuntimeException();

        ms.forEach(System.out::println);
        System.out.printf("Average temperature: %.02f",
                ms.stream().mapToDouble(Measurment::getTemp).average().getAsDouble());
    }

    public int getDays() {
        return Days;
    }

    public int total() {
        return measurments.size();
    }
}

public class WeatherStationTest {
    public static void main(String[] args) throws ParseException {
        Scanner scanner = new Scanner(System.in);
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        int n = scanner.nextInt();
        scanner.nextLine();
        WeatherStation ws = new WeatherStation(n);
        while (true) {
            String line = scanner.nextLine();
            if (line.equals("=====")) {
                break;
            }
            String[] parts = line.split(" ");
            float temp = Float.parseFloat(parts[0]);
            float wind = Float.parseFloat(parts[1]);
            float hum = Float.parseFloat(parts[2]);
            float vis = Float.parseFloat(parts[3]);
            line = scanner.nextLine();
            Date date = df.parse(line);

            ws.addMeasurment(temp, wind, hum, vis, date);
        }
        String line = scanner.nextLine();
        Date from = df.parse(line);
        line = scanner.nextLine();
        Date to = df.parse(line);
        scanner.close();
        System.out.println(ws.total());
        try {
            ws.status(from, to);
        } catch (RuntimeException e) {
            System.out.println(e);
        }
    }
}