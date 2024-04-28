package K1.MinMax_Generic;


import java.util.Scanner;


class MinMax<T extends Comparable<T>> {

    T min;
    T max;
    int total;
    int minCount;
    int maxCount;

    public MinMax() {
        this.total = 0;
        maxCount = 0;
        minCount = 0;
    }

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }

    void update(T element) {
        if (total == 0) {
            this.min = element;
            this.max = element;
        }
        ++total;
        if (element.compareTo(min) < 0) {
            min = element;
            minCount = 1;
        } else {
            if (element.compareTo(min) == 0) {
                minCount++;
            }
        }
        if (element.compareTo(max) > 0) {
            max = element;
            maxCount = 1;
        } else {
            if (element.compareTo(max) == 0)
                maxCount++;
        }
    }

    @Override
    public String toString() {
        int result = total-(maxCount+minCount);
        return min + " " + max + " " + result + " \n";
    }
}

public class MinAndMax {
    public static void main(String[] args) throws ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        MinMax<String> strings = new MinMax<>();
        for (int i = 0; i < n; ++i) {
            String s = scanner.next();
            strings.update(s);
        }
        System.out.println(strings);
        MinMax<Integer> ints = new MinMax<>();
        for (int i = 0; i < n; ++i) {
            int x = scanner.nextInt();
            ints.update(x);
        }
        System.out.println(ints);
    }
}