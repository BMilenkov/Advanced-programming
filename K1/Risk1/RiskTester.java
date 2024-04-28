package K1.Risk1;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Attack {

    List<String> attacks;
    List<String> defences;

    public Attack() {
        attacks = new ArrayList<>();
        defences = new ArrayList<>();
    }

    public Attack(String line) {
        String[] parts = line.split(";");
        this.attacks = Arrays.asList(parts[0].split("\\s+"));
        this.defences = Arrays.asList(parts[1].split("\\s+"));
    }

    public boolean isSuccessful() {

        Collections.sort(attacks);
        Collections.sort(defences);

        return IntStream.range(0, attacks.size())
                .allMatch(i -> Integer.parseInt(attacks.get(i)) >
                        Integer.parseInt(defences.get(i)));
    }
}

class Risk {
    private List<Attack> attacks;

    public Risk() {
        this.attacks = new ArrayList<>();
    }

    public int processAttacksData(InputStream is) {

        BufferedReader bufferedReader = new BufferedReader
                (new InputStreamReader(is));

        attacks = bufferedReader.lines()
                .map(Attack::new)
                .collect(Collectors.toList());

        return (int) attacks.stream()
                .filter(Attack::isSuccessful)
                .count();
    }
}


public class RiskTester {
    public static void main(String[] args) {

        Risk risk = new Risk();
        System.out.println(risk.processAttacksData(System.in));

    }
}