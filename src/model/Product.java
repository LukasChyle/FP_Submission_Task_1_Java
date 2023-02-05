package model;

public class Product {

    private final int id;
    private final String articleNumber;
    private final int modelId;
    private final double price;
    private final int balance;
    private final int brandId;
    private final int colourId;
    private final int sizeId;

    public Product(int id, String articleNumber, int modelId, double price, int balance, int brandId, int colourId, int sizeId) {
        this.articleNumber = articleNumber;
        this.id = id;
        this.modelId = modelId;
        this.price = price;
        this.balance = balance;
        this.brandId = brandId;
        this.colourId = colourId;
        this.sizeId = sizeId;
    }

    public int getId() {
        return id;
    }

    public String getArticleNumber() {
        return articleNumber;
    }

    public int getModelId() {
        return modelId;
    }

    public double getPrice() {
        return price;
    }

    public int getBalance() {
        return balance;
    }

    public int getBrandId() {
        return brandId;
    }

    public int getColourId() {
        return colourId;
    }

    public int getSizeId() {
        return sizeId;
    }
}
