package com.mycompany.studentcrud;

import javax.swing.*;
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
        JFrame frame = new JFrame("Student CRUD");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(9, 2, 5, 5));
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {
        panel.add(new JLabel("ID:"));
        idText = new JTextField();
        panel.add(idText);

        panel.add(new JLabel("Name:"));
        nameText = new JTextField();
        panel.add(nameText);

        panel.add(new JLabel("Email:"));
        emailText = new JTextField();
        panel.add(emailText);

        panel.add(new JLabel("Age:"));
        ageText = new JTextField();
        panel.add(ageText);

        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> executeQuery("INSERT INTO student (name, email, age) VALUES (?, ?, ?)", false));
        panel.add(addButton);

        JButton readButton = new JButton("Read");
        readButton.addActionListener(e -> fetchStudents());
        panel.add(readButton);

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> executeQuery("UPDATE student SET name = ?, email = ?, age = ? WHERE id = ?", true));
        panel.add(updateButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> executeQuery("DELETE FROM student WHERE id = ?", true));
        panel.add(deleteButton);

        resultArea = new JTextArea(8, 30);
        resultArea.setEditable(false);
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
            if (rowsAffected > 0) {
                resultArea.setText("Operation successful. " + rowsAffected + " row(s) affected.");
            } else {
                resultArea.setText("Operation failed. No changes were made.");
            }

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
