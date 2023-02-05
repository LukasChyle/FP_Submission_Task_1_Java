package model;

public class Order {

    private int id;
    private final int customerId;
    private int orderNumber;
    private final String adress;
    private final String zipCode;
    private final String locality;
    private String created;

    public Order(int id, int customerId, int orderNumber, String adress,
                 String zipCode, String locality, String created) {
        this.id = id;
        this.customerId = customerId;
        this.orderNumber = orderNumber;
        this.adress = adress;
        this.zipCode = zipCode;
        this.locality = locality;
        this.created = created;
    }

    public Order(int customerId, String adress, String zipCode, String locality) {
        this.customerId = customerId;
        this.adress = adress;
        this.zipCode = zipCode;
        this.locality = locality;
    }

    public int getId() {
        return id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public String getAdress() {
        return adress;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getLocality() {
        return locality;
    }

    public String getCreated() {
        return created;
    }
}