package textEditor.model;

import textEditor.controller.Project;
import textEditor.controller.User;
import textEditor.controller.ProjectImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.rmi.RemoteException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        boolean flag = false;
        try {
            String userExistQuery = "SELECT * FROM uzytkownicy WHERE login=?";
            PreparedStatement userExistStatement = con.prepareStatement(userExistQuery);
            userExistStatement.setString(1, username);
            ResultSet result = userExistStatement.executeQuery();

            if (result.next()) {
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }


    @Override
    public boolean checkPassword(String userName, String password) {
        boolean flag = false;
        if(password == null)
            return false;
        try {
            String checkPasswordQuery = "SELECT haslo FROM uzytkownicy WHERE login=?";
            PreparedStatement checkPasswordStatement = con.prepareStatement(checkPasswordQuery);
            checkPasswordStatement.setString(1, password);

            ResultSet rs = checkPasswordStatement.executeQuery();
            if (rs.next()) {
                String validPassword = rs.getString(1);
                if (validPassword.equals(password))
                    flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }

    @Override
    public boolean registerUser(ArrayList<String> data) {
        try {
            // Insert into 'uzytkownicy' table
            String insertUzytkownicyQuery = "INSERT INTO `uzytkownicy` (`id_uzytkownika`, `login`, `haslo`) VALUES (NULL, ?, ?)";
            PreparedStatement insertUzytkownicyStatement = con.prepareStatement(insertUzytkownicyQuery, Statement.RETURN_GENERATED_KEYS);
            insertUzytkownicyStatement.setString(1, data.get(USER_LOGIN));
            insertUzytkownicyStatement.setString(2, data.get(USER_PASSWORD));
            insertUzytkownicyStatement.executeUpdate();

            ResultSet resultSet = insertUzytkownicyStatement.getGeneratedKeys();
            Integer idUser = null;
            if (resultSet.next()) {
                idUser = resultSet.getInt(1);
            }

            // Insert into 'dane_uzytkownika' table
            String insertDaneQuery = "INSERT INTO `dane_uzytkownika` (`id_uzytkownika`, `imie`, `nazwisko`, `email`) VALUES (?, ?, ?, ?)";
            PreparedStatement insertDaneStatement = con.prepareStatement(insertDaneQuery);
            insertDaneStatement.setInt(1, idUser);
            insertDaneStatement.setString(2, data.get(FIRST_NAME));
            insertDaneStatement.setString(3, data.get(LAST_NAME));
            insertDaneStatement.setString(4, data.get(EMAIL));
            insertDaneStatement.executeUpdate();

            // Insert into 'adres' table
            String insertAdresQuery = "INSERT INTO `adres` (`id_uzytkownika`, `region`, `adres`, `kodPocztowy`) VALUES (?, ?, ?, ?)";
            PreparedStatement insertAdresStatement = con.prepareStatement(insertAdresQuery);
            insertAdresStatement.setInt(1, idUser);
            insertAdresStatement.setString(2, data.get(REGION));
            insertAdresStatement.setString(3, data.get(ADRESS));
            insertAdresStatement.setString(4, data.get(ZIPCODE));
            insertAdresStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public List<Project> getProjects(User user) throws RemoteException {
        List<Project> projects = new ArrayList<>();
        try {
            String getProjectsQuery = "SELECT projekt.* FROM projekt NATURAL JOIN uzytkownik_projekt WHERE uzytkownik_projekt.id_uzytkownika = ?";
            PreparedStatement getProjectsStatement = con.prepareStatement(getProjectsQuery);
            getProjectsStatement.setInt(1, user.getId());

            ResultSet result = getProjectsStatement.executeQuery();
            while (result.next()) {
                int id_projektu = result.getInt("id_projektu");
                String nazwa = result.getString("nazwa");
                String opis = result.getString("opis");
                String data = result.getString("data_utworzenia"); // Think about adding it into project info

                projects.add(new ProjectImpl(id_projektu, nazwa, opis, getContributors(id_projektu)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return projects;
    }

    private List<String> getContributors(int id_projektu) {
        List<String> contributors = new ArrayList<>();
        try {
            String getContributorsQuery = "SELECT uzytkownicy.login FROM uzytkownik_projekt NATURAL JOIN uzytkownicy WHERE uzytkownik_projekt.id_projektu = ?";
            PreparedStatement getContributorsStatement = con.prepareStatement(getContributorsQuery);
            getContributorsStatement.setInt(1, id_projektu);
            ResultSet resultSet = getContributorsStatement.executeQuery();
            while (resultSet.next()) {
                contributors.add(resultSet.getString("login"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contributors;
    }

    @Override
    public int getUserId(String login) {
        try {
            String getUserQuery = "SELECT * FROM `uzytkownicy` WHERE uzytkownicy.login = ?";
            PreparedStatement getUserStatement = con.prepareStatement(getUserQuery);
            getUserStatement.setString(1, login);
            ResultSet result = getUserStatement.executeQuery();
            if (result.next()) {
                return result.getInt(1);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void removeProject(Project projectToDelete) throws RemoteException {
        try {
            //Delete contributors
            String deleteContributorQuery = "DELETE FROM `uzytkownik_projekt` WHERE `uzytkownik_projekt`.`id_projektu` = ?";
            PreparedStatement deleteContributorStatement = con.prepareStatement(deleteContributorQuery);
            deleteContributorStatement.setInt(1, projectToDelete.getId());
            int result = deleteContributorStatement.executeUpdate();
            if(result == 0){
                throw new SQLException("Failed to delete contributors");
            }

            //Delete project
            String deleteProjectQuery = "DELETE FROM `projekt` WHERE `projekt`.`id_projektu` = ?";
            PreparedStatement deleteProjectStatement = con.prepareStatement(deleteProjectQuery);
            deleteProjectStatement.setInt(1, projectToDelete.getId());
            result = deleteProjectStatement.executeUpdate();
            if(result == 0){
                throw new SQLException("Failed to delete contributors");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addProject(Project project) throws RemoteException {
        try {
            // Inserting project into database
            String insertProjectQuery = "INSERT INTO `projekt` (`id_projektu`, `nazwa`, `opis`, `data_utworzenia`) VALUES (NULL, ?, ?, ?)";
            PreparedStatement insertProjectStatement = con.prepareStatement(insertProjectQuery, Statement.RETURN_GENERATED_KEYS);

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime now = LocalDateTime.now();

            insertProjectStatement.setString(1, project.getTitle());
            insertProjectStatement.setString(2, project.getDescription());
            insertProjectStatement.setString(3, now.toString());

            int affectedRow = insertProjectStatement.executeUpdate();
            if(affectedRow == 0){
                throw new SQLException("User is not added!");
            }
            ResultSet resultSet = insertProjectStatement.getGeneratedKeys();
            Integer idProject = null;
            if (resultSet.next())
            {
                idProject = resultSet.getInt(1);
            }
            if(idProject == null) {
                throw new SQLException("Key wasn't generate");
            }

            //Inserting contributors into database
            String insertContributorQuery = "INSERT INTO `uzytkownik_projekt` (`id_uzytkownika`, `id_projektu`) VALUES (?, ?)";
            for (String contributor:
                 project.getContributors()) {
                PreparedStatement insertContributorStatement = con.prepareStatement(insertContributorQuery);
                insertContributorStatement.setInt(1, getUserId(contributor));
                insertContributorStatement.setInt(2, idProject);
                insertContributorStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


