package textEditor.model;

import textEditor.model.interfaces.DatabaseModel;
import textEditor.model.interfaces.Project;
import textEditor.model.interfaces.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.rmi.RemoteException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static textEditor.utils.Const.RegistrationFields.*;

public class DatabaseModelImpl implements DatabaseModel {

    Connection con;

    public DatabaseModelImpl() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost/", "root", "");
        init();
    }

    private void init() {
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
            String userExistQuery = "SELECT * FROM users WHERE login=?";
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
        if (password == null)
            return false;
        try {
            String checkPasswordQuery = "SELECT password FROM users WHERE login=?";
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
            // Insert into 'users' table
            String insertUzytkownicyQuery = "INSERT INTO `users` (`id_user`, `login`, `password`) VALUES (NULL, ?, ?)";
            PreparedStatement insertUzytkownicyStatement = con.prepareStatement(insertUzytkownicyQuery, Statement.RETURN_GENERATED_KEYS);
            insertUzytkownicyStatement.setString(1, data.get(USER_LOGIN));
            insertUzytkownicyStatement.setString(2, data.get(USER_PASSWORD));
            insertUzytkownicyStatement.executeUpdate();

            ResultSet resultSet = insertUzytkownicyStatement.getGeneratedKeys();
            Integer idUser = null;
            if (resultSet.next()) {
                idUser = resultSet.getInt(1);
            }

            // Insert into 'user_data' table
            String insertDaneQuery = "INSERT INTO `user_data` (`id_user`, `first_name`, `last_name`, `email`) VALUES (?, ?, ?, ?)";
            PreparedStatement insertDaneStatement = con.prepareStatement(insertDaneQuery);
            insertDaneStatement.setInt(1, idUser);
            insertDaneStatement.setString(2, data.get(FIRST_NAME));
            insertDaneStatement.setString(3, data.get(LAST_NAME));
            insertDaneStatement.setString(4, data.get(EMAIL));
            insertDaneStatement.executeUpdate();

            // Insert into 'address' table
            String insertAdresQuery = "INSERT INTO `address` (`id_user`, `region`, `address`, `zip_code`) VALUES (?, ?, ?, ?)";
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
            String getProjectsQuery = "SELECT project.* FROM project NATURAL JOIN user_project WHERE user_project.id_user = ?";
            PreparedStatement getProjectsStatement = con.prepareStatement(getProjectsQuery);
            getProjectsStatement.setInt(1, user.getId());

            ResultSet result = getProjectsStatement.executeQuery();
            while (result.next()) {
                int projectId = result.getInt("id_project");
                String name = result.getString("name");
                String description = result.getString("description");
                String date = result.getString("date_of_creation"); // Think about adding it into project info

                projects.add(new ProjectImpl(projectId, name, description, getContributors(projectId)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return projects;
    }

    private List<User> getContributors(int id_project) {
        List<User> contributors = new ArrayList<>();
        try {
            String getContributorsQuery = "SELECT users.id_user, users.login FROM user_project NATURAL JOIN users WHERE user_project.id_project = ?";
            PreparedStatement getContributorsStatement = con.prepareStatement(getContributorsQuery);
            getContributorsStatement.setInt(1, id_project);
            ResultSet resultSet = getContributorsStatement.executeQuery();
            while (resultSet.next()) {
                contributors.add(new UserImpl(resultSet.getInt(1), resultSet.getString(2)));
            }
        } catch (SQLException | RemoteException e) {
            e.printStackTrace();
        }
        return contributors;
    }

    @Override
    public int getUserId(String login) {
        try {
            String getUserQuery = "SELECT * FROM `users` WHERE users.login = ?";
            PreparedStatement getUserStatement = con.prepareStatement(getUserQuery);
            getUserStatement.setString(1, login);
            ResultSet result = getUserStatement.executeQuery();
            if (result.next()) {
                return result.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public String getUserLogin(int id) {
        try {
            String getUserQuery = "SELECT * FROM `users` WHERE users.id_user = ?";
            PreparedStatement getUserStatement = con.prepareStatement(getUserQuery);
            getUserStatement.setInt(1, id);
            ResultSet result = getUserStatement.executeQuery();
            if (result.next()) {
                return result.getString(2);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void removeProject(Project projectToDelete) throws RemoteException {
        try {
            //Delete contributors
            String deleteContributorQuery = "DELETE FROM `user_project` WHERE `user_project`.`id_project` = ?";
            PreparedStatement deleteContributorStatement = con.prepareStatement(deleteContributorQuery);
            deleteContributorStatement.setInt(1, projectToDelete.getId());
            int result = deleteContributorStatement.executeUpdate();
            if (result == 0) {
                throw new SQLException("Failed to delete contributors");
            }

            //Delete project
            String deleteProjectQuery = "DELETE FROM `project` WHERE `project`.`id_project` = ?";
            PreparedStatement deleteProjectStatement = con.prepareStatement(deleteProjectQuery);
            deleteProjectStatement.setInt(1, projectToDelete.getId());
            result = deleteProjectStatement.executeUpdate();
            if (result == 0) {
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
            String insertProjectQuery = "INSERT INTO `project` (`id_project`, `name`, `description`, `date_of_creation`) VALUES (NULL, ?, ?, ?)";
            PreparedStatement insertProjectStatement = con.prepareStatement(insertProjectQuery, Statement.RETURN_GENERATED_KEYS);

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime now = LocalDateTime.now();

            insertProjectStatement.setString(1, project.getTitle());
            insertProjectStatement.setString(2, project.getDescription());
            insertProjectStatement.setString(3, now.toString());

            int affectedRow = insertProjectStatement.executeUpdate();
            if (affectedRow == 0) {
                throw new SQLException("User is not added!");
            }
            ResultSet resultSet = insertProjectStatement.getGeneratedKeys();
            Integer idProject = null;
            if (resultSet.next()) {
                idProject = resultSet.getInt(1);
            }
            if (idProject == null) {
                throw new SQLException("Key wasn't generate");
            }

            //Inserting contributors into database
            String insertContributorQuery = "INSERT INTO `user_project` (`id_user`, `id_project`) VALUES (?, ?)";

            for (User contributor : project.getContributors()) {
                PreparedStatement insertContributorStatement = con.prepareStatement(insertContributorQuery);
                insertContributorStatement.setInt(1, contributor.getId());
                insertContributorStatement.setInt(2, idProject);
                insertContributorStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void editProject(Project editedProject) {
        try {
            String editProjectQuery = "UPDATE `project` SET `name` = ?, `description` = ? WHERE `project`.`id_project` = ?";
            PreparedStatement editProjectStatement = con.prepareStatement(editProjectQuery);

            editProjectStatement.setString(1, editedProject.getTitle());
            editProjectStatement.setString(2, editedProject.getDescription());
            editProjectStatement.setInt(3, editedProject.getId());

            editProjectStatement.executeUpdate();

            String deleteContributorsQuery = "DELETE FROM `user_project` WHERE `user_project`.`id_project` = ?";
            PreparedStatement deleteContributorsStatement = con.prepareStatement(deleteContributorsQuery);
            deleteContributorsStatement.setInt(1, editedProject.getId());
            deleteContributorsStatement.executeUpdate();
            String insertContributorsQuery = "INSERT INTO `user_project` (`id_user`, `id_project`) VALUES (?,?)";
            for (User contributor : editedProject.getContributors()) {
                if (contributor.getId() != -1) {
                    PreparedStatement insertContributorsStatement = con.prepareStatement(insertContributorsQuery);
                    insertContributorsStatement.setInt(1, contributor.getId());
                    insertContributorsStatement.setInt(2, editedProject.getId());
                    insertContributorsStatement.executeUpdate();
                }
            }
        } catch (SQLException | RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<User> getFriends(User user) throws RemoteException {
        List<User> friends = new ArrayList<>();
        try {
            String getProjectsQuery = "SELECT friend_list.* FROM friend_list WHERE id_user = ?";
            PreparedStatement getProjectsStatement = con.prepareStatement(getProjectsQuery);
            getProjectsStatement.setInt(1, user.getId());

            ResultSet result = getProjectsStatement.executeQuery();
            while (result.next()) {
                int id_user = result.getInt("id_friend");
                User friend = new UserImpl(id_user, getUserLogin(id_user));

                friends.add(friend);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return friends;
    }

    @Override
    public void addFriend(User user, User friend) throws RemoteException {
        try {
            // Inserting project into database
            String insertUserQuery = "INSERT INTO `friend_list` (`id_user`, `id_friend`) VALUES (?, ?)";
            PreparedStatement statement = con.prepareStatement(insertUserQuery, Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, user.getId());
            statement.setInt(2, friend.getId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Failed to add friend!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeFriend(User user, User friend) throws RemoteException {
        try {
            //Delete project
            String deleteProjectQuery = "DELETE FROM `friend_list` WHERE `id_user`=? AND `id_friend`=?";
            PreparedStatement deleteProjectStatement = con.prepareStatement(deleteProjectQuery);
            deleteProjectStatement.setInt(1, user.getId());
            deleteProjectStatement.setInt(2, friend.getId());
            int affectedRows = deleteProjectStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Failed to delete friend!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


