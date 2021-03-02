package uk.ac.soton.comp1206;

import javafx.application.Application;
import javafx.stage.Stage;
import uk.ac.soton.comp1206.Scenes.Menu;

public class App extends Application {
    public void start(Stage stage) {
        Stage test = new Stage();
        test.setScene(new Menu(700, 500));
        test.setTitle("TetrECS");

        test.show();
    }

    public static void main(String[] args) {
        launch();
    }
}