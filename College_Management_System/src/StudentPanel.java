import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentPanel extends JFrame {
    private String studentID;
    private JButton viewDetailsButton, viewCoursesButton, viewResultsButton, updateDetailsButton, logoutButton;

    public StudentPanel(String studentID) {
        this.studentID = studentID;

        // Set up the frame
        this.setTitle("Student Panel");
        this.setSize(600, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        // Create the layout
        setLayout(new BorderLayout());

        // Title panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Welcome, Student: " + studentID);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        titlePanel.setBackground(new Color(135, 206, 250));
        add(titlePanel, BorderLayout.NORTH);

        // Button panel for actions
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        viewDetailsButton = new JButton("View My Details");
        viewCoursesButton = new JButton("View My Enrolled Courses");
        viewResultsButton = new JButton("View My Results");
        updateDetailsButton = new JButton("Update My Details");
        logoutButton = new JButton("Logout");

        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(viewCoursesButton);
        buttonPanel.add(viewResultsButton);
        buttonPanel.add(updateDetailsButton);
        buttonPanel.add(logoutButton);

        // Add button panel to the center of the frame
        add(buttonPanel, BorderLayout.CENTER);

        // Add action listeners for buttons
        viewDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewStudentDetails();
            }
        });

        viewCoursesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewEnrolledCourses();
            }
        });

        viewResultsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewStudentResults();
            }
        });

        updateDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStudentDetails();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });

        // Display the frame
        this.setVisible(true);
    }

    // Method to view student details
    private void viewStudentDetails() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/college_db", "root", "password")) {
            String query = "SELECT * FROM students WHERE StudentID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, studentID);  // Assuming StudentID is the identifier
            ResultSet resultSet = statement.executeQuery();
    
            if (resultSet.next()) {
                String details = "Student ID: " + studentID + "\n";
                details += "Name: " + resultSet.getString("FirstName") + " " + resultSet.getString("LastName") + "\n";
                details += "Email: " + resultSet.getString("Email") + "\n";
                details += "Phone: " + resultSet.getString("Phone") + "\n";
                details += "Department: " + resultSet.getString("Department") + "\n";
    
                JOptionPane.showMessageDialog(this, details);
            } else {
                JOptionPane.showMessageDialog(this, "No details found for this Student ID.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching student details. Check if the StudentID is correct.");
        }
    }
    
    

    // Method to view courses the student is enrolled in
    private void viewEnrolledCourses() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/college_db", "root", "password")) {
            // Query to retrieve courses for the student
            String query = "SELECT c.CourseID, c.CourseName FROM courses c " +
                    "JOIN enrollments e ON c.CourseID = e.CourseID WHERE e.StudentID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, studentID);
            ResultSet resultSet = statement.executeQuery();

            StringBuilder courseList = new StringBuilder("Enrolled Courses:\n");
            while (resultSet.next()) {
                courseList.append("Course ID: ").append(resultSet.getString("CourseID"))
                        .append(", Course Name: ").append(resultSet.getString("CourseName"))
                        .append("\n");
            }

            if (courseList.length() == 0) {
                JOptionPane.showMessageDialog(this, "No courses found for this Student.");
            } else {
                JOptionPane.showMessageDialog(this, courseList.toString());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching enrolled courses. Check if the StudentID exists.");
        }
    }

    // Method to view student results
    private void viewStudentResults() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/college_db", "root", "password")) {
            // Query to retrieve results
            String query = "SELECT r.CourseID, c.CourseName, r.Marks FROM results r " +
                    "JOIN courses c ON r.CourseID = c.CourseID WHERE r.StudentID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, studentID);
            ResultSet resultSet = statement.executeQuery();

            StringBuilder resultsList = new StringBuilder("Results:\n");
            while (resultSet.next()) {
                resultsList.append("Course ID: ").append(resultSet.getString("CourseID"))
                        .append(", Course Name: ").append(resultSet.getString("CourseName"))
                        .append(", Marks: ").append(resultSet.getInt("Marks"))
                        .append("\n");
            }

            if (resultsList.length() == 0) {
                JOptionPane.showMessageDialog(this, "No results found for this Student.");
            } else {
                JOptionPane.showMessageDialog(this, resultsList.toString());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching results. Check if the StudentID exists.");
        }
    }

    // Method to update student details
    private void updateStudentDetails() {
        String newPhone = JOptionPane.showInputDialog(this, "Enter new phone number:");
        String newEmail = JOptionPane.showInputDialog(this, "Enter new email address:");

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/college_db", "root", "password")) {
            String query = "UPDATE students SET Email = ?, Phone = ? WHERE StudentID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, newEmail);
            statement.setString(2, newPhone);
            statement.setString(3, studentID);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Details updated successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Error updating details.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating details.");
        }
    }

    // Method to logout
    private void logout() {
        this.dispose();
        new LoginPage().setVisible(true); // Go back to login page
    }

    public static void main(String[] args) {
        new StudentPanel("S12345"); // Example: Creating an instance with a dummy Student ID
    }
}
