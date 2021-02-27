package me.uwu.haxpoll;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Hax.fxml"));
        primaryStage.setTitle("HaxPoll");
        primaryStage.setScene(new Scene(root, 300, 500));
        primaryStage.getScene().getStylesheets().add(String.valueOf(getClass().getResource("style.css")));
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
        primaryStage.show();
        primaryStage.resizableProperty().set(false);
    }
}
