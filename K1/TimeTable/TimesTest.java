package K1.TimeTable;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


class UnsupportedFormatException extends Exception {
    public UnsupportedFormatException(String line) {
        super(line);
    }
}

class InvalidTimeException extends Exception {
    public InvalidTimeException(String line) {
        super(line);
    }
}

class Time implements Comparable<Time> {
    private int hour;
    private int minutes;

    public Time(String line)
            throws UnsupportedFormatException, InvalidTimeException {
        String[] parts = line.split("\\.");
        if (parts.length == 1) {
            parts = line.split(":");
        }
        if (parts.length == 1)
            throw new UnsupportedFormatException(line);

        int HOUR = Integer.parseInt(parts[0]);
        int MINUTES = Integer.parseInt(parts[1]);

        if (HOUR < 0 || HOUR > 23 || MINUTES < 0 || MINUTES > 59)
            throw new InvalidTimeException(line);

        this.hour = Integer.parseInt(parts[0]);
        this.minutes = Integer.parseInt(parts[1]);

    }

    @Override
    public int compareTo(Time o) {
        if (hour == o.hour)
            return Integer.compare(minutes, o.minutes);
        return Integer.compare(hour, o.hour);
    }

    @Override
    public String toString() {
        return String.format("%2d:%02d", hour, minutes);
    }

    public String converted() {
        if (hour == 0)
            return String.format("%2d:%02d AM", hour + 12, minutes);
        if (hour >= 1 && hour <= 11)
            return String.format("%2d:%02d AM", hour, minutes);
        if (hour == 12)
            return String.format("%2d:%02d PM", hour, minutes);
        if (hour >= 13 && hour <= 23)
            return String.format("%2d:%02d PM", hour - 12, minutes);
        return null;
    }
}

class TimeTable {

    private List<Time> times;

    public TimeTable() {
        times = new ArrayList<>();
    }


    public void readTimes(InputStream inputStream)
            throws InvalidTimeException, UnsupportedFormatException {

        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] parts = line.split("\\s+");
            for (String part : parts) {
                times.add(new Time(part));
            }
        }
    }

    public void writeTimes(OutputStream outputStream, TimeFormat format) {
        PrintWriter pw = new PrintWriter(outputStream);

        if (format.equals(TimeFormat.FORMAT_24))
            times.stream().sorted().forEach(pw::println);
        else
            times.stream().sorted().forEach(time -> pw.println(time.converted()));

        pw.flush();
    }
}


public class TimesTest {

    public static void main(String[] args) {
        TimeTable timeTable = new TimeTable();
        try {
            timeTable.readTimes(System.in);
        } catch (UnsupportedFormatException e) {
            System.out.println("UnsupportedFormatException: " + e.getMessage());
        } catch (InvalidTimeException e) {
            System.out.println("InvalidTimeException: " + e.getMessage());
        }
        System.out.println("24 HOUR FORMAT");
        timeTable.writeTimes(System.out, TimeFormat.FORMAT_24);
        System.out.println("AM/PM FORMAT");
        timeTable.writeTimes(System.out, TimeFormat.FORMAT_AMPM);
    }

}

enum TimeFormat {
    FORMAT_24, FORMAT_AMPM
}