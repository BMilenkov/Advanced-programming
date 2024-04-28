package K2.Cluster_Generic;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;


//One point only got id as property, and that is the synonym for the id class called Point;

abstract class Point {
    private final long id;

    Point(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public abstract double distance(Point element);

}

class Point2D extends Point {
    private final float x;
    private final float y;

    Point2D(long id, float x, float y) {
        super(id);
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    //$\sqrt{{(x1 - x2)^2} + {(y1 - y2)^2}}$
    @Override
    public double distance(Point point) {
        Point2D e = (Point2D) point;
        return Math.sqrt(Math.pow((x - e.x), 2)
                + Math.pow((y - e.y), 2));
    }

}

class Cluster<T extends Point> {

    private final Map<Long, T> clusterElements;

    public Cluster() {
        this.clusterElements = new HashMap<>();
    }

    public void addItem(T element) {
        clusterElements.put(element.getId(), element);
    }

    public void near(long id, int n) {
        T element = clusterElements.get(id);
        AtomicInteger atomicInteger = new AtomicInteger(1);

        clusterElements.values()
                .stream()
                .sorted((e1, e2) -> Double.compare(e1.distance(element), e2.distance(element)))
                .skip(1)
                .limit(n).forEach(e -> {
                            System.out.print(atomicInteger.getAndIncrement());
                            System.out.printf(". %d -> %.03f\n", e.getId(), e.distance(element));
                        }
                );
    }
}

public class ClusterTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Cluster<Point2D> cluster = new Cluster<>();
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(" ");
            long id = Long.parseLong(parts[0]);
            float x = Float.parseFloat(parts[1]);
            float y = Float.parseFloat(parts[2]);
            cluster.addItem(new Point2D(id, x, y));
        }
        int id = scanner.nextInt();
        int top = scanner.nextInt();
        cluster.near(id, top);
        scanner.close();
    }
}
