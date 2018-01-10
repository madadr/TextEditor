package textEditor.model;

import textEditor.controller.Project;
import textEditor.controller.User;
import textEditor.controller.ProjectImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.rmi.RemoteException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static textEditor.controller.RegistrationFields.*;

public class DatabaseModelImpl implements DatabaseModel {

    Connection con;
    private Statement stmt;

    public DatabaseModelImpl() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection(
                "jdbc:mysql://localhost/",
                "root",
                "");
        System.out.println("We are connected");
        init();
    }


    @Override
    public void init(){
        String s;
        StringBuffer sb = new StringBuffer();

        try {
            FileReader fr = new FileReader(new File(getClass().getResource("database.sql").getFile()));
            BufferedReader br = new BufferedReader(fr);

            // Getting commands from database.sql file and device it into single command array
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
            br.close();

            String[] inst = sb.toString().split(";");

            // Creating database if not exist
            Statement st = con.createStatement();
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS `texteditor`");
            st.close();
            con.close();

            // If we are sure that the database exist we are connecting to it
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost/texteditor",
                    "root",
                    "");
            st = con.createStatement();

            // Executing the command from file
            for (int i = 0; i < inst.length; i++) {
                if (!inst[i].trim().equals("")) {
                    st.executeUpdate(inst[i]);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean userExist(String username) {
        ResultSet rs;
        boolean flag = false;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM uzytkownicy WHERE login=\"" + username + "\"");
            while (rs.next()) {
                flag = true;
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }


    @Override
    public boolean checkPassword(String userName, String password) {
        ResultSet rs;
        boolean flag = false;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT haslo FROM uzytkownicy WHERE login=\"" + userName + "\"");
            while (rs.next()) {
                String validPassword = rs.getString("haslo");
                if (password != null && validPassword.equals(password))
                    flag = true;
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }

    @Override
    public boolean registerUser(ArrayList<String> data) {
        try {
            stmt = con.createStatement();
            stmt.executeUpdate("INSERT INTO `uzytkownicy` (`id_uzytkownika`, `login`, `haslo`) VALUES (NULL, '" + data.get(USER_LOGIN) + "', '" + data.get(USER_PASSWORD) + "')");
            ResultSet rs = stmt.executeQuery("SELECT id_uzytkownika FROM uzytkownicy WHERE login=\"" + data.get(USER_LOGIN) + "\"");
            rs.next();
            int id = rs.getInt(1);
            stmt.executeUpdate("INSERT INTO `dane_uzytkownika` (`id_uzytkownika`, `imie`, `nazwisko`, `email`) VALUES ('" + id + "', '" + data.get(FIRST_NAME) + "', '"
                    + data.get(LAST_NAME) + "', '" + data.get(EMAIL) + "');");
            stmt.executeUpdate("INSERT INTO `adres` (`id_uzytkownika`, `region`, `adres`, `kodPocztowy`) VALUES ('" + id + "', '" + data.get(REGION) + "', '"
                    + data.get(ADRESS) + "', '" + data.get(ZIPCODE) + "');");
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public List<Project> getProjects(User user) throws RemoteException {

        List<Project> projects = new ArrayList<>();

        ResultSet rs;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT projekt.* FROM projekt NATURAL JOIN uzytkownik_projekt WHERE uzytkownik_projekt.id_uzytkownika = " + user.getId());
            while (rs.next()) {
                System.out.println("LOL");
                int id_projektu = rs.getInt("id_projektu");
                String nazwa = rs.getString("nazwa");
                String opis = rs.getString("opis");
                String data = rs.getString("data_utworzenia");

                projects.add(new ProjectImpl(id_projektu, nazwa, opis, getContributors(id_projektu)));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return projects;
    }

    private List<String> getContributors(int id_projektu) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT uzytkownicy.login FROM uzytkownik_projekt NATURAL JOIN uzytkownicy WHERE uzytkownik_projekt.id_projektu = " + id_projektu);
        List<String> contributors = new ArrayList<>();
        while (resultSet.next()) {
            contributors.add(resultSet.getString("login"));
        }
        resultSet.close();
        statement.close();

        return contributors;
    }

    @Override
    public int getUserId(String login) {

        try {
            stmt = con.createStatement();
            ResultSet userID = stmt.executeQuery("SELECT * FROM `uzytkownicy` WHERE uzytkownicy.login = " + "'" + login + "'");
            while (userID.next()) {
                return userID.getInt("id_uzytkownika");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void removeProject(Project projectToDelete) throws RemoteException {
        try {
            stmt = con.createStatement();
            stmt.executeUpdate("DELETE FROM `uzytkownik_projekt` WHERE `uzytkownik_projekt`.`id_projektu` = " + projectToDelete.getId());
            stmt.executeUpdate("DELETE FROM `projekt` WHERE `projekt`.`id_projektu` = " + projectToDelete.getId());
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


