package controllers;

import javafx.scene.control.Button;
import main.DatabaseHandler;
import main.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController extends DatabaseHandler implements Initializable {
    @FXML
    public Button adminAccount;
    public static Stage adminAuthorizationStage = null;
    public static Stage authorizationStage = null;
    public static Stage accountStage = null;
    private static int userId = 0;
    private static int adminId = 0;

    public static int getUserId() {
        return userId;
    }

    public static void setUserId(int userId) {
        MainController.userId = userId;
    }

    public static int getAdminId() {
        return adminId;
    }

    public static void setAdminId(int adminId) {
        MainController.adminId = adminId;
    }

    @FXML
    public void authorizeAdmin(MouseEvent mouseEvent) {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getClassLoader().getResource("fx/AdminAuthorization.fxml")));
        Stage adminAuthorizationWindow = new Stage();
        try {
            adminAuthorizationWindow.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
//            authorizationWindow.getIcons().add(new Image("images/railway_icon.png"));
        adminAuthorizationWindow.setTitle("вход");
        adminAuthorizationWindow.initModality(Modality.WINDOW_MODAL);
        adminAuthorizationWindow.initOwner(Main.primaryStage);
        adminAuthorizationWindow.show();
//            authorizationWindow.setResizable(false);

        adminAuthorizationStage = adminAuthorizationWindow;
    }

    @FXML
    public void handlePersonalAccount(MouseEvent mouseEvent) {
        if (userId == 0) {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getClassLoader().getResource("fx/Authorization.fxml")));
            Stage authorizationWindow = new Stage();
            try {
                authorizationWindow.setScene(new Scene(loader.load()));
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
//            authorizationWindow.getIcons().add(new Image("images/railway_icon.png"));
            authorizationWindow.setTitle("вход");
            authorizationWindow.initModality(Modality.WINDOW_MODAL);
            authorizationWindow.initOwner(Main.primaryStage);
            authorizationWindow.show();
//            authorizationWindow.setResizable(false);

            authorizationStage = authorizationWindow;
        } else {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getClassLoader().getResource("fx/PersonalAccount.fxml")));
            Stage accountWindow = new Stage();
            try {
                accountWindow.setScene(new Scene(loader.load()));
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
//            accountWindow.getIcons().add(new Image("images/railway_icon.png"));
            accountWindow.setTitle("профиль пользователя");
            accountWindow.initModality(Modality.WINDOW_MODAL);
            accountWindow.initOwner(Main.primaryStage);
            accountWindow.show();

            accountStage = accountWindow;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            getDbConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (userId != 0) {
            adminAccount.setVisible(false);
            adminAccount.setDisable(true);
        }
    }
}