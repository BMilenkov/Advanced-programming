package K1.Quiz;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;


class InvalidOperationException extends Exception {

    public InvalidOperationException(String message) {
        super(message);
    }
}

abstract class Question implements Comparable<Question> {

    private String text;
    private double points;
    private String correctAnswer;

    public Question(String text, double points, String correctAnswer) {
        this.text = text;
        this.points = points;
        this.correctAnswer = correctAnswer;
    }

    @Override
    public int compareTo(Question o) {
        return Double.compare(points, o.points);
    }

    public String getText() {
        return text;
    }

    public double getPoints() {
        return points;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public abstract String getType();

    public abstract double getWrongAnswerPoints();
}

class MC extends Question {

    private String type;

    public MC(String type, String text, double points, String correctAnswer)
            throws InvalidOperationException {
        super(text, points, correctAnswer);
        this.type = type;
        if (!correctAnswer.equals("A") && !correctAnswer.equals("B") &&
                !correctAnswer.equals("C") && !correctAnswer.equals("D") && !correctAnswer.equals("E"))
            throw new InvalidOperationException
                    (String.format("%s is not allowed option for this question", correctAnswer));
    }

    @Override
    public String toString() {
        return String.format("Multiple Choice Question: %s Points %d Answer: %s", getText(),
                (int) getPoints(), getCorrectAnswer());
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public double getWrongAnswerPoints() {
        return (-1) * 0.2 * getPoints();
    }
}


class TF extends Question {
    private String type;

    public TF(String type, String text, double points, String correctAnswer) {
        super(text, points, correctAnswer);
        this.type = type;
    }

    public String toString() {
        return String.format("True/False Question: %s Points: %d Answer: %s", getText(),
                (int) getPoints(), getCorrectAnswer());
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public double getWrongAnswerPoints() {
        return 0.0;
    }
}

class Quiz {
    private List<Question> questions;

    public Quiz() {
        this.questions = new ArrayList<>();
    }

    public void addQuestion(String questionData) {
        String[] parts = questionData.split(";");
        Question question = null;

        if (parts[0].equals("TF")) {
            question = new TF(parts[0], parts[1], Double.parseDouble(parts[2]), parts[3]);
            questions.add(question);
        } else {
            try {
                question = new MC(parts[0], parts[1], Double.parseDouble(parts[2]), parts[3]);
                questions.add(question);
            } catch (InvalidOperationException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void printQuiz(OutputStream os) {
        PrintWriter pw = new PrintWriter(os);
        questions.stream().sorted(Comparator.reverseOrder()).forEach(pw::println);
        pw.flush();
    }

    public void answerQuiz(List<String> answers, OutputStream os)
            throws InvalidOperationException {
        PrintWriter pw = new PrintWriter(os);
        if (answers.size() != questions.size())
            throw new InvalidOperationException("Answers and questions must" +
                    " be of same length!");

        double tottalPoints = 0.0;
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            String answer = answers.get(i);

            if (q.getCorrectAnswer().equals(answer)) {
                pw.printf("%d. %.02f\n", i + 1, q.getPoints());
                tottalPoints += q.getPoints();
            } else {
                pw.printf("%d. %.02f\n", i + 1, q.getWrongAnswerPoints());
                tottalPoints += q.getWrongAnswerPoints();
            }
        }
        pw.printf("Total points: %.02f", tottalPoints);
        pw.flush();
    }
}


public class QuizTest {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        Quiz quiz = new Quiz();

        int questions = Integer.parseInt(sc.nextLine());

        for (int i = 0; i < questions; i++) {
            quiz.addQuestion(sc.nextLine());
        }

        List<String> answers = new ArrayList<>();

        int answersCount = Integer.parseInt(sc.nextLine());

        for (int i = 0; i < answersCount; i++) {
            answers.add(sc.nextLine());
        }

        int testCase = Integer.parseInt(sc.nextLine());

        if (testCase == 1) {
            quiz.printQuiz(System.out);
        } else if (testCase == 2) {
            try {
                quiz.answerQuiz(answers, System.out);
            } catch (InvalidOperationException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Invalid test case");
        }
    }
}

