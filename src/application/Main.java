package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {
	 private Database database;
	    private Movie currentMovie;
	    private Button guessButton;
	    private TextField guessTextField;
	    private Text feedbackText;
	    private VBox entitiesVBox;
	    private VBox commonEntitiesVBox;
	    private int remainingTries;
	    private int wins;
	    private int losses;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        database = new Database("imdb_top_250.csv");
        currentMovie = database.getRandomMovie();
        remainingTries = 5;
        wins = 0;
        losses = 0;

        primaryStage.setTitle("MOVIDLE - Guess the Movie Title");

        GridPane root = createRootPane();
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridPane createRootPane() {
        GridPane root = new GridPane();
        root.setAlignment(Pos.CENTER);
        root.setHgap(10);
        root.setVgap(10);
        root.setPadding(new Insets(10));

        Text titleText = createTitleText();
        root.add(titleText, 0, 0, 4, 1);

        VBox movieInfoBox = createMovieInfoBox();
        root.add(movieInfoBox, 0, 1, 4, 1);

        Label guessLabel = new Label("Enter your guess:");
        root.add(guessLabel, 0, 2);

        guessTextField = new TextField();
        root.add(guessTextField, 1, 2);

        guessButton = new Button("Guess");
        root.add(guessButton, 0, 3, 2, 1);

        feedbackText = new Text();
        feedbackText.setFont(Font.font(14));
        root.add(feedbackText, 0, 4, 2, 1);

        Text entitiesTitleText = new Text("Entities");
        entitiesTitleText.setFont(Font.font(16));
        root.add(entitiesTitleText, 0, 5, 2, 1);

        entitiesVBox = new VBox();
        root.add(entitiesVBox, 0, 6, 2, 1);

        Text commonEntitiesTitleText = new Text("Common Entities");
        commonEntitiesTitleText.setFont(Font.font(16));
        root.add(commonEntitiesTitleText, 2, 5, 2, 1);

        commonEntitiesVBox = new VBox();
        root.add(commonEntitiesVBox, 2, 6, 2, 1);

        Text statsText = new Text();
        statsText.setFont(Font.font(14));
        updateStatsText(statsText);
        root.add(statsText, 0, remainingTries + 6, 4, 1);

        handleGuessButton();

        return root;
    }

    private Text createTitleText() {
        Text titleText = new Text("MOVIDLE - Guess the Movie Title");
        titleText.setFont(Font.font(20));
        titleText.setFill(Color.DARKBLUE);
        return titleText;
    }

    private VBox createMovieInfoBox() {
        VBox movieInfoBox = new VBox(10);
        movieInfoBox.setAlignment(Pos.CENTER_LEFT);

        Text movieTitleText = new Text("Movie Information:");
        movieTitleText.setFont(Font.font(16));

        Text movieInfoText = new Text();
        movieInfoText.setFont(Font.font(14));
        updateMovieInfoText(movieInfoText);

        movieInfoBox.getChildren().addAll(movieTitleText, movieInfoText);
        return movieInfoBox;
    }

    private void updateMovieInfoText(Text movieInfoText) {
        String movieInfo = String.format("Year: %s\nGenre: %s\nOrigin: %s\nDirector: %s\nStar: %s",
                currentMovie.getYear(), currentMovie.getGenre(), currentMovie.getOrigin(),
                currentMovie.getDirector(), currentMovie.getStar());
        movieInfoText.setText(movieInfo);
    }
    private void updateCommonEntitiesText(String userGuess) {
        String[] movieEntities = currentMovie.getEntities();
        String[] guessEntities = userGuess.split(";");

        commonEntitiesVBox.getChildren().clear();
        for (String guessEntity : guessEntities) {
            for (String movieEntity : movieEntities) {
                if (guessEntity.trim().equalsIgnoreCase(movieEntity.trim())) {
                    Label commonEntityLabel = new Label(guessEntity.trim());
                    commonEntityLabel.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                    commonEntitiesVBox.getChildren().add(commonEntityLabel);
                    break;
                }
            }
        }
    }
    private void handleGuessButton() {
        guessButton.setOnAction(event -> {
            String userGuess = guessTextField.getText().trim();
            Movie guessedMovie = database.findMovieByTitle(userGuess); // This method should be added in the database class
            boolean isGuessCorrect = currentMovie.guessTitle(userGuess);

            if (isGuessCorrect) {
                feedbackText.setText("Correct!");
                feedbackText.setFill(Color.GREEN);
                wins++;
                updateStatsText();
                currentMovie = database.getRandomMovie();
                updateMovieInfoText();
            } else {
                if (remainingTries > 1) {
                    if (guessedMovie != null) {
                        String guessedEntities = String.join("; ", guessedMovie.getEntities());
                        updateEntitiesText(guessedEntities);
                        updateCommonEntitiesText(guessedEntities);
                    }
                    remainingTries--;
                    feedbackText.setText("Wrong guess. Try again.");
                    feedbackText.setFill(Color.RED);
                } else {
                    feedbackText.setText("Wrong guess. Out of tries.");
                    feedbackText.setFill(Color.RED);
                    losses++;
                    updateStatsText();
                    showResultDialog();
                    resetGame();
                    currentMovie = null;
                }
            }

            guessTextField.clear();
            guessTextField.requestFocus();
        });
    }

    private boolean hasCommonEntities(String userGuess) {
        String[] movieEntities = currentMovie.getEntities();
        String[] guessEntities = userGuess.split(";");

        for (String guessEntity : guessEntities) {
            for (String movieEntity : movieEntities) {
                if (guessEntity.trim().equalsIgnoreCase(movieEntity.trim())) {
                    return true;
                }
            }
        }

        return false;
    }

    private void updateEntitiesText(String userGuess) {
        String[] movieEntities = currentMovie.getEntities();
        String[] guessEntities = userGuess.split(";");

        entitiesVBox.getChildren().clear();
        for (String guessEntity : guessEntities) {
            boolean isCorrect = false;
            for (String movieEntity : movieEntities) {
                if (guessEntity.trim().equalsIgnoreCase(movieEntity.trim())) {
                    isCorrect = true;
                    break;
                }
            }
            
            Label entityLabel = new Label(guessEntity.trim());
            entityLabel.setBackground(new Background(new BackgroundFill(isCorrect ? Color.GREEN : Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
            entitiesVBox.getChildren().add(entityLabel);
        }
    }
    
    private void updateMovieInfoText() {
        String movieInfo = String.format("Year: %s\nGenre: %s\nOrigin: %s\nDirector: %s\nStar: %s",
                currentMovie.getYear(), currentMovie.getGenre(), currentMovie.getOrigin(),
                currentMovie.getDirector(), currentMovie.getStar());
        VBox movieInfoBox = (VBox) guessButton.getScene().getRoot().getChildrenUnmodifiable().get(1);
        Text movieInfoText = (Text) movieInfoBox.getChildren().get(1);
        movieInfoText.setText(movieInfo);
    }

    private void updateStatsText() {
        Text statsText = (Text) guessButton.getScene().getRoot().getChildrenUnmodifiable().get(
                guessButton.getScene().getRoot().getChildrenUnmodifiable().size() - 2);
        updateStatsText(statsText);
    }

    private void updateStatsText(Text statsText) {
        String stats = String.format("Wins: %d    Losses: %d", wins, losses);
        statsText.setText(stats);
    }

    private void showResultDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("Game Over");
        alert.setContentText("You have exhausted all your tries. Game over!");

        alert.showAndWait();
    }

    private void resetGame() {
        remainingTries = 5;
        wins = 0;
        losses = 0;
    }
}