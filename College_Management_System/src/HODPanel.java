import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class HODPanel extends JFrame {
    private String hodID;
    private JButton viewFacultyButton, viewCoursesButton, viewResultsButton, approveLeaveButton, logoutButton;

    public HODPanel(String hodID) {
        this.hodID = hodID;

        // Set up the frame
        this.setTitle("HOD Panel");
        this.setSize(600, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        // Create the layout
        setLayout(new BorderLayout());

        // Title panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Welcome, HOD: " + hodID);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        titlePanel.setBackground(new Color(135, 206, 250));
        add(titlePanel, BorderLayout.NORTH);

        // Button panel for actions
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        viewFacultyButton = new JButton("View All Faculty Members");
        viewCoursesButton = new JButton("View and Assign Courses");
        viewResultsButton = new JButton("View Students' Results");
        approveLeaveButton = new JButton("Approve Leave Requests");
        logoutButton = new JButton("Logout");

        buttonPanel.add(viewFacultyButton);
        buttonPanel.add(viewCoursesButton);
        buttonPanel.add(viewResultsButton);
        buttonPanel.add(approveLeaveButton);
        buttonPanel.add(logoutButton);

        // Add button panel to the center of the frame
        add(buttonPanel, BorderLayout.CENTER);

        // Add action listeners for buttons
        viewFacultyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewFaculty();
            }
        });

        viewCoursesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewAndAssignCourses();
            }
        });

        viewResultsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewResults();
            }
        });

        approveLeaveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                approveLeave();
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

    // Method to view all faculty members
    private void viewFaculty() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/college_db", "root", "password")) {
            // Query to retrieve all faculty members (without HOD_ID filter)
            String query = "SELECT * FROM faculty";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            StringBuilder facultyList = new StringBuilder("Faculty List:\n");
            while (resultSet.next()) {
                facultyList.append("Faculty ID: ").append(resultSet.getString("FacultyID"))
                        .append(", Name: ").append(resultSet.getString("FirstName") + " " + resultSet.getString("LastName"))
                        .append(", Department: ").append(resultSet.getString("Department"))
                        .append("\n");
            }

            if (facultyList.length() == 0) {
                JOptionPane.showMessageDialog(this, "No faculty found.");
            } else {
                JOptionPane.showMessageDialog(this, facultyList.toString());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching faculty.");
        }
    }

    // Method to view and assign courses
    private void viewAndAssignCourses() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/college_db", "root", "password")) {
            // Query to retrieve all courses
            String query = "SELECT * FROM courses";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            StringBuilder coursesList = new StringBuilder("Courses List:\n");
            while (resultSet.next()) {
                coursesList.append("Course ID: ").append(resultSet.getString("CourseID"))
                        .append(", Course Name: ").append(resultSet.getString("CourseName"))
                        .append(", Faculty ID: ").append(resultSet.getString("FacultyID"))
                        .append("\n");
            }

            if (coursesList.length() == 0) {
                JOptionPane.showMessageDialog(this, "No courses found.");
            } else {
                JOptionPane.showMessageDialog(this, coursesList.toString());
            }

            // Option to assign a new course
            String newCourseName = JOptionPane.showInputDialog(this, "Enter new course name:");
            String newFacultyID = JOptionPane.showInputDialog(this, "Enter faculty ID to assign the course to:");

            if (newCourseName != null && !newCourseName.isEmpty() && newFacultyID != null && !newFacultyID.isEmpty()) {
                String assignQuery = "INSERT INTO courses (CourseName, FacultyID) VALUES (?, ?)";
                PreparedStatement insertStatement = connection.prepareStatement(assignQuery);
                insertStatement.setString(1, newCourseName);
                insertStatement.setString(2, newFacultyID);
                int rowsInserted = insertStatement.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "New course assigned successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to assign course.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching or assigning courses.");
        }
    }

    // Method to view student results
    private void viewResults() {
        String studentID = JOptionPane.showInputDialog(this, "Enter Student ID:");
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/college_db", "root", "password")) {
            String query = "SELECT * FROM results WHERE StudentID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, studentID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String results = "Results for Student ID: " + studentID + "\n";
                results += "Course ID: " + resultSet.getString("CourseID") + "\n";
                results += "Marks: " + resultSet.getInt("Marks") + "\n";

                JOptionPane.showMessageDialog(this, results);
            } else {
                JOptionPane.showMessageDialog(this, "No results found for this Student ID.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching results.");
        }
    }

    // Method to approve student leave requests
    private void approveLeave() {
        String studentID = JOptionPane.showInputDialog(this, "Enter Student ID for Leave Approval:");
        String approvalStatus = JOptionPane.showInputDialog(this, "Enter approval status (Approved/Rejected):");

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/college_db", "root", "password")) {
            String query = "UPDATE leave_requests SET Status = ? WHERE StudentID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, approvalStatus);
            statement.setString(2, studentID);
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Leave request updated successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "No leave request found for this Student ID.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error approving leave.");
        }
    }

    // Method to logout
    private void logout() {
        this.dispose();
        new LoginPage().setVisible(true); // Go back to login page
    }

    public static void main(String[] args) {
        new HODPanel("HOD_12345"); // Example: Creating an instance with a dummy HOD ID
    }
}
