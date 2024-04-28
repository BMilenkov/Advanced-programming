package K1.Subtitles;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


class Subtitle {

    private int id;
    private String startTime;
    private String endTime;
    private String text;

    public Subtitle(int id, String startTime, String endTime,
                    String text) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.text = text;
    }

    @Override
    public String toString() {
        return String.format(id + "\n" + startTime + " --> " + endTime + "\n" + text);
    }


    public int StringToInt(String s) {
        String[] parts = s.split(",");
        int miliseconds = Integer.parseInt(parts[1]);
        String[] sp = parts[0].split(":");
        miliseconds += Integer.parseInt(sp[0]) * 60 * 60 * 1000;
        miliseconds += Integer.parseInt(sp[1]) * 60 * 1000;
        miliseconds += Integer.parseInt(sp[2]) * 1000;
        return miliseconds;
    }

    public String intToString(int ms) {
        int hour = ms / 1000 / 60 / 60;
        ms = ms % (1000 * 60 * 60);
        int minutes = ms / 1000 / 60;
        ms = ms % (1000 * 60);
        int seconds = ms / 1000;
        ms = ms % (1000);

        return String.format("%02d:%02d:%02d,%03d", hour, minutes, seconds, ms);
    }

    public void shift(int ms) {
        int miliseconds = StringToInt(startTime) + ms;
        startTime = intToString(miliseconds);
        miliseconds = StringToInt(endTime) + ms;
        endTime = intToString(miliseconds);
    }
}


class Subtitles {
    private List<Subtitle> subtitles;

    public Subtitles() {
        this.subtitles = new ArrayList<>();
    }

    public int loadSubtitles(InputStream inputStream)
            throws IOException {
        Scanner scanner = new Scanner(inputStream);

        while (scanner.hasNextLine()) {
            int ID = Integer.parseInt(scanner.nextLine());

            String[] parts = scanner.nextLine().split("-->");
            String startTime = parts[0].trim();
            String endTime = parts[1].trim();
            StringBuilder text = new StringBuilder();
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                if (line.length() == 0)
                    break;
                text.append(line).append("\n");
            }
            subtitles.add(new Subtitle(ID, startTime, endTime, text.toString()));
        }
        scanner.close();
        return subtitles.size();
    }

    public void print() {
        subtitles.forEach(System.out::println);
    }

    public void shift(int ms) {
        subtitles.forEach(e -> e.shift(ms));
    }
}


public class SubtitlesTest {
    public static void main(String[] args) {
        Subtitles subtitles = new Subtitles();
        int n = 0;
        try {
            n = subtitles.loadSubtitles(System.in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("+++++ ORIGINIAL SUBTITLES +++++");
        subtitles.print();
        int shift = n * 37;
        shift = (shift % 2 == 1) ? -shift : shift;
        System.out.printf("SHIFT FOR %d ms%n", shift);
        subtitles.shift(shift);
        System.out.println("+++++ SHIFTED SUBTITLES +++++");
        subtitles.print();
    }
}
