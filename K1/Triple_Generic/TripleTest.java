package K1.Triple_Generic;


import java.text.DecimalFormat;
import java.util.Scanner;

class Triple<T extends Number> {

    T firstNum;
    T secondNum;
    T thirdNum;

    public Triple(T firstNum, T secondNum, T thirdNum) {
        this.firstNum = firstNum;
        this.secondNum = secondNum;
        this.thirdNum = thirdNum;
    }

    public double max() {

        double max = firstNum.doubleValue();

        if (secondNum.doubleValue() > max)
            max = secondNum.doubleValue();

        if (thirdNum.doubleValue() > max)
            max = thirdNum.doubleValue();

        return max;
    }

    public double avarage() {

        return (firstNum.doubleValue() + secondNum.doubleValue()
                + thirdNum.doubleValue()) / 3;

    }

    public void sort() {

        if (firstNum.doubleValue() > secondNum.doubleValue()) {
            T temp = firstNum;
            firstNum = secondNum;
            secondNum = temp;
        }

        if (secondNum.doubleValue() > thirdNum.doubleValue()) {
            T temp = thirdNum;
            thirdNum = secondNum;
            secondNum = temp;
        }
        if (firstNum.doubleValue() > thirdNum.doubleValue()) {
            T temp = firstNum;
            firstNum = thirdNum;
            thirdNum = temp;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        DecimalFormat dm = new DecimalFormat("0.00");
        sort();
        sb.append(dm.format(firstNum.doubleValue())).append(" ").append(dm.format(secondNum.doubleValue())).append(" ").append(dm.format(thirdNum.doubleValue()));
        return sb.toString();
    }
}

public class TripleTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int a = scanner.nextInt();
        int b = scanner.nextInt();
        int c = scanner.nextInt();
        Triple<Integer> tInt = new Triple<Integer>(a, b, c);
        System.out.printf("%.2f\n", tInt.max());
        System.out.printf("%.2f\n", tInt.avarage());
        tInt.sort();
        System.out.println(tInt);
        float fa = scanner.nextFloat();
        float fb = scanner.nextFloat();
        float fc = scanner.nextFloat();
        Triple<Float> tFloat = new Triple<Float>(fa, fb, fc);
        System.out.printf("%.2f\n", tFloat.max());
        System.out.printf("%.2f\n", tFloat.avarage());
        tFloat.sort();
        System.out.println(tFloat);
        double da = scanner.nextDouble();
        double db = scanner.nextDouble();
        double dc = scanner.nextDouble();
        Triple<Double> tDouble = new Triple<Double>(da, db, dc);
        System.out.printf("%.2f\n", tDouble.max());
        System.out.printf("%.2f\n", tDouble.avarage());
        tDouble.sort();
        System.out.println(tDouble);
    }
}



