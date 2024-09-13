package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.DatabaseHandler;
import main.Main;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class AdminAuthorizationController extends DatabaseHandler implements Initializable {
    public static int userId = 0;

    public static int getUserId() {
        return userId;
    }

    public static void setUserId(int userId) {
        AdminAuthorizationController.userId = userId;
        MainController.setAdminId(userId);
    }

    @FXML
    public TextField login;
    @FXML
    public PasswordField password;
    @FXML
    public Label errorMessage;

    @FXML
    private void handleAuthorization() throws IOException {
        if (!checkLoginAndPassword("admins", login.getText(), password.getText())) {
            errorMessage.setText("не совпадает пароль или логин.");
        } else {
            setUserId(getUserId("admins", login.getText(), password.getText()));
            showMainWindow(Main.primaryStage);
            MainController.adminAuthorizationStage.close();
        }
    }

    private void showMainWindow(Stage newPrimaryStage) throws IOException {
//        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fx/AdminMain.fxml")));
//
//        Scene mainScene = new Scene(root);
//        Main.setScene(mainScene);
////        newPrimaryStage.getIcons().add(new Image("images/railway_icon.png"));
//        newPrimaryStage.setTitle("админка");
//        newPrimaryStage.setScene(mainScene);
//        newPrimaryStage.setResizable(true);
//        newPrimaryStage.show();
//        MainController.adminAuthorizationStage.close();
//
//        Main.primaryStage = newPrimaryStage;

        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getClassLoader().getResource("fx/AdminMain.fxml")));
        Stage accountWindow = new Stage();
        try {
            accountWindow.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
//            accountWindow.getIcons().add(new Image("images/railway_icon.png"));
        accountWindow.setTitle("админка");
        accountWindow.show();

        MainController.accountStage = accountWindow;
        Main.primaryStage.close();
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
