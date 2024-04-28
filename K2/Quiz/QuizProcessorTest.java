package K2.Quiz;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;


class QuizCanNotBeProcessedException extends Exception {
    public QuizCanNotBeProcessedException(String line) {
        super(line);
    }
}

class Quiz {
    private final String id;
    private final List<String> correctAnswers;
    private final List<String> answers;


    //DONT NEED!
    public Quiz(String id, List<String> correctAnswers, List<String> answers) {
        this.id = id;
        this.correctAnswers = correctAnswers;
        this.answers = answers;
    }

    public Quiz(String line)
            throws QuizCanNotBeProcessedException {
        String[] parts = line.split(";");
        this.id = parts[0];
        this.correctAnswers = Arrays.asList(parts[1].split(","));
        this.answers = Arrays.asList(parts[2].split(","));
        if (correctAnswers.size() != answers.size())
            throw new QuizCanNotBeProcessedException
                    ("A quiz must have same number of correct and selected answers");
    }

    public String getId() {
        return id;
    }

    public double getPoints() {
        double points = 0;
        for (int i = 0; i < correctAnswers.size(); i++) {
            if (correctAnswers.get(i).equals(answers.get(i)))
                points += 1;
            else points -= 0.25;
        }
        return points;
    }
}


class QuizProcessor {


    public static Map<String, Double> processAnswers(InputStream in) {

        BufferedReader bf = new BufferedReader(new InputStreamReader(in));
        return bf.lines()
                .map(line -> {
                    try {
                        return new Quiz(line);
                    } catch (QuizCanNotBeProcessedException e) {
                        System.out.println(e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Quiz::getId, TreeMap::new,
                        Collectors.summingDouble(Quiz::getPoints)));


    }
}

public class QuizProcessorTest {
    public static void main(String[] args) {
        QuizProcessor.processAnswers(System.in)
                .forEach((k, v) -> System.out.printf("%s -> %.2f%n", k, v));
    }
}