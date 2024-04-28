package K2.Book;

import java.util.*;
import java.util.stream.Collectors;


class Book {

    private String title;
    private String category;
    private float price;

    public Book(String title, String category, float price) {
        this.title = title;
        this.category = category;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public float getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) %.02f", title, category, price);
    }
}

class BookCollection {
    private final Set<Book> books;
    private final Map<String, Set<Book>> booksByCategory;

    static Comparator<Book> comparator = Comparator.comparing(Book::getTitle)
            .thenComparing(Book::getPrice).thenComparing(Book::getCategory);
    static Comparator<Book> priceComparator = Comparator.comparing(Book::getPrice)
            .thenComparing(Book::getTitle).thenComparing(Book::getCategory);

    public BookCollection() {
        this.books = new TreeSet<>(priceComparator);
        this.booksByCategory = new HashMap<>();
    }

    public void addBook(Book book) {
        books.add(book);
//        booksByCategory.putIfAbsent(book.getCategory().toLowerCase(), new TreeSet<>(comparator));
//        booksByCategory.get((book.getCategory().toLowerCase())).add(book);
        booksByCategory.computeIfAbsent(book.getCategory().toLowerCase(), k -> new TreeSet<>(comparator)).add(book);
    }

    public void printByCategory(String category) {
        booksByCategory.get(category.toLowerCase()).forEach(System.out::println);
    }

    public List<Book> getCheapestN(int n) {
        return books.stream().limit(n).collect(Collectors.toList());
    }
}


public class BooksTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        BookCollection booksCollection = new BookCollection();
        Set<String> categories = fillCollection(scanner, booksCollection);
        System.out.println("=== PRINT BY CATEGORY ===");
        for (String category : categories) {
            System.out.println("CATEGORY: " + category);
            booksCollection.printByCategory(category);
        }
        System.out.println("=== TOP N BY PRICE ===");
        print(booksCollection.getCheapestN(n));
    }

    static void print(List<Book> books) {
        for (Book book : books) {
            System.out.println(book);
        }
    }

    static TreeSet<String> fillCollection(Scanner scanner, BookCollection collection) {
        TreeSet<String> categories = new TreeSet<String>();
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] parts = line.split(":");
            Book book = new Book(parts[0], parts[1], Float.parseFloat(parts[2]));
            collection.addBook(book);
            categories.add(parts[1]);
        }
        return categories;
    }

}