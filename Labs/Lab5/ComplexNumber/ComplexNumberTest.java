package Labs.Lab5.ComplexNumber;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Scanner;


class ComplexNumber<T extends Number, U extends Number>
        implements Comparable<ComplexNumber<?, ?>> {

    private T realPart;
    private U imaginaryPart;

    public ComplexNumber(T realPart, U imaginaryPart) {
        this.realPart = realPart;
        this.imaginaryPart = imaginaryPart;
    }

    public T getRealPart() {
        return realPart;
    }

    public U getImaginaryPart() {
        return imaginaryPart;
    }

    public double modul() {
        return Math.sqrt(Math.pow(realPart.doubleValue(), 2) +
                Math.pow(imaginaryPart.doubleValue(), 2));
    }

    @Override
    public int compareTo(ComplexNumber<?, ?> o) {
        return Double.compare(modul(), o.modul());
    }

    // 2.30+3.00i


    @Override
    public String toString() {
        if(Double.parseDouble(String.valueOf(imaginaryPart)) >= 0)
            return String.format("%.02f+%.02fi", Double.parseDouble(String.valueOf(realPart)), Double.parseDouble(String.valueOf(imaginaryPart)));
        else
            return String.format("%.02f%.02fi", Double.parseDouble(String.valueOf(realPart)), Double.parseDouble(String.valueOf(imaginaryPart)));
    }
}


public class ComplexNumberTest {

    public static void main(String[] args) {
        Scanner jin = new Scanner(System.in);
        int k = jin.nextInt();
        if (k == 0) { //test simple functions int
            int r = jin.nextInt();
            int i = jin.nextInt();
            ComplexNumber<Integer, Integer> c = new ComplexNumber<Integer, Integer>(r, i);
            System.out.println(c);
            System.out.println(c.getRealPart());
            System.out.println(c.getImaginaryPart());
            System.out.println(c.modul());
        }
        if (k == 1) { //test simple functions float
            float r = jin.nextFloat();
            float i = jin.nextFloat();
            ComplexNumber<Float, Float> c = new ComplexNumber<Float, Float>(r, i);
            System.out.println(c);
            System.out.println(c.getRealPart());
            System.out.println(c.getImaginaryPart());
            System.out.println(c.modul());
        }
        if (k == 2) { //compareTo int
            LinkedList<ComplexNumber<Integer, Integer>> complex = new LinkedList<ComplexNumber<Integer, Integer>>();
            while (jin.hasNextInt()) {
                int r = jin.nextInt();
                int i = jin.nextInt();
                complex.add(new ComplexNumber<Integer, Integer>(r, i));
            }
            System.out.println(complex);
            Collections.sort(complex);
            System.out.println(complex);
        }
        if (k == 3) { //compareTo double
            LinkedList<ComplexNumber<Double, Double>> complex = new LinkedList<ComplexNumber<Double, Double>>();
            while (jin.hasNextDouble()) {
                double r = jin.nextDouble();
                double i = jin.nextDouble();
                complex.add(new ComplexNumber<Double, Double>(r, i));
            }
            System.out.println(complex);
            Collections.sort(complex);
            System.out.println(complex);
        }
        if (k == 4) { //compareTo mixed
            LinkedList<ComplexNumber<Double, Integer>> complex = new LinkedList<ComplexNumber<Double, Integer>>();
            while (jin.hasNextDouble()) {
                double r = jin.nextDouble();
                int i = jin.nextInt();
                complex.add(new ComplexNumber<Double, Integer>(r, i));
            }
            System.out.println(complex);
            Collections.sort(complex);
            System.out.println(complex);
        }
    }
}