/**
 * Autores:
 * Afonso Freitas - 21467
 * Miguel Correia - 21194
 */
package pt.ipbeja.estig.po2.snowman.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    /**
     * Starts the JavaFX application, sets up the main window and scene.
     * @param stage the primary stage for this application
     * @throws Exception if an error occurs during application start
     */
    @Override
    public void start(Stage stage) throws Exception {
        View view = new View();
        Scene scene = new Scene(view, 900, 800);
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/MONSTER.png")));
        stage.setTitle("A Good Snowman Is Hard To Build");
        stage.show();

        view.requestFocus();
    }

    /**
     * Main method to launch the JavaFX application.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}