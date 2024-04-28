package K2.Weather_Observer;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

interface WeatherObserver {
    void update();
    int priority();
}

class WeatherDispatcher {
    private final Set<WeatherObserver> observers;
    private float currentTemperature;
    private float currentHumidity;
    private float currentPressure;

    public WeatherDispatcher() {
        this.observers = new HashSet<>();
    }

    public float getCurrentTemperature() {
        return currentTemperature;
    }

    public float getCurrentHumidity() {
        return currentHumidity;
    }

    public float getCurrentPressure() {
        return currentPressure;
    }

    public void register(WeatherObserver observer) {
        observers.add(observer);
    }

    public void remove(WeatherObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        observers.stream().sorted(Comparator.comparing(WeatherObserver::priority)).forEach(WeatherObserver::update);
    }

    public void setMeasurements(float temperature, float humidity, float pressure) {
        this.currentTemperature = temperature;
        this.currentHumidity = humidity;
        this.currentPressure = pressure;
        notifyObservers();
        System.out.println();
    }
}

class CurrentConditionsDisplay implements WeatherObserver {
    private final WeatherDispatcher weatherDispatcher;

    public CurrentConditionsDisplay(WeatherDispatcher weatherDispatcher) {
        weatherDispatcher.register(this);
        this.weatherDispatcher = weatherDispatcher;
    }

    @Override
    public void update() {
        System.out.println("Temperature: " + weatherDispatcher.getCurrentTemperature() + "F");
        System.out.println("Humidity: " + weatherDispatcher.getCurrentHumidity() + "%");
    }

    @Override
    public int priority() {
        return 0;
    }
}

class ForecastDisplay implements WeatherObserver {
    private final WeatherDispatcher weatherDispatcher;
    private float previousPressure;

    public ForecastDisplay(WeatherDispatcher weatherDispatcher) {
        weatherDispatcher.register(this);
        this.weatherDispatcher = weatherDispatcher;
        this.previousPressure = 0.0f;
    }

    @Override
    public void update() {
        System.out.print("Forecast: ");
        if (weatherDispatcher.getCurrentPressure() > previousPressure) {
            System.out.println("Improving");
        } else if (weatherDispatcher.getCurrentPressure() < previousPressure) {
            System.out.println("Cooler");
        } else {
            System.out.println("Same");
        }
        this.previousPressure = weatherDispatcher.getCurrentPressure();
    }

    @Override
    public int priority() {
        return 1;
    }
}

public class WeatherApplication {

    public static void main(String[] args) {
        WeatherDispatcher weatherDispatcher = new WeatherDispatcher();

        CurrentConditionsDisplay currentConditions = new CurrentConditionsDisplay(weatherDispatcher);
        ForecastDisplay forecastDisplay = new ForecastDisplay(weatherDispatcher);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] parts = line.split("\\s+");
            weatherDispatcher.setMeasurements(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]), Float.parseFloat(parts[2]));
            if (parts.length > 3) {
                int operation = Integer.parseInt(parts[3]);
                if (operation == 1) {
                    weatherDispatcher.remove(forecastDisplay);
                }
                if (operation == 2) {
                    weatherDispatcher.remove(currentConditions);
                }
                if (operation == 3) {
                    weatherDispatcher.register(forecastDisplay);
                }
                if (operation == 4) {
                    weatherDispatcher.register(currentConditions);
                }
            }
        }
    }
}
