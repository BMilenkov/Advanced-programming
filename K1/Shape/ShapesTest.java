package K1.Shape;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

enum Color {
    RED, GREEN, BLUE
}

interface Stackable {
    double weight();

}

interface Scalable {
    void scale(double scaleFactor);
}


abstract class Shape implements Scalable, Stackable {
    private String id;
    private Color color;

    public Shape(String id, Color color) {
        this.id = id;
        this.color = color;
    }

    public abstract double weight();

    public abstract void scale(double scaleFactor);

    public String getId() {
        return id;
    }

    public Color getColor() {
        return color;
    }
}

class Circle extends Shape {
    private double radius;

    public Circle(String id, Color color, double radius) {
        super(id, color);
        this.radius = radius;
    }

    @Override
    public void scale(double scaleFactor) {
        radius *= scaleFactor;
    }

    @Override
    public double weight() {
        return radius * radius * Math.PI;
    }

    public String toString() {
        return String.format("C: %-5s%-10s%10.2f\n", getId(), getColor(), weight());
    }

}

class Rectangle extends Shape {

    private double width;
    private double height;

    public Rectangle(String id, Color color, double width, double height) {
        super(id, color);
        this.width = width;
        this.height = height;
    }

    @Override
    public double weight() {
        return width * height;
    }

    @Override
    public void scale(double scaleFactor) {
        width *= scaleFactor;
        height *= scaleFactor;
    }

    public String toString() {
        return String.format("R: %-5s%-10s%10.2f\n", getId(), getColor(), weight());
    }
}

class Canvas {

    List<Shape> shapes;

    public Canvas() {
        this.shapes = new ArrayList<Shape>();
    }

    void add(String id, Color color, double radius) {
        Shape s = new Circle(id, color, radius);
        if (shapes.size() == 0) {
            shapes.add(s);
            return;
        } else {
            for (int i = 0; i < shapes.size(); i++) {
                if (s.weight() > shapes.get(i).weight()) {
                    shapes.add(i, s);
                    return;
                }
            }
        }
        shapes.add(s);
    }

    void add(String id, Color color, double width, double height) {
        Shape s = new Rectangle(id, color, width, height);
        if (shapes.size() == 0) {
            shapes.add(s);
            return;
        } else {
            for (int i = 0; i < shapes.size(); i++) {
                if (s.weight() > shapes.get(i).weight()) {
                    shapes.add(i, s);
                    return;
                }
            }
        }
        shapes.add(s);
    }

    Optional<Shape> findAny(String id) {
        return shapes.stream()
                .filter(shape -> shape.getId().equals(id))
                .findAny();
    }

    void scale(String id, double scaleFactor) {
        Optional<Shape> shape = findAny(id);

        Shape s = shape.get();
        s.scale(scaleFactor);
        shapes.remove(s);

        for (int i = 0; i < shapes.size(); i++) {
            if (s.weight() > shapes.get(i).weight()) {
                shapes.add(i, s);
                return;
            }
        }
        shapes.add(s);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Shape shape : shapes) {
            sb.append(shape.toString());
        }
        return sb.toString();
    }
}

public class ShapesTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Canvas canvas = new Canvas();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(" ");
            int type = Integer.parseInt(parts[0]);
            String id = parts[1];
            if (type == 1) {
                Color color = Color.valueOf(parts[2]);
                float radius = Float.parseFloat(parts[3]);
                canvas.add(id, color, radius);
            } else if (type == 2) {
                Color color = Color.valueOf(parts[2]);
                float width = Float.parseFloat(parts[3]);
                float height = Float.parseFloat(parts[4]);
                canvas.add(id, color, width, height);
            } else if (type == 3) {
                float scaleFactor = Float.parseFloat(parts[2]);
                System.out.println("ORIGNAL:");
                System.out.print(canvas);
                canvas.scale(id, scaleFactor);
                System.out.printf("AFTER SCALING: %s %.2f\n", id, scaleFactor);
                System.out.print(canvas);
            }

        }
    }
}