package com.dsa.dsa_spellchecker_project;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

/**
 * JavaFX GUI for the Spell Checker application with logo support.
 */
public class SpellCheckerGUI extends Application {

    private SpellChecker spellChecker;
    private TextField inputField;
    private FlowPane suggestionsPane;
    private VBox mainLayout;
    private final String DICTIONARY_PATH = "small_dictionary.txt";
    private final Color DARK_BG_COLOR = Color.WHITE;
    private final Color SUGGESTION_BG_COLOR = Color.rgb(54,81,94);
    private final Color TEXT_COLOR = Color.WHITE;

    @Override
    public void start(Stage primaryStage) {
        // Check if dictionary file exists
        File dictionaryFile = new File(DICTIONARY_PATH);
        if (!dictionaryFile.exists()) {
            System.err.println("Dictionary file not found at: " + DICTIONARY_PATH);
            System.err.println("Please ensure the dictionary file is in the correct location.");
            return;
        }

        // Initialize spell checker
        spellChecker = new SpellChecker(DICTIONARY_PATH);

        // Create main layout
        mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setPadding(new Insets(30, 20, 30, 20));
        mainLayout.setBackground(new Background(new BackgroundFill(
                DARK_BG_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));

        // Add logo
        try {
            Image logoImage = new Image(getClass().getResourceAsStream("/com/dsa/DSA_SpellChecker_Project/AAST-LOGO-BLUE.png"));
            ImageView logoView = new ImageView(logoImage);
            logoView.setFitWidth(130);  // Adjust logo width
            logoView.setPreserveRatio(true);
            logoView.setSmooth(true);

            HBox logoContainer = new HBox(logoView);
            logoContainer.setAlignment(Pos.CENTER);
            logoContainer.setPadding(new Insets(0, 0, 25, 0));  // Bottom padding

            mainLayout.getChildren().add(logoContainer);
        } catch (NullPointerException e) {
            System.err.println("Logo image not found! Ensure it's in:");
            System.err.println("src/main/resources/com/dsa/testing_gui/logo.png");
            // Continue without logo
        }

        // Create input field
        inputField = new TextField();
        inputField.setPromptText("Type here...");
        inputField.setStyle("-fx-background-color: #cdebf9; " +
                "-fx-text-fill: black; " +
                "-fx-font-size: 18px; " +
                "-fx-padding: 10px;");
        inputField.setPrefWidth(400);

        // Add key event listener
        inputField.addEventHandler(KeyEvent.KEY_RELEASED, this::handleTextInput);

        // Create suggestions pane
        suggestionsPane = new FlowPane();
        suggestionsPane.setHgap(10);
        suggestionsPane.setVgap(10);
        suggestionsPane.setAlignment(Pos.CENTER_LEFT);
        suggestionsPane.setPrefWrapLength(400);

        // Add components to main layout
        mainLayout.getChildren().addAll(inputField, suggestionsPane);

        // Create scene
        Scene scene = new Scene(mainLayout, 500, 350);  // Increased height for logo
        scene.setFill(DARK_BG_COLOR);

        // Configure stage
        primaryStage.setTitle("Spell Checker");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void handleTextInput(KeyEvent event) {
        String text = inputField.getText().trim();
        suggestionsPane.getChildren().clear();

        if (text.isEmpty()) {
            return;
        }

        List<String> words = spellChecker.splitText(text);
        if (words.isEmpty()) {
            return;
        }

        String lastWord = words.get(words.size() - 1);

        if (!spellChecker.checkWord(lastWord)) {
            List<String> suggestions = spellChecker.suggestCorrections(lastWord);

            int count = 0;
            for (String suggestion : suggestions) {
                if (count >= 5) break;
                HBox suggestionChip = createSuggestionChip(suggestion);
                suggestionsPane.getChildren().add(suggestionChip);
                count++;
            }
        }
    }

    private HBox createSuggestionChip(String word) {
        HBox chip = new HBox();
        chip.setAlignment(Pos.CENTER);
        chip.setPadding(new Insets(8, 15, 8, 15));

        chip.setBackground(new Background(new BackgroundFill(
                SUGGESTION_BG_COLOR, new CornerRadii(18), Insets.EMPTY)));

        Label wordLabel = new Label(word);
        wordLabel.setTextFill(TEXT_COLOR);
        wordLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));

        chip.getChildren().add(wordLabel);

        chip.setOnMouseClicked(event -> {
            String currentText = inputField.getText();
            List<String> words = spellChecker.splitText(currentText);

            if (!words.isEmpty()) {
                StringBuilder newText = new StringBuilder();
                for (int i = 0; i < words.size() - 1; i++) {
                    newText.append(words.get(i)).append(" ");
                }
                newText.append(word);

                inputField.setText(newText.toString());
                inputField.positionCaret(newText.length());
                suggestionsPane.getChildren().clear();
            }
        });

        chip.setOnMouseEntered(e ->
                chip.setBackground(new Background(new BackgroundFill(
                        Color.rgb(60, 60, 60), new CornerRadii(18), Insets.EMPTY))));

        chip.setOnMouseExited(e ->
                chip.setBackground(new Background(new BackgroundFill(
                        SUGGESTION_BG_COLOR, new CornerRadii(18), Insets.EMPTY))));

        return chip;
    }

    public static void main(String[] args) {
        launch(args);
    }
}