package application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Database {

    private List<Movie> movies;

    public Database(String csvFilePath) {
        movies = readMoviesFromCSV(csvFilePath);
    }

    private List<Movie> readMoviesFromCSV(String csvFilePath) {
        List<Movie> movieList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            // Skip the first line (column headers)
            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                if (data.length >= 8) {
                    String title = data[1].trim();
                    int year = Integer.parseInt(data[2].trim());
                    String genre = data[3].trim();
                    String origin = data[4].trim();
                    String director = data[5].trim();
                    String star = data[6].trim();
                    String imdbLink = data[7].trim();
                    movieList.add(new Movie(title, year, genre, origin, director, star, imdbLink));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return movieList;
    }
    
    public Movie findMovieByTitle(String title) {
        for (Movie movie : movies) {
            if (movie.getTitle().equalsIgnoreCase(title)) {
                return movie;
            }
        }
        return null;
    }
    
    public Movie getMovie(String userGuess) {
        for (Movie movie : movies) {
            if (movie.getTitle().equalsIgnoreCase(userGuess)) {
                return movie;
            }
        }
        return null;
    }

    public Movie getRandomMovie() {
        Random random = new Random();
        int randomIndex = random.nextInt(movies.size());
        return movies.get(randomIndex);
    }

    public boolean hasCommonEntities(String userGuess) {
        for (Movie movie : movies) {
            if (movie.hasCommonEntities(userGuess)) {
                return true;
            }
        }
        return false;
    }
}