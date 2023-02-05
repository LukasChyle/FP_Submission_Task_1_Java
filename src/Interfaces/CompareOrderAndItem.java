package Interfaces;

import model.Order;
import model.OrderItem;

@FunctionalInterface
public interface CompareOrderAndItem {
    boolean compare(Order o, OrderItem i);
}