package pt.ipbeja.estig.po2.snowman.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class HelloApplication extends Application {

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

    public static void main(String[] args) {
        launch(args);
    }
}