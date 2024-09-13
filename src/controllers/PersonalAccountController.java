package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import main.DatabaseHandler;
import main.Main;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class PersonalAccountController extends DatabaseHandler implements Initializable {
    private static int userId;
    private static boolean checkToSave = true;
    private static boolean checkNewLogin, checkOldPassword, checkNewPassword, checkEmail;

    private final String LOGIN_REGEX = "^(?=.*[a-z0-9]$)[a-z][a-z\\d._\\-]{2,20}$";
    private final String PASSWORD_REGEX = "((?=.*[A-Z])(?=.*[a-z])(?=.*[0-9]).{8,})";
    private final String EMAIL_REGEX = "^(?=(.{1,64}@))(?=(.{6,255}$))((([A-Za-zА-Яа-яЁё0-9]+)[-._]?" +
            "([A-Za-z0-9А-Яа-яЁё]+)?)@(([A-Za-zА-Яа-яЁё][A-Za-zА-Яа-яЁё\\\\-_]*.)+([A-Za-zА-Яа-яЁё0-9]{2,})" +
            "|(((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9]).){3}(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9]))))$";

    private final String NULL_FX_BORDER_COLOR = "-fx-border-color: null";
    private final String RED_FX_BORDER_COLOR = "-fx-border-color: red";

    @FXML
    public TextField oldLogin, newLogin;
    @FXML
    public PasswordField oldPassword, newPassword;
    @FXML
    public TextField surname, passengerSurname;
    @FXML
    public TextField name, passengerName;
    @FXML
    public TextField patronymic, passengerPatronymic;
    @FXML
    public DatePicker birthdate, passengerBirthdate, passengerDocumentValidity;
    @FXML
    public RadioButton maleGender, passengerMale;
    @FXML
    public RadioButton femaleGender, passengerFemale;
    @FXML
    public TextField phone, email;
    @FXML
    public TextField passengerDocumentNumber;
    @FXML
    public Label loginAndPasswordErrorMessage, phoneAndEmailErrorMessage, passengerErrorMessage;
    @FXML
    public ComboBox passengerList, passengerCountry, passengerDocumentType;
    @FXML
    public Button save, savePassenger, saveNewPassenger;
    @FXML
    public StackPane personalAccountPane;
    @FXML
    public StackPane myPassengersPane;
    @FXML
    public StackPane myTicketsPane;
    @FXML
    public BorderPane passengerInfo;
    @FXML
    public StackPane savePassengerPane;
    @FXML
    public StackPane saveNewPassengerPane;

    private void setNewLoginListener() {
        newLogin.textProperty().addListener((observable, oldValue, newValue) -> {
            if (checkLogin("users", newLogin.getText())) {
                loginAndPasswordErrorMessage.setText("логин занят.");
                newLogin.setStyle(RED_FX_BORDER_COLOR);
                loginAndPasswordErrorMessage.setVisible(true);
                checkNewLogin = false;
            } else if (newLogin.getText().matches(LOGIN_REGEX)) {
                newLogin.setStyle(NULL_FX_BORDER_COLOR);
                loginAndPasswordErrorMessage.setVisible(false);
                checkNewLogin = true;
            } else {
                loginAndPasswordErrorMessage.setText("допустимы только латинские буквы, цифры, точка и знак нижнего подчеркивания; не менее 3-х символов.");
                newLogin.setStyle(RED_FX_BORDER_COLOR);
                loginAndPasswordErrorMessage.setVisible(true);
                checkNewLogin = false;
            }
        });
    }

    private void setOldPasswordListener() {
        oldPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!checkPassword("users", userId, oldPassword.getText())) {
                loginAndPasswordErrorMessage.setText("старый пароль введен неверно.");
                oldPassword.setStyle(RED_FX_BORDER_COLOR);
                loginAndPasswordErrorMessage.setVisible(true);
                checkOldPassword = false;
            } else {
                oldPassword.setStyle(NULL_FX_BORDER_COLOR);
                loginAndPasswordErrorMessage.setVisible(false);
                checkOldPassword = true;
            }
        });
    }

    private void setNewPasswordListener() {
        newPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldPassword.getText().isBlank()) {
                loginAndPasswordErrorMessage.setText("чтобы изменить пароль, необходимо подтвердить старый.");
                newPassword.setStyle(RED_FX_BORDER_COLOR);
                loginAndPasswordErrorMessage.setVisible(true);
                checkNewPassword = false;
            } else if (checkPassword("users", userId, newPassword.getText())) {
                loginAndPasswordErrorMessage.setText("старый и новый пароли не должны совпадать.");
                newPassword.setStyle(RED_FX_BORDER_COLOR);
                loginAndPasswordErrorMessage.setVisible(true);
                checkNewPassword = false;
            } else if (newPassword.getText().matches(PASSWORD_REGEX)) {
                newPassword.setStyle(NULL_FX_BORDER_COLOR);
                loginAndPasswordErrorMessage.setVisible(false);
                checkNewPassword = true;
            } else {
                loginAndPasswordErrorMessage.setText("допустимы только прописные и строчные буквы латинского алфавита, а также цифры; не менее 8-ми символов.");
                newPassword.setStyle(RED_FX_BORDER_COLOR);
                loginAndPasswordErrorMessage.setVisible(true);
                checkNewPassword = false;
            }
        });
    }

    private void setNewEmailListener() {
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
    private void logOut(MouseEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("выход");
        alert.setHeaderText(null);
        alert.setContentText("вы уверены, что хотите выйти из аккаунта?");

        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == ButtonType.OK) {
            MainController.setUserId(0);
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fx/Main.fxml")));

            Scene mainScene = new Scene(root);
            Main.setScene(mainScene);
//            newPrimaryStage.getIcons().add(new Image("images/railway_icon.png"));
            Main.primaryStage.setTitle("(не)яндекс");
            Main.primaryStage.setScene(mainScene);
            Main.primaryStage.setResizable(true);

            Main.primaryStage.show();
            MainController.accountStage.close();
        }
    }

    @FXML
    private void saveChanges(MouseEvent event) {
        checkToSave = checkNewLogin && checkOldPassword && checkNewPassword && checkEmail;

        if (!checkToSave) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("ошибка!!");
            alert.setHeaderText(null);
            alert.setContentText("некоторые данные были введены некорректно.\nпожалуйста, повторите попытку.");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("внимание!!");
        alert.setHeaderText(null);
        alert.setContentText("сохранить все измененения?");

        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == ButtonType.OK) {
            if (!newLogin.getText().isBlank()) {
                updateAccountInfo("users", userId, "login", newLogin.getText());
            }

            if (!oldPassword.getText().isBlank() && !newPassword.getText().isBlank()) {
                updateAccountInfo("users", userId, "password", newPassword.getText());
            }

            if (!surname.getText().isBlank()) {
                updateAccountInfo("users", userId, "surname", surname.getText());
            }

            if (!name.getText().isBlank()) {
                updateAccountInfo("users", userId, "name", name.getText());
            }

            if (!patronymic.getText().isBlank()) {
                updateAccountInfo("users", userId, "patronymic", patronymic.getText());
            }

            if (!phone.getText().isBlank()) {
                updateAccountInfo("users", userId, "phone_number", phone.getText());
            }

            if (!email.getText().isBlank()) {
                updateAccountInfo("users", userId, "email", email.getText());
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = birthdate.getValue();
            String birthdateStr = "NULL";
            if (date != null) {
                birthdateStr = (formatter.format(date));
                updateAccountInfo("users", userId, "birth_date", birthdateStr);
            }

            int genderId = 0;
            String genderStr;
            if (maleGender.isSelected()) {
                genderId = 1;
            } else if (femaleGender.isSelected()) {
                genderId = 2;
            }
            if (genderId != 0) {
                genderStr = Integer.toString(genderId);
                updateAccountInfo("users", userId, "gender_id", genderStr);
            }

            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("сохранение");
            alert.setHeaderText(null);
            alert.setContentText("данные были успешно сохранены.");
            alert.showAndWait();
        }
    }

    @FXML
    public void showPersonalAccount(MouseEvent event) {
        showAccountPane();
    }

    public void showAccountPane() {
        personalAccountPane.setVisible(true);
        personalAccountPane.setDisable(false);

        myPassengersPane.setVisible(false);
        myPassengersPane.setDisable(true);
        myTicketsPane.setVisible(false);
        myTicketsPane.setDisable(true);

        oldLogin.setText(getNameById("users", "login", userId));
        phone.setText(getNameById("users", "phone_number", userId));
        email.setText(getNameById("users", "email", userId));

        if (!getNameById("users", "surname", userId).equals("")) {
            surname.setText(getNameById("users", "surname", userId));
        }

        if (!getNameById("users", "name", userId).equals("")) {
            surname.setText(getNameById("users", "name", userId));
        }

        if (!getNameById("users", "patronymic", userId).equals("")) {
            surname.setText(getNameById("users", "patronymic", userId));
        }

        if (!getNameById("users", "gender_id", userId).equals("")) {
            if (getNameById("users", "gender_id", userId).equals("1")) {
                maleGender.setSelected(true);
            } else if (getNameById("users", "gender_id", userId).equals("2")) {
                femaleGender.setSelected(true);
            }
        }

        if (!getNameById("users", "birth_date", userId).equals("")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDate date = LocalDate.parse(getNameById("users", "birth_date", userId), formatter);
            birthdate.setValue(date);
        }
    }

    @FXML
    public void showPassengers(MouseEvent event) {
        myPassengersPane.setVisible(true);
        myPassengersPane.setDisable(false);
        passengerInfo.setVisible(false);
        passengerInfo.setDisable(true);

        personalAccountPane.setVisible(false);
        personalAccountPane.setDisable(true);
        myTicketsPane.setVisible(false);
        myTicketsPane.setDisable(true);
    }

    @FXML
    public void showPassengerList(MouseEvent event) {
        ObservableList<String> passengerObservList = FXCollections.observableArrayList();
        LinkedList<LinkedList<String>> passengers = getPassengersNames(userId);
        for (LinkedList<String> passenger : passengers) {
            passengerObservList.add(passenger.get(0) + " " + passenger.get(1) + " " + passenger.get(2));
        }
        passengerList.setItems(passengerObservList);
    }

    @FXML
    public void showCountryList(MouseEvent event) {
        ObservableList<String> countryObservList = FXCollections.observableArrayList();
        LinkedList<String> countries = getNames("countries", "name");
        for (String country : countries) {
            countryObservList.add(country);
        }
        passengerCountry.setItems(countryObservList);
    }

    @FXML
    public void showDocumentTypeList(MouseEvent event) {
        ObservableList<String> docTypeObservList = FXCollections.observableArrayList();
        LinkedList<String> docTypes = getNames("documents", "name");
        for (String docType : docTypes) {
            docTypeObservList.add(docType);
        }
        passengerDocumentType.setItems(docTypeObservList);
    }

    public void showPassenger(int passengerIndex) {
        passengerInfo.setVisible(true);
        passengerInfo.setDisable(false);

        savePassengerPane.setVisible(true);
        savePassengerPane.setDisable(false);
        saveNewPassengerPane.setVisible(false);
        saveNewPassengerPane.setDisable(true);

        if (!passengerList.getSelectionModel().isEmpty()) {
            LinkedList<LinkedList<String>> passengers = getPassengersInfo(userId);
            LinkedList<String> passenger = passengers.get(passengerIndex);

            passengerSurname.setText(passenger.get(0));
            passengerName.setText(passenger.get(1));
            passengerPatronymic.setText(passenger.get(2));

            if (passenger.get(3).equals("1")) {
                passengerMale.setSelected(true);
            } else if (passenger.get(3).equals("2")) {
                passengerFemale.setSelected(true);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDate date = LocalDate.parse(passenger.get(4), formatter);
            passengerBirthdate.setValue(date);

            passengerCountry.setPromptText(getNameById("countries", "name", Integer.parseInt(passenger.get(5))));
            passengerDocumentType.setPromptText(getNameById("documents", "name", Integer.parseInt(passenger.get(6))));

            passengerDocumentNumber.setText(passenger.get(7));

            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            date = LocalDate.parse(passenger.get(8), formatter);
            passengerDocumentValidity.setValue(date);
        } else {
            passengerSurname.clear();
            passengerName.clear();
            passengerPatronymic.clear();
            passengerBirthdate.setValue(null);
            passengerMale.setSelected(false);
            passengerFemale.setSelected(false);
            passengerCountry.setPromptText(null);
            passengerDocumentType.setPromptText(null);
            passengerDocumentNumber.clear();
            passengerDocumentValidity.setValue(null);
        }
    }

    @FXML
    public void showNewPassenger(MouseEvent event) {
        passengerList.getSelectionModel().clearSelection();
        passengerList.setPromptText("список пассажиров");

        passengerInfo.setVisible(true);
        passengerInfo.setDisable(false);

        saveNewPassengerPane.setVisible(true);
        saveNewPassengerPane.setDisable(false);
        savePassengerPane.setVisible(false);
        savePassengerPane.setDisable(true);
    }

    @FXML
    public void savePassengerInfo(MouseEvent event) {

    }

    @FXML
    public void saveNewPassengerInfo(MouseEvent event) {
        if (passengerSurname.getText().isBlank() || passengerName.getText().isBlank() || passengerPatronymic.getText().isBlank()
                || passengerBirthdate.getValue() == null || (!passengerMale.isSelected() && !passengerFemale.isSelected())
                || passengerCountry.getSelectionModel().getSelectedItem() == null || passengerDocumentType.getValue() == null
                || passengerDocumentNumber.getText().isBlank() || passengerDocumentValidity.getValue() == null) {
            passengerErrorMessage.setText("все поля должны быть заполнены.");
            passengerErrorMessage.setVisible(true);
            saveNewPassenger.setDisable(true);
        } else {
            passengerErrorMessage.setVisible(false);
            saveNewPassenger.setDisable(false);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("внимание!!");
            alert.setHeaderText(null);
            alert.setContentText("сохранить все измененения?");

            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == ButtonType.OK) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate birthdate = passengerBirthdate.getValue();
                String passengerBirthdateStr = "NULL";
                LocalDate documentValidity = passengerDocumentValidity.getValue();
                String passengerDocumentValidityStr = "NULL";
                if (birthdate != null) {
                    passengerBirthdateStr = (formatter.format(birthdate));
                    passengerDocumentValidityStr = (formatter.format(documentValidity));
                }

                int passengerGenderId = 0;
                if (passengerMale.isSelected()) {
                    passengerGenderId = 1;
                } else if (passengerFemale.isSelected()) {
                    passengerGenderId = 2;
                }

                int passengerCategoryId = 0;
                int passengerAge = Period.between(birthdate, LocalDate.now()).getYears();
                if (passengerAge < 5) {
                    passengerCategoryId = 1;
                } else if (passengerAge < 10) {
                    passengerCategoryId = 2;
                } else {
                    passengerCategoryId = 3;
                }

                int passengerCountryId = getIdByName("countries", "name", passengerCountry.getValue().toString());
                int passengerDocumentId = getIdByName("documents", "name", passengerDocumentType.getValue().toString());;

                insertNewPassenger(userId, passengerCategoryId, passengerSurname.getText(), passengerName.getText(), passengerPatronymic.getText(), passengerBirthdateStr, passengerGenderId, passengerCountryId, passengerDocumentId, passengerDocumentNumber.getText(), passengerDocumentValidityStr);

                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("добавление пассажира");
                alert.setHeaderText(null);
                alert.setContentText("пассажир был успешно добавлен.");
                alert.showAndWait();
            }
        }
    }

    @FXML
    public void showTickets(MouseEvent event) {
        myTicketsPane.setVisible(true);
        myTicketsPane.setDisable(false);

        personalAccountPane.setVisible(false);
        personalAccountPane.setDisable(true);
        myPassengersPane.setVisible(false);
        myPassengersPane.setDisable(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            getDbConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        userId = MainController.getUserId();

        ToggleGroup groupInfo = new ToggleGroup();
        maleGender.setToggleGroup(groupInfo);
        femaleGender.setToggleGroup(groupInfo);

        ToggleGroup groupPassenger = new ToggleGroup();
        passengerMale.setToggleGroup(groupPassenger);
        passengerFemale.setToggleGroup(groupPassenger);

        setNewLoginListener();
        setOldPasswordListener();
        setNewPasswordListener();
        setNewEmailListener();

        passengerList.setOnAction(event -> showPassenger(passengerList.getSelectionModel().getSelectedIndex()));
        showAccountPane();
    }
}