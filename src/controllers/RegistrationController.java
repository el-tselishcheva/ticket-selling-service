package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import main.DatabaseHandler;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class RegistrationController extends DatabaseHandler implements Initializable {
    private static boolean checkToSignIn = true;
    private static boolean checkLogin, checkPassword, checkEmail, checkPhone;

    private final String LOGIN_REGEX = "^(?=.*[a-z0-9]$)[a-z][a-z\\d._\\-]{2,20}$";
    private final String PASSWORD_REGEX = "((?=.*[A-Z])(?=.*[a-z])(?=.*[0-9]).{8,})";
    private final String EMAIL_REGEX = "^(?=(.{1,64}@))(?=(.{6,255}$))((([A-Za-zА-Яа-яЁё0-9]+)[-._]?" +
            "([A-Za-z0-9А-Яа-яЁё]+)?)@(([A-Za-zА-Яа-яЁё][A-Za-zА-Яа-яЁё\\\\-_]*.)+([A-Za-zА-Яа-яЁё0-9]{2,})" +
            "|(((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9]).){3}(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9]))))$";

    private final String NULL_FX_BORDER_COLOR = "-fx-border-color: null";
    private final String RED_FX_BORDER_COLOR = "-fx-border-color: red";

    @FXML
    public TextField login;
    @FXML
    public PasswordField password;
    @FXML
    public TextField surname;
    @FXML
    public TextField name;
    @FXML
    public TextField patronymic;
    @FXML
    public DatePicker birthdate;
    @FXML
    public RadioButton maleGender;
    @FXML
    public RadioButton femaleGender;
    @FXML
    public TextField phone;
    @FXML
    public TextField email;
    @FXML
    public Label loginAndPasswordErrorMessage;
    @FXML
    public Label phoneAndEmailErrorMessage;
    @FXML
    public Button registration;

    private void setLoginListener() {
        login.textProperty().addListener((observable, oldValue, newValue) -> {
            if (checkLogin("users", login.getText())) {
                loginAndPasswordErrorMessage.setText("логин занят.");
                login.setStyle(RED_FX_BORDER_COLOR);
                loginAndPasswordErrorMessage.setVisible(true);
                checkLogin = false;
            } else if (login.getText().matches(LOGIN_REGEX)) {
                login.setStyle(NULL_FX_BORDER_COLOR);
                loginAndPasswordErrorMessage.setVisible(false);
                checkLogin = true;
            } else {
                loginAndPasswordErrorMessage.setText("допустимы только латинские буквы, цифры, точка и знак нижнего подчеркивания; не менее 3-х символов.");
                login.setStyle(RED_FX_BORDER_COLOR);
                loginAndPasswordErrorMessage.setVisible(true);
                checkLogin = false;
            }
        });
    }

    private void setPasswordListener() {
        password.textProperty().addListener((observable, oldValue, newValue) -> {
            if (password.getText().matches(PASSWORD_REGEX)) {
                password.setStyle(NULL_FX_BORDER_COLOR);
                loginAndPasswordErrorMessage.setVisible(false);
                checkPassword = true;
            } else {
                loginAndPasswordErrorMessage.setText("допустимы только прописные и строчные буквы латинского алфавита, а также цифры; не менее 8-ми символов.");
                password.setStyle(RED_FX_BORDER_COLOR);
                loginAndPasswordErrorMessage.setVisible(true);
                checkPassword = false;
            }
        });
    }

    private void setEmailListener() {
        email.textProperty().addListener((observable, oldValue, newValue) -> {
            if (email.getText().matches(EMAIL_REGEX)) {
                email.setStyle(NULL_FX_BORDER_COLOR);
                phoneAndEmailErrorMessage.setVisible(false);
                checkEmail = true;
            } else {
                email.setStyle(RED_FX_BORDER_COLOR);
                phoneAndEmailErrorMessage.setText("введенный e-mail не соответствует формату.");
                phoneAndEmailErrorMessage.setVisible(true);
                checkEmail = false;
            }
        });
    }

    @FXML
    public void showAuthorizationWindow(MouseEvent event) {
        checkPhone = phone.getText().isBlank();
        checkToSignIn = checkLogin && checkPassword && checkEmail && checkPhone;

        if (checkToSignIn || email.getText().isBlank()) {
            email.setStyle(RED_FX_BORDER_COLOR);
            phone.setStyle(RED_FX_BORDER_COLOR);
            phoneAndEmailErrorMessage.setText("заполнены не все из обязательных полей.");
            phoneAndEmailErrorMessage.setVisible(true);
            return;
        } else if (!checkToSignIn) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("ошибка!!");
            alert.setHeaderText(null);
            alert.setContentText("некоторые данные были введены некорректно.\nпожалуйста, повторите попытку.");
            alert.showAndWait();
            return;
        }

        int genderId = 0;
        String gender = "NULL";
        if (maleGender.isSelected()) {
        genderId = 1;
        } else if (femaleGender.isSelected()) {
        genderId = 2;
        }
        if (genderId != 0) {
        gender = Integer.toString(genderId);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = birthdate.getValue();
        String birthdateStr = "NULL";
        if (date != null) {
        birthdateStr = (formatter.format(date));
        }

        addNewUser(login.getText(), password.getText(), surname.getText(), name.getText(), patronymic.getText(), birthdateStr, gender, phone.getText(), email.getText());
        AuthorizationController.registrationStage.close();
        MainController.authorizationStage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            getDbConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        ToggleGroup group = new ToggleGroup();
        maleGender.setToggleGroup(group);
        femaleGender.setToggleGroup(group);

        setLoginListener();
        setPasswordListener();
        setEmailListener();
    }
}
