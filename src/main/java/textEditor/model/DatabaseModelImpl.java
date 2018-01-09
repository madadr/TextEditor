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
import java.util.Arrays;
import java.util.List;

public class DatabaseModelImpl implements DatabaseModel {

    Connection con;
    private Statement stmt;

    public DatabaseModelImpl() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost/",
                    "root",
                    "");
            System.out.println("We are connected");
            init();
        } catch (SQLException | RemoteException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void init() throws RemoteException {
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
    public boolean registerUser(String login, String password, String email, String zipCode, String address, String region, String lastName, String firstName) throws RemoteException {
        try {
            stmt = con.createStatement();
            stmt.executeUpdate("INSERT INTO `uzytkownicy` (`id_uzytkownika`, `login`, `haslo`) VALUES (NULL, '" + login + "', '" + password + "')");
            ResultSet rs = stmt.executeQuery("SELECT id_uzytkownika FROM uzytkownicy WHERE login=\"" + login + "\"");
            rs.next();
            int id = rs.getInt(1);
            stmt.executeUpdate("INSERT INTO `dane_uzytkownika` (`id_uzytkownika`, `imie`, `nazwisko`, `email`) VALUES ('" + id + "', '" + firstName + "', '" + lastName + "', '" + email + "');");
            stmt.executeUpdate("INSERT INTO `adres` (`id_uzytkownika`, `region`, `adres`, `kodPocztowy`) VALUES ('" + id + "', '" + region + "', '" + address + "', '" + zipCode + "');");
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public List<Project> getProjects(User user) throws RemoteException {
        // STUB
        System.out.println("getting project");
        List<Project> projects = new ArrayList<>();

        projects.add(new ProjectImpl("Proj1", "Proj1 desc\nNo elo elo 3 2 0", new ArrayList<>(Arrays.asList("User1", "User2"))));
        projects.add(new ProjectImpl("Proj2", "Proj2 desc\nNo elo elo 3 2 0", new ArrayList<>(Arrays.asList("User2"))));
        projects.add(new ProjectImpl("Proj3", "Proj3 desc\nNo elo elo 3 2 0", new ArrayList<>(Arrays.asList("User3"))));
        projects.add(new ProjectImpl("Proj4", "Proj4 desc\nNo elo elo 3 2 0", new ArrayList<>(Arrays.asList("User4"))));

        return projects;
    }

    @Override
    public int getUserId(String login) throws RemoteException {
        // STUB
        // add getting id from database, when having login
        return 1;
    }
}


