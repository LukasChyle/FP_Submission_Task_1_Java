package model;

import enums.Tables;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Repository {

    private int customerId;
    private int currentOrderId;
    private int currentOrderNumber;
    private JButton newOrderBut;
    private ListCreator listCreator;

    public void setCustomerId(int id) {
        customerId = id;
    }

    public void setNewOrderBut(JButton newOrderBut) {
        this.newOrderBut = newOrderBut;
    }

    public void setListCreator(ListCreator listCreator) {
        this.listCreator = listCreator;
    }

    public void createOrder(String adress, String zip, String locality) {
        Properties p = new Properties();
        try {
            p.load(new FileInputStream("resources/Properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (Connection con = DriverManager.getConnection(
                p.getProperty("connectionString"), p.getProperty("username"), p.getProperty("password"));
             CallableStatement stmt = con.prepareCall("call lukaschyle_webbshop.createOrder(?,?,?,?,?,?)")) {

            stmt.setString(1, adress);
            stmt.setString(2, zip);
            stmt.setString(3, locality);
            stmt.setInt(4, customerId);
            stmt.registerOutParameter(5, Types.INTEGER);
            stmt.registerOutParameter(6, Types.INTEGER);
            stmt.executeQuery();
            currentOrderId = stmt.getInt(5);
            currentOrderNumber = stmt.getInt(6);

            stmt.close();
            con.close();
            JOptionPane.showMessageDialog(null, "New order with orderNr: " +
                    currentOrderNumber + " was created," +
                    " everything you buy in this session will be put into this order.");
            newOrderBut.setEnabled(false);
            listCreator.setDisplayList();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null, "Something went wrong and your order was not created.");
        }
    }

    public void buyProduct(String productString, int quantity) {
        if (currentOrderId == 0) {
            JOptionPane.showMessageDialog(null, "You need you create a order for this session.");
        } else {
            String artNr = productString.trim().substring(0, 14).trim();

            Properties p = new Properties();
            try {
                p.load(new FileInputStream("resources/Properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try (Connection con = DriverManager.getConnection(
                    p.getProperty("connectionString"), p.getProperty("username"), p.getProperty("password"));
                 CallableStatement stmt = con.prepareCall("call lukaschyle_webbshop.AddToOrder(?,?,?,?)")) {

                stmt.setInt(1, currentOrderId);
                stmt.setString(2, artNr);
                stmt.setInt(3, quantity);
                stmt.registerOutParameter(4, Types.VARCHAR);
                stmt.executeQuery();

                String model = stmt.getString(4);

                stmt.close();
                con.close();
                JOptionPane.showMessageDialog(null, "Art.Nr:" + artNr + ", Model: " +
                        model + ", was added to order: " + currentOrderNumber);
                listCreator.setDisplayList();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(
                        null, "Something went wrong and the product was not bought");
            }
        }
    }

    public List<?> getTable(Tables tableToFetch) {
        Properties p = new Properties();
        try {
            p.load(new FileInputStream("resources/Properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (Connection con = DriverManager.getConnection(
                p.getProperty("connectionString"), p.getProperty("username"), p.getProperty("password"));
             Statement stmt = con.createStatement()) {

            return switch (tableToFetch) {
                case PRODUCTS -> getProductsList(stmt);
                case CUSTOMERS -> getCustomersList(stmt);
                case ORDERS -> getOrdersList(stmt);
                case ORDER_ITEMS -> getOrderItemsList(stmt);
                case MODELS -> getModelsList(stmt);
                case BRANDS -> getBrandsList(stmt);
                case COLOURS -> getColorsList(stmt);
                case SIZES -> getSizesList(stmt);
                case ACCESS_INFO -> getAccessInfoList(stmt);
            };
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Product> getProductsList(Statement stmt) throws SQLException {
        ResultSet rs = stmt.executeQuery("select id, artikelnummer, modellId," +
                " märkeId, färgId, storlekId, pris, saldo" +
                " from lukaschyle_webbshop.produkt");
        List<Product> products = new ArrayList<>();
        while (rs.next()) {
            products.add(new Product(
                    rs.getInt("id"),
                    rs.getString("artikelnummer"),
                    rs.getInt("modellId"),
                    rs.getDouble("pris"),
                    rs.getInt("saldo"),
                    rs.getInt("märkeId"),
                    rs.getInt("färgId"),
                    rs.getInt("storlekId")));
        }
        return products;
    }

    private List<Customer> getCustomersList(Statement stmt) throws SQLException {
        ResultSet rs = stmt.executeQuery("select id, personnummer, förnamn, efternamn," +
                " telefonnummer, lösenord, created From lukaschyle_webbshop.kund");
        List<Customer> customers = new ArrayList<>();
        while (rs.next()) {
            customers.add(new Customer(
                    rs.getInt("id"),
                    rs.getString("personnummer"),
                    rs.getString("förnamn"),
                    rs.getString("efternamn"),
                    rs.getString("telefonnummer"),
                    rs.getString("lösenord"),
                    rs.getString("created")));
        }
        return customers;
    }

    private List<Order> getOrdersList(Statement stmt) throws SQLException {
        ResultSet rs = stmt.executeQuery("select id, kundId, ordernr, postadress, postnummer," +
                " postort, created From lukaschyle_webbshop.beställning");
        List<Order> orders = new ArrayList<>();
        while (rs.next()) {
            orders.add(new Order(
                    rs.getInt("id"),
                    rs.getInt("kundId"),
                    rs.getInt("ordernr"),
                    rs.getString("postadress"),
                    rs.getString("postnummer"),
                    rs.getString("postort"),
                    rs.getString("created")));
        }
        return orders;
    }

    private List<OrderItem> getOrderItemsList(Statement stmt) throws SQLException {
        ResultSet rs = stmt.executeQuery("select id, beställningId, produktId, antal, pris" +
                " From lukaschyle_webbshop.artikel");
        List<OrderItem> orderItems = new ArrayList<>();
        while (rs.next()) {
            orderItems.add(new OrderItem(
                    rs.getInt("id"),
                    rs.getInt("beställningId"),
                    rs.getInt("produktId"),
                    rs.getInt("antal"),
                    rs.getDouble("pris")));
        }
        return orderItems;
    }

    private List<Model> getModelsList(Statement stmt) throws SQLException {
        ResultSet rs = stmt.executeQuery("select id, namn" +
                " From lukaschyle_webbshop.modell");
        List<Model> models = new ArrayList<>();
        while (rs.next()) {
            models.add(new Model(
                    rs.getInt("id"),
                    rs.getString("namn")));
        }
        return models;
    }

    private List<Brand> getBrandsList(Statement stmt) throws SQLException {
        ResultSet rs = stmt.executeQuery("select id, namn" +
                " From lukaschyle_webbshop.märke");
        List<Brand> brands = new ArrayList<>();
        while (rs.next()) {
            brands.add(new Brand(
                    rs.getInt("id"),
                    rs.getString("namn")));
        }
        return brands;
    }

    private List<Colour> getColorsList(Statement stmt) throws SQLException {
        ResultSet rs = stmt.executeQuery("select id, namn" +
                " From lukaschyle_webbshop.färg");
        List<Colour> colours = new ArrayList<>();
        while (rs.next()) {
            colours.add(new Colour(
                    rs.getInt("id"),
                    rs.getString("namn")));
        }
        return colours;
    }

    private List<Size> getSizesList(Statement stmt) throws SQLException {
        ResultSet rs = stmt.executeQuery("select id, nummer" +
                " From lukaschyle_webbshop.storlek");
        List<Size> sizes = new ArrayList<>();
        while (rs.next()) {
            sizes.add(new Size(
                    rs.getInt("id"),
                    rs.getString("nummer")));
        }
        return sizes;
    }

    private List<Customer> getAccessInfoList(Statement stmt) throws SQLException {
        ResultSet rs = stmt.executeQuery("select id, personnummer, lösenord" +
                " From lukaschyle_webbshop.kund");
        List<Customer> accessInfo = new ArrayList<>();
        while (rs.next()) {
            accessInfo.add(new Customer(
                    rs.getInt("id"),
                    rs.getString("personnummer"),
                    rs.getString("lösenord")));
        }
        return accessInfo;
    }
}