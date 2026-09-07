package happybot;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controls the main HappyBot window.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    private HappyBot happyBot;

    /**
     * Configures behavior that depends only on controls from the FXML view.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
    }

    /**
     * Supplies the chatbot and displays its welcome message.
     *
     * @param happyBot The chatbot that responds to commands.
     */
    public void setHappyBot(HappyBot happyBot) {
        this.happyBot = happyBot;
        dialogContainer.getChildren().add(DialogBox.getBotDialog(happyBot.getWelcomeMessage()));
        // Requests focus after JavaFX finishes displaying and laying out the window.
        Platform.runLater(userInput::requestFocus);
    }

    /**
     * Displays the user's command and HappyBot's response.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().strip();
        if (input.isEmpty()) {
            return;
        }

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getBotDialog(happyBot.getResponse(input)));
        userInput.clear();
    }
}
