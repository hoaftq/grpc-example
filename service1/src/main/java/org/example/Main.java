package org.example;

import io.grpc.ServerBuilder;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        var server = ServerBuilder.forPort(4000)
                .addService(new ProductServiceImpl())
                .build();
        server.start();
        server.awaitTermination();
    }
}