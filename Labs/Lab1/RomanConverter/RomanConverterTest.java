package Labs.Lab1.RomanConverter;


import java.util.Scanner;
import java.util.stream.IntStream;

public class RomanConverterTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        IntStream.range(0, n)
                .forEach(x -> System.out.println(RomanConverter.toRoman(scanner.nextInt())));
        scanner.close();
    }
}

class RomanConverter {

    public static String toRoman(int n) {
        // your solution here

        StringBuilder sb = new StringBuilder();
        String[] thousands = {"", "M"};
        String[] hundreds = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
        String[] tens = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
        String[] ones = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};

        int thousand = n / 1000;

        for (int i = 0; i <thousand; i++) {
            sb.append("M");
        }
        sb.append(hundreds[n / 100 % 10]).append(tens[n / 10 % 10]).append(ones[n % 10]);

        return sb.toString();
    }

}