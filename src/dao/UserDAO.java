package dao;

import config.config;
import model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author miwa
 */
public class UserDAO {
    
    private config dbConfig;
    
    public UserDAO() {
        dbConfig = new config();
    }
    
    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (first_name, last_name, email, password, role) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = config.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Hash the password before storing
            String hashedPassword = dbConfig.hashPassword(user.getPassword());
            
            if (hashedPassword == null) {
                System.err.println("Error: Password hashing returned null!");
                return false;
            }
            
            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, hashedPassword);
            pstmt.setString(5, user.getRole());
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("User registered: " + user.getEmail() + " with role: " + user.getRole());
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public User loginUser(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ?";
        
        try (Connection conn = config.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // Get stored password hash
                String storedPasswordHash = rs.getString("password");
                // Hash the provided password and compare
                String providedPasswordHash = dbConfig.hashPassword(password);
                
                // Debug output
                System.out.println("Login attempt for: " + email);
                System.out.println("Stored hash: " + (storedPasswordHash != null ? storedPasswordHash.substring(0, Math.min(20, storedPasswordHash.length())) + "..." : "null"));
                System.out.println("Provided hash: " + (providedPasswordHash != null ? providedPasswordHash.substring(0, Math.min(20, providedPasswordHash.length())) + "..." : "null"));
                
                // Compare hashed passwords
                if (storedPasswordHash != null && providedPasswordHash != null && storedPasswordHash.equals(providedPasswordHash)) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setRole(rs.getString("role"));
                    System.out.println("Login successful for role: " + user.getRole());
                    return user;
                } else {
                    System.out.println("Password hash mismatch!");
                }
            } else {
                System.out.println("User not found with email: " + email);
            }
            
        } catch (SQLException e) {
            System.err.println("Error logging in user: " + e.getMessage());
        }
        
        return null;
    }
    
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try (Connection conn = config.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking email: " + e.getMessage());
        }
        
        return false;
    }
    
    public boolean createAdminUser(String email, String password) {
        if (emailExists(email)) {
            System.err.println("Email already exists");
            return false;
        }
        
        User admin = new User("Admin", "User", email, password);
        admin.setRole("admin");
        return registerUser(admin);
    }
    
    public java.util.List<java.util.Map<String, Object>> getAllUsers() {
        java.util.List<java.util.Map<String, Object>> users = new java.util.ArrayList<>();
        String sql = "SELECT id, first_name, last_name, email, role, created_at FROM users ORDER BY created_at DESC";
        
        try (Connection conn = config.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                java.util.Map<String, Object> user = new java.util.HashMap<>();
                user.put("id", rs.getInt("id"));
                user.put("first_name", rs.getString("first_name"));
                user.put("last_name", rs.getString("last_name"));
                user.put("email", rs.getString("email"));
                user.put("role", rs.getString("role"));
                user.put("created_at", rs.getString("created_at"));
                users.add(user);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
            e.printStackTrace();
        }
        
        return users;
    }
}
