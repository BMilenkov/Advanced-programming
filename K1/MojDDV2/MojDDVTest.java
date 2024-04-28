package K1.MojDDV2;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


class AmountNotAllowedException extends Exception {
    public AmountNotAllowedException(String line) {
        super(line);
    }
}

class Product {
    private final int price;
    private final String type;

    public Product(int price, String type) {
        this.price = price;
        this.type = type;
    }

    public int getPrice() {
        return price;
    }

    public double getTax() {
        if (type.equals("A"))
            return (0.18 * price) * 0.15;
        if (type.equals("B"))
            return (0.05 * price) * 0.15;
        return 0;
    }
}


class Smetka {

    private String ID;
    private List<Product> products;


    public Smetka(String line)
            throws AmountNotAllowedException {
        String[] parts = line.split("\\s+");

        this.products = new ArrayList<>();
        this.ID = parts[0];
        for (int i = 1; i < parts.length; i += 2) {
            products.add(new Product(Integer.parseInt(parts[i]), parts[i + 1]));
        }
        if (getSumAmount() > 30000)
            throw new AmountNotAllowedException(String.format("Receipt with amount" +
                    " %d is not allowed to be scanned", getSumAmount()));
    }

    public int getSumAmount() {
        return products.stream().mapToInt(Product::getPrice).sum();
    }
    public double getTax() {
        return products.stream().mapToDouble(Product::getTax).sum();
    }
    @Override
    public String toString() {
        return String.format("%s %d %.02f", ID, getSumAmount(), getTax());
    }
}

class MojDDV {
    List<Smetka> bills;

    public MojDDV() {
        this.bills = new ArrayList<>();
    }

    public void readRecords(InputStream inputStream) {

//        BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream));
//        bills = bf.lines()
//                .map(line -> {
//                    try {
//                        return new Smetka(line);
//                    } catch (AmountNotAllowedException e) {
//                        System.out.println(e.getMessage());
//                        return null;
//                    }
//                })
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());

        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNextLine()) {
            try {
                bills.add(new Smetka(scanner.nextLine()));
            } catch (AmountNotAllowedException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void printTaxReturns(OutputStream outputStream) {

        PrintWriter pw = new PrintWriter(outputStream);
        bills.forEach(pw::println);
        pw.flush();
    }
}


public class MojDDVTest {
    public static void main(String[] args) {

        MojDDV mojDDV = new MojDDV();

        System.out.println("===READING RECORDS FROM INPUT STREAM===");
        mojDDV.readRecords(System.in);

        System.out.println("===PRINTING TAX RETURNS RECORDS TO OUTPUT STREAM ===");
        mojDDV.printTaxReturns(System.out);

    }
}