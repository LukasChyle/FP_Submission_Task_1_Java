package model;

import enums.ListOf;
import enums.Tables;

import javax.swing.*;
import java.util.List;

public class ComboBoxSetup {

    private final Repository rep;

    public ComboBoxSetup(JComboBox<String> listOf, JComboBox<String> brands,
                         JComboBox<String> colours, JComboBox<String> sizes) {

        rep = new Repository();
        setListOfBox(listOf);
        setBrandsBox(brands);
        setColoursBox(colours);
        setSizesBox(sizes);
    }

    private void setListOfBox(JComboBox<String> cBox) {
        cBox.addItem(ListOf.PRODUCTS.getName());
        cBox.addItem(ListOf.ORDERS.getName());
        cBox.addItem(ListOf.ORDER_ITEMS.getName());
        cBox.addItem(ListOf.CUSTOMERS.getName());
        cBox.addItem(ListOf.CUSTOMER_BOUGHT.getName());
        cBox.addItem(ListOf.LOCALITIES.getName());
        cBox.addItem(ListOf.MODELS.getName());
    }

    @SuppressWarnings("unchecked")
    private void setBrandsBox(JComboBox<String> cBox) {
        final List<Brand> brands = (List<Brand>) rep.getTable(Tables.BRANDS);
        cBox.addItem("All");
        brands.forEach(b -> cBox.addItem(b.name()));
    }

    @SuppressWarnings("unchecked")
    private void setColoursBox(JComboBox<String> cBox) {
        final List<Colour> colours = (List<Colour>) rep.getTable(Tables.COLOURS);
        cBox.addItem("All");
        colours.forEach(c -> cBox.addItem(c.name()));
    }

    @SuppressWarnings("unchecked")
    private void setSizesBox(JComboBox<String> cBox) {
        final List<Size> sizes = (List<Size>) rep.getTable(Tables.SIZES);
        cBox.addItem("All");
        sizes.forEach(s -> cBox.addItem(String.valueOf(s.size())));
    }
}