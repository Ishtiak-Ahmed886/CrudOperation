package com.mycompany.mavenproject1;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class DatabaseConnection {

    public static void main(String[] args) {
        // Create a JFrame (Window) for the GUI
        JFrame frame = new JFrame("Database Connection with User Input");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create a JPanel
        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        // Display the window
        frame.setVisible(true);
    }

    // Method to place components on the JPanel
    private static void placeComponents(JPanel panel) {
        panel.setLayout(null);

        // Name Label and TextField
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(10, 20, 80, 25);
        panel.add(nameLabel);

        JTextField nameText = new JTextField(20);
        nameText.setBounds(100, 20, 165, 25);
        panel.add(nameText);

        // Age Label and TextField
        JLabel ageLabel = new JLabel("Age:");
        ageLabel.setBounds(10, 50, 80, 25);
        panel.add(ageLabel);

        JTextField ageText = new JTextField(20);
        ageText.setBounds(100, 50, 165, 25);
        panel.add(ageText);

        // Submit Button
        JButton submitButton = new JButton("Submit");
        submitButton.setBounds(10, 80, 80, 25);
        panel.add(submitButton);

        // Action Listener for Submit Button
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Get user input
                String name = nameText.getText();
                String age = ageText.getText();
                
                // Database URL, username, and password
                String url = "jdbc:mysql://localhost:3306/test";
                String user = "root";  // Replace with your MySQL username
                String password = "";  // Replace with your MySQL password

                // Insert user input into the database
                try {
                    // Load the MySQL JDBC driver
                    Class.forName("com.mysql.cj.jdbc.Driver");

                    // Get the connection
                    Connection conn = DriverManager.getConnection(url, user, password);

                    // Prepare SQL query to insert data
                    String query = "INSERT INTO users (name, age) VALUES (?, ?)";
                    PreparedStatement pst = conn.prepareStatement(query);
                    pst.setString(1, name);
                    pst.setInt(2, Integer.parseInt(age));

                    // Execute query
                    pst.executeUpdate();

                    System.out.println("Data inserted successfully!");

                    // Close the connection
                    conn.close();
                } catch (ClassNotFoundException ex) {
                    System.out.println("MySQL JDBC Driver not found!");
                    ex.printStackTrace();
                } catch (SQLException ex) {
                    System.out.println("Connection failed!");
                    ex.printStackTrace();
                }
            }
        });
    }
}
