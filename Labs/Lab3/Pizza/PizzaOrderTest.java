package Labs.Lab3.Pizza;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


class InvalidExtraTypeException extends Exception {
}

class InvalidPizzaTypeException extends Exception {
}

class ItemOutOfStockException extends Exception {
    private Item item;

    public ItemOutOfStockException(Item item) {
        super();
        this.item = item;
    }
}

class EmptyOrder extends Exception {
}

class OrderLockedException extends Exception {
}

class ArrayIndexOutOfBoundsException extends Exception {
    private int index;

    public ArrayIndexOutOfBoundsException(int index) {
        super();
        this.index = index;
    }
}


interface Item {
    int getPrice();

    String getType();
}

class ExtraItem implements Item {
    //Coke,Ketchup
    private String type;
    private int price;

    public ExtraItem(String type)
            throws InvalidExtraTypeException {
        if (!type.equals("Coke") && !type.equals("Ketchup"))
            throw new InvalidExtraTypeException();
        this.type = type;
        if (type.equals("Coke"))
            this.price = 5;
        else this.price = 3;
    }

    @Override
    public int getPrice() {
        return price;
    }

    public String getType() {
        return type;
    }

}

class PizzaItem implements Item {
    //Standaard,Pepperoni , Vegetarian
    private String type;
    private int price;

    public PizzaItem(String type)
            throws InvalidPizzaTypeException {
        if (!type.equals("Standard") && !type.equals("Pepperoni") && !type.equals("Vegetarian"))
            throw new InvalidPizzaTypeException();
        this.type = type;
        if (type.equals("Standard"))
            this.price = 10;
        if (type.equals("Pepperoni"))
            this.price = 12;
        if (type.equals("Vegetarian"))
            this.price = 8;
    }

    @Override
    public int getPrice() {
        return price;
    }

    public String getType() {
        return type;
    }
}

class Order {

    private final List<Item> items;
    private boolean isLocked;

    public Order() {
        this.items = new ArrayList<>();
        this.isLocked = false;
    }

    public void addItem(Item item, int count)
            throws OrderLockedException, ItemOutOfStockException {

        if (isLocked())
            throw new OrderLockedException();
        if (count > 10) {
            throw new ItemOutOfStockException(item);
        }
        items.removeAll(items.stream().filter(item1 ->
                item1.getType().equals(item.getType())).collect(Collectors.toList()));
        for (int i = 0; i < count; i++) {
            items.add(item);
        }
    }

    public int getPrice() {
        return items.stream().mapToInt(Item::getPrice).sum();
    }

    public void removeItem(int idx)
            throws ArrayIndexOutOfBoundsException {
        if (idx >= items.size())
            throw new ArrayIndexOutOfBoundsException(idx);
        Item item = items.get(idx);
        items.removeAll(items.stream()
                .filter(item1 -> item1.getType().equals(item.getType()))
                .collect(Collectors.toList()));
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void displayOrder() {
        StringBuilder sb = new StringBuilder();
        int i = 1;

        if (items.stream().anyMatch(item -> item.getType().equals("Standard"))) {
            int count = (int) items.stream()
                    .filter(item -> item.getType().equals("Standard")).count();

            sb.append(String.format("%3d.", i++)).append(String.format("%-15s", "Standard"))
                    .append(String.format("x %d", count))
                    .append(String.format("%5d$",count * 10)).append("\n");
        }

        if (items.stream().anyMatch(item -> item.getType().equals("Vegetarian"))) {
            int count = (int) items.stream()
                    .filter(item -> item.getType().equals("Vegetarian")).count();

            sb.append(String.format("%3d.", i++)).append(String.format("%-15s", "Vegetarian"))
                    .append(String.format("x %d", count))
                    .append(String.format("%5d$",count * 8)).append("\n");
        }
        if (items.stream().anyMatch(item -> item.getType().equals("Pepperoni"))) {
            int count = (int) items.stream()
                    .filter(item -> item.getType().equals("Pepperoni")).count();

            sb.append(String.format("%3d.", i++)).append(String.format("%-15s", "Pepperoni"))
                    .append(String.format("x %d", count))
                    .append(String.format("%5d$",count * 12)).append("\n");
        }
        if (items.stream().anyMatch(item -> item.getType().equals("Coke"))) {
            int count = (int) items.stream()
                    .filter(item -> item.getType().equals("Coke")).count();

            sb.append(String.format("%3d.", i++)).append(String.format("%-15s", "Coke"))
                    .append(String.format("x %d", count))
                    .append(String.format("%5d$",count * 5)).append("\n");
        }
        if (items.stream().anyMatch(item -> item.getType().equals("Ketchup"))) {
            int count = (int) items.stream()
                    .filter(item -> item.getType().equals("Ketchup")).count();

            sb.append(String.format("%3d.", i)).append(String.format("%-15s", "Ketchup"))
                    .append(String.format("x %d", count))
                    .append(String.format("%5d$",count * 3)).append("\n");
        }

        sb.append(String.format("%-22s","Total:")).append(String.format("%5d$",getPrice()));
        System.out.println(sb);
    }

    public void lock() throws EmptyOrder {
        this.isLocked = true;
        if (items.isEmpty()) {
            throw new EmptyOrder();
        }
    }
}

public class PizzaOrderTest {
    public static void main(String[] args) {
        Scanner jin = new Scanner(System.in);
        int k = jin.nextInt();
        if (k == 0) { //test Item
            try {
                String type = jin.next();
                String name = jin.next();
                Item item = null;
                if (type.equals("Pizza")) item = new PizzaItem(name);
                else item = new ExtraItem(name);
                System.out.println(item.getPrice());
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
        }
        if (k == 1) { // test simple order
            Order order = new Order();
            while (true) {
                try {
                    String type = jin.next();
                    String name = jin.next();
                    Item item = null;
                    if (type.equals("Pizza")) item = new PizzaItem(name);
                    else item = new ExtraItem(name);
                    if (!jin.hasNextInt()) break;
                    order.addItem(item, jin.nextInt());
                } catch (Exception e) {
                    System.out.println(e.getClass().getSimpleName());
                }
            }
            jin.next();
            System.out.println(order.getPrice());
            order.displayOrder();
            while (true) {
                try {
                    String type = jin.next();
                    String name = jin.next();
                    Item item = null;
                    if (type.equals("Pizza")) item = new PizzaItem(name);
                    else item = new ExtraItem(name);
                    if (!jin.hasNextInt()) break;
                    order.addItem(item, jin.nextInt());
                } catch (Exception e) {
                    System.out.println(e.getClass().getSimpleName());
                }
            }
            System.out.println(order.getPrice());
            order.displayOrder();
        }
        if (k == 2) { // test order with removing
            Order order = new Order();
            while (true) {
                try {
                    String type = jin.next();
                    String name = jin.next();
                    Item item = null;
                    if (type.equals("Pizza")) item = new PizzaItem(name);
                    else item = new ExtraItem(name);
                    if (!jin.hasNextInt()) break;
                    order.addItem(item, jin.nextInt());
                } catch (Exception e) {
                    System.out.println(e.getClass().getSimpleName());
                }
            }
            jin.next();
            System.out.println(order.getPrice());
            order.displayOrder();
            while (jin.hasNextInt()) {
                try {
                    int idx = jin.nextInt();
                    order.removeItem(idx);
                } catch (Exception e) {
                    System.out.println(e.getClass().getSimpleName());
                }
            }
            System.out.println(order.getPrice());
            order.displayOrder();
        }
        if (k == 3) { //test locking & exceptions
            Order order = new Order();
            try {
                order.lock();
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
            try {
                order.addItem(new ExtraItem("Coke"), 1);
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
//            try {
//                order.lock();
//            } catch (Exception e) {
//                System.out.println(e.getClass().getSimpleName());
//            }
//            try {
//                order.removeItem(0);
//            } catch (Exception e) {
//                System.out.println(e.getClass().getSimpleName());
//            }
        }
    }

}