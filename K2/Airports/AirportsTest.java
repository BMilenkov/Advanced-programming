package K2.Airports;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


class NoDirectFlightsException extends Exception {
    public NoDirectFlightsException(String message) {
        super(message);
    }
}

class Flight {

    private final String from;
    private final String to;
    private final int time;
    private final int duration;

    public Flight(String from, String to, int time, int duration) {
        this.from = from;
        this.to = to;
        this.time = time;
        this.duration = duration;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public int getTime() {
        return time;
    }

    public String duration() {
        return String.format("%dh%02dm", duration / 60, duration % 60);
    }

    public String flightTime(int time, int duration) {

        int hours = time / 60;
        int minutes = time % 60;
        LocalTime localTime = LocalTime.of(hours, minutes);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        if (localTime.plusMinutes(duration).isBefore(localTime))
            return localTime.format(formatter) + "-" + localTime.plusMinutes(duration).format(formatter) + " +1d";
        return localTime.format(formatter) + "-" + localTime.plusMinutes(duration).format(formatter);
    }

    @Override
    public String toString() {
        return String.format("%s-%s %s %s", from, to, flightTime(time, duration), duration());
    }
}

class Airport {
    private final String name;
    private final String country;
    private final String code;
    private final int passengers;

    public Airport(String name, String country, String code, int passengers) {
        this.name = name;
        this.country = country;
        this.code = code;
        this.passengers = passengers;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)\n%s\n%d", name, code, country, passengers);
    }
}

class Airports {

    private final Set<Airport> airports;
    private final Map<String, Set<Flight>> flightsFromAirport;
    private final Map<String, Set<Flight>> flightsToAirport;
    static Comparator<Flight> comparator = Comparator.comparing(Flight::getTo).thenComparing(Flight::getTime).thenComparing(Flight::duration);

    public Airports() {
        this.airports = new HashSet<>();
        this.flightsFromAirport = new HashMap<>();
        this.flightsToAirport = new HashMap<>();
    }

    public void addAirport(String name, String country, String code, int passengers) {
        airports.add(new Airport(name, country, code, passengers));
        flightsFromAirport.putIfAbsent(code, new TreeSet<>(comparator));
        flightsToAirport.putIfAbsent(code, new TreeSet<>(comparator));
    }

    public void addFlights(String from, String to, int time, int duration) {
        flightsFromAirport.putIfAbsent(from, new TreeSet<>(comparator));
        flightsToAirport.putIfAbsent(to, new TreeSet<>(comparator));
        flightsFromAirport.get(from).add(new Flight(from, to, time, duration));
        flightsToAirport.get(to).add(new Flight(from, to, time, duration));
    }

    public void showFlightsFromAirport(String from) {
        Airport airport = airports.stream().filter(a -> a.getCode().equals(from)).findAny().get();
        System.out.println(airport);
        List<Flight> flightsFrom = flightsFromAirport.get(from).stream().collect(Collectors.toList());
        for (int i = 0; i < flightsFrom.size(); i++) {
            System.out.println(String.format("%d. ", i + 1) + flightsFrom.get(i));
        }
    }

    public void showDirectFlightsFromTo(String from, String to) throws NoDirectFlightsException {
        List<Flight> directFlights = flightsFromAirport.get(from).stream().filter(f -> flightsToAirport.get(to).contains(f)).collect(Collectors.toList());
        if (directFlights.isEmpty())
            throw new NoDirectFlightsException(String.format("No flights from %s to %s", from, to));
        directFlights.forEach(System.out::println);
    }

    public void showDirectFlightsTo(String to) {
        flightsToAirport.get(to).forEach(System.out::println);
    }
}


public class AirportsTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Airports airports = new Airports();
        int n = scanner.nextInt();
        scanner.nextLine();
        String[] codes = new String[n];
        for (int i = 0; i < n; ++i) {
            String al = scanner.nextLine();
            String[] parts = al.split(";");
            airports.addAirport(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]));
            codes[i] = parts[2];
        }
        int nn = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < nn; ++i) {
            String fl = scanner.nextLine();
            String[] parts = fl.split(";");
            airports.addFlights(parts[0], parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
        }
        int f = scanner.nextInt();
        int t = scanner.nextInt();
        String from = codes[f];
        String to = codes[t];
        System.out.printf("===== FLIGHTS FROM %S =====\n", from);
        airports.showFlightsFromAirport(from);
        System.out.printf("===== DIRECT FLIGHTS FROM %S TO %S =====\n", from, to);
        try {
            airports.showDirectFlightsFromTo(from, to);
        } catch (NoDirectFlightsException e) {
            System.out.println(e.getMessage());
        }
        t += 5;
        t = t % n;
        to = codes[t];
        System.out.printf("===== DIRECT FLIGHTS TO %S =====\n", to);
        airports.showDirectFlightsTo(to);
    }
}
