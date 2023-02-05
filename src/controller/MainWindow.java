package controller;

import enums.ListOf;
import model.*;
import javax.swing.*;

public class MainWindow extends JFrame {

    private JPanel mainPanel;
    private JList<String> displayList;
    private JComboBox<String> sortAfterCBox, brandsCBox, coloursCBox, sizesCBox, categoriesCBox, listOfCBox;
    private JButton resetBut, refreshBut, buyBut, openBut, deleteBut, newOrderBut,
            newArticleBut, putCatBut, modifyBut, logoutBut, searchBut;
    private JLabel infoLabel, sumLabel;
    private JTextField searchTF;
    private JSpinner quantitySpinner;
    private final ListCreator listCreator;

    public MainWindow(Repository rep) {
        new ComboBoxSetup(listOfCBox, brandsCBox, coloursCBox, sizesCBox);
        listCreator = new ListCreator(displayList, infoLabel, sumLabel, listOfCBox, brandsCBox, coloursCBox, sizesCBox);
        rep.setNewOrderBut(newOrderBut);
        rep.setListCreator(listCreator);
        listOfCBox.setSelectedItem(ListOf.PRODUCTS.getName());
        listCreator.setDisplayList();

        // not implemented.
        openBut.setEnabled(false);
        searchTF.setEnabled(false);
        searchBut.setEnabled(false);
        categoriesCBox.setEnabled(false);
        sortAfterCBox.setEnabled(false);
        deleteBut.setEnabled(false);
        newArticleBut.setEnabled(false);
        putCatBut.setEnabled(false);
        modifyBut.setEnabled(false);

        setTitle("Webbshop");
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(mainPanel);
        setSize(2000, 1000);
        setLocationRelativeTo(null);

        listOfCBox.addItemListener(e -> {
            if (listOfCBox.getSelectedItem() == ListOf.PRODUCTS.getName() ||
                    listOfCBox.getSelectedItem() == ListOf.CUSTOMER_BOUGHT.getName() ||
                    listOfCBox.getSelectedItem() == ListOf.ORDER_ITEMS.getName()) {
                enableFilterBoxes();
            } else {
                disableFilterBoxes();
            }
            buyBut.setEnabled(listOfCBox.getSelectedItem() == ListOf.PRODUCTS.getName());
            listCreator.setDisplayList();
        });

        brandsCBox.addItemListener(e -> {
            listCreator.setDisplayList();
        });

        coloursCBox.addItemListener(e -> {
            listCreator.setDisplayList();
        });

        sizesCBox.addItemListener(e -> {
            listCreator.setDisplayList();
        });

        refreshBut.addActionListener(e -> {
            listCreator.setDisplayList();
        });

        resetBut.addActionListener(e -> {
            brandsCBox.setSelectedIndex(0);
            coloursCBox.setSelectedIndex(0);
            sizesCBox.setSelectedIndex(0);
            listCreator.setDisplayList();
        });

        buyBut.addActionListener(e -> {
            if (!(displayList.getSelectedValue() == null)) {
                rep.buyProduct(displayList.getSelectedValue(), (Integer) quantitySpinner.getValue());
            } else {
                JOptionPane.showMessageDialog(null,"No product selected.");
            }
        });

        newOrderBut.addActionListener(e -> {
            new CreateOrder(this, rep);
            this.setEnabled(false);
        });

        logoutBut.addActionListener(e -> {
            new Login();
            dispose();
        });
    }

    private void disableFilterBoxes() {
        brandsCBox.setEnabled(false);
        coloursCBox.setEnabled(false);
        sizesCBox.setEnabled(false);
    }

    private void enableFilterBoxes() {
        brandsCBox.setEnabled(true);
        coloursCBox.setEnabled(true);
        sizesCBox.setEnabled(true);
    }

    private void createUIComponents() {
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 200, 1));
    }
}