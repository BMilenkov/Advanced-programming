package K2.FootballTable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


class Team {
    private final String name;
    private final Set<Game> gameLost;
    private final Set<Game> gameWon;
    private final Set<Game> gameTie;
    private int goalRatio;

    public Team(String name) {
        this.name = name;
        this.goalRatio = 0;
        this.gameWon = new HashSet<>();
        this.gameLost = new HashSet<>();
        this.gameTie = new HashSet<>();
    }

    void addGameWon(Game game) {
        gameWon.add(game);
    }

    void addGameLost(Game game) {
        gameLost.add(game);
    }

    void addGameTie(Game game) {
        gameTie.add(game);
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return gameWon.size() * 3 + gameTie.size();
    }

    public int getGoalRatio() {
        return goalRatio;
    }

    public void increaseMyRatio(int goalRatio) {
        this.goalRatio += goalRatio;
    }

    public void decreaseMyRatio(int goalRatio) {
        this.goalRatio -= goalRatio;
    }

    @Override
    public String toString() {
        return String.format("%-15s%5s%5s%5s%5s%5s",
                name, gameWon.size() + gameLost.size() + gameTie.size(), gameWon.size(), gameTie.size(), gameLost.size(), getPoints());
    }
}

class Game {
    private String homeTeam;
    private String awayTeam;
    private int homeGoals;
    private int awayGoals;

    public Game(String homeTeam, String awayTeam, int homeGoals, int awayGoals) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeGoals = homeGoals;
        this.awayGoals = awayGoals;
    }

}

class FootballTable {
    private final Map<String, Team> teams;

    public FootballTable() {
        this.teams = new HashMap<>();
    }

    Comparator<Team> comparator = Comparator.comparing(Team::getPoints).thenComparing(Team::getGoalRatio).reversed().thenComparing(Team::getName);

    public void addGame(String homeTeam, String awayTeam, int homeGoals, int awayGoals) {
        Game game = new Game(homeTeam, awayTeam, homeGoals, awayGoals);
        if (!teams.containsKey(homeTeam))
            teams.put(homeTeam, new Team(homeTeam));
        if (!teams.containsKey(awayTeam))
            teams.put(awayTeam, new Team(awayTeam));

        int RATIO = homeGoals - awayGoals;
        teams.get(homeTeam).increaseMyRatio(RATIO);
        teams.get(awayTeam).decreaseMyRatio(RATIO);

        if (homeGoals > awayGoals) {
            teams.get(homeTeam).addGameWon(game);
            teams.get(awayTeam).addGameLost(game);
        } else if (homeGoals < awayGoals) {
            teams.get(awayTeam).addGameWon(game);
            teams.get(homeTeam).addGameLost(game);
        } else {
            teams.get(homeTeam).addGameTie(game);
            teams.get(awayTeam).addGameTie(game);
        }
    }

    public void printTable() {
        AtomicInteger atomicInteger = new AtomicInteger(1);
        teams.values().stream().sorted(comparator).forEach(team -> {
            System.out.printf("%2d. ", atomicInteger.get());
            System.out.println(team);
            atomicInteger.getAndIncrement();
        });
    }
}

public class FootballTableTest {
    public static void main(String[] args) throws IOException {
        FootballTable table = new FootballTable();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        reader.lines()
                .map(line -> line.split(";"))
                .forEach(parts -> table.addGame(parts[0], parts[1],
                        Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[3])));
        reader.close();
        System.out.println("=== TABLE ===");
        System.out.printf("%-19s%5s%5s%5s%5s%5s\n", "Team", "P", "W", "D", "L", "PTS");
        table.printTable();
    }
}