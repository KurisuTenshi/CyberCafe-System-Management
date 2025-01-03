package application;

import java.time.LocalDateTime;
import java.sql.Time;

public class ActiveSessionsTable {
    private final Integer sessionID;
    private final Time duration;  // Changed back to sql.Time
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final String status;
    private final Integer adminID;
    private final Integer userID;
    private final Integer unitID;

    public ActiveSessionsTable(Integer sessionID, Time duration, LocalDateTime startTime, 
                             LocalDateTime endTime, String status, Integer adminID, 
                             Integer userID, Integer unitID) {
        this.sessionID = sessionID;
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.adminID = adminID;
        this.userID = userID;
        this.unitID = unitID;
    }

    // Getters
    public Integer getSessionID() { return sessionID; }
    public Time getDuration() { return duration; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public String getStatus() { return status; }
    public Integer getAdminID() { return adminID; }
    public Integer getUserID() { return userID; }
    public Integer getUnitID() { return unitID; }
}