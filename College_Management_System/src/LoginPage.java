import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginPage extends JFrame {
    private JTextField userIdField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginPage() {
        setTitle("College Management System - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout
        setLayout(null);

        // Background image
        ImageIcon backgroundIcon = new ImageIcon("background.jpg"); // Place your image file in the project folder
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setBounds(0, 0, 400, 300); // Set bounds according to your window size
        add(backgroundLabel);

        // UI Components
        JLabel userLabel = new JLabel("UserID:");
        userLabel.setForeground(Color.BLACK);
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userLabel.setBounds(50, 50, 100, 30);
        backgroundLabel.add(userLabel);

        userIdField = new JTextField();
        userIdField.setBounds(150, 50, 200, 30);
        backgroundLabel.add(userIdField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.BLACK);
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passLabel.setBounds(50, 100, 100, 30);
        backgroundLabel.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 100, 200, 30);
        backgroundLabel.add(passwordField);

        loginButton = new JButton("Login");
        loginButton.setBackground(new Color(34, 167, 240)); // Light Blue color
        loginButton.setForeground(Color.BLACK);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBounds(150, 150, 100, 30);
        backgroundLabel.add(loginButton);

        // Event Handling
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userID = userIdField.getText();
                String password = new String(passwordField.getPassword());

                if (authenticate(userID, password)) {
                    String role = getUserRole(userID);
                    if (role.equals("Admin")) {
                        new AdminPanel();
                    } else if (role.equals("Student")) {
                        new StudentPanel(userID);
                    } else if (role.equals("Faculty")) {
                        new FacultyPanel(userID);
                    } else if (role.equals("HOD")) {
                        new HODPanel(userID);
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid role");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid login credentials");
                }
            }
        });

        // Check Database Connection First
        if (checkDatabaseConnection()) {
            System.out.println("Database connected successfully.");
        } else {
            System.out.println("Database connection failed.");
            JOptionPane.showMessageDialog(null, "Database connection failed.");
            System.exit(0); // Exit the application if DB connection fails
        }
    }

    // Method to check if the DB connection is working
    private boolean checkDatabaseConnection() {
        boolean isConnected = false;

        try {
            // Register the JDBC driver explicitly (if not automatically detected)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Attempt to establish the connection
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/college_db", "root", "password")) {
                if (connection != null) {
                    isConnected = true;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "MySQL JDBC Driver not found!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "SQLException: " + ex.getMessage());
        }

        return isConnected;
    }

    // Method to authenticate user based on UserID and Password
    private boolean authenticate(String userID, String password) {
        boolean isValid = false;

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/college_db", "root", "password")) {
            String query = "SELECT * FROM users WHERE UserID = ? AND Password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, userID);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                isValid = true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return isValid;
    }

    // Method to get the role of the user
    private String getUserRole(String userID) {
        String role = "";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/college_db", "root", "password")) {
            String query = "SELECT Role FROM users WHERE UserID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, userID);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                role = resultSet.getString("Role");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return role;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }
}
