package com.mycompany.studentcrud;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentCRUD {
    private static JTextField idText, nameText, emailText, ageText;
    private static JTextArea resultArea;
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/student_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        JFrame frame = new JFrame("Student Management System");
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // Header
        JLabel headerLabel = new JLabel("Student Management System", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setBorder(new EmptyBorder(20, 10, 20, 10));
        frame.add(headerLabel, BorderLayout.NORTH);

        // Main Panel
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 50, 20, 50));
        frame.add(panel, BorderLayout.CENTER);

        placeComponents(panel);

        // Footer
        JLabel footerLabel = new JLabel("© 2025 Student CRUD App", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        footerLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        frame.add(footerLabel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("ID:"));
        idText = new JTextField();
        formPanel.add(idText);

        formPanel.add(new JLabel("Name:"));
        nameText = new JTextField();
        formPanel.add(nameText);

        formPanel.add(new JLabel("Email:"));
        emailText = new JTextField();
        formPanel.add(emailText);

        formPanel.add(new JLabel("Age:"));
        ageText = new JTextField();
        formPanel.add(ageText);

        panel.add(formPanel);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        JButton addButton = new JButton("Add");
        addButton.setBackground(new Color(34, 139, 34));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> executeQuery("INSERT INTO student (name, email, age) VALUES (?, ?, ?)", false));
        buttonPanel.add(addButton);

        JButton readButton = new JButton("Read");
        readButton.setBackground(new Color(70, 130, 180));
        readButton.setForeground(Color.WHITE);
        readButton.addActionListener(e -> fetchStudents());
        buttonPanel.add(readButton);

        JButton updateButton = new JButton("Update");
        updateButton.setBackground(new Color(255, 165, 0));
        updateButton.setForeground(Color.WHITE);
        updateButton.addActionListener(e -> executeQuery("UPDATE student SET name = ?, email = ?, age = ? WHERE id = ?", true));
        buttonPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.setBackground(new Color(220, 20, 60));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(e -> executeQuery("DELETE FROM student WHERE id = ?", true));
        buttonPanel.add(deleteButton);

        panel.add(buttonPanel);

        resultArea = new JTextArea(10, 30);
        resultArea.setEditable(false);
        resultArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.add(new JScrollPane(resultArea));
    }

    private static void executeQuery(String query, boolean requiresID) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pst = conn.prepareStatement(query)) {

            if (query.startsWith("INSERT")) {
                if (nameText.getText().isEmpty() || emailText.getText().isEmpty() || ageText.getText().isEmpty()) {
                    resultArea.setText("Error: Name, Email, and Age cannot be empty.");
                    return;
                }
                pst.setString(1, nameText.getText());
                pst.setString(2, emailText.getText());
                pst.setInt(3, Integer.parseInt(ageText.getText()));
            } else if (query.startsWith("UPDATE")) {
                if (idText.getText().isEmpty() || nameText.getText().isEmpty() || emailText.getText().isEmpty() || ageText.getText().isEmpty()) {
                    resultArea.setText("Error: ID, Name, Email, and Age are required for update.");
                    return;
                }
                pst.setString(1, nameText.getText());
                pst.setString(2, emailText.getText());
                pst.setInt(3, Integer.parseInt(ageText.getText()));
                pst.setInt(4, Integer.parseInt(idText.getText()));
            } else if (query.startsWith("DELETE")) {
                if (idText.getText().isEmpty()) {
                    resultArea.setText("Error: ID is required for deletion.");
                    return;
                }
                pst.setInt(1, Integer.parseInt(idText.getText()));
            }

            int rowsAffected = pst.executeUpdate();
            resultArea.setText("Operation successful. " + rowsAffected + " row(s) affected.");

        } catch (NumberFormatException ex) {
            resultArea.setText("Error: Invalid number format.");
        } catch (Exception ex) {
            resultArea.setText("Error: " + ex.getMessage());
        }
    }

    private static void fetchStudents() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM student")) {

            StringBuilder results = new StringBuilder("Students List:\n");
            while (rs.next()) {
                results.append("ID: ").append(rs.getInt("id"))
                        .append(", Name: ").append(rs.getString("name"))
                        .append(", Email: ").append(rs.getString("email"))
                        .append(", Age: ").append(rs.getInt("age"))
                        .append("\n");
            }
            resultArea.setText(results.toString());

        } catch (Exception ex) {
            resultArea.setText("Error: " + ex.getMessage());
        }
    }
}

