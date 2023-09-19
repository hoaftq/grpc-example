package org.example;

import com.google.protobuf.Empty;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        var channel = ManagedChannelBuilder.forAddress("localhost", 4000)
                .usePlaintext()
                .build();
        useBlockingStub(channel);
        useStub(channel);
        useFutureStub(channel);
    }

    private static void useBlockingStub(Channel channel) {
        System.out.println("1. Using blocking stub");
        var blockingStub = ProductServiceGrpc.newBlockingStub(channel);

        System.out.println("Get one product");
        var product = blockingStub.getProduct(Product.ProductIdentifier.newBuilder().setId(1).build());
        System.out.println("Got product\n" + product);

        System.out.println("Get all products");
        List<Product.ProductDetails> products = new ArrayList<>();
        blockingStub.getAllProducts(Empty.newBuilder().build()).forEachRemaining(products::add);
        System.out.printf("Got %d products\n", products.size());
        products.forEach(System.out::println);
    }

    private static void useStub(Channel channel) throws InterruptedException {
        System.out.println("2. Using stub");
        var stub = ProductServiceGrpc.newStub(channel);

        System.out.println("Get one product");
        stub.getProduct(Product.ProductIdentifier.newBuilder().setId(1).build(), new StreamObserver<>() {
            Product.ProductDetails product;

            @Override
            public void onNext(Product.ProductDetails value) {
                product = value;
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onCompleted() {
                System.out.println("Got product\n" + product);
            }
        });

        System.out.println("Get all products");
        stub.getAllProducts(Empty.newBuilder().build(), new StreamObserver<>() {
            final List<Product.ProductDetails> products = new ArrayList<>();

            @Override
            public void onNext(Product.ProductDetails value) {
                products.add(value);
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onCompleted() {
                System.out.printf("Got %d products\n", products.size());
                products.forEach(System.out::println);
            }
        });

        System.out.println("Get products by ids");
        var getProductsRequestObserver = stub.getProducts(new StreamObserver<>() {
            final List<Product.ProductDetails> products = new ArrayList<>();

            @Override
            public void onNext(Product.ProductDetails value) {
                products.add(value);
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onCompleted() {
                System.out.printf("Got %d products\n", products.size());
                products.forEach(System.out::println);
            }
        });
        getProductsRequestObserver.onNext(Product.ProductIdentifier.newBuilder().setId(3).build());
        getProductsRequestObserver.onNext(Product.ProductIdentifier.newBuilder().setId(4).build());
        getProductsRequestObserver.onCompleted();

        System.out.println("Check product availability");
        var checkProductsAvailableRequestObserver = stub.checkProductsAvailable(new StreamObserver<>() {
            boolean isAvailable;

            @Override
            public void onNext(Product.ProductRequestResult value) {
                isAvailable = value.getAvailable();
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onCompleted() {
                System.out.printf("Is available: %b\n", isAvailable);
            }
        });
        checkProductsAvailableRequestObserver.onNext(Product.ProductRequest.newBuilder().setId(1).setQuantity(10).build());
        checkProductsAvailableRequestObserver.onNext(Product.ProductRequest.newBuilder().setId(2).setQuantity(6).build());
        checkProductsAvailableRequestObserver.onCompleted();

        Thread.sleep(5000);
    }

    private static void useFutureStub(Channel channel) throws ExecutionException, InterruptedException {
        System.out.println("3. Using blocking stub");
        var futureStub = ProductServiceGrpc.newFutureStub(channel);

        System.out.println("Get one product");
        var foundProduct = futureStub.getProduct(Product.ProductIdentifier.newBuilder().setId(4).build());
        System.out.println(foundProduct.get());
    }
}