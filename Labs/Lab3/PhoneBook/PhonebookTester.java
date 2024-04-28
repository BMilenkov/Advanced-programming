package Labs.Lab3.PhoneBook;

import java.io.*;
import java.util.*;


class InvalidFormatException extends Exception {
    public InvalidFormatException() {
        super();
    }
}

class InvalidNameException extends Exception {

    public String name;

    public InvalidNameException() {
        super();
    }

    public InvalidNameException(String name) {
        super();
        this.name = name;
    }
}

class MaximumSizeExceddedException extends Exception {

    public MaximumSizeExceddedException() {
        super();
    }

}

class InvalidNumberException extends Exception {

    public InvalidNumberException() {
        super();
    }

}

class Contact {
    private String Name;
    private List<String> telephoneNumbers;

    public Contact(String name, String... phonenumber) throws MaximumSizeExceddedException, InvalidNumberException, InvalidNameException {

        checkName(name);
        this.Name = name;
        checkPhoneNumbersValidation(phonenumber);
        telephoneNumbers = new ArrayList<>();
        telephoneNumbers.addAll(Arrays.asList(phonenumber));
    }


    private void checkPhoneNumbersValidation(String[] phonenumber) throws InvalidNumberException, MaximumSizeExceddedException {
        if (phonenumber.length > 5)
            throw new MaximumSizeExceddedException();
        for (String s : phonenumber) {
            if (s.length() != 9)
                throw new InvalidNumberException();
            checkPrefix(s);
        }
    }

    private void checkPrefix(String number) throws InvalidNumberException {
        String prefix = number.substring(0, 3);
        if (!(prefix.equals("070") || prefix.equals("071") || prefix.equals("072")
                || prefix.equals("075") || prefix.equals("076") || prefix.equals("077")
                || prefix.equals("078")))
            throw new InvalidNumberException();

        //double check for integers in number
        for (int i = 0; i < number.length(); i++) {
            if (!Character.isDigit(number.charAt(i)))
                throw new InvalidNumberException();
        }

    }


    private void checkName(String name) throws InvalidNameException {
        if (name.length() < 5 || name.length() > 10)
            throw new InvalidNameException(name);

        char[] arr = name.toCharArray();
        for (char c : arr) {
            if (!Character.isLetterOrDigit(c))
                throw new InvalidNameException(name);
        }
    }

    public String getName() {
        return Name;
    }

    public String[] getNumbers() {
        List<String> copyOf = new ArrayList<>(telephoneNumbers);
        Collections.sort(copyOf);
        return copyOf.stream().toArray(String[]::new);
    }

    public void addNumber(String phonenumber) throws InvalidNumberException {
        if (telephoneNumbers.size() == 5)
            return;
        checkPrefix(phonenumber);
        telephoneNumbers.add(phonenumber);
    }

    @Override
    public String toString() {
        //Andrej
        //3
        //072244654
        //077211370
        //078085778
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName()).append("\n").append(telephoneNumbers.size()).append("\n");
        for (String s : getNumbers()) {
            sb.append(s).append("\n");
        }
        return sb.toString();
    }
}

class PhoneBook {
    private List<Contact> contacts;
    private int n;

    public PhoneBook() {
        contacts = new ArrayList<>();
    }

    public void addContact(Contact contact) throws MaximumSizeExceddedException, InvalidNameException {
        if (n == 250)
            throw new MaximumSizeExceddedException();
        checkSameContactName(contact.getName());
        contacts.add(contact);
        n++;
    }

    private void checkSameContactName(String name) throws InvalidNameException {
        boolean n = contacts.stream().anyMatch(c -> c.getName().equals(name));
        if (n)
            throw new InvalidNameException(name);
    }

    public Contact getContactForName(String name) {
        return contacts.stream().filter(c -> c.getName().equals(name)).findFirst().orElse(null);
    }

    public int numberOfContacts() {
        return n;
    }

    public Contact[] getContacts() {
        return contacts.stream().sorted(Comparator.comparing(Contact::getName)).toArray(Contact[]::new);
    }

    public boolean removeContact(String name) {
        Contact c = getContactForName(name);
        if (c == null)
            return false;
        contacts.remove(c);
        n--;
        return true;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<getContacts().length; i++)
            if(getContacts()[i]!=null)
                sb.append(getContacts()[i]).append('\n');
        return sb.toString();
    }

    public static boolean saveAsTextFile(PhoneBook phonebook, String path) throws IOException {
        File f = new File(path);
        if (phonebook == null)
            return false;
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
            oos.writeObject(phonebook);
        }
        return false;
    }

    public static PhoneBook loadFromTextFile(String path) throws IOException, InvalidFormatException {
        File f = new File(path);
        if (!f.exists())
            throw new IOException();
        if (!f.canWrite())
            throw new InvalidFormatException();
        PhoneBook p = null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            p = (PhoneBook) ois.readObject();
        } catch (ClassNotFoundException ife) {
            ife.printStackTrace();
        }
        return p;

    }

    public Contact[] getContactsForNumber(String number_prefix) {
        List<Contact> list = new ArrayList<>();
        for (Contact contact : contacts) {
            for (String s : contact.getNumbers()) {
                if (s.startsWith(number_prefix)&&!list.contains(contact)) {
                    list.add(contact);
                    continue;
                }
            }
        }
        return list.stream().toArray(Contact[]::new);
    }
}


public class PhonebookTester {

    public static void main(String[] args) throws Exception {
        Scanner jin = new Scanner(System.in);
        String line = jin.nextLine();
        switch (line) {
            case "test_contact":
                testContact(jin);
                break;
            case "test_phonebook_exceptions":
                testPhonebookExceptions(jin);
                break;
            case "test_usage":
                testUsage(jin);
                break;
        }
    }

    private static void testFile(Scanner jin) throws Exception {
        PhoneBook phonebook = new PhoneBook();
        while (jin.hasNextLine())
            phonebook.addContact(new Contact(jin.nextLine(), jin.nextLine().split("\\s++")));
        String text_file = "phonebook.txt";
        PhoneBook.saveAsTextFile(phonebook, text_file);
        PhoneBook pb = PhoneBook.loadFromTextFile(text_file);
        if (!pb.equals(phonebook)) System.out.println("Your file saving and loading doesn't seem to work right");
        else System.out.println("Your file saving and loading works great. Good job!");
    }

    private static void testUsage(Scanner jin) throws Exception {
        PhoneBook phonebook = new PhoneBook();
        while (jin.hasNextLine()) {
            String command = jin.nextLine();
            switch (command) {
                case "add":
                    phonebook.addContact(new Contact(jin.nextLine(), jin.nextLine().split("\\s++")));
                    break;
                case "remove":
                    phonebook.removeContact(jin.nextLine());
                    break;
                case "print":
                    System.out.println(phonebook.numberOfContacts());
                    System.out.println(Arrays.toString(phonebook.getContacts()));
                    System.out.println(phonebook.toString());
                    break;
                case "get_name":
                    System.out.println(phonebook.getContactForName(jin.nextLine()));
                    break;
                case "get_number":
                    System.out.println(Arrays.toString(phonebook.getContactsForNumber(jin.nextLine())));
                    break;
            }
        }
    }

    private static void testPhonebookExceptions(Scanner jin) {
        PhoneBook phonebook = new PhoneBook();
        boolean exception_thrown = false;
        try {
            while (jin.hasNextLine()) {
                phonebook.addContact(new Contact(jin.nextLine()));
            }
        } catch (InvalidNameException e) {
            System.out.println(e.name);
            exception_thrown = true;
        } catch (Exception e) {
        }
        if (!exception_thrown) System.out.println("Your addContact method doesn't throw InvalidNameException");
        /*
		exception_thrown = false;
		try {
		phonebook.addContact(new Contact(jin.nextLine()));
		} catch ( MaximumSizeExceddedException e ) {
			exception_thrown = true;
		}
		catch ( Exception e ) {}
		if ( ! exception_thrown ) System.out.println("Your addContact method doesn't throw MaximumSizeExcededException");
        */
    }

    private static void testContact(Scanner jin) throws Exception {
        boolean exception_thrown = true;
        String names_to_test[] = {"And\nrej", "asd", "AAAAAAAAAAAAAAAAAAAAAA", "Ð�Ð½Ð´Ñ€ÐµÑ˜A123213", "Andrej#", "Andrej<3"};
        for (String name : names_to_test) {
            try {
                new Contact(name);
                exception_thrown = false;
            } catch (InvalidNameException e) {
                exception_thrown = true;
            }
            if (!exception_thrown) System.out.println("Your Contact constructor doesn't throw an InvalidNameException");
        }
        String numbers_to_test[] = {"+071718028", "number", "078asdasdasd", "070asdqwe", "070a56798", "07045678a", "123456789", "074456798", "073456798", "079456798"};
        for (String number : numbers_to_test) {
            try {
                new Contact("Andrej", number);
                exception_thrown = false;
            } catch (InvalidNumberException e) {
                exception_thrown = true;
            }
            if (!exception_thrown)
                System.out.println("Your Contact constructor doesn't throw an InvalidNumberException");
        }
        String nums[] = new String[10];
        for (int i = 0; i < nums.length; ++i) nums[i] = getRandomLegitNumber();
        try {
            new Contact("Andrej", nums);
            exception_thrown = false;
        } catch (MaximumSizeExceddedException e) {
            exception_thrown = true;
        }
        if (!exception_thrown)
            System.out.println("Your Contact constructor doesn't throw a MaximumSizeExceddedException");
        Random rnd = new Random(5);
        Contact contact = new Contact("Andrej", getRandomLegitNumber(rnd), getRandomLegitNumber(rnd), getRandomLegitNumber(rnd));
        System.out.println(contact.getName());
        System.out.println(Arrays.toString(contact.getNumbers()));
        System.out.println(contact.toString());
        contact.addNumber(getRandomLegitNumber(rnd));
        System.out.println(Arrays.toString(contact.getNumbers()));
        System.out.println(contact.toString());
        contact.addNumber(getRandomLegitNumber(rnd));
        System.out.println(Arrays.toString(contact.getNumbers()));
        System.out.println(contact.toString());
    }

    static String[] legit_prefixes = {"070", "071", "072", "075", "076", "077", "078"};
    static Random rnd = new Random();

    private static String getRandomLegitNumber() {
        return getRandomLegitNumber(rnd);
    }

    private static String getRandomLegitNumber(Random rnd) {
        StringBuilder sb = new StringBuilder(legit_prefixes[rnd.nextInt(legit_prefixes.length)]);
        for (int i = 3; i < 9; ++i)
            sb.append(rnd.nextInt(10));
        return sb.toString();
    }


}
