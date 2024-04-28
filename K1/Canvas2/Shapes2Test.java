package K1.Canvas2;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


class IrregularCanvasException extends Exception {
    public IrregularCanvasException(String line) {
        super(line);
    }
}

abstract class Shape {

    private String type;

    public Shape(String type) {
        this.type = type;
    }

    public abstract double getArea();
}

class Square extends Shape {

    private int side;

    public Square(String type, int side) {
        super(type);
        this.side = side;
    }


    @Override
    public double getArea() {
        return side * side;
    }
}

class Circle extends Shape {
    private final int radius;

    public Circle(String type, int radius) {
        super(type);
        this.radius = radius;
    }


    @Override
    public double getArea() {
        return Math.PI * radius * radius;
    }
}


class Canvas implements Comparable<Canvas> {
    private String ID;
    List<Shape> shapes;
    double maxArea;


    public Canvas(String line, double maxArea)
            throws IrregularCanvasException {

        this.shapes = new ArrayList<>();
        this.maxArea = maxArea;
        String[] parts = line.split("\\s+");
        this.ID = parts[0];
        Shape shape = null;

        for (int i = 1; i < parts.length; i += 2) {
            if (parts[i].equals("S"))
                shape = new Square("S", Integer.parseInt(parts[i + 1]));
            else
                shape = new Circle("C", Integer.parseInt(parts[i + 1]));
            try {
                addShape(shape);
            } catch (IrregularCanvasException e) {
                throw new IrregularCanvasException(String.format("Canvas %s has a shape " +
                        "with area larger than %.2f", ID, maxArea));
            }
        }
    }
    public void addShape(Shape shape)
            throws IrregularCanvasException {
        if (shape.getArea() > maxArea)
            throw new IrregularCanvasException(String.format("Canvas %s has a shape " +
                    "with area larger than %.2f", ID, maxArea));
        shapes.add(shape);
    }

    public double Sum() {
        return shapes.stream().mapToDouble(Shape::getArea).sum();
    }

    @Override
    public int compareTo(Canvas o) {
        return Double.compare(Sum(), o.Sum());
    }

    public int tottalCircles() {
        return (int) shapes.stream().filter(shape -> shape instanceof Circle).count();
    }

    public int tottalSquares() {
        return (int) shapes.stream().filter(shape -> shape instanceof Square).count();
    }

    @Override
    public String toString() {

        DoubleSummaryStatistics dss = shapes.stream()
                .mapToDouble(Shape::getArea)
                .summaryStatistics();
        return String.format("%s %d %d %d% .02f %.02f %.02f", ID,
                shapes.size(), tottalCircles(), tottalSquares(),
                dss.getMin(), dss.getMax(), dss.getAverage());
    }
}


class ShapesApplication {
    private List<Canvas> canvases;
    private final double maxArea;

    public ShapesApplication(double maxArea) {
        this.maxArea = maxArea;
        canvases = new ArrayList<>();
    }

    public void readCanvases(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        canvases = bufferedReader.lines()
                .map(line -> {
                    try {
                        return new Canvas(line, maxArea);
                    } catch (IrregularCanvasException e) {
                        System.out.println(e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

    }

    public void printCanvases(OutputStream os) {
        PrintWriter pw = new PrintWriter(os);

        canvases = canvases.stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        for (Canvas c : canvases) {
            pw.println(c);
        }
        pw.flush();
    }

}


public class Shapes2Test {

    public static void main(String[] args) {

        ShapesApplication shapesApplication = new ShapesApplication(10000);

        System.out.println("===READING CANVASES AND SHAPES FROM INPUT STREAM===");
        shapesApplication.readCanvases(System.in);

        System.out.println("===PRINTING SORTED CANVASES TO OUTPUT STREAM===");
        shapesApplication.printCanvases(System.out);


    }
}