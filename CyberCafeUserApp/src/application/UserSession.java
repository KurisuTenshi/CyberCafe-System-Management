package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class UserSession {
    private static User currentUser;

    private static final String URL = "jdbc:mysql://localhost:3306/CyberCafeSystemManagement";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "";

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    // Method to start a session
    public static void startSession(User user, int sessionID) {
        user.setSessionID(sessionID);
        setCurrentUser(user);
    }

    // Method to end the session and calculate duration
    public static boolean endSessionWithDuration() {
        if (currentUser != null) {
            int sessionID = currentUser.getSessionID();
            if (sessionID != 0) { // Make sure sessionID is valid
                LocalDateTime now = LocalDateTime.now();
                String endSessionQuery = "UPDATE session SET EndTime = ?, Duration = TIMESTAMPDIFF(MINUTE, StartTime, ?), Status = 'ended' WHERE SessionID = ?";

                try (Connection conn = DriverManager.getConnection(URL, DB_USERNAME, DB_PASSWORD);
                     PreparedStatement stmt = conn.prepareStatement(endSessionQuery)) {

                    stmt.setTimestamp(1, Timestamp.valueOf(now));
                    stmt.setTimestamp(2, Timestamp.valueOf(now)); // Use current time to calculate duration
                    stmt.setInt(3, sessionID);

                    int rowsUpdated = stmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        System.out.println("Session ended successfully for SessionID: " + sessionID);
                        return true;
                    } else {
                        System.err.println("Failed to update session for SessionID: " + sessionID);
                        return false;
                    }
                } catch (SQLException e) {
                    System.err.println("Database error occurred while ending session.");
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }

    // Clear the current session
    public static void clear() {
        setCurrentUser(null); // Clear the current user session
    }

    // Get the session ID
    public static int getSessionID() {
        return currentUser != null ? currentUser.getSessionID() : 0;
    }
}
