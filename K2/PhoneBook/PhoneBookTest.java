package K2.PhoneBook;

import java.util.*;


class DuplicateNumberException extends Exception {
    public DuplicateNumberException(String message) {
        super(message);
    }
}

class Contact {
    String name;
    String number;

    public Contact(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return String.format("%s %s", name, number);
    }
}

class PhoneBook {

    Set<String> numbers;
    Map<String, Set<Contact>> contactsByNameMap;
    Map<String, Set<Contact>> contactsByNumberPartsMap;

    static Comparator<Contact> COMPARATOR = Comparator.comparing(Contact::getName)
            .thenComparing(Contact::getNumber);

    public PhoneBook() {
        numbers = new HashSet<>();
        contactsByNameMap = new HashMap<>();
        contactsByNumberPartsMap = new HashMap<>();
    }

    public void addContact(String name, String number)
            throws DuplicateNumberException {
        if (numbers.contains(number))
            throw new DuplicateNumberException(String.format(
                    "Duplicate number: %s", number));

        contactsByNameMap.putIfAbsent(name, new TreeSet<>(COMPARATOR));
        contactsByNameMap.get(name).add(new Contact(name, number));
        List<String> keys = getKeys(number, 3);
        for (String key : keys) {
            contactsByNumberPartsMap.putIfAbsent(key, new TreeSet<>(COMPARATOR));
            contactsByNumberPartsMap.get(key).add(new Contact(name, number));
        }
        numbers.add(number);
    }

    private List<String> getKeys(String key, int minLen) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i <= key.length() - minLen; ++i) {
            for (int len = minLen; len <= (key.length() - i); ++len) {
                String k = key.substring(i, i + len);
                result.add(k);
            }
        }
        return result;
    }

    public void contactsByNumber(String number) {
        if (contactsByNumberPartsMap.containsKey(number))
            contactsByNumberPartsMap.get(number).forEach(System.out::println);
        else System.out.println("NOT FOUND");
    }

    public void contactsByName(String name) {
        if (contactsByNameMap.containsKey(name))
            contactsByNameMap.get(name).forEach(System.out::println);
        else System.out.println("NOT FOUND");
    }
}


public class PhoneBookTest {

    public static void main(String[] args) {
        PhoneBook phoneBook = new PhoneBook();
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(":");
            try {
                phoneBook.addContact(parts[0], parts[1]);
            } catch (DuplicateNumberException e) {
                System.out.println(e.getMessage());
            }
        }
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            System.out.println(line);
            String[] parts = line.split(":");
            if (parts[0].equals("NUM")) {
                phoneBook.contactsByNumber(parts[1]);
            } else {
                phoneBook.contactsByName(parts[1]);
            }
        }
    }

}
