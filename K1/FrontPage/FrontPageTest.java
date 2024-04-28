package K1.FrontPage;

import java.util.*;
import java.util.stream.Collectors;


class CategoryNotFoundException extends Exception {
    public CategoryNotFoundException(String line) {
        super(line);
    }
}

class Category {
    private String name;

    public Category(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(name, category.name);
    }

    public String getName() {
        return name;
    }
}

abstract class NewsItem {
    private String title;
    private Date datePublished;
    private Category category;

    public NewsItem(String title, Date datePublished, Category category) {
        this.title = title;
        this.datePublished = datePublished;
        this.category = category;
    }

    public Date getDatePublished() {
        return datePublished;
    }

    public String getTitle() {
        return title;
    }

    public Category getCategory() {
        return category;
    }

    public abstract String getTeaser();
}

class TextNewsItem extends NewsItem {
    private String text;

    public TextNewsItem(String title, Date datePublished,
                        Category category, String text) {
        super(title, datePublished, category);
        this.text = text;
    }

    public int getMinutes() {
        Date now = new Date();
        return (int) (now.getTime() - getDatePublished().getTime()) / 60000;

    }

    public String getText80() {
        if (text.length() < 80)
            return text;
        return text.substring(0, 80);
    }

    @Override
    public String getTeaser() {
        return String.format(getTitle() + "\n" + getMinutes() + "\n" + getText80() + "\n");
    }
}

class MediaNewsItem extends NewsItem {

    private String URL;
    private int views;

    public MediaNewsItem(String title, Date datePublished,
                         Category category, String URL, int views) {
        super(title, datePublished, category);
        this.URL = URL;
        this.views = views;
    }

    public int getMinutes() {
        Date now = new Date();
        return (int) (now.getTime() - getDatePublished().getTime()) / 60000;

    }

    @Override
    public String getTeaser() {
        return String.format(getTitle() + "\n" + getMinutes() + "\n" + URL + "\n" + views + "\n");
    }
}

class FrontPage {

    private List<NewsItem> items;
    private Category[] categories;

    public FrontPage(Category[] categories) {
        this.categories = categories;
        this.items = new ArrayList<>();
    }

    public void addNewsItem(NewsItem newsItem) {
        items.add(newsItem);
    }

    public List<NewsItem> listByCategory(Category category) {

        return items.stream()
                .filter(item -> item.getCategory().equals(category))
                .collect(Collectors.toList());
    }

    public List<NewsItem> listByCategoryName(String category)
            throws CategoryNotFoundException {

        if (Arrays.stream(categories).noneMatch(category1 -> category1.getName().equals(category)))
            throw new CategoryNotFoundException
                    ("Category " + category + " was not found");

        return items.stream().filter(item -> item.getCategory().getName().equals(category))
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (NewsItem newsItem : items) {
            sb.append(newsItem.getTeaser());
        }
        return sb.toString();
    }
}


public class FrontPageTest {
    public static void main(String[] args) {
        // Reading
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        String[] parts = line.split(" ");
        Category[] categories = new Category[parts.length];
        for (int i = 0; i < categories.length; ++i) {
            categories[i] = new Category(parts[i]);
        }
        int n = scanner.nextInt();
        scanner.nextLine();
        FrontPage frontPage = new FrontPage(categories);
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < n; ++i) {
            String title = scanner.nextLine();
            cal = Calendar.getInstance();
            int min = scanner.nextInt();
            cal.add(Calendar.MINUTE, -min);
            Date date = cal.getTime();
            scanner.nextLine();
            String text = scanner.nextLine();
            int categoryIndex = scanner.nextInt();
            scanner.nextLine();
            TextNewsItem tni = new TextNewsItem(title, date, categories[categoryIndex], text);
            frontPage.addNewsItem(tni);
        }

        n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String title = scanner.nextLine();
            int min = scanner.nextInt();
            cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -min);
            scanner.nextLine();
            Date date = cal.getTime();
            String url = scanner.nextLine();
            int views = scanner.nextInt();
            scanner.nextLine();
            int categoryIndex = scanner.nextInt();
            scanner.nextLine();
            MediaNewsItem mni = new MediaNewsItem(title, date, categories[categoryIndex], url, views);
            frontPage.addNewsItem(mni);
        }
        // Execution
        String category = scanner.nextLine();
        System.out.println(frontPage);
        for (Category c : categories) {
            System.out.println(frontPage.listByCategory(c).size());
        }
        try {
            System.out.println(frontPage.listByCategoryName(category).size());
        } catch (CategoryNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}

