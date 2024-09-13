package main;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class DatabaseHandler {
    Connection dbConnection;
    protected String dbUrl = "jdbc:postgresql://127.0.0.1:5432/ticket-selling-service";
    protected String dbPass = "1234";
    protected String dbName = "postgres";

    public void getDbConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            dbConnection = DriverManager.getConnection(dbUrl, dbName, dbPass);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (dbConnection != null) {
            System.out.println("Successfully connected to database now.");
        } else {
            System.out.println("Failed to make connection to database.");
        }
    }

    public boolean checkLogin(String table, String login) {
        Statement statement;
        try {
            statement = dbConnection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ResultSet resultSet;
        String select = "SELECT id FROM " + table + " WHERE login = '" + login + "'";
        try {
            resultSet = statement.executeQuery(select);
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getUserId(String table, String login, String password) {
        Statement statement;
        try {
            statement = dbConnection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ResultSet resultSet;
        int userId = 0;
        String select = "SELECT id FROM " + table + " WHERE login = '" + login + "' AND password = '" + password + "'";
        try {
            resultSet = statement.executeQuery(select);
            if (resultSet.next()) {
                userId = resultSet.getInt("id");
            }
            return userId;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkPassword(String table, int id, String password) {
        Statement statement;
        try {
            statement = dbConnection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ResultSet resultSet;
        String select = "SELECT password FROM " + table + " WHERE id = " + id + " AND password = '" + password + "'";
        try {
            resultSet = statement.executeQuery(select);
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkLoginAndPassword(String table, String login, String password) {
        Statement statement;
        try {
            statement = dbConnection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ResultSet resultSet;
        String select = "SELECT id FROM " + table + " WHERE login = '" + login + "' AND password = '" + password + "'";
        try {
            resultSet = statement.executeQuery(select);
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateAccountInfo(String table, int id, String column, String change) {
        Statement statement;
        try {
            statement = dbConnection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (!column.equals("gender_id")) {
            change = "'" + change + "'";
        }
        String update = "UPDATE " + table + " SET " + column + " = " + change + " WHERE id = " + id;
        try {
            statement.executeUpdate(update);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addNewUser(String login, String password, String surname, String name, String patronymic, String birthdate, String genderId, String phone, String email) {
        Statement statement;
        try {
            statement = dbConnection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (!birthdate.equals("NULL")) {
            birthdate = "'" + birthdate + "'";
        }
        String insert = "INSERT INTO users (login, password, surname, name, patronymic, birth_date, gender_id, phone_number, email) " +
                "VALUES ('" + login + "', '" + password + "', '" + surname + "', '" + name + "', '" + patronymic +
                "', " + birthdate + ", " + genderId + ", '" + phone + "', '" + email + "')";
        try {
            statement.executeUpdate(insert);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public LinkedList<String> getNames(String table, String column) {
        Statement statement;
        try {
            statement = dbConnection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ResultSet resultSet;
        LinkedList<String> values = new LinkedList<>();
        String select = "SELECT " + column + " FROM " + table;
        try {
            resultSet = statement.executeQuery(select);
            while (resultSet.next()) {
                values.add(resultSet.getString(column));
            }
            return values;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public LinkedList<LinkedList<String>> getPassengersNames(int userId) {
        Statement statement;
        try {
            statement = dbConnection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ResultSet resultSet;
        LinkedList<LinkedList<String>> values = new LinkedList<>();
        String select = "SELECT surname, name, patronymic FROM passengers WHERE user_id = " + userId;
        try {
            resultSet = statement.executeQuery(select);
            while (resultSet.next()) {
                values.add(new LinkedList<String>(List.of(resultSet.getString("surname"), resultSet.getString("name"), resultSet.getString("patronymic"))));
            }
            return values;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public LinkedList<String> getHubsByMean(int meanId) {
        Statement statement;
        try {
            statement = dbConnection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ResultSet resultSet;
        LinkedList<String> values = new LinkedList<>();
        String select = "SELECT th.name " +
                "FROM transport_hubs th " +
                "INNER JOIN means_of_transport mot ON mot.id = th.mean_id " +
                "WHERE mot.id = " + meanId;
        try {
            resultSet = statement.executeQuery(select);
            while (resultSet.next()) {
                values.add(resultSet.getString("name"));
            }
            return values;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public LinkedList<LinkedList<String>> getPassengersInfo(int userId) {
        Statement statement;
        try {
            statement = dbConnection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ResultSet resultSet;
        LinkedList<LinkedList<String>> values = new LinkedList<>();
        String select = "SELECT surname, name, patronymic, gender_id, birth_date, " +
                "country_id, document_id, document_number, document_validity FROM passengers WHERE user_id = " + userId;
        try {
            resultSet = statement.executeQuery(select);
            while (resultSet.next()) {
                values.add(new LinkedList<String>(List.of(resultSet.getString("surname"), resultSet.getString("name"), resultSet.getString("patronymic"),
                        resultSet.getString("gender_id"), resultSet.getString("birth_date"), resultSet.getString("country_id"),
                        resultSet.getString("document_id"), resultSet.getString("document_number"), resultSet.getString("document_validity"))));
            }
            return values;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getIdByName(String table, String column, String name) {
        Statement statement;
        try {
            statement = dbConnection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ResultSet resultSet;
        int id = 0;
        String select = "SELECT id FROM " + table + " WHERE " + column + " = '" + name + "'";
        try {
            resultSet = statement.executeQuery(select);
            if (resultSet.next()) {
                id = resultSet.getInt("id");
            }
            return id;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getNameById(String table, String column, int id) {
        Statement statement;
        try {
            statement = dbConnection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ResultSet resultSet;
        String name = null;
        String select = "SELECT " + column + " FROM " + table + " WHERE id = " + id;
        try {
            resultSet = statement.executeQuery(select);
            if (resultSet.next()) {
                name = resultSet.getString(column);
            }
            return name;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertNewPassenger(int userId, int categoryId, String surname, String name, String patronymic, String birthdate, int genderId, int countryId, int documentTypeId, String documentNumber, String documentValidity) {
        Statement statement;
        try {
            statement = dbConnection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String insert = "INSERT INTO passengers (user_id, passenger_category_id, surname, name, patronymic, gender_id, birth_date, country_id, document_id, document_number, document_validity) " +
                "VALUES (" + userId + ", " + categoryId + ", '" + surname + "', '" + name + "', '" + patronymic + "', " + genderId + ", '" + birthdate +
                "', " + countryId + ", " + documentTypeId + ", '" + documentNumber + "', '" + documentValidity + "')";
        System.out.println(insert);
        try {
            statement.executeUpdate(insert);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertNewTrip(int meanId, String tripNumber, int fromHub, int toHub, int companyId) {
        Statement statement;
        try {
            statement = dbConnection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String insert = "INSERT INTO trips (mean_id, trip_number, from_id, to_id, company_id) " +
                "VALUES (" + meanId + ", '" + tripNumber +
                "', " + fromHub + ", " + toHub + ", " + companyId + ")";
        System.out.println(insert);
        try {
            statement.executeUpdate(insert);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertNewRoute(int tripId, int fromHubId, int toHubId, String stopTime, String betweenHubsTime) {
        Statement statement;
        try {
            statement = dbConnection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String toHubIdStr = "NULL";
        if (toHubId != 0) {
            toHubIdStr = Integer.toString(toHubId);
        }

        String insert = "INSERT INTO routes (trip_id, hub_id, next_id, stop_duration, between_hubs_duration) " +
                "VALUES (" + tripId + ", " + fromHubId + ", " + toHubIdStr +
                ", '" + stopTime + "', '" + betweenHubsTime + "')";
        System.out.println(insert);
        try {
            statement.executeUpdate(insert);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public LinkedList<String> getTripNumbers() {
        Statement statement;
        try {
            statement = dbConnection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ResultSet resultSet;
        LinkedList<String> values = new LinkedList<>();
        String select = "SELECT trip_number FROM trips";
        try {
            resultSet = statement.executeQuery(select);
            while (resultSet.next()) {
                values.add(resultSet.getString("trip_number"));
            }
            return values;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getSettlementByTripNumber(String column, String tripNumber) {
        Statement statement;
        try {
            statement = dbConnection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ResultSet resultSet;
        String name = null;
        String select = "SELECT t.trip_number, s.name " +
                "FROM trips t " +
                "INNER JOIN transport_hubs th on t." + column + " = th.id " +
                "INNER JOIN settlements s on th.settlement_id = s.id " +
                "WHERE t.trip_number = '" + tripNumber + "'";
        try {
            resultSet = statement.executeQuery(select);
            if (resultSet.next()) {
                name = resultSet.getString("name");
            }
            return name;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
