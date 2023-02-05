package Interfaces;

import model.Customer;
import model.Order;

@FunctionalInterface
public interface CompareCustomerAndOrder {
    boolean compare(Customer c, Order o);
}