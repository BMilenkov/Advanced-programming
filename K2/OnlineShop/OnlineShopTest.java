package K2.OnlineShop;


import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

enum COMPARATOR_TYPE {
    NEWEST_FIRST,
    OLDEST_FIRST,
    LOWEST_PRICE_FIRST,
    HIGHEST_PRICE_FIRST,
    MOST_SOLD_FIRST,
    LEAST_SOLD_FIRST
}

class ComparatorGenerator {

    public static Comparator<PRODUCT> productComparator(COMPARATOR_TYPE type) {
        if (type == COMPARATOR_TYPE.NEWEST_FIRST)
            return Comparator.comparing(PRODUCT::getCreatedAt).reversed();
        if (type == COMPARATOR_TYPE.OLDEST_FIRST)
            return Comparator.comparing(PRODUCT::getCreatedAt);
        if (type == COMPARATOR_TYPE.LOWEST_PRICE_FIRST)
            return Comparator.comparing(PRODUCT::getPrice);
        if (type == COMPARATOR_TYPE.HIGHEST_PRICE_FIRST)
            return Comparator.comparing(PRODUCT::getPrice).reversed();
        if (type == COMPARATOR_TYPE.MOST_SOLD_FIRST)
            return Comparator.comparing(PRODUCT::getQuantitySold).reversed();
        if (type == COMPARATOR_TYPE.LEAST_SOLD_FIRST)
            return Comparator.comparing(PRODUCT::getQuantitySold);
        return null;
    }
}

class ProductNotFoundException extends Exception {
    ProductNotFoundException(String message) {
        super(message);
    }
}


class PRODUCT {

    private final String category;
    private final String id;
    private final String name;
    private final LocalDateTime createdAt;
    private final double price;
    private int quantitySold;


    public PRODUCT(String category, String id, String name, LocalDateTime createdAt, double price) {
        this.category = category;
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.price = price;
        this.quantitySold = 0;
    }

    public double getPrice() {
        return price;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(int quantitySold) {
        this.quantitySold += quantitySold;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                ", price=" + price +
                ", quantitySold=" + quantitySold +
                '}';
    }
}

class OnlineShop {

    private final Map<String, PRODUCT> productMap;
    private final Map<String, List<PRODUCT>> productsByCategory;

    OnlineShop() {
        this.productMap = new HashMap<>();
        this.productsByCategory = new HashMap<>();
    }

    void addProduct(String category, String id, String name, LocalDateTime createdAt, double price) {
        PRODUCT p = new PRODUCT(category, id, name, createdAt, price);
        productMap.put(id, p);
        productsByCategory.computeIfAbsent(category, k -> new ArrayList<>()).add(p);
    }

    double buyProduct(String id, int quantity) throws ProductNotFoundException {
        if (!productMap.containsKey(id))
            throw new ProductNotFoundException("Product with id " + id + " does not exist in the online shop!");
        productMap.get(id).setQuantitySold(quantity);
        return productMap.get(id).getPrice() * quantity;
    }

    List<List<PRODUCT>> listProducts(String category, COMPARATOR_TYPE comparatorType, int pageSize) {
        List<List<PRODUCT>> result = new ArrayList<>();
        result.add(new ArrayList<>());
        AtomicInteger counter = new AtomicInteger();
        if (category != null) {
            productsByCategory.get(category).stream()
                    .sorted(ComparatorGenerator.productComparator(comparatorType))
                    .forEach(product -> {
                        if (result.get(counter.intValue()).size() == pageSize) {
                            result.add(new ArrayList<>());
                            counter.getAndIncrement();
                        }
                        result.get(counter.intValue()).add(product);
                    });
        } else {
            productMap.values().stream()
                    .sorted(ComparatorGenerator.productComparator(comparatorType))
                    .forEach(product -> {
                        if (result.get(counter.intValue()).size() == pageSize) {
                            result.add(new ArrayList<>());
                            counter.getAndIncrement();
                        }
                        result.get(counter.intValue()).add(product);
                    });
        }
        return result;
    }

}

public class OnlineShopTest {

    public static void main(String[] args) {
        OnlineShop onlineShop = new OnlineShop();
        double totalAmount = 0.0;
        Scanner sc = new Scanner(System.in);
        String line;
        while (sc.hasNextLine()) {
            line = sc.nextLine();
            String[] parts = line.split("\\s+");
            if (parts[0].equalsIgnoreCase("addproduct")) {
                String category = parts[1];
                String id = parts[2];
                String name = parts[3];
                LocalDateTime createdAt = LocalDateTime.parse(parts[4]);
                double price = Double.parseDouble(parts[5]);
                onlineShop.addProduct(category, id, name, createdAt, price);
            } else if (parts[0].equalsIgnoreCase("buyproduct")) {
                String id = parts[1];
                int quantity = Integer.parseInt(parts[2]);
                try {
                    totalAmount += onlineShop.buyProduct(id, quantity);
                } catch (ProductNotFoundException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                String category = parts[1];
                if (category.equalsIgnoreCase("null"))
                    category = null;
                String comparatorString = parts[2];
                int pageSize = Integer.parseInt(parts[3]);
                COMPARATOR_TYPE comparatorType = COMPARATOR_TYPE.valueOf(comparatorString);
                printPages(onlineShop.listProducts(category, comparatorType, pageSize));
            }
        }
        System.out.println("Total revenue of the online shop is: " + totalAmount);

    }

    private static void printPages(List<List<PRODUCT>> listProducts) {
        for (int i = 0; i < listProducts.size(); i++) {
            System.out.println("PAGE " + (i + 1));
            listProducts.get(i).forEach(System.out::println);
        }
    }
}

