package K2.Stadium;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.IntStream;


class SeatTakenException extends Exception {
}

class SeatNotAllowedException extends Exception {
}

class Sector {
    private final String code;
    private final int capacity;
    private final Map<Integer, Boolean> taken;
    private int type;

    public Sector(String code, int capacity) {
        this.code = code;
        this.capacity = capacity;
        this.taken = new HashMap<>();
        this.type = 0;
    }

    public void addSeat(int seat) {
        taken.put(seat, true);
    }

    public boolean isTaken(int seat) {
        return taken.containsKey(seat);
    }

    public int freeSeats() {
        return capacity - taken.size();
    }

    public int getType() {
        return type;
    }

    public String getCode() {
        return code;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("%s\t%d/%d\t%.1f%%", code, freeSeats(), capacity, (capacity - freeSeats()) *
                100.0 / capacity);
    }
}

class Stadium {
    private String stadiumName;
    private final Map<String, Sector> sectors;

    Comparator<Sector> comparator =
            Comparator.comparing(Sector::freeSeats).reversed().thenComparing(Sector::getCode);

    public Stadium(String stadiumName) {
        this.stadiumName = stadiumName;
        this.sectors = new HashMap<>();
    }

    public void createSectors(String[] sectorNames, int[] sizes) {
        IntStream.range(0, sectorNames.length)
                .forEach(i -> sectors.put(sectorNames[i], new Sector(sectorNames[i], sizes[i])));
    }

    public void buyTicket(String sectorName, int seat, int type)
            throws SeatTakenException, SeatNotAllowedException {
        Sector sector = sectors.get(sectorName);

        if (sector.isTaken(seat))
            throw new SeatTakenException();

        if ((type == 1 && sector.getType() == 2) || (type == 2 && sector.getType() == 1)) {
            throw new SeatNotAllowedException();
        }
        if (type != 0 && sector.getType() == 0)
            sector.setType(type);
        sector.addSeat(seat);
    }

    public void showSectors() {
        sectors.values().stream().sorted(comparator).forEach(System.out::println);
    }
}


public class StaduimTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        String[] sectorNames = new String[n];
        int[] sectorSizes = new int[n];
        String name = scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            sectorNames[i] = parts[0];
            sectorSizes[i] = Integer.parseInt(parts[1]);
        }
        Stadium stadium = new Stadium(name);
        stadium.createSectors(sectorNames, sectorSizes);
        n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            try {
                stadium.buyTicket(parts[0], Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2]));
            } catch (SeatNotAllowedException e) {
                System.out.println("SeatNotAllowedException");
            } catch (SeatTakenException e) {
                System.out.println("SeatTakenException");
            }
        }
        stadium.showSectors();
    }
}
