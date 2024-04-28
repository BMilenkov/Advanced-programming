package K2.Canvas;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;


class InvalidIDException extends Exception {
    public InvalidIDException(String line) {
        super(line);
    }
}

class InvalidDimensionException extends Exception {
    public InvalidDimensionException(String line) {
        super(line);
    }
}

abstract class Shape {
    private final String id;

    public Shape(String creator) throws InvalidIDException {
        if (!checkID(creator))
            throw new InvalidIDException(String.format("ID %s is not valid", creator));
        this.id = creator;
    }

    public boolean checkID(String id) {
        return id.length() == 6 && id.matches("[a-zA-Z0-9]+");
    }

    public String getId() {
        return id;
    }

    public abstract double getPerimeter();

    public abstract double getArea();

    public abstract void scale(double coef);
}

class Circle extends Shape {
    double radius;

    public Circle(String creator, double radius) throws InvalidIDException,
            InvalidDimensionException {
        super(creator);
        if (radius == 0)
            throw new InvalidDimensionException("Dimension 0 is not allowed!");
        this.radius = radius;
    }

    @Override
    public double getPerimeter() {
        return 2 * radius * Math.PI;
    }

    @Override
    public double getArea() {
        return Math.pow(radius, 2) * Math.PI;
    }

    @Override
    public void scale(double coef) {
        radius *= coef;
    }

    @Override
    public String toString() {
        return String.format("Circle -> Radius: %.02f Area: %.02f Perimeter: %.02f"
                , radius, getArea(), getPerimeter());
    }
}

class Square extends Shape {
    double side;

    public Square(String creator, double side) throws InvalidIDException,
            InvalidDimensionException {
        super(creator);
        if (side == 0)
            throw new InvalidDimensionException("Dimension 0 is not allowed!");
        this.side = side;
    }

    @Override
    public double getPerimeter() {
        return 4 * side;
    }

    @Override
    public double getArea() {
        return Math.pow(side, 2);
    }

    @Override
    public void scale(double coef) {
        side *= coef;
    }

    @Override
    public String toString() {
        return String.format("Square: -> Side: %.02f Area: %.02f Perimeter: %.02f"
                , side, getArea(), getPerimeter());
    }
}

class Rectangle extends Shape {
    double length;
    double height;

    public Rectangle(String creator, double length, double height) throws InvalidIDException,
            InvalidDimensionException {
        super(creator);
        if (length == 0 || height == 0)
            throw new InvalidDimensionException("Dimension 0 is not allowed!");
        this.length = length;
        this.height = height;
    }

    @Override
    public double getPerimeter() {
        return 2 * (length + height);
    }

    @Override
    public double getArea() {
        return length * height;
    }

    @Override
    public void scale(double coef) {
        length *= coef;
        height *= coef;
    }

    @Override
    public String toString() {
        return String.format("Rectangle: -> Sides: %.02f, %.02f Area: %.02f Perimeter: %.02f"
                , length, height, getArea(), getPerimeter());
    }
}

class ShapeFactory {
    static Shape createShape(String line) throws InvalidDimensionException, InvalidIDException {
        String[] parts = line.split("\\s+");
        if (parts[0].equals("1"))
            return new Circle(parts[1], Double.parseDouble(parts[2]));
        else if (parts[0].equals("2"))
            return new Square(parts[1], Double.parseDouble(parts[2]));
        return new Rectangle(parts[1], Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
    }
}

class Canvas {
    private Set<Shape> shapes;

    Comparator<Shape> comparator = Comparator.comparing(Shape::getArea);
    Comparator<Map.Entry<String, Set<Shape>>> com = Comparator.comparingInt((Map.Entry<String, Set<Shape>> e)
            -> e.getValue().size()).reversed().thenComparingDouble(e -> e.getValue().stream().mapToDouble(Shape::getArea).sum());


    public Canvas() {
        this.shapes = new TreeSet<>(comparator);
    }

    public void readShapes(InputStream in) {
        BufferedReader bf = new BufferedReader(new InputStreamReader(in));
        AtomicBoolean stop = new AtomicBoolean(false);
        this.shapes = bf.lines()
                .map(line -> {
                    if (stop.get()) {
                        return null;
                    }
                    try {
                        return ShapeFactory.createShape(line);
                    } catch (InvalidIDException e) {
                        System.out.println(e.getMessage());
                        return null;
                    } catch (InvalidDimensionException e) {
                        System.out.println(e.getMessage());
                        stop.set(true);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(() -> new TreeSet<>(comparator)));
    }

    public void scaleShapes(String number, double coef) {
        shapes.stream().filter(shape -> shape.getId().equals(number)).collect(Collectors.toList())
                .forEach(shape -> shape.scale(coef));
    }

    public void printAllShapes(OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        shapes.forEach(pw::println);

        pw.flush();
    }

    public void printByUserId(OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        Map<String, Set<Shape>> shapesById = shapes.stream().collect(Collectors.groupingBy(Shape::getId, Collectors.toSet()));

        shapesById.entrySet().stream().sorted(com)
                .forEach(e ->
                {
                    pw.printf("Shapes of user: %s\n", e.getKey());
                    e.getValue().stream().sorted(Comparator.comparingDouble(Shape::getPerimeter))
                            .forEach(pw::println);
                });

        pw.flush();
    }

    public void statistics(OutputStream out) {
        PrintWriter pw = new PrintWriter(out);

        DoubleSummaryStatistics ds = shapes.stream().mapToDouble(Shape::getArea).summaryStatistics();

        pw.printf("count: %d\nsum: %.02f\nmin: %.02f\naverage: %.02f\nmax: %.02f\n"
                , ds.getCount(), ds.getSum(), ds.getMin(), ds.getAverage(), ds.getMax());
        pw.flush();
    }
}


public class CanvasTest {

    public static void main(String[] args) {
        Canvas canvas = new Canvas();

        System.out.println("READ SHAPES AND EXCEPTIONS TESTING");
        canvas.readShapes(System.in);

        System.out.println("BEFORE SCALING");
        canvas.printAllShapes(System.out);
        canvas.scaleShapes("123456", 1.5);
        System.out.println("AFTER SCALING");
        canvas.printAllShapes(System.out);

        System.out.println("PRINT BY USER ID TESTING");
        canvas.printByUserId(System.out);

        System.out.println("PRINT STATISTICS");
        canvas.statistics(System.out);
    }
}