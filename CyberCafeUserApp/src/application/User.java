package application;

public class User {
    private int userID;
    private String username;
    private String password; // Optional if you want to store it locally
    private int sessionID;

    // Constructor for User object
    public User(int userID, String username) {
        this.userID = userID;
        this.username = username;
    }

    // Getter and Setter for userID
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    // Getter and Setter for username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter and Setter for sessionID
    public int getSessionID() {
        return sessionID;
    }

    public void setSessionID(int sessionID) {
        this.sessionID = sessionID;
    }

    // Optional: Getter and Setter for password (if needed)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
               "userID=" + userID +
               ", username='" + username + '\'' +
               ", sessionID=" + sessionID +
               '}';
    }
}
