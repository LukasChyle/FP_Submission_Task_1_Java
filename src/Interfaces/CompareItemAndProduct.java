package Interfaces;

import model.OrderItem;
import model.Product;

@FunctionalInterface
public interface CompareItemAndProduct {
    boolean compare(OrderItem i, Product p);
}