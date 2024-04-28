package K2.Task_Decorator;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


class DeadlineNotValidException extends Exception {
    public DeadlineNotValidException(String line) {
        super(line);
    }
}

interface ITask {
    int getPriority();

    LocalDateTime getDeadLine();

    String getCategory();
}

class BaseTask implements ITask {

    String category;
    String TaskName;
    String description;

    public BaseTask(String category, String taskName, String description) {
        this.category = category;
        TaskName = taskName;
        this.description = description;
    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public LocalDateTime getDeadLine() {
        return LocalDateTime.MAX;
    }

    @Override
    public String getCategory() {
        return category;
    }


    @Override
    public String toString() {
        return "Task{" +
                "name='" + TaskName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

abstract class TaskDecorator implements ITask {
    ITask wrap;

    public TaskDecorator(ITask wrap) {
        this.wrap = wrap;
    }
}

class ExpiringTaskDecorator extends TaskDecorator {
    private final LocalDateTime deadLine;

    public ExpiringTaskDecorator(ITask wrap, LocalDateTime deadLine)
            throws DeadlineNotValidException {
        super(wrap);
        if (deadLine.isBefore(LocalDateTime.of(2020, 6, 2, 23, 59, 59)))
            throw new DeadlineNotValidException(String.format("The deadline %s has already passed", deadLine));
        this.deadLine = deadLine;
    }

    @Override
    public int getPriority() {
        return wrap.getPriority();
    }

    @Override
    public LocalDateTime getDeadLine() {
        return deadLine;
    }

    @Override
    public String getCategory() {
        return wrap.getCategory();
    }

    @Override
    public String toString() {
        return wrap.toString().substring(0, wrap.toString().length() - 1) +
                ", deadline=" + deadLine +
                '}';
    }
}

class PriorityTaskDecorator extends TaskDecorator {
    private final int priority;

    public PriorityTaskDecorator(ITask wrap, int priority) {
        super(wrap);
        this.priority = priority;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public LocalDateTime getDeadLine() {
        return wrap.getDeadLine();
    }

    @Override
    public String getCategory() {
        return wrap.getCategory();
    }


    public String toString() {
        return wrap.toString().substring(0, wrap.toString().length() - 1) +
                ", priority=" + priority +
                '}';
    }
}


class TaskFactory {
    public static ITask createTask(String line) throws DeadlineNotValidException {
        String[] parts = line.split(",");
        String category = parts[0];
        String name = parts[1];
        String description = parts[2];
        BaseTask task = new BaseTask(category, name, description);
        if (parts.length == 3)
            return task;
        else if (parts.length == 4) {
            try {
                int priority = Integer.parseInt(parts[3]);
                return new PriorityTaskDecorator(task, priority);
            } catch (Exception e) {
                LocalDateTime ldt = LocalDateTime.parse(parts[3]);
                return new ExpiringTaskDecorator(task, ldt);
            }
        } else {
            LocalDateTime ldt = LocalDateTime.parse(parts[3]);
            int priority = Integer.parseInt(parts[4]);
            return new PriorityTaskDecorator(new ExpiringTaskDecorator(task, ldt), priority);
        }
    }
}


class TaskManager {
    Map<String, List<ITask>> tasks;

    public TaskManager() {
        this.tasks = new TreeMap<>();
    }

    public void readTasks(InputStream inputStream) {
        tasks = new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .map(line -> {
                    try {
                        return TaskFactory.createTask(line);
                    } catch (DeadlineNotValidException e) {
                        System.out.println(e.getMessage());
                    }
                    return null;
                }).filter(Objects::nonNull)
                .collect(Collectors.groupingBy(ITask::getCategory
                        , TreeMap::new,
                        Collectors.toList()));
    }

    public void printTasks(OutputStream os, boolean includePriority, boolean includeCategory) {
        PrintWriter pw = new PrintWriter(os);

        Comparator<ITask> priorityComparator = Comparator.comparing(ITask::getPriority)
                .thenComparing(task -> Duration.between(LocalDateTime.now(),task.getDeadLine() ));

        Comparator<ITask> simpleComparator = Comparator.comparing(
                task -> Duration.between(LocalDateTime.now(),
                        task.getDeadLine()));

        if (includeCategory) {
            tasks.forEach((key, value) -> {
                pw.println(key.toUpperCase());
                value.stream()
                        .sorted(includePriority ? priorityComparator : simpleComparator)
                        .forEach(pw::println);
            });
        } else {
            tasks.values()
                    .stream().flatMap(Collection::stream)
                    .sorted(includePriority ? priorityComparator : simpleComparator)
                    .forEach(pw::println);
        }
        pw.flush();
    }
}


public class TasksManagerTest {

    public static void main(String[] args) {

        TaskManager manager = new TaskManager();
        System.out.println("Tasks reading");
        manager.readTasks(System.in);
        System.out.println("By categories with priority");
        manager.printTasks(System.out, true, true);
        System.out.println("-------------------------");
        System.out.println("By categories without priority");
        manager.printTasks(System.out, false, true);
        System.out.println("-------------------------");
        System.out.println("All tasks without priority");
        manager.printTasks(System.out, false, false);
        System.out.println("-------------------------");
        System.out.println("All tasks with priority");
        manager.printTasks(System.out, true, false);
        System.out.println("-------------------------");

    }
}

