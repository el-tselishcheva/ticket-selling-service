package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    public static Stage primaryStage;
    private static Scene scene;

    public Scene getScene() {
        return scene;
    }

    public static void setScene(Scene scene) {
        Main.scene = scene;
    }

    @Override
    public void start(Stage newPrimaryStage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fx/Main.fxml")));

        Scene mainScene = new Scene(root);
        setScene(mainScene);
//        newPrimaryStage.getIcons().add(new Image("images/railway_icon.png"));
        newPrimaryStage.setTitle("(не)яндекс");
        newPrimaryStage.setScene(mainScene);
        newPrimaryStage.setResizable(true);
        newPrimaryStage.show();

        primaryStage = newPrimaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}