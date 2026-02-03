package onlineenrollment.dao;

import onlineenrollment.model.User;
import config.config;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for User operations
 */
public class UserDAO {
    private config dbConfig;
    
    public UserDAO() {
        dbConfig = new config();
        initializeDatabase();
    }
    
    /**
     * Initialize database and create users table if it doesn't exist
     */
    private void initializeDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT NOT NULL UNIQUE," +
                "email TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL" +
                ")";
        boolean success = dbConfig.executeSQL(createTableSQL);
        if (!success) {
            System.err.println("Warning: Failed to initialize database table");
        }
    }
    
    /**
     * Register a new user
     * @param user User object with registration details
     * @return true if registration successful, false otherwise
     */
    public boolean registerUser(User user) {
        try {
            // Hash the password before storing
            String hashedPassword = dbConfig.hashPassword(user.getPassword());
            if (hashedPassword == null) {
                System.err.println("Failed to hash password");
                return false;
            }
            
            String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
            boolean success = dbConfig.addRecord(sql, user.getUsername(), user.getEmail(), hashedPassword);
            if (!success) {
                System.err.println("Failed to add record to database");
            }
            return success;
        } catch (Exception e) {
            System.err.println("Error registering user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Login user by email and password
     * @param email User email
     * @param password User password
     * @return User object if login successful, null otherwise
     */
    public User loginUser(String email, String password) {
        try {
            // Hash the password for comparison
            String hashedPassword = dbConfig.hashPassword(password);
            if (hashedPassword == null) {
                return null;
            }
            
            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
            List<Map<String, Object>> results = dbConfig.fetchRecords(sql, email, hashedPassword);
            
            if (results != null && !results.isEmpty()) {
                Map<String, Object> row = results.get(0);
                User user = new User();
                user.setId(((Number) row.get("id")).intValue());
                user.setUsername((String) row.get("username"));
                user.setEmail((String) row.get("email"));
                user.setPassword((String) row.get("password"));
                return user;
            }
        } catch (Exception e) {
            System.err.println("Error logging in user: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Check if email already exists
     * @param email Email to check
     * @return true if email exists, false otherwise
     */
    public boolean emailExists(String email) {
        try {
            String sql = "SELECT * FROM users WHERE email = ? LIMIT 1";
            List<Map<String, Object>> results = dbConfig.fetchRecords(sql, email);
            return results != null && !results.isEmpty();
        } catch (Exception e) {
            System.err.println("Error checking email: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Check if username already exists
     * @param username Username to check
     * @return true if username exists, false otherwise
     */
    public boolean usernameExists(String username) {
        try {
            String sql = "SELECT * FROM users WHERE username = ? LIMIT 1";
            List<Map<String, Object>> results = dbConfig.fetchRecords(sql, username);
            return results != null && !results.isEmpty();
        } catch (Exception e) {
            System.err.println("Error checking username: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
}
