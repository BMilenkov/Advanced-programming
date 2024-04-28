package K2.Discounts;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * K2.Discounts
 */

class Product {
    private final int discountPrice;
    private final int price;

    public Product(String product) {
        String[] parts = product.split(":");
        this.discountPrice = Integer.parseInt(parts[0]);
        this.price = Integer.parseInt(parts[1]);
    }

    public int calculateDiscountProcent() {
        return 100 * (price - discountPrice) / price;
    }

    public int calculateAbsoluteDiscount() {
        return price - discountPrice;
    }

    @Override
    public String toString() {
        return String.format("%2d%% %d/%d", calculateDiscountProcent(), discountPrice, price);
    }

}

class Store {
    private final String nameStore;
    private final List<Product> products;

    Comparator<Product> comparator = Comparator.comparing(Product::calculateDiscountProcent).
            thenComparing(Product::calculateAbsoluteDiscount).reversed();

    public Store(String store) {
        this.products = new ArrayList<>();
        String[] parts = store.split("\\s+");
        this.nameStore = parts[0];
        for (int i = 1; i < parts.length; i++) {
            products.add(new Product(parts[i]));
        }
        products.sort(comparator);
    }

    public String getNameStore() {
        return nameStore;
    }

    public double averageDiscountInStore() {
        return products.stream().mapToDouble(Product::calculateDiscountProcent).average().getAsDouble();
    }

    public int totalAbsoluteDiscountInStore() {
        return products.stream().mapToInt(Product::calculateAbsoluteDiscount).sum();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        DecimalFormat dm = new DecimalFormat("#.0");
        dm.setRoundingMode(RoundingMode.HALF_UP);
        sb.append(nameStore).append("\n").append("Average discount: ").append(dm.format(averageDiscountInStore())).append("%\n").append("Total discount: ").append(totalAbsoluteDiscountInStore()).append("\n");

        sb.append(products.stream().map(Product::toString).collect(Collectors.joining("\n")));


        //        for (int i = 0; i < products.size() - 1; i++) {
//            sb.append(products.get(i)).append("\n");
//        }
//        sb.append(products.get(products.size() - 1));
        return sb.toString();
    }
}

class Discounts {
    private List<Store> stores;

    public Discounts() {
        this.stores = new ArrayList<>();
    }

    Comparator<Store> comparator = Comparator.comparing(Store::averageDiscountInStore).reversed().thenComparing(Store::getNameStore);

    Comparator<Store> comparator1 = Comparator.comparing(Store::totalAbsoluteDiscountInStore).thenComparing(Store::getNameStore);

    public int readStores(InputStream in) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        this.stores = bufferedReader.lines().map(Store::new).collect(Collectors.toList());
        return stores.size();
    }

    public List<Store> byAverageDiscount() {
        return stores.stream().sorted(comparator).limit(3).collect(Collectors.toList());
    }

    public List<Store> byTotalDiscount() {
        return stores.stream().sorted(comparator1).limit(3).collect(Collectors.toList());
    }

}


public class DiscountsTest {
    public static void main(String[] args) {
        Discounts discounts = new Discounts();
        int stores = discounts.readStores(System.in);
        System.out.println("Stores read: " + stores);
        System.out.println("=== By average discount ===");
        discounts.byAverageDiscount().forEach(System.out::println);
        System.out.println("=== By total discount ===");
        discounts.byTotalDiscount().forEach(System.out::println);
    }
}