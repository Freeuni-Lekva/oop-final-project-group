package User;

public class User {
    private int userId;
    private String username;
    private String password;
    private String email;

    public User(int id, String name, String password) {
        this.userId = id;
        this.username = name;
        this.password = password;
        this.email = "";
    }

    public User(String name, String password) {
        this.username = name;
        this.password = password;
        this.email = "";
    }

    public User(String name, String password, String email) {
        this.username = name;
        this.password = password;
        this.email = email;
    }

    public int getId() {
        return userId;
    }
public int getUserId() {
        return this.userId;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String name) {
        this.username = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
