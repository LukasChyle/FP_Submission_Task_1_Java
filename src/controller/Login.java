package controller;

import model.Customer;
import model.Repository;
import enums.Tables;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

public class Login extends JFrame {

    private JPanel mainPanel;
    private JTextField ssnTF;
    private JPasswordField passField;
    private JButton loginBut, createBut;
    private final Repository rep;

    @SuppressWarnings("unchecked")
    public Login() {
        rep = new Repository();

        //not implemented.
        createBut.setEnabled(false);

        setTitle("Login");
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(mainPanel);
        setSize(300, 200);
        setLocationRelativeTo(null);

        loginBut.addActionListener(e -> {
            if (!ssnTF.getText().isBlank() || passField.getPassword().length != 0) {

                List<Customer> accessInfoList = (List<Customer>) rep.getTable(Tables.ACCESS_INFO);
                accessInfoList.stream()
                        .filter(c -> ssnTF.getText().trim().equals(c.getSsn()) &&
                                Arrays.equals(passField.getPassword(), c.getPassword().toCharArray()))
                        .findFirst().ifPresentOrElse(c -> {
                            new MainWindow(rep);
                            rep.setCustomerId(c.getId());
                            dispose();
                        }, () ->
                                JOptionPane.showMessageDialog(
                                        null, "SSN or Password was invalid.")
                        );
            } else {
                JOptionPane.showMessageDialog(null, "You have to fill username and password.");
            }
        });
    }
}