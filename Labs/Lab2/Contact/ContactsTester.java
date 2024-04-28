package Labs.Lab2.Contact;


import java.text.DecimalFormat;
import java.util.Scanner;



enum Operator {
    VIP, ONE, TMOBILE
}

abstract class Contact {

    private String date;

    public Contact(String date) {
        this.date = date;
    }

    public boolean isNewerThan(Contact c){

        String parts[] = this.date.split("-");
        String parts1[] = c.date.split("-");

        if(Integer.parseInt(parts[0]) > Integer.parseInt(parts1[0]))
            return true;
        else if (Integer.parseInt(parts[0]) < Integer.parseInt(parts1[0]))
            return false;
        else{
            if(Integer.parseInt(parts[1]) > Integer.parseInt(parts1[1]))
                return true;
            else if (Integer.parseInt(parts[1]) < Integer.parseInt(parts1[1]))
                return false;
            else{
                return Integer.parseInt(parts[2]) > Integer.parseInt(parts1[2]);
            }
        }
    }
    public abstract String getType();
}


class EmailContact extends Contact {

    private String email;

    public EmailContact(String date, String email) {
        super(date);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getType() {
        return "Email";
    }

}
class Faculty {
    private String name;
    private Student[] students;

    public Faculty(String name, Student[] students) {
        this.name = name;
        this.students = students;
    }

    public int countStudentsFromCity(String cityName) {
        int counter = 0;
        for (Student s : students) {
            if (cityName.equals(s.getCity()))
                counter++;
        }
        return counter;
    }

    public Student getStudent(long index) {
        for (Student s : students) {
            if (s.getIndex() == index)
                return s;
        }
        return null;
    }

    public double getAverageNumberOfContacts() {
        double sum = 0.0;
        for (Student s : students) {
            sum += s.getNum();
        }
        return sum / students.length;
    }

    public Student getStudentWithMostContacts() {
        Student swmc = students[0];
        for (int i = 1; i < students.length; i++) {
            if (students[i].getNum() > swmc.getNum()) {
                swmc = students[i];
            } else if (students[i].getNum() == swmc.getNum()) {
                if (students[i].getIndex() > swmc.getIndex())
                    swmc = students[i];
            }
        }
        return swmc;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("{\"fakultet\":\"").append(name).append("\", \"studenti\":[");
        for (int i = 0; i < students.length - 1; i++) {
            sb.append(students[i].toString()).append(", ");
        }
        sb.append(students[students.length - 1].toString()).append("]}");
        return sb.toString();
    }

}
class PhoneContact extends Contact {
    private String phoneNumber;
    private Operator type;


    public PhoneContact(String date, String phoneNumber) {
        super(date);
        this.phoneNumber = phoneNumber;

        if(phoneNumber.charAt(2) == '0' || phoneNumber.charAt(2) == '1'
                || phoneNumber.charAt(2) == '2'){
            this.type = Operator.TMOBILE;
        } else if (phoneNumber.charAt(2) == '5' || phoneNumber.charAt(2) == '6') {
            this.type = Operator.ONE;
        }
        else
            this.type = Operator.VIP;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public Operator getOperator()
    {
        return type;
    }
    @Override
    public String getType() {
        return "Phone";
    }
}


class Student {


    private String firstName;
    private String lastName;
    private String city;
    private int age;
    private long index;
    private Contact[] contacts;
    private int num;
    private int phonecontacts;
    private int emailcontacts;

    public Student(String firstName, String lastName, String city, int age,
                   long index) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.age = age;
        this.index = index;
        this.contacts = null;
        this.num = 0;
        this.phonecontacts = 0;
        this.emailcontacts = 0;
    }

    public void addEmailContact(String date, String email) {
        Contact[] temp = new Contact[num + 1];
        for (int i = 0; i < num; i++) {
            temp[i] = contacts[i];
        }
        temp[num++] = new EmailContact(date, email);
        contacts = temp;
        emailcontacts++;
    }

    public void addPhoneContact(String date, String phone) {
        Contact[] temp = new Contact[num + 1];
        for (int i = 0; i < num; i++) {
            temp[i] = contacts[i];
        }
        temp[num++] = new PhoneContact(date, phone);
        contacts = temp;
        phonecontacts++;
    }

    public Contact[] getEmailContacts() {
        int counter = 0;
        Contact[] emailContacts = new EmailContact[emailcontacts];
        for (Contact c : contacts) {
            if (c.getType().equals("Email"))
                emailContacts[counter++] = c;
        }
        return emailContacts;
    }

    public Contact[] getPhoneContacts() {
        int counter = 0;
        Contact[] phoneContacts = new PhoneContact[phonecontacts];
        for (Contact c : contacts) {
            if (c.getType().equals("Phone"))
                phoneContacts[counter++] = c;
        }
        return phoneContacts;
    }

    public String getCity() {
        return this.city;
    }

    public long getIndex() {
        return this.index;
    }

    public String getFullName() {
        StringBuilder sb = new StringBuilder();
        sb.append(firstName.toUpperCase()).append(" ").append(lastName.toUpperCase());
        return sb.toString();
    }

    public Contact getLatestContact() {
        Contact newest = contacts[0];
        for (int i = 1; i < num; i++) {
            if (contacts[i].isNewerThan(newest))
                newest = contacts[i];
        }
        return newest;
    }

    public int getNum() {
        return num;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("{\"ime\":\"").append(firstName).append("\", \"prezime\":\"").append(lastName);
        sb.append("\", \"vozrast\":").append(age).append(", \"grad\":\"").append(city);
        sb.append("\", \"indeks\":").append(index).append(", \"telefonskiKontakti\":[");

        PhoneContact[] pc = (PhoneContact[]) this.getPhoneContacts();
        for (int i = 0; i < phonecontacts - 1; i++) {
            sb.append("\"").append(pc[i].getPhoneNumber()).append("\", ");
        }
        if (phonecontacts > 0)
            sb.append("\"").append(pc[phonecontacts - 1].getPhoneNumber()).append("\"");
        sb.append("], ");

        sb.append("\"emailKontakti\":[");
        EmailContact[] ec = (EmailContact[]) this.getEmailContacts();
        for (int i = 0; i < emailcontacts - 1; i++) {
            sb.append("\"").append(ec[i].getEmail()).append("\", ");
        }
        if (emailcontacts > 0)
            sb.append("\"").append(ec[emailcontacts - 1].getEmail()).append("\"");
        sb.append("]}");

        return sb.toString();
    }

    public static class DoubleMatrix {
    }
}

public class ContactsTester {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int tests = scanner.nextInt();
        Faculty faculty = null;

        int rvalue = 0;
        long rindex = -1;

        DecimalFormat df = new DecimalFormat("0.00");

        for (int t = 0; t < tests; t++) {

            rvalue++;
            String operation = scanner.next();

            switch (operation) {
                case "CREATE_FACULTY": {
                    String name = scanner.nextLine().trim();
                    int N = scanner.nextInt();

                    Student[] students = new Student[N];

                    for (int i = 0; i < N; i++) {
                        rvalue++;

                        String firstName = scanner.next();
                        String lastName = scanner.next();
                        String city = scanner.next();
                        int age = scanner.nextInt();
                        long index = scanner.nextLong();

                        if ((rindex == -1) || (rvalue % 13 == 0))
                            rindex = index;

                        Student student = new Student(firstName, lastName, city,
                                age, index);
                        students[i] = student;
                    }

                    faculty = new Faculty(name, students);
                    break;
                }

                case "ADD_EMAIL_CONTACT": {
                    long index = scanner.nextInt();
                    String date = scanner.next();
                    String email = scanner.next();

                    rvalue++;

                    if ((rindex == -1) || (rvalue % 3 == 0))
                        rindex = index;

                    faculty.getStudent(index).addEmailContact(date, email);
                    break;
                }

                case "ADD_PHONE_CONTACT": {
                    long index = scanner.nextInt();
                    String date = scanner.next();
                    String phone = scanner.next();

                    rvalue++;

                    if ((rindex == -1) || (rvalue % 3 == 0))
                        rindex = index;

                    faculty.getStudent(index).addPhoneContact(date, phone);
                    break;
                }

                case "CHECK_SIMPLE": {
                    System.out.println("Average number of contacts: "
                            + df.format(faculty.getAverageNumberOfContacts()));

                    rvalue++;

                    String city = faculty.getStudent(rindex).getCity();
                    System.out.println("Number of students from " + city + ": "
                            + faculty.countStudentsFromCity(city));

                    break;
                }

                case "CHECK_DATES": {

                    rvalue++;

                    System.out.print("Latest contact: ");
                    Contact latestContact = faculty.getStudent(rindex)
                            .getLatestContact();
                    if (latestContact.getType().equals("Email"))
                        System.out.println(((EmailContact) latestContact)
                                .getEmail());
                    if (latestContact.getType().equals("Phone"))
                        System.out.println(((PhoneContact) latestContact)
                                .getPhoneNumber()
                                + " ("
                                + ((PhoneContact) latestContact).getOperator()
                                .toString() + ")");

                    if (faculty.getStudent(rindex).getEmailContacts().length > 0
                            && faculty.getStudent(rindex).getPhoneContacts().length > 0) {
                        System.out.print("Number of email and phone contacts: ");
                        System.out
                                .println(faculty.getStudent(rindex)
                                        .getEmailContacts().length
                                        + " "
                                        + faculty.getStudent(rindex)
                                        .getPhoneContacts().length);

                        System.out.print("Comparing dates: ");
                        int posEmail = rvalue
                                % faculty.getStudent(rindex).getEmailContacts().length;
                        int posPhone = rvalue
                                % faculty.getStudent(rindex).getPhoneContacts().length;

                        System.out.println(faculty.getStudent(rindex)
                                .getEmailContacts()[posEmail].isNewerThan(faculty
                                .getStudent(rindex).getPhoneContacts()[posPhone]));
                    }

                    break;
                }

                case "PRINT_FACULTY_METHODS": {
                    System.out.println("Faculty: " + faculty.toString());
                    System.out.println("Student with most contacts: "
                            + faculty.getStudentWithMostContacts().toString());
                    break;
                }

            }

        }

        scanner.close();
    }
}






