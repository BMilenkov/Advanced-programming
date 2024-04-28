package K1.MojDDV1;

import java.io.*;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

class AmountNotAllowedException extends Exception {
    public AmountNotAllowedException(String line) {
        super(line);
    }
}

class Product {

    private String type;
    private int price;

    public Product(int price, String type) {
        this.type = type;
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public double getCashBack() {
        if (type.equals("A"))
            return (0.18 * getPrice()) * 0.15;

        if (type.equals("B"))
            return (0.05 * getPrice()) * 0.15;

        return 0;
    }
}

class Smetka {
    private List<Product> products;
    private String ID;


    public Smetka(String line)
            throws AmountNotAllowedException {
        String[] parts = line.split("\\s+");
        this.ID = parts[0];

        this.products = new ArrayList<>();
        for (int i = 1; i < parts.length; i += 2) {
            products.add(new Product(Integer.parseInt(parts[i]), parts[i + 1]));
        }
        if (billAmount() > 30000) {
            throw new AmountNotAllowedException
                    (String.format("Receipt with amount %d is not allowed to be scanned", billAmount()));
        }
    }


    public int billAmount() {
        return products.stream().mapToInt(Product::getPrice).sum();
    }

    public double taxReturn() {
        return products.stream().mapToDouble(Product::getCashBack).sum();
    }

    @Override
    public String toString() {
        return String.format("%10s\t%10d\t%10.5f", ID, billAmount(), taxReturn());
    }
}

class MojDDV {

    List<Smetka> bills;

    public MojDDV() {
        this.bills = new ArrayList<>();
    }

    public void readRecords(InputStream inputStream) {

        BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream));

        bills = bf.lines().
                map(line -> {
                    try {
                        return new Smetka(line);
                    } catch (AmountNotAllowedException e) {
                        System.out.println(e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void printTaxReturns(OutputStream outputStream) {

        PrintWriter pw = new PrintWriter(outputStream);
        for (Smetka s : bills) {
            pw.println(s);
        }
        pw.flush();
    }

    public String getStatistics() {

        DoubleSummaryStatistics dss = bills.stream()
                .mapToDouble(Smetka::taxReturn).summaryStatistics();
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("min:\t%.03f\n", dss.getMin()))
                .append(String.format("max:\t%.03f\n", dss.getMax()))
                .append(String.format("sum:\t%.03f\n", dss.getSum()))
                .append(String.format("count:\t%-5d\n", (int) dss.getCount()))
                .append(String.format("avg:\t%.03f\n", dss.getAverage()));

        return sb.toString();
    }

    public void printStatistics(OutputStream outputStream) {

        PrintWriter pw = new PrintWriter(outputStream);
        pw.println(getStatistics());
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

        System.out.println("===PRINTING SUMMARY STATISTICS FOR TAX RETURNS TO OUTPUT STREAM===");
        mojDDV.printStatistics(System.out);

    }
}