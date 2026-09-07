package happybot;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Displays HappyBot in a JavaFX window.
 */
public class Main extends Application {
    private static final double WINDOW_WIDTH = 500;
    private static final double WINDOW_HEIGHT = 650;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), WINDOW_WIDTH, WINDOW_HEIGHT);
        MainWindow mainWindow = fxmlLoader.getController();
        mainWindow.setHappyBot(new HappyBot());

        stage.setTitle("HappyBot");
        stage.setMinWidth(WINDOW_WIDTH);
        stage.setMinHeight(WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.show();
    }
}
