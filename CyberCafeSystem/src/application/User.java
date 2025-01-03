package application;

public class User {
    private int userId;
    private String username;
    private String firstName;
    private String lastName;
    private String contact;
    private double balance;
    
    public User(int userId, String username, String firstName, String lastName, String contact, double balance) {
        this.userId = userId;           // Use 'this' to refer to class field
        this.username = username;       // Use 'this' to refer to class field
        this.firstName = firstName;     // Use 'this' to refer to class field
        this.lastName = lastName;       // Use 'this' to refer to class field
        this.contact = contact;         // Use 'this' to refer to class field
        this.balance = balance;         // Use 'this' to refer to class field
    }
    
    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
}