package controller;

import model.Repository;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CreateOrder extends JFrame {
    private JPanel mainPanel;
    private JTextField addressTF, zipTF, localityTF;
    private JButton createBut, cancelBut;

    public CreateOrder(MainWindow mainW, Repository rep) {
        setTitle("New order");
        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(mainPanel);
        setSize(400, 300);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mainW.setEnabled(true);
                mainW.toFront();
            }
        });

        cancelBut.addActionListener(e -> {
            dispose();
            mainW.setEnabled(true);
            mainW.toFront();
        });

        createBut.addActionListener(e -> {
            if (!addressTF.getText().isBlank() && !zipTF.getText().isBlank() && !localityTF.getText().isBlank()) {
                rep.createOrder(addressTF.getText(),zipTF.getText(),localityTF.getText());
                dispose();
                mainW.setEnabled(true);
                mainW.toFront();
            }
        });
    }
}
