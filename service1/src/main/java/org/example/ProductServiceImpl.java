package org.example;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductServiceImpl extends ProductServiceGrpc.ProductServiceImplBase {
    @Override
    public void getProduct(Product.ProductIdentifier request, StreamObserver<Product.ProductDetails> responseObserver) {
        var foundProduct = Arrays.stream(ProductProvider.PRODUCTS)
                .filter(p -> p.getId() == request.getId())
                .findFirst();
        foundProduct.ifPresent(responseObserver::onNext);
        responseObserver.onCompleted();
    }

    @Override
    public void getAllProducts(Empty request, StreamObserver<Product.ProductDetails> responseObserver) {
        for (var product : ProductProvider.PRODUCTS) {
            responseObserver.onNext(product);
        }

        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<Product.ProductIdentifier> getProducts(StreamObserver<Product.ProductDetails> responseObserver) {
        return new StreamObserver<>() {
            final List<Long> productIds = new ArrayList<>();

            @Override
            public void onNext(Product.ProductIdentifier value) {
                productIds.add(value.getId());
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onCompleted() {
                Arrays.stream(ProductProvider.PRODUCTS).filter(p -> productIds.contains(p.getId()))
                        .forEach(responseObserver::onNext);
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<Product.ProductRequest> checkProductsAvailable(StreamObserver<Product.ProductRequestResult> responseObserver) {

        return new StreamObserver<>() {
            final List<Product.ProductRequest> productRequests = new ArrayList<>();

            @Override
            public void onNext(Product.ProductRequest value) {
                productRequests.add(value);
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onCompleted() {
                var isAvailable = productRequests.stream().allMatch(pr -> Arrays.stream(ProductProvider.PRODUCTS)
                        .anyMatch(p -> p.getId() == pr.getId() && p.getStockQuantity() >= pr.getQuantity()));
                responseObserver.onNext(Product.ProductRequestResult.newBuilder().setAvailable(isAvailable).build());
                responseObserver.onCompleted();
            }
        };


    }

    @Override
    public void getProductV2(Product.ProductIdentifier request, StreamObserver<Product.ProductDetails> responseObserver) {
        // New version of the API
        super.getProductV2(request, responseObserver);
    }
}
