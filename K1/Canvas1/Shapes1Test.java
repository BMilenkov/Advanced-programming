package K1.Canvas1;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


class Square {

    private int side;
    public Square(int side) {
        this.side = side;
    }
    public int perimetar() {
        return side * 4;
    }

}

class Canvas implements Comparable<Canvas> {

    private String id;
    private List<Square> squares;

    public Canvas(String line) {
        squares = new ArrayList<>();
        String[] parts = line.split("\\s+");
        this.id = parts[0];
        for (int i = 1; i < parts.length; i++) {
            squares.add(new Square(Integer.parseInt(parts[i])));
        }
    }
    public int getSquaresSize(){
        return squares.size();
    }
    public int totalSquaresPerimetar() {
        return squares.stream().mapToInt(Square::perimetar).sum();
    }
    @Override
    public String toString() {
        return id + " " + squares.size() + " " + totalSquaresPerimetar();
    }

    @Override
    public int compareTo(Canvas o) {
        return Integer.compare(totalSquaresPerimetar(), o.totalSquaresPerimetar());
    }
}

class ShapesApplication {

    private List<Canvas> canvases;

    public ShapesApplication() {
        this.canvases = new ArrayList<>();
    }

    public int readCanvases(InputStream inputStream) {
        BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream));

        canvases = bf.lines()
                .map(Canvas::new)
                .collect(Collectors.toList());

        return canvases.stream().mapToInt(Canvas::getSquaresSize).sum();
    }

    void printLargestCanvasTo(OutputStream outputStream) {
        PrintWriter pw = new PrintWriter(outputStream);
        pw.println(canvases.stream().max(Comparator.naturalOrder()).get());
        pw.flush();
    }
}


public class Shapes1Test {

    public static void main(String[] args) {
        ShapesApplication shapesApplication = new ShapesApplication();

        System.out.println("===READING SQUARES FROM INPUT STREAM===");
        System.out.println(shapesApplication.readCanvases(System.in));
        System.out.println("===PRINTING LARGEST CANVAS TO OUTPUT STREAM===");
        shapesApplication.printLargestCanvasTo(System.out);

    }
}