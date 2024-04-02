package application;

public class Movie {

	private String title;
    private int year;
    private String genre;
    private String origin;
    private String director;
    private String star;
    private String imdbLink;

    public Movie(String title, int year, String genre, String origin, String director, String star, String imdbLink) {
        this.title = title;
        this.year = year;
        this.genre = genre;
        this.origin = origin;
        this.director = director;
        this.star = star;
        this.imdbLink = imdbLink;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public String getGenre() {
        return genre;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDirector() {
        return director;
    }

    public String getStar() {
        return star;
    }

    public String getImdbLink() {
        return imdbLink;
    }

    public String[] getEntities() {
        return new String[]{String.valueOf(year), genre, origin, director, star};
    }

    public boolean guessTitle(String userGuess) {
        return title.equalsIgnoreCase(userGuess.trim());
    }

    public boolean hasCommonEntities(String userGuess) {
        return genre.contains(userGuess) || origin.contains(userGuess) || director.contains(userGuess) || star.contains(userGuess);
    }
}