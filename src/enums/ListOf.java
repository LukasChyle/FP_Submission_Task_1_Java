package enums;

public enum ListOf {
    PRODUCTS("Products"),
    ORDERS("Orders"),
    ORDER_ITEMS("Order items"),
    CUSTOMERS("Customers"),
    CUSTOMER_BOUGHT("Customers Bought"),
    LOCALITIES("Localities"),
    MODELS("Models");

    private final String name;

    ListOf(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}