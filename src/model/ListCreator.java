package model;

import Interfaces.CompareCustomerAndOrder;
import Interfaces.CompareOrderAndItem;
import Interfaces.CompareItemAndProduct;
import enums.ListOf;
import enums.Tables;

import javax.swing.*;
import java.util.*;

public class ListCreator {

    Repository repository;
    List<String> stringList;
    DefaultListModel<String> listModel;
    JLabel infoLabel, sumLabel;
    JList<String> displayList;
    JComboBox<String> listOfCBox, brandsCBox, coloursCBox, sizesCBox;
    List<Product> products;
    List<Customer> customers;
    List<Order> orders;
    List<OrderItem> orderItems;
    List<Model> models;
    List<Brand> brands;
    List<Colour> colours;
    List<Size> sizes;

    CompareCustomerAndOrder customerAndOrder = (c, o) -> c.getId() == o.getCustomerId();
    CompareOrderAndItem orderAndItem = (o, i) -> o.getId() == i.getOrderId();
    CompareItemAndProduct itemAndProduct = (p, i) -> p.getProductId() == i.getId();

    public ListCreator(JList<String> displayList, JLabel infoLabel, JLabel sumLabel, JComboBox<String> listOfCBox,
                       JComboBox<String> brandsCBox, JComboBox<String> coloursCBox, JComboBox<String> sizesCBox) {
        this.displayList = displayList;
        this.infoLabel = infoLabel;
        this.sumLabel = sumLabel;
        this.brandsCBox = brandsCBox;
        this.coloursCBox = coloursCBox;
        this.sizesCBox = sizesCBox;
        this.listOfCBox = listOfCBox;
        repository = new Repository();
    }

    public void setDisplayList() {
        stringList = new ArrayList<>();
        listModel = new DefaultListModel<>();
        Arrays.stream(ListOf.values())
                .filter(e -> e.getName().equals(listOfCBox.getSelectedItem()))
                .findFirst().ifPresent(e -> {
                    switch (e) {
                        case PRODUCTS -> setProducts();
                        case ORDERS -> setOrders();
                        case CUSTOMERS -> setCustomers();
                        case ORDER_ITEMS -> setOrderItems();
                        case MODELS -> setModels();
                        case LOCALITIES -> setLocalities();
                        case CUSTOMER_BOUGHT -> setCustomersBought();
                    }
                });

        listModel.addAll(stringList);
        displayList.setModel(listModel);
    }

    private void setProducts() {
        updateProducts();
        updateOrderItems();
        updateModels();
        updateBrands();
        updateColours();
        updateSizes();

        String[] infoString = {"Art.nr.", "Model", "Brand", "Colour", "Size", "Price", "Balance", "Quantity sold"};
        infoLabel.setText(getFlexSpace(infoString));
        List<String[]> productsValues = filterProductList().stream().map(p -> {
            int quantitySold = orderItems.stream().filter(i -> itemAndProduct.compare(i , p))
                    .mapToInt(OrderItem::getQuantity).sum();
            return new String[]{
                    p.getArticleNumber(),
                    getModelName(p.getModelId()),
                    getBrandName(p.getBrandId()),
                    getColorName(p.getColourId()),
                    getSizeNumber(p.getSizeId()),
                    String.valueOf(p.getPrice()),
                    String.valueOf(p.getBalance()),
                    String.valueOf(quantitySold)
            };
        }).toList();

        productsValues.stream().sorted(Comparator.comparing(o -> o[1])).forEach(p -> stringList.add(getFlexSpace(p)));

        String[] sumString = {
                "Number of products: " + productsValues.size(),
                "Number of models: " + models.size(),
                "Quantity sold: " + productsValues.stream().mapToInt(p -> Integer.parseInt(p[7])).sum()};
        sumLabel.setText(getSpace(sumString));
    }


    private void setOrders() {
        updateOrders();
        updateCustomers();
        updateOrderItems();

        String[] infoString = {"Order Nr.", "Customer (SSN)", "Address", "ZIP code",
                "Locality", "Created", "Order items", "Bought for"};
        infoLabel.setText(getFlexSpace(infoString));

        List<String[]> ordersValues = orders.stream().map(o -> {
            String ssn = customers.stream().filter(c -> customerAndOrder.compare(c, o)).findFirst().get().getSsn();
            // values: orderItems, boughtFor.
            List<double[]> values = orderItems.stream().filter(i -> orderAndItem.compare(o, i))
                    .map(i -> new double[]{
                            i.getQuantity(),
                            (i.getQuantity() * i.getPrice())
                    }).toList();
            return new String[]{
                    String.valueOf(o.getOrderNumber()),
                    ssn,
                    o.getAdress(),
                    o.getZipCode(),
                    o.getLocality(),
                    o.getCreated(),
                    String.valueOf(values.stream().mapToInt(v -> (int) v[0]).sum()),
                    String.valueOf(values.stream().mapToDouble(v -> v[1]).sum())
            };
        }).toList();

        ordersValues.stream().sorted(Comparator.comparingInt(o -> Integer.parseInt(o[0])))
                .forEach(o -> stringList.add(getFlexSpace(o)));

        String[] sumString = {"Number of orders: " + orders.size(),
                "Number of order items: " + ordersValues.stream().mapToInt(v -> Integer.parseInt(v[6])).sum(),
                "sum bought for: " + ordersValues.stream().mapToDouble(v -> Double.parseDouble(v[7])).sum()};
        sumLabel.setText(getSpace(sumString));
    }

    private void setCustomers() {
        updateCustomers();
        updateOrders();
        updateOrderItems();

        String[] infoString = {"SSN", "First name", "Last name", "Phone number",
                "Created", "Orders", "Order items", "Bought for"};
        infoLabel.setText(getFlexSpace(infoString));

        List<String[]> customersValues = customers.stream().map(c -> {
            // values: orderItems, boughtFor.
            List<double[]> values = orders.stream().filter(o -> customerAndOrder.compare(c, o)).map(o -> {
                // values2: orderItems, boughtFor.
                List<double[]> values2 = orderItems.stream().filter(i -> orderAndItem.compare(o, i))
                        .map(i -> new double[]{
                                i.getQuantity(),
                                i.getPrice() * i.getQuantity()
                        }).toList();
                return new double[]{
                        values2.stream().mapToDouble(v -> v[0]).sum(),
                        values2.stream().mapToDouble(v -> v[1]).sum()
                };
            }).toList();
            return new String[]{
                    c.getSsn(),
                    c.getFirstname(),
                    c.getLastname(),
                    c.getPhoneNumber(),
                    c.getCreated(),
                    String.valueOf(values.size()),
                    String.valueOf(values.stream().mapToInt(v -> (int) v[0]).sum()),
                    String.valueOf(values.stream().mapToDouble(v -> v[1]).sum())
            };
        }).toList();

        customersValues.stream().sorted(Comparator.comparing(o -> o[1])).forEach(c -> stringList.add(getFlexSpace(c)));

        String[] sumString = {
                "Number of customers: " + customers.size(),
                "Number of orders: " + customersValues.stream().mapToInt(v -> Integer.parseInt(v[5])).sum(),
                "Number of order items: " + customersValues.stream().mapToInt(v -> Integer.parseInt(v[6])).sum(),
                "sum bought for: " + customersValues.stream().mapToDouble(v -> Double.parseDouble(v[7])).sum()};
        sumLabel.setText(getSpace(sumString));
    }

    private void setOrderItems() {
        updateProducts();
        updateOrderItems();
        updateOrders();
        updateModels();
        updateBrands();
        updateColours();
        updateSizes();

        String[] infoString = {"Order Nr.", "Art.nr.", "Model", "Brand", "Colour", "Size", "Price", "Quantity"};
        infoLabel.setText(getFlexSpace(infoString));

        List<String[]> itemsValues = new ArrayList<>();
        filterProductList().forEach(p -> orderItems.stream().filter(i -> itemAndProduct.compare(i, p)).forEach(i -> {
                    int orderNumb = orders.stream()
                            .filter(o -> orderAndItem.compare(o, i)).findFirst().get().getOrderNumber();
                    itemsValues.add(new String[]{
                            String.valueOf(orderNumb),
                            p.getArticleNumber(),
                            getModelName(p.getModelId()),
                            getBrandName(p.getBrandId()),
                            getColorName(p.getColourId()),
                            getSizeNumber(p.getSizeId()),
                            String.valueOf(i.getPrice()),
                            String.valueOf(i.getQuantity()
                            )}
                    );
        }));

        itemsValues.stream().sorted(Comparator.comparingInt(o -> Integer.parseInt(o[0])))
                .forEach(i -> stringList.add(getFlexSpace(i)));

        String[] sumString = {
                "Number of items: " + itemsValues.size(),
                "Total quantity: " + itemsValues.stream().mapToInt(v -> Integer.parseInt(v[7])).sum(),
                "Sum bought for: " + itemsValues.stream()
                        .mapToDouble(v -> Double.parseDouble(v[7]) * Double.parseDouble(v[6])).sum()};
        sumLabel.setText(getSpace(sumString));
    }

    private void setModels() {
        updateModels();
        updateBrands();
        updateProducts();
        updateOrderItems();

        String[] infoString = {"Model", "Brand", "Number of products",
                "Total Balance", "Quantity sold", "Sum sold for"};
        infoLabel.setText(getFlexSpace(infoString));

        List<String[]> modelsValues = models.stream().map(m -> {
            // values: brandId, balance, quantitySold, soldFor.
            List<double[]> values = products.stream().filter(p -> p.getModelId() == m.id()).map(p -> {
                // values2: quantitySold, soldFor.
                List<double[]> values2 = orderItems.stream().filter(i -> itemAndProduct.compare(i, p))
                        .map(i -> new double[]{
                                i.getQuantity(),
                                (i.getQuantity() * i.getPrice())
                        }).toList();
                return new double[]{
                        p.getBrandId(),
                        p.getBalance(),
                        values2.stream().mapToDouble(v -> v[0]).sum(),
                        values2.stream().mapToDouble(v -> v[1]).sum()
                };
            }).toList();
            return new String[]{
                    m.name(),
                    getBrandName(values.stream().mapToInt(v -> (int) v[0]).findAny().getAsInt()),
                    String.valueOf(values.size()),
                    String.valueOf(values.stream().mapToInt(v -> (int) v[1]).sum()),
                    String.valueOf(values.stream().mapToInt(v -> (int) v[2]).sum()),
                    String.valueOf(values.stream().mapToDouble(v -> v[3]).sum())
            };
        }).toList();

        modelsValues.stream().sorted(((o1, o2) -> Integer.parseInt(o2[4]) - Integer.parseInt(o1[4])))
                .forEach(s -> stringList.add(getFlexSpace(s)));

        String[] sumString = {
                "Number of models: " + models.size(),
                "Number of products: " + products.size(),
                "Total balance: " + modelsValues.stream().mapToInt(v -> Integer.parseInt(v[3])).sum(),
                "Total quantitySold: " + modelsValues.stream().mapToInt(v -> Integer.parseInt(v[4])).sum(),
                "Total sum sold for: " + modelsValues.stream().mapToDouble(v -> Double.parseDouble(v[5])).sum()};
        sumLabel.setText(getSpace(sumString));
    }

    private void setLocalities() {
        updateOrders();
        updateOrderItems();

        String[] infoString = {"Locality", "Orders", "Products bought", "Bought for"};
        infoLabel.setText(getFlexSpace(infoString));

        List<String> localities = orders.stream().map(Order::getLocality).distinct().toList();
        // localitiesValues: locality, orders, quantity, BoughtFor
        List<String[]> localitiesValues = new ArrayList<>(localities.stream().map(locality -> {
            // ordersValues: quantity, BoughtFor.
            List<double[]> ordersValues = orders.stream()
                    .filter(o -> locality.equalsIgnoreCase(o.getLocality())).map(o -> {
                // orderItemsValues: quantity, BoughtFor.
                List<double[]> orderItemsValues = orderItems.stream().filter(i -> orderAndItem.compare(o, i))
                        .map(i -> new double[]{
                                i.getQuantity(),
                                (i.getPrice() * i.getQuantity())
                        }).toList();
                return new double[]{
                        orderItemsValues.stream().mapToDouble(v -> v[0]).sum(),
                        orderItemsValues.stream().mapToDouble(v -> v[1]).sum()
                };
            }).toList();
            return new String[]{
                    locality,
                    String.valueOf(ordersValues.size()),
                    String.valueOf(ordersValues.stream().mapToInt(v -> (int) v[0]).sum()),
                    String.valueOf(ordersValues.stream().mapToDouble(v -> v[1]).sum())
            };
        }).toList());

        localitiesValues.sort((o1, o2) -> Double.compare(Double.parseDouble(o2[3]), (Double.parseDouble(o1[3]))));
        localitiesValues.forEach(l -> stringList.add(getFlexSpace(l)));

        String[] sumString = {
                "Localities: " + localities.size(),
                "Orders: " + localitiesValues.stream().mapToInt(l -> Integer.parseInt(l[1])).sum(),
                "Products bought: " + localitiesValues.stream().mapToInt(l -> Integer.parseInt(l[2])).sum(),
                "Bought for: " + localitiesValues.stream().mapToDouble(l -> Double.parseDouble(l[3])).sum()};
        sumLabel.setText(getSpace(sumString));
    }

    private void setCustomersBought() {
        updateProducts();
        updateOrderItems();
        updateOrders();
        updateCustomers();
        updateBrands();
        updateColours();
        updateSizes();

        String[] infoString = {"SSN", "First name", "Last name", "Phone number", "Created"};
        infoLabel.setText(getFlexSpace(infoString));

        List<Customer> customerList = new ArrayList<>();
        filterProductList().forEach(p ->
                orderItems.stream().filter(i -> itemAndProduct.compare(i, p)).forEach(i ->
                        orders.stream().filter(o -> orderAndItem.compare(o, i)).forEach(o ->
                                customers.stream().filter(c -> customerAndOrder.compare(c, o))
                                        .forEach(customerList::add)
                        )
                )
        );

        List<String[]> customerValues = new ArrayList<>(customerList.stream()
                .distinct().map(c -> new String[]{
                        c.getSsn(),
                        c.getFirstname(),
                        c.getLastname(),
                        c.getPhoneNumber(),
                        c.getCreated()
                }).toList()
        );

        customerValues.stream().sorted(Comparator.comparing(o -> o[1])).forEach(c -> stringList.add(getFlexSpace(c)));

        String[] sumString = {customerValues.size() + " Customers bought", "Brand: " + brandsCBox.getSelectedItem(),
                "Colour: " + coloursCBox.getSelectedItem(), "Size: " + sizesCBox.getSelectedItem()};
        sumLabel.setText(getSpace(sumString));
    }

    private List<Product> filterProductList() {
        return new ArrayList<>(products.stream()
                .filter(p -> Objects.equals(brandsCBox.getSelectedItem(), getBrandName(p.getBrandId()))
                        || brandsCBox.getSelectedIndex() == 0)
                .filter(p -> Objects.equals(coloursCBox.getSelectedItem(), getColorName(p.getColourId()))
                        || coloursCBox.getSelectedIndex() == 0)
                .filter(p -> Objects.equals(sizesCBox.getSelectedItem(), getSizeNumber(p.getSizeId()))
                        || sizesCBox.getSelectedIndex() == 0).toList()
        );
    }

    @SuppressWarnings("unchecked")
    private void updateProducts() {
        products = (List<Product>) repository.getTable(Tables.PRODUCTS);
    }

    @SuppressWarnings("unchecked")
    private void updateCustomers() {
        customers = (List<Customer>) repository.getTable(Tables.CUSTOMERS);
    }

    @SuppressWarnings("unchecked")
    private void updateOrders() {
        orders = (List<Order>) repository.getTable(Tables.ORDERS);
    }

    @SuppressWarnings("unchecked")
    private void updateOrderItems() {
        orderItems = (List<OrderItem>) repository.getTable(Tables.ORDER_ITEMS);
    }

    @SuppressWarnings("unchecked")
    private void updateModels() {
        models = (List<Model>) repository.getTable(Tables.MODELS);
    }

    @SuppressWarnings("unchecked")
    private void updateBrands() {
        brands = (List<Brand>) repository.getTable(Tables.BRANDS);
    }

    @SuppressWarnings("unchecked")
    private void updateColours() {
        colours = (List<Colour>) repository.getTable(Tables.COLOURS);
    }

    @SuppressWarnings("unchecked")
    private void updateSizes() {
        sizes = (List<Size>) repository.getTable(Tables.SIZES);
    }

    private String getModelName(int modelId) {
        return models.stream().filter(m -> modelId == m.id()).findFirst().get().name();
    }

    private String getBrandName(int brandId) {
        return brands.stream().filter(b -> brandId == b.id()).findFirst().get().name();
    }

    private String getColorName(int colourId) {
        return colours.stream().filter(c -> colourId == c.id()).findFirst().get().name();
    }

    private String getSizeNumber(int sizeId) {
        return sizes.stream().filter(s -> sizeId == s.id()).findFirst().get().size();
    }

    private String getFlexSpace(String[] stringArray) {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(" ".repeat(5));
        Arrays.stream(stringArray).forEach(s -> sBuilder.append(s).append(" ".repeat(25 - s.length())));
        return sBuilder.toString();
    }

    private String getSpace(String[] stringsList) {
        return String.join(" ".repeat(10), stringsList);
    }
}