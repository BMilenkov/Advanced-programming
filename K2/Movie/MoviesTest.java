package K2.Movie;


import java.util.*;
import java.util.stream.Collectors;


class Movie {
    private final String title;
    private double ratingCoef;
    private final List<Integer> ratings;

    public Movie(String title, List<Integer> ratings) {
        this.title = title;
        this.ratings = ratings;
    }

    public double getAverageRating() {
        return ratings.stream().mapToDouble(r -> r).average().orElse(0);
    }

    public String getTitle() {
        return title;
    }

    public int getNumOfRatings() {
        return ratings.size();
    }

    public void setRatingCoef(int maxRatings) {
        ratingCoef = getAverageRating() * ratings.size() / maxRatings;
    }

    public double getRatingCoef() {
        return ratingCoef;
    }

    @Override
    public String toString() {
        //Story of Women (1989) (6.63) of 8 ratings
        return String.format("%s (%.02f) of %d ratings", title, getAverageRating(), ratings.size());
    }

}

class MoviesList {
    private final List<Movie> movies;

    Comparator<Movie> comparator = Comparator.comparing(Movie::getAverageRating).reversed().thenComparing(Movie::getTitle);

    public MoviesList() {
        this.movies = new ArrayList<>();
    }

    public void addMovie(String title, Integer[] ratings) {
        movies.add(new Movie(title, Arrays.asList(ratings)));
    }

    public List<Movie> top10ByAvgRating() {
        return movies.stream().sorted(comparator).limit(10).collect(Collectors.toList());
    }

    public void setRatingCoefToAllMovies() {
        int coef = movies.stream().mapToInt(Movie::getNumOfRatings).sum();
        movies.forEach(movie -> movie.setRatingCoef(coef));
    }

    public List<Movie> top10ByRatingCoef() {
        setRatingCoefToAllMovies();
        return movies.stream().sorted(Comparator.comparing(Movie::getRatingCoef).reversed().thenComparing(Movie::getTitle)).limit(10).collect(Collectors.toList());
    }
}


public class MoviesTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MoviesList moviesList = new MoviesList();
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String title = scanner.nextLine();
            int x = scanner.nextInt();
            Integer[] ratings = new Integer[x];
            for (int j = 0; j < x; ++j) {
                ratings[j] = scanner.nextInt();
            }
            scanner.nextLine();
            moviesList.addMovie(title, ratings);
        }
        scanner.close();
        List<Movie> movies = moviesList.top10ByAvgRating();
        System.out.println("=== TOP 10 BY AVERAGE RATING ===");
        for (Movie movie : movies) {
            System.out.println(movie);
        }
        movies = moviesList.top10ByRatingCoef();
        System.out.println("=== TOP 10 BY RATING COEFFICIENT ===");
        for (Movie movie : movies) {
            System.out.println(movie);
        }
    }
}
