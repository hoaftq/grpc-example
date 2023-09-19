package org.example;

public final class ProductProvider {
    public final static Product.ProductDetails[] PRODUCTS = {
            Product.ProductDetails.newBuilder()
                    .setId(1)
                    .setName("Product 1")
                    .setPrice(12.1f)
                    .setStockQuantity(100)
                    .build(),
            Product.ProductDetails.newBuilder()
                    .setId(2)
                    .setName("Product 2")
                    .setPrice(30)
                    .setStockQuantity(5)
                    .build(),
            Product.ProductDetails.newBuilder()
                    .setId(3)
                    .setName("Product 3")
                    .setPrice(100)
                    .setStockQuantity(10)
                    .build(),
            Product.ProductDetails.newBuilder()
                    .setId(4)
                    .setName("Product 4")
                    .setPrice(1.2f)
                    .setStockQuantity(9)
                    .build()
    };
}
