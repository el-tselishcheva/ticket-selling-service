package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.DatabaseHandler;
import main.Main;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class AuthorizationController extends DatabaseHandler implements Initializable {
    public static int userId = 0;
    public static Stage registrationStage = null;

    public static int getUserId() {
        return userId;
    }

    public static void setUserId(int userId) {
        AuthorizationController.userId = userId;
        MainController.setUserId(userId);
    }

    @FXML
    public TextField login;
    @FXML
    public PasswordField password;
    @FXML
    public Label errorMessage;

    @FXML
    private void showRegistrationWindow(MouseEvent event) {
        Parent registration = null;
        try {
            registration = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fx/Registration.fxml")));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        Scene registrationScene = new Scene(registration);
        Stage registrationWindow = new Stage();

//        registrationWindow.getIcons().add(new Image("images/railway_icon.png"));
        registrationWindow.setTitle("регистрация");
        registrationWindow.setScene(registrationScene);
        registrationWindow.initModality(Modality.WINDOW_MODAL);
        registrationWindow.initOwner(Main.primaryStage);
        registrationWindow.show();
//        registrationWindow.setResizable(false);
        MainController.authorizationStage.close();

        registrationStage = registrationWindow;
    }

    @FXML
    private void handleAuthorization() throws IOException {
        if (!checkLoginAndPassword("users", login.getText(), password.getText())) {
            errorMessage.setText("не совпадает пароль или логин.");
        } else {
            setUserId(getUserId("users", login.getText(), password.getText()));
            showMainWindow(Main.primaryStage);
            MainController.authorizationStage.close();
        }
    }

    private void showMainWindow(Stage newPrimaryStage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fx/Main.fxml")));

        Scene mainScene = new Scene(root);
        Main.setScene(mainScene);
//        newPrimaryStage.getIcons().add(new Image("images/railway_icon.png"));
        newPrimaryStage.setTitle("(не)яндекс");
        newPrimaryStage.setScene(mainScene);
        newPrimaryStage.setResizable(true);
        newPrimaryStage.show();
        MainController.authorizationStage.close();

        Main.primaryStage = newPrimaryStage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            getDbConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
