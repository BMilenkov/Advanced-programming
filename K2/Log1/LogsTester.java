package K2.Log1;


import java.util.*;
import java.util.stream.Collectors;

abstract class Log {
    private final String serviceName;
    private final String microserviceName;
    private final String message;
    private final long timestamp;

    public Log(String serviceName, String microserviceName, String message, long timestamp) {
        this.serviceName = serviceName;
        this.microserviceName = microserviceName;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getMicroserviceName() {
        return microserviceName;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public abstract int severity();

    @Override
    public abstract String toString();
}

class InfoLog extends Log {
    public InfoLog(String serviceName, String microserviceName, String message, long timestamp) {
        super(serviceName, microserviceName, message, timestamp);
    }

    @Override
    public int severity() {
        return 0;
    }

    @Override
    public String toString() {
        return String.format("%s|%s [INFO] %s T:%d", getServiceName(), getMicroserviceName()
                , getMessage(), getTimestamp());
    }

}

class WarnLog extends Log {
    public WarnLog(String serviceName, String microserviceName, String message, long timestamp) {
        super(serviceName, microserviceName, message, timestamp);
    }

    @Override
    public int severity() {
        return 1 + (getMessage().contains("might cause error") ? 1 : 0);
    }

    @Override
    public String toString() {
        return String.format("%s|%s [WARN] %s T:%d", getServiceName(), getMicroserviceName()
                , getMessage(), getTimestamp());
    }
}

class ErrorLog extends Log {
    public ErrorLog(String serviceName, String microserviceName, String message, long timestamp) {
        super(serviceName, microserviceName, message, timestamp);
    }

    @Override
    public int severity() {
        return 3 + (getMessage().contains("fatal") ? 2 : 0) + (getMessage().contains("exception") ? 3 : 0);
    }

    @Override
    public String toString() {
        return String.format("%s|%s [ERROR] %s T:%d", getServiceName(), getMicroserviceName()
                , getMessage(), getTimestamp());
    }
}

class LogFactory {
    static Log createLog(String line) {
        String[] parts = line.split("\\s++");
        if (parts[2].equals("INFO"))
            return new InfoLog(parts[0], parts[1], Arrays.stream(parts).skip(3).collect(Collectors.joining(" ")), Long.parseLong(parts[parts.length - 1]));
        else if (parts[2].equals("WARN"))
            return new WarnLog(parts[0], parts[1], Arrays.stream(parts).skip(3).collect(Collectors.joining(" ")), Long.parseLong(parts[parts.length - 1]));
        return new ErrorLog(parts[0], parts[1], Arrays.stream(parts).skip(3).collect(Collectors.joining(" ")), Long.parseLong(parts[parts.length - 1]));
    }

}

class ComparatorFactory {
    public static Comparator<Log> createComparator(String type) {
        switch (type) {
            case "NEWEST_FIRST":
                return Comparator.comparing(Log::getTimestamp).thenComparing(Log::getTimestamp).reversed();
            case "OLDEST_FIRST":
                return Comparator.comparing(Log::getTimestamp);
            case "MOST_SEVERE_FIRST":
                return Comparator.comparing(Log::severity).thenComparing(Log::getTimestamp).reversed();
            case "LEAST_SEVERE_FIRST":
                return Comparator.comparing(Log::severity);
            default:
                return Comparator.comparing(Log::getServiceName);
        }
    }
}

class LogCollector {
    Map<String, List<Log>> serviceLogMap;
    Map<String, List<Log>> microServiceLogMap;

    Comparator<Map.Entry<String, List<Log>>> comparator =
            Comparator.comparingDouble(e -> e.getValue().stream().mapToDouble(Log::severity).average().getAsDouble());


    public LogCollector() {
        this.serviceLogMap = new HashMap<>();
        microServiceLogMap = new HashMap<>();
    }

    public void addLog(String log) {
        Log LOG = LogFactory.createLog(log);
        serviceLogMap.computeIfAbsent(LOG.getServiceName(), k -> new ArrayList<>()).add(LOG);
        microServiceLogMap.computeIfAbsent(LOG.getMicroserviceName(), k -> new ArrayList<>()).add(LOG);
    }

    public void printServicesBySeverity() {
        serviceLogMap.entrySet()
                .stream().sorted(comparator.reversed())
                .forEach(e -> System.out.println(print(e.getKey())));
    }

    public String print(String service) {
        return String.format("Service name: %s Count of microservices: %d " +
                        "Total logs in service: %d Average severity for all logs: %.02f Average number of logs per microservice: %.02f",
                service,
                serviceLogMap.get(service).stream().map(Log::getMicroserviceName).collect(Collectors.toSet()).size(),
                serviceLogMap.get(service).size(),
                serviceLogMap.get(service).stream().mapToDouble(Log::severity).average().getAsDouble(),
                (double) serviceLogMap.get(service).size() / serviceLogMap.get(service).stream().map(Log::getMicroserviceName).collect(Collectors.toSet()).size()
        );

    }

    public Map<Integer, Integer> getSeverityDistribution(String service, String microservice) {

        if (microservice != null)
            return microServiceLogMap.get(microservice).stream().filter(log -> log.getServiceName().equals(service))
                    .collect(Collectors.groupingBy(Log::severity, Collectors.collectingAndThen(
                            Collectors.counting(),
                            Long::intValue
                    )));
        return serviceLogMap.get(service).stream()
                .collect(Collectors.groupingBy(Log::severity, Collectors.collectingAndThen(
                        Collectors.counting(),
                        Long::intValue
                )));
    }

    public void displayLogs(String service, String microservice, String order) {

        if (microservice == null)
            microServiceLogMap.values().stream().flatMap(Collection::stream)
                    .filter(log -> log.getServiceName().equals(service))
                    .sorted(ComparatorFactory.createComparator(order)).
                    forEach(System.out::println);
        else
            microServiceLogMap.get(microservice).stream().filter(log -> log.getServiceName().equals(service))
                    .sorted(ComparatorFactory.createComparator(order))
                    .forEach(System.out::println);

    }
}

public class LogsTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        LogCollector collector = new LogCollector();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.startsWith("addLog")) {
                collector.addLog(line.replace("addLog ", ""));
            } else if (line.startsWith("printServicesBySeverity")) {
                collector.printServicesBySeverity();
            } else if (line.startsWith("getSeverityDistribution")) {
                String[] parts = line.split("\\s+");
                String service = parts[1];
                String microservice = null;
                if (parts.length == 3) {
                    microservice = parts[2];
                }
                collector.getSeverityDistribution(service, microservice).forEach((k, v) -> System.out.printf("%d -> %d%n", k, v));
            } else if (line.startsWith("displayLogs")) {
                String[] parts = line.split("\\s+");
                String service = parts[1];
                String microservice = null;
                String order = null;
                if (parts.length == 4) {
                    microservice = parts[2];
                    order = parts[3];
                } else {
                    order = parts[2];
                }
                System.out.println(line);

                collector.displayLogs(service, microservice, order);
            }
        }
    }
}

