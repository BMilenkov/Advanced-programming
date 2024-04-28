package K1.ShoppingCart;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


class InvalidOperationException extends Exception {
    public InvalidOperationException(String line) {
        super(line);
    }
}

abstract class Item implements Comparable<Item> {
    private String type;
    private String ID;
    private String name;
    private double price;

    public Item(String type, String ID, String name, double price) {
        this.type = type;
        this.ID = ID;
        this.name = name;
        this.price = price;

    }

    public String getID() {
        return ID;
    }

    public double getPrice() {
        return price;
    }

    public abstract double getQuantity();

    public abstract double totalPrice();
}

class WS extends Item {

    private int quantity;


    public WS(String type, String ID, String name, double price, int quantity) {
        super(type, ID, name, price);
        this.quantity = quantity;
    }


    @Override
    public double getQuantity() {
        return quantity;
    }

    @Override
    public double totalPrice() {
        return getPrice() * quantity;
    }

    @Override
    public int compareTo(Item o) {
        return Double.compare(totalPrice(), o.totalPrice());
    }

    @Override
    public String toString() {
        return String.format("%s - %.02f", getID(), totalPrice());
    }
}

class PS extends Item {

    double quantity;

    public PS(String type, String ID, String name, double price, double quantity) {
        super(type, ID, name, price);
        this.quantity = quantity;
    }

    @Override
    public double getQuantity() {
        return quantity;
    }

    @Override
    public double totalPrice() {
        return getPrice() * (quantity / 1000);
    }

    @Override
    public int compareTo(Item o) {
        return Double.compare(totalPrice(), o.totalPrice());
    }

    public String toString() {
        return String.format("%s - %.02f", getID(), totalPrice());
    }

}


class ShoppingCart {

    private List<Item> items;

    public ShoppingCart() {
        this.items = new ArrayList<>();
    }

    public void addItem(String itemData)
            throws InvalidOperationException {
        String[] parts = itemData.split(";");
        Item item = null;
        if (parts[0].equals("WS")) {
            item = new WS(parts[0], parts[1], parts[2],
                    Double.parseDouble(parts[3]), Integer.parseInt(parts[4]));
        } else {
            item = new PS(parts[0], parts[1], parts[2], Double.parseDouble(parts[3]),
                    Double.parseDouble(parts[4]));
        }
        if (item.getQuantity() == 0)
            throw new InvalidOperationException(String.format("The quantity of the product" +
                    " with id %s can not be 0.", parts[1]));
        items.add(item);
    }

    public void blackFridayOffer(List<Integer> discountItems, OutputStream os)
            throws InvalidOperationException {

        PrintWriter printWriter = new PrintWriter(os);

        List<Item> DiscountItems = items.stream()
                .filter(item -> discountItems
                        .contains(Integer.parseInt(item.getID())))
                .collect(Collectors.toList());

        if (DiscountItems.isEmpty())
            throw new InvalidOperationException("There are no products with discount.");

        for (Item da : DiscountItems) {
            printWriter.println(String.format("%s - %.02f",
                    da.getID(), da.totalPrice() - da.totalPrice() * 0.9));
        }
        printWriter.flush();
    }

    public void printShoppingCart(OutputStream os) {
        PrintWriter printWriter = new PrintWriter(os);

        items.stream().sorted(Comparator.reverseOrder()).forEach(printWriter::println);

        printWriter.flush();
    }

}


public class ShoppingTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ShoppingCart cart = new ShoppingCart();
        boolean exceptionOcuured = false;
        int items = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < items; i++) {
            try {
                cart.addItem(sc.nextLine());
            } catch (InvalidOperationException e) {
                System.out.println(e.getMessage());
                exceptionOcuured = true;
            }
        }

        List<Integer> discountItems = new ArrayList<>();
        int discountItemsCount = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < discountItemsCount; i++) {
            discountItems.add(Integer.parseInt(sc.nextLine()));
        }

        int testCase = Integer.parseInt(sc.nextLine());

        if (testCase == 1) {
            cart.printShoppingCart(System.out);
        } else if (testCase == 2) {
            try {
                cart.blackFridayOffer(discountItems, System.out);
            } catch (InvalidOperationException e) {
                if (exceptionOcuured)
                    return;
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Invalid test case");
        }
    }
}