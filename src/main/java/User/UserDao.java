package User;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {
    private DBConnection connection;
    private Encryptor encryptor;

    public UserDao(){
        connection = new DBConnection();
        encryptor = new Encryptor();

        DBCreate dbCreate = new DBCreate();
        try {
            dbCreate.createDataBase(connection);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean containsUser(String username) throws ClassNotFoundException {
        boolean result = false;
        String containsUser = "SELECT * FROM user where  username = ?";


        try {
            PreparedStatement preparedStatement = connection.getConnection().prepareStatement(containsUser);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();
            result = resultSet.next();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }


    public boolean accountHasPass(String username, String password) throws ClassNotFoundException {
        boolean result = false;
        String containsUser = "SELECT *FROM user where  username = ? and password = ?";
        String getSalt = "SELECT SALT FROM user WHERE username = ?";


        try {
            PreparedStatement saltStatement = connection.getConnection().prepareStatement(getSalt);
            saltStatement.setString(1, username);
            ResultSet resultSetSalt = saltStatement.executeQuery();
            resultSetSalt.next();
            byte[] salt = resultSetSalt.getBytes(1);

            PreparedStatement preparedStatement = connection.getConnection().prepareStatement(containsUser);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, encryptor.encrypt(password,salt));

            ResultSet resultSet = preparedStatement.executeQuery();
            result = resultSet.next();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }


    public void registerUser(User user) throws ClassNotFoundException {
        String registerUser = "INSERT INTO user" + "(username, password, salt) VALUES" + "(?,?, ?);";

        try {
            PreparedStatement preparedStatement = connection.getConnection().prepareStatement(registerUser);
            preparedStatement.setString(1, user.getName());
            byte[] salt = encryptor.generateSalt();
            preparedStatement.setString(2, encryptor.encrypt(user.getPassword(), salt));
            preparedStatement.setBytes(3, salt);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeUser(User user) throws ClassNotFoundException {
        String deleteUser = "DELETE FROM user WHERE username = ?";

        try {
            PreparedStatement preparedStatement = connection.getConnection().prepareStatement(deleteUser);
            preparedStatement.setString(1, user.getName());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
