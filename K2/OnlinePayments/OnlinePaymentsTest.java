package K2.OnlinePayments;


import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


class StudentNotFoundException extends Exception {
    public StudentNotFoundException(String line) {
        super(line);
    }
}

class Item {
    private final String index;
    private final String itemName;
    private final int price;

    public Item(String index, String itemName, int price) {
        this.index = index;
        this.itemName = itemName;
        this.price = price;
    }

    public Item(String line) {
        String[] parts = line.split(";");
        this.index = parts[0];
        this.itemName = parts[1];
        this.price = Integer.parseInt(parts[2]);
    }

    public String getIndex() {
        return index;
    }

    public int getPrice() {
        return price;
    }

    public String getItemName() {
        return itemName;
    }

    @Override
    public String toString() {
        return itemName + " " + price;
    }
}


class OnlinePayments {
    private Map<String, Set<Item>> studentItems;

    Comparator<Item> comparator = Comparator.comparing(Item::getPrice).thenComparing(Item::getItemName).reversed();

    public OnlinePayments() {
        this.studentItems = new HashMap<>();
    }

    public void readItems(InputStream in) {

        this.studentItems = new BufferedReader(new InputStreamReader(in))
                .lines()
                .map(Item::new)
                .collect(Collectors.groupingBy(Item::getIndex,
                        Collectors.toCollection(() -> new TreeSet<>(comparator))));
    }

    public void printStudentReport(String id, PrintStream out)
            throws StudentNotFoundException {
        PrintWriter pw = new PrintWriter(out);
        AtomicInteger integer = new AtomicInteger(1);

        if (!studentItems.containsKey(id))
            throw new StudentNotFoundException(String.format("Student %s not found!", id));

        pw.printf("Student: %s Net: %d Fee: %d Total: %d\nItems:\n", id, sum(id), provision(id), sum(id) + provision(id));

        studentItems.get(id).forEach(i ->
                pw.printf("%d. %s\n", integer.getAndIncrement(), i)
        );

        pw.flush();
    }

    private int sum(String id) {
        return studentItems.get(id).stream().mapToInt(Item::getPrice).sum();
    }

    private int provision(String id) {
        if (Math.round(sum(id) * 0.0114) < 3)
            return 3;
        else if (Math.round(sum(id) * 0.0114) > 300) {
            return 300;
        }
        return (int) Math.round(sum(id) * 0.0114);
    }
}


public class OnlinePaymentsTest {
    public static void main(String[] args) {
        OnlinePayments onlinePayments = new OnlinePayments();

        onlinePayments.readItems(System.in);

        IntStream.range(151020, 151025).mapToObj(String::valueOf).forEach(id -> {
            try {
                onlinePayments.printStudentReport(id, System.out);
            } catch (StudentNotFoundException e) {
                System.out.println(e.getMessage());
            }
        });
    }
}