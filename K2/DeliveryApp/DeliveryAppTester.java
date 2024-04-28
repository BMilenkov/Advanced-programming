package K2.DeliveryApp;


import java.util.*;

/*
YOUR CODE HERE
DO NOT MODIFY THE interfaces and classes below!!!
*/
interface Location {
    int getX();

    int getY();

    default int distance(Location other) {
        int xDiff = Math.abs(getX() - other.getX());
        int yDiff = Math.abs(getY() - other.getY());
        return xDiff + yDiff;
    }
}

class LocationCreator {
    public static Location create(int x, int y) {

        return new Location() {
            @Override
            public int getX() {
                return x;
            }

            @Override
            public int getY() {
                return y;
            }
        };
    }
}


class Address {

    private String addressName;
    private Location location;

    public Address(String addressName, Location location) {
        this.addressName = addressName;
        this.location = location;
    }

    public String getAddressName() {
        return addressName;
    }

    public Location getLocation() {
        return location;
    }
}

class User {
    private final String id;
    private final String name;
    private HashMap<String, Address> addresses;
    private List<Double> order;

    public User(String id, String name) {
        this.id = id;
        this.name = name;
        this.addresses = new HashMap<>();
        order = new ArrayList<>();
    }

    public void addAddress(Address address) {
        addresses.put(address.getAddressName(), address);
    }

    DoubleSummaryStatistics UserStatistics() {
        return order.stream().mapToDouble(i -> i).summaryStatistics();
    }

    public double totalSpent() {
        return UserStatistics().getSum();
    }

    public void makeOrder(float cost) {
        order.add((double) cost);
    }

    public HashMap<String, Address> getAddresses() {
        return addresses;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("ID: %s Name: %s Total orders:" +
                        " %d Total amount spent: %.02f Average amount spent: %.02f",
                id, name, UserStatistics().getCount(), UserStatistics().getSum(),
                UserStatistics().getAverage());
    }
}

class Restaurant {
    private String id;
    private String name;
    private Location location;
    private List<Double> order;

    public Restaurant(String id, String name, Location location) {
        this.id = id;
        this.name = name;
        this.location = location;
        order = new ArrayList<>();
    }

    DoubleSummaryStatistics restaurantStatistics() {
        return order.stream().mapToDouble(i -> i).summaryStatistics();
    }

    public void makeOrder(float cost) {
        order.add((double) cost);
    }

    public double averageEarned() {
        return restaurantStatistics().getAverage();
    }

    public Location getLocation() {
        return location;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("ID: %s Name: %s Total orders: %d Total amount earned:" +
                        " %.02f Average amount earned: %.02f", id, name, restaurantStatistics().getCount()
                , restaurantStatistics().getSum(), restaurantStatistics().getAverage());
    }
}

class DeliveryPerson {
    private String id;
    private String name;
    private Location currentLocation;
    private List<Double> order;

    public DeliveryPerson(String id, String name, Location currentLocation) {
        this.id = id;
        this.name = name;
        this.currentLocation = currentLocation;
        order = new ArrayList<>();
    }

    public double totalEarned() {
        return deliveryGuyStatistics().getSum();
    }

    DoubleSummaryStatistics deliveryGuyStatistics() {
        return order.stream().mapToDouble(i -> i).summaryStatistics();
    }

    public int distanceToRestaurant(Location location) {
        return currentLocation.distance(location);
    }

    public int distanceComparing(DeliveryPerson o, Location location) {
        if (distanceToRestaurant(location) == o.distanceToRestaurant(location))
            return Integer.compare(order.size(), o.order.size());
        return Integer.compare(distanceToRestaurant(location), o.distanceToRestaurant(location));
    }

    public void makeOrder(int distance, Location location) {
        this.currentLocation = location;
        order.add((double) (90 + 10 * (distance / 10)));
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("ID: %s Name: %s Total deliveries: %d Total delivery fee:" +
                        " %.02f Average delivery fee: %.02f", id, name, deliveryGuyStatistics().getCount()
                , deliveryGuyStatistics().getSum(), deliveryGuyStatistics().getAverage());
    }
}

class DeliveryApp {
    private final String name;
    private Map<String, DeliveryPerson> deliveryPeople;
    private Map<String, Restaurant> restaurants;
    private Map<String, User> users;

    public DeliveryApp(String name) {
        this.name = name;
        deliveryPeople = new HashMap<>();
        restaurants = new HashMap<>();
        users = new HashMap<>();
    }

    public void registerDeliveryPerson(String id, String name, Location location) {
        deliveryPeople.put(id, new DeliveryPerson(id, name, location));
    }

    public void addRestaurant(String id, String name, Location location) {
        restaurants.put(id, new Restaurant(id, name, location));
    }

    public void addUser(String id, String name) {
        users.put(id, new User(id, name));
    }

    public void addAddress(String id, String name, Location location) {
        users.get(id).addAddress(new Address(name, location));
    }


    //TODO IMPLEMENT
    public void orderFood(String userId, String userAddressName,
                          String restaurantId, float cost) {

        users.get(userId).makeOrder(cost);
        restaurants.get(restaurantId).makeOrder(cost);

        DeliveryPerson chosenGuy =
                deliveryPeople.values().stream().min((l, r) -> l.distanceComparing(r, restaurants.get(restaurantId).getLocation())).get();


        int distance = restaurants.get(restaurantId)
                .getLocation().distance(chosenGuy.getCurrentLocation());
        chosenGuy.makeOrder(distance, users.get(userId).getAddresses().get(userAddressName).getLocation());
    }

    public void printUsers() {
        users.values().stream().sorted(Comparator.comparing(User::totalSpent)
                .thenComparing(User::getId).reversed()).forEach(System.out::println);
    }

    public void printRestaurants() {
        restaurants.values().stream().sorted(Comparator.comparing(Restaurant::averageEarned)
                .thenComparing(Restaurant::getId).reversed()).forEach(System.out::println);

    }

    public void printDeliveryPeople() {
        deliveryPeople.values().stream().sorted(Comparator.comparing(DeliveryPerson::totalEarned)
                .thenComparing(DeliveryPerson::getId).reversed()).forEach(System.out::println);
    }
}


public class DeliveryAppTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String appName = sc.nextLine();
        DeliveryApp app = new DeliveryApp(appName);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split(" ");

            if (parts[0].equals("addUser")) {
                String id = parts[1];
                String name = parts[2];
                app.addUser(id, name);
            } else if (parts[0].equals("registerDeliveryPerson")) {
                String id = parts[1];
                String name = parts[2];
                int x = Integer.parseInt(parts[3]);
                int y = Integer.parseInt(parts[4]);
                app.registerDeliveryPerson(id, name, LocationCreator.create(x, y));
            } else if (parts[0].equals("addRestaurant")) {
                String id = parts[1];
                String name = parts[2];
                int x = Integer.parseInt(parts[3]);
                int y = Integer.parseInt(parts[4]);
                app.addRestaurant(id, name, LocationCreator.create(x, y));
            } else if (parts[0].equals("addAddress")) {
                String id = parts[1];
                String name = parts[2];
                int x = Integer.parseInt(parts[3]);
                int y = Integer.parseInt(parts[4]);
                app.addAddress(id, name, LocationCreator.create(x, y));
            } else if (parts[0].equals("orderFood")) {
                String userId = parts[1];
                String userAddressName = parts[2];
                String restaurantId = parts[3];
                float cost = Float.parseFloat(parts[4]);
                app.orderFood(userId, userAddressName, restaurantId, cost);
            } else if (parts[0].equals("printUsers")) {
                app.printUsers();
            } else if (parts[0].equals("printRestaurants")) {
                app.printRestaurants();
            } else {
                app.printDeliveryPeople();
            }

        }
    }
}
