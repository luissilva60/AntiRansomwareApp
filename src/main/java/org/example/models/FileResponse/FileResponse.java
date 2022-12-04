package org.example.models.FileResponse;

public class FileResponse {
    private final int product_id;
    private final String product_name;
    private final String product_description;
    private final Double product_stock;
    private final String product_photo;
    private final int product_store_id;

    public FileResponse(int product_id, String product_name, String product_description, Double product_stock, String product_photo, int product_store_id) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.product_description = product_description;
        this.product_stock = product_stock;
        this.product_photo = product_photo;
        this.product_store_id = product_store_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getProduct_description() {
        return product_description;
    }

    public Double getProduct_stock() {
        return product_stock;
    }

    public String getProduct_photo() {
        return product_photo;
    }

    public int getProduct_store_id() {
        return product_store_id;
    }
}

