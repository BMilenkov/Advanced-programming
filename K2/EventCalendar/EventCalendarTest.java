package K2.EventCalendar;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.IntStream;


class WrongDateException extends Exception {
    public WrongDateException(String line) {
        super(line);
    }
}

class Event {
    private final String name;
    private final String location;
    private final Date date;

    public Event(String name, String location, Date date) {
        this.name = name;
        this.location = location;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public int getMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        DateFormat dateFormat = new SimpleDateFormat("dd MMM, yyy HH:mm");
        return String.format("%s at %s, %s", dateFormat.format(date), location, name);
    }
}


class EventCalendar {
    int year;
    Map<String, Set<Event>> events;

    public EventCalendar(int year) {
        this.year = year;
        this.events = new HashMap<>();
    }

    Comparator<Event> comparator = Comparator.comparing(Event::getDate)
            .thenComparing(Event::getName);

    public void addEvent(String name, String location, Date date)
            throws WrongDateException {

        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        if (calendar.get(Calendar.YEAR) != this.year)
            throw new WrongDateException(String.format("Wrong date: %s", date));
        events.computeIfAbsent(df.format(date), k -> new TreeSet<>(comparator)).add(new Event(name, location, date));
    }

    public void listEvents(Date date) {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        if (events.getOrDefault(df.format(date), Collections.emptySet()).isEmpty())
            System.out.println("No events on this day!");
        else
            events.get(df.format(date)).forEach(System.out::println);
    }

    public void listByMonth() {
        IntStream.range(1, 13).forEach(i ->
                System.out.printf("%d : %d\n", i, events.values().stream().flatMap(Collection::stream)
                        .mapToInt(Event::getMonth).filter(e -> e == i).count()));
    }
}


public class EventCalendarTest {
    public static void main(String[] args) throws ParseException {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        int year = scanner.nextInt();
        scanner.nextLine();
        EventCalendar eventCalendar = new EventCalendar(year);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            String name = parts[0];
            String location = parts[1];
            Date date = df.parse(parts[2]);
            try {
                eventCalendar.addEvent(name, location, date);
            } catch (WrongDateException e) {
                System.out.println(e.getMessage());
            }
        }
        Date date = df.parse(scanner.nextLine());
        eventCalendar.listEvents(date);
        eventCalendar.listByMonth();
    }
}