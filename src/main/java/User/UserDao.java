package User;

import dao.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    private Connection connection;
    private Encryptor encryptor;

    public UserDao() {
        connection = DatabaseConnection.getConnection();
        encryptor = new Encryptor();

    }

    public boolean containsUser(String username) throws ClassNotFoundException {
        boolean result = false;
        String containsUser = "SELECT * FROM Users where  username = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(containsUser);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();
            result = resultSet.next();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public boolean accountHasPass(String username, String password) throws ClassNotFoundException {
        String getInfoQuery = "SELECT password_hash, salt FROM Users WHERE username = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(getInfoQuery);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String hash = rs.getString("password_hash");
                String saltHex = rs.getString("salt");
                byte[] salt = Encryptor.stringToHex(saltHex);

                String providedPasswordHash = Encryptor.encrypt(password, salt);
                return providedPasswordHash.equals(hash);
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerUser(User user) throws ClassNotFoundException {
        String registerUser = "INSERT INTO Users" + "(username, email, password_hash, salt) VALUES" + "(?,?,?,?);";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(registerUser);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getEmail());
            byte[] salt = encryptor.generateSalt();
            preparedStatement.setString(3, encryptor.encrypt(user.getPassword(), salt));
            preparedStatement.setString(4, Encryptor.hexToString(salt));

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeUser(User user) throws ClassNotFoundException {
        String deleteUser = "DELETE FROM Users WHERE email = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(deleteUser);
            preparedStatement.setString(1, user.getEmail());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User findUserByUsername(String username) {
        String query = "SELECT * FROM Users WHERE username = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new User(resultSet.getInt("user_id"), resultSet.getString("username"), resultSet.getString("password_hash"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
public User getUserById(int userId) {
        String query = "SELECT * FROM Users WHERE user_id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new User(resultSet.getInt("user_id"), resultSet.getString("username"), resultSet.getString("password_hash"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public List<User> findUsersByUsername(String username) {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM Users WHERE username LIKE ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, "%" + username + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                users.add(new User(resultSet.getInt("user_id"), resultSet.getString("username"), resultSet.getString("password_hash")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM Users";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                users.add(new User(resultSet.getInt("user_id"), resultSet.getString("username"), resultSet.getString("password_hash")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }
    public void updateUser(User user) {
        String query = "UPDATE Users SET username = ?, email = ? WHERE user_id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setInt(3, user.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteUser(int id) {
        String query = "DELETE FROM Users WHERE user_id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
