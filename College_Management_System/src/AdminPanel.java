import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdminPanel extends JFrame {
    private JButton addUserButton, viewUsersButton, deleteUserButton, viewResultsButton, logoutButton;

    public AdminPanel() {
        // Set up the frame
        this.setTitle("Admin Panel");
        this.setSize(400, 300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        // Create the layout
        setLayout(new GridLayout(6, 1));

        // Create buttons for each task
        addUserButton = new JButton("Add New User");
        viewUsersButton = new JButton("View Users");
        deleteUserButton = new JButton("Delete User");
        viewResultsButton = new JButton("View Results");
        logoutButton = new JButton("Logout");

        // Add buttons to the frame
        add(addUserButton);
        add(viewUsersButton);
        add(deleteUserButton);
        add(viewResultsButton);
        add(logoutButton);

        // Add action listeners for each button
        addUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Add new user (can be student or faculty)
                addNewUser();
            }
        });

        viewUsersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // View all users (students, faculty, etc.)
                viewUsers();
            }
        });

        deleteUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Delete user
                deleteUser();
            }
        });

        viewResultsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // View results of students
                viewResults();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Logout and go back to login page
                logout();
            }
        });

        // Display the frame
        this.setVisible(true);
    }

    private void addNewUser() {
        // Add new user (student/faculty/admin)
        String userId = JOptionPane.showInputDialog("Enter UserID");
        String password = JOptionPane.showInputDialog("Enter Password");
        String role = JOptionPane.showInputDialog("Enter Role (Admin, Student, Faculty)");

        // Code to insert this new user into the database
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/college_db", "root", "password")) {
            String query = "INSERT INTO users (UserID, Password, Role) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, userId);
            statement.setString(2, password);
            statement.setString(3, role);
            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "User added successfully!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding user.");
        }
    }

    private void viewUsers() {
        // View users (students, faculty, admins)
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/college_db", "root", "password")) {
            String query = "SELECT * FROM users";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            StringBuilder userList = new StringBuilder("Users List:\n");
            while (resultSet.next()) {
                userList.append("UserID: ").append(resultSet.getString("UserID"))
                        .append(", Role: ").append(resultSet.getString("Role")).append("\n");
            }

            JOptionPane.showMessageDialog(this, userList.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching users.");
        }
    }

    private void deleteUser() {
        // Delete a user by UserID
        String userIdToDelete = JOptionPane.showInputDialog("Enter UserID to Delete");

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/college_db", "root", "password")) {
            String query = "DELETE FROM users WHERE UserID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, userIdToDelete);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "User deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "User not found.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting user.");
        }
    }

    private void viewResults() {
        // View student results (example for student PRN)
        String prn = JOptionPane.showInputDialog("Enter Student PRN");

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/college_db", "root", "password")) {
            String query = "SELECT * FROM results WHERE PRN = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, prn);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String results = "Results for PRN: " + prn + "\n";
                // Example: Displaying few subjects
                results += "Maths1: " + resultSet.getInt("maths1") + "\n";
                results += "Physics: " + resultSet.getInt("physics") + "\n";
                // Add more fields as per the database schema

                JOptionPane.showMessageDialog(this, results);
            } else {
                JOptionPane.showMessageDialog(this, "No results found for this PRN.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching results.");
        }
    }

    private void logout() {
        // Logout functionality (Redirect to Login page)
        this.dispose();
        new LoginPage().setVisible(true); // Go back to login page
    }

    public static void main(String[] args) {
        new AdminPanel(); // Launch the admin panel
    }
}
