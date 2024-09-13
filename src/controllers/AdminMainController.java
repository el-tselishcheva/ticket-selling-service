package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.DatabaseHandler;
import main.Main;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class AdminMainController extends DatabaseHandler implements Initializable {
    public static Stage tripRouteStage = null;
    private static int userId;
    private static int meanId;
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
    public TextField surname, name, patronymic;
    @FXML
    public DatePicker birthdate;
    @FXML
    public RadioButton maleGender, femaleGender;
    @FXML
    public TextField phone, email;
    @FXML
    public Label adminRole, loginAndPasswordErrorMessage, phoneAndEmailErrorMessage;
    @FXML
    public Button save;
    @FXML
    public ComboBox tripsList, meansOfTransportList, companiesList;
    @FXML
    public TextField tripNumber;
    @FXML
    public Label tripErrorMessage;
    @FXML
    public Button newRow, addTrip, saveTrip, saveNewTrip;
    @FXML
    public BorderPane tripInfo;
    @FXML
    public VBox tripRoutePane;
    @FXML
    public StackPane saveTripPane, saveNewTripPane;
    @FXML
    public StackPane personalAccountPane, tripsPane, routesPane/*, schedulesPane*/;

    private void setNewLoginListener() {
        newLogin.textProperty().addListener((observable, oldValue, newValue) -> {
            if (checkLogin("admins", newLogin.getText())) {
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
            if (!checkPassword("admins", userId, oldPassword.getText())) {
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
            } else if (checkPassword("admins", userId, newPassword.getText())) {
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

    private void setMeanOfTransportListener() {
        meansOfTransportList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                meanId = getIdByName("means_of_transport", "name", meansOfTransportList.getValue().toString());
            } catch (NullPointerException ignored) { }
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
            MainController.setAdminId(0);
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
    public void showPersonalAccount(MouseEvent event) {
        showAccountPane();
    }

    @FXML
    public void showTripsHandler(MouseEvent event) {
        tripsPane.setVisible(true);
        tripsPane.setDisable(false);
        tripInfo.setVisible(false);
        tripInfo.setDisable(true);

        personalAccountPane.setVisible(false);
        personalAccountPane.setDisable(true);
        routesPane.setVisible(false);
        routesPane.setDisable(true);
    }

    @FXML
    public void showRoutesHandler(MouseEvent event) {

    }

    @FXML
    public void showSchedulesHandler(MouseEvent event) {

    }

    public void showAccountPane() {
        personalAccountPane.setVisible(true);
        personalAccountPane.setDisable(false);

        tripsPane.setVisible(false);
        tripsPane.setDisable(true);
        routesPane.setVisible(false);
        routesPane.setDisable(true);

        adminRole.setText(getNameById("admin_roles", "name", Integer.parseInt(getNameById("admins", "role_id", userId))));
        oldLogin.setText(getNameById("admins", "login", userId));
        phone.setText(getNameById("admins", "phone_number", userId));
        email.setText(getNameById("admins", "email", userId));

        if (getNameById("admins", "surname", userId) != null) {
            if (!getNameById("admins", "surname", userId).equals("")) {
                surname.setText(getNameById("admins", "surname", userId));
            }
        }

        if (getNameById("admins", "name", userId) != null) {
            if (!getNameById("admins", "name", userId).equals("")) {
                surname.setText(getNameById("admins", "name", userId));
            }
        }

        if (getNameById("admins", "patronymic", userId) != null) {
            if (!getNameById("admins", "patronymic", userId).equals("")) {
                surname.setText(getNameById("admins", "patronymic", userId));
            }
        }

        if (getNameById("admins", "gender_id", userId) != null) {
            if (!getNameById("admins", "gender_id", userId).equals("")) {
                if (getNameById("admins", "gender_id", userId).equals("1")) {
                    maleGender.setSelected(true);
                } else if (getNameById("admins", "gender_id", userId).equals("2")) {
                    femaleGender.setSelected(true);
                }
            }
        }

        if (getNameById("admins", "birth_date", userId) != null) {
            if (!getNameById("admins", "birth_date", userId).equals("")) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDate date = LocalDate.parse(getNameById("admins", "birth_date", userId), formatter);
                birthdate.setValue(date);
            }
        }
    }

    @FXML
    private void showTripsList(MouseEvent event) {
        ObservableList<String> tripsObservList = FXCollections.observableArrayList();
        LinkedList<String> trips = getTripNumbers();
        for (String trip : trips) {
            tripsObservList.add(trip + " "
                    + getSettlementByTripNumber("from_id", trip) + "—"
                    + getSettlementByTripNumber("to_id", trip));
        }
        tripsList.setItems(tripsObservList);
    }

    public void showTrip(int tripIndex) {
        tripInfo.setVisible(true);
        tripInfo.setDisable(false);

        saveTripPane.setVisible(true);
        saveTripPane.setDisable(false);
        saveNewTripPane.setVisible(false);
        saveNewTripPane.setDisable(true);

        if (!tripsList.getSelectionModel().isEmpty()) {
            LinkedList<String> trips = getTripNumbers();
            String trip = trips.get(tripIndex);

            int tripId = getIdByName("trips", "trip_number", trip);
            meansOfTransportList.setPromptText(getNameById("means_of_transport", "name", Integer.parseInt(getNameById("trips", "mean_id", tripId))));
            companiesList.setPromptText(getNameById("companies", "name", Integer.parseInt(getNameById("trips", "company_id", tripId))));

            tripNumber.setText(trip);
        } else {
            meansOfTransportList.setPromptText(null);
            tripNumber.clear();
//            fromHubList.setPromptText(null);
//            toHubList.setPromptText(null);
        }
    }

    @FXML
    private void showNewTrip(MouseEvent event) {
        tripsList.getSelectionModel().clearSelection();
        tripsList.setPromptText("рейсы");

        tripInfo.setVisible(true);
        tripInfo.setDisable(false);

        saveNewTripPane.setVisible(true);
        saveNewTripPane.setDisable(false);
        saveTripPane.setVisible(false);
        saveTripPane.setDisable(true);

        meansOfTransportList.getSelectionModel().clearSelection();
        companiesList.getSelectionModel().clearSelection();
        tripNumber.clear();

        while (tripRoutePane.getChildren().size() > 1) {
            tripRoutePane.getChildren().remove(1);
        }

        Node node = tripRoutePane.getChildren().get(0);
        HBox hbox = (HBox) node;
        Node node1 = hbox.getChildren().get(0);
        ComboBox fromHub = (ComboBox) node1;
        fromHub.getSelectionModel().clearSelection();
        Node node2 = hbox.getChildren().get(2);
        ComboBox toHub = (ComboBox) node2;
        toHub.getSelectionModel().clearSelection();
        Node node3 = hbox.getChildren().get(4);
        TextField stopTime = (TextField) node3;
        stopTime.clear();
        Node node4 = hbox.getChildren().get(6);
        TextField betweenHubsTime = (TextField) node4;
        betweenHubsTime.clear();
    }

    @FXML
    public void showMeansOfTransportList(MouseEvent event) {
        ObservableList<String> meansObservList = FXCollections.observableArrayList();
        LinkedList<String> means = getNames("means_of_transport", "name");
        for (String mean : means) {
            meansObservList.add(mean);
        }
        meansOfTransportList.setItems(meansObservList);
    }

    @FXML
    public void showCompaniesList(MouseEvent event) {
        ObservableList<String> companiesObservList = FXCollections.observableArrayList();
        LinkedList<String> companies = getNames("companies", "name");
        for (String company : companies) {
            companiesObservList.add(company);
        }
        companiesList.setItems(companiesObservList);
    }

    @FXML
    public void addNewRow(MouseEvent event) {
        try {
//            FXMLLoader loader = new FXMLLoader();
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getClassLoader().getResource("fx/TripRoute.fxml")));
//            loader.setLocation(getClass().getResource("fx/TripRoute.fxml"));
            HBox row = loader.load();
            TripRouteController tripRouteController = loader.getController();
            tripRoutePane.getChildren().add(row);

            Node node = tripRoutePane.getChildren().get(tripRoutePane.getChildren().size() - 1);
            HBox hbox = (HBox) node;
            Node node1 = hbox.getChildren().get(0);
            ComboBox fromHub = (ComboBox) node1;
            fromHub.setOnMouseClicked(event1 -> showFromHubList(event1, fromHub));
            Node node2 = hbox.getChildren().get(2);
            ComboBox toHub = (ComboBox) node2;
            toHub.setOnMouseClicked(event2 -> showToHubList(event2, toHub));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void showFromHubList(MouseEvent event, ComboBox fromHubList) {
        ObservableList<String> hubsObservList = FXCollections.observableArrayList();
        LinkedList<String> hubs = getHubsByMean(meanId);
        for (String hub : hubs) {
            hubsObservList.add(hub);
        }
        fromHubList.setItems(hubsObservList);
    }

    @FXML
    public void showToHubList(MouseEvent event, ComboBox toHubList) {
        ObservableList<String> hubsObservList = FXCollections.observableArrayList();
        LinkedList<String> hubs = getHubsByMean(meanId);
        for (String hub : hubs) {
            hubsObservList.add(hub);
        }
        toHubList.setItems(hubsObservList);
    }

    @FXML
    private void saveTripInfo(MouseEvent event) {

    }

    @FXML
    private void saveNewTripInfo(MouseEvent event) {
//        if (meansOfTransportList.getSelectionModel().getSelectedItem() == null
//                || tripNumber.getText().isBlank()
//                || toHubList.getSelectionModel().getSelectedItem() == null
//                || fromHubList.getSelectionModel().getSelectedItem() == null) {
//            tripErrorMessage.setText("все поля должны быть заполнены.");
//            tripErrorMessage.setVisible(true);
//            saveNewTrip.setDisable(true);
//        } else {
//            tripErrorMessage.setVisible(false);
//            saveNewTrip.setDisable(false);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("внимание!!");
            alert.setHeaderText(null);
            alert.setContentText("сохранить все измененения?");

            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == ButtonType.OK) {
                int tripMeanId = getIdByName("means_of_transport", "name", meansOfTransportList.getValue().toString());
                int companyId = getIdByName("companies", "name", companiesList.getValue().toString());

                Node node = tripRoutePane.getChildren().get(0);
                HBox hbox = (HBox) node;
                Node node1 = hbox.getChildren().get(0);
                ComboBox fromHub = (ComboBox) node1;
                int departureHubId = getIdByName("transport_hubs", "name", fromHub.getValue().toString());

                node = tripRoutePane.getChildren().get(tripRoutePane.getChildren().size() - 1);
                hbox = (HBox) node;
                node1 = hbox.getChildren().get(2);
                ComboBox toHub = (ComboBox) node1;
                int arrivalHubId = getIdByName("transport_hubs", "name", toHub.getValue().toString());

                insertNewTrip(tripMeanId, tripNumber.getText(), departureHubId, arrivalHubId, companyId);

                for (Node child : tripRoutePane.getChildren()) {
                    hbox = (HBox) child;
                    node1 = hbox.getChildren().get(0);
                    fromHub = (ComboBox) node1;
                    Node node2 = hbox.getChildren().get(2);
                    toHub = (ComboBox) node2;
                    Node node3 = hbox.getChildren().get(4);
                    TextField stopTime = (TextField) node3;
                    Node node4 = hbox.getChildren().get(6);
                    TextField betweenHubsTime = (TextField) node4;

                    int toHubId = 0;
                    if (toHub.getSelectionModel().getSelectedItem() != null) {
                        toHubId = getIdByName("transport_hubs", "name", toHub.getValue().toString());
                    }

                    int fromHubId = getIdByName("transport_hubs", "name", fromHub.getValue().toString());
                    int tripId = getIdByName("trips", "trip_number", tripNumber.getText());

                    insertNewRoute(tripId, fromHubId, toHubId, stopTime.getText(), betweenHubsTime.getText());
                }

                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("добавление рейса");
                alert.setHeaderText(null);
                alert.setContentText("рейс был успешно добавлен.");
                alert.showAndWait();
            }
//        }
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
                updateAccountInfo("admins", userId, "login", newLogin.getText());
            }

            if (!oldPassword.getText().isBlank() && !newPassword.getText().isBlank()) {
                updateAccountInfo("admins", userId, "password", newPassword.getText());
            }

            if (!surname.getText().isBlank()) {
                updateAccountInfo("admins", userId, "surname", surname.getText());
            }

            if (!name.getText().isBlank()) {
                updateAccountInfo("admins", userId, "name", name.getText());
            }

            if (!patronymic.getText().isBlank()) {
                updateAccountInfo("admins", userId, "patronymic", patronymic.getText());
            }

            if (!phone.getText().isBlank()) {
                updateAccountInfo("admins", userId, "phone_number", phone.getText());
            }

            if (!email.getText().isBlank()) {
                updateAccountInfo("admins", userId, "email", email.getText());
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = birthdate.getValue();
            String birthdateStr = "NULL";
            if (date != null) {
                birthdateStr = (formatter.format(date));
                updateAccountInfo("admins", userId, "birth_date", birthdateStr);
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
                updateAccountInfo("admins", userId, "gender_id", genderStr);
            }

            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("сохранение");
            alert.setHeaderText(null);
            alert.setContentText("данные были успешно сохранены.");
            alert.showAndWait();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            getDbConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        userId = MainController.getAdminId();
        meanId = 0;

        ToggleGroup groupInfo = new ToggleGroup();
        maleGender.setToggleGroup(groupInfo);
        femaleGender.setToggleGroup(groupInfo);

        setNewLoginListener();
        setOldPasswordListener();
        setNewPasswordListener();
        setNewEmailListener();
        setMeanOfTransportListener();

        tripsList.setOnAction(event -> showTrip(tripsList.getSelectionModel().getSelectedIndex()));

        Node node = tripRoutePane.getChildren().get(tripRoutePane.getChildren().size() - 1);
        HBox hbox = (HBox) node;
        Node node1 = hbox.getChildren().get(0);
        ComboBox fromHub = (ComboBox) node1;
        fromHub.setOnMouseClicked(event1 -> showFromHubList(event1, fromHub));
        Node node2 = hbox.getChildren().get(2);
        ComboBox toHub = (ComboBox) node2;
        toHub.setOnMouseClicked(event2 -> showToHubList(event2, toHub));

        showAccountPane();
    }
}
