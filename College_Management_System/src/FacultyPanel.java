import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FacultyPanel extends JFrame {
    private String facultyID;
    private JButton viewCoursesButton, manageResultsButton, applyLeaveButton, logoutButton;

    public FacultyPanel(String facultyID) {
        this.facultyID = facultyID;

        // Set up the frame
        setTitle("Faculty Panel");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create the layout
        setLayout(new BorderLayout());

        // Title panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Welcome Faculty: " + facultyID);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        titlePanel.setBackground(new Color(135, 206, 250));
        add(titlePanel, BorderLayout.NORTH);

        // Button panel for actions
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        viewCoursesButton = new JButton("View Assigned Courses");
        manageResultsButton = new JButton("Manage Student Results");
        applyLeaveButton = new JButton("Apply for Leave");
        logoutButton = new JButton("Logout");

        buttonPanel.add(viewCoursesButton);
        buttonPanel.add(manageResultsButton);
        buttonPanel.add(applyLeaveButton);
        buttonPanel.add(logoutButton);

        // Add button panel to the center of the frame
        add(buttonPanel, BorderLayout.CENTER);

        // Add action listeners for buttons
        viewCoursesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewAssignedCourses();
            }
        });

        manageResultsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manageResults();
            }
        });

        applyLeaveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyLeave();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });

        // Display the frame
        setVisible(true);
    }

    // Method to view assigned courses
    private void viewAssignedCourses() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/college_db", "root", "password")) {
            // Query to retrieve courses assigned to the faculty using FacultyID in the courses table
            String query = "SELECT CourseID, CourseName FROM courses WHERE FacultyID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, facultyID);  // Assuming FacultyID is a column in the courses table
            
            ResultSet resultSet = statement.executeQuery();
    
            StringBuilder coursesList = new StringBuilder("Assigned Courses:\n");
            while (resultSet.next()) {
                coursesList.append("Course ID: ").append(resultSet.getString("CourseID"))
                           .append(", Course Name: ").append(resultSet.getString("CourseName"))
                           .append("\n");
            }
    
            if (coursesList.length() == 0) {
                JOptionPane.showMessageDialog(this, "No courses assigned.");
            } else {
                JOptionPane.showMessageDialog(this, coursesList.toString());
            }
        } catch (SQLException ex) {
            ex.printStackTrace(); // Debugging: Print stack trace for better error tracking
            JOptionPane.showMessageDialog(this, "Error fetching courses. " + ex.getMessage());
        }
    }
    
    
    
    // Method to manage student results
    private void manageResults() {
        String studentPRN = JOptionPane.showInputDialog(this, "Enter Student PRN:");
        String courseID = JOptionPane.showInputDialog(this, "Enter Course ID:");

        if (studentPRN != null && courseID != null) {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/college_db", "root", "password")) {
                // Query to check if the student is enrolled in the course
                String checkQuery = "SELECT * FROM results WHERE StudentID = ? AND CourseID = ?";
                PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
                checkStatement.setString(1, studentPRN);
                checkStatement.setString(2, courseID);
                ResultSet checkResult = checkStatement.executeQuery();

                if (checkResult.next()) {
                    // If the student exists in the course, update the results
                    int marks = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter marks:"));
                    String updateQuery = "UPDATE results SET Marks = ? WHERE StudentID = ? AND CourseID = ?";
                    PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                    updateStatement.setInt(1, marks);
                    updateStatement.setString(2, studentPRN);
                    updateStatement.setString(3, courseID);
                    int rowsUpdated = updateStatement.executeUpdate();

                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(this, "Results updated successfully.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Error updating results.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Student is not enrolled in this course.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error managing results.");
            }
        }
    }

    // Method to apply for leave
    private void applyLeave() {
        String leaveDate = JOptionPane.showInputDialog(this, "Enter Leave Date (YYYY-MM-DD):");
        String leaveReason = JOptionPane.showInputDialog(this, "Enter Leave Reason:");

        if (leaveDate != null && leaveReason != null) {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/college_db", "root", "password")) {
                String query = "INSERT INTO leave_requests (FacultyID, LeaveDate, LeaveReason) VALUES (?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, facultyID);
                statement.setString(2, leaveDate);
                statement.setString(3, leaveReason);
                int rowsInserted = statement.executeUpdate();

                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "Leave request submitted successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error submitting leave request.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error applying for leave.");
            }
        }
    }

    // Method to logout
    private void logout() {
        this.dispose();
        new LoginPage().setVisible(true); // Go back to login page
    }

    public static void main(String[] args) {
        new FacultyPanel("FAC_12345"); // Example: Creating an instance with a dummy Faculty ID
    }
}
