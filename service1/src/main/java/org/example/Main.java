package org.example;

import io.grpc.Grpc;
import io.grpc.TlsServerCredentials;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        // Path to file service1.pem
        var certChainFilePath = new File(args[0]);

        // Path to file service1.key
        var privateKeyFilePath = new File(args[1]);

        // Path to file ca.pem
        var trustCertFilePath = new File(args[2]);

        var credentials = TlsServerCredentials.newBuilder()
                .keyManager(certChainFilePath, privateKeyFilePath)
                .trustManager(trustCertFilePath) // to use mutual SSL
                .build();

        var server = Grpc.newServerBuilderForPort(4000, credentials)
                .addService(new ProductServiceImpl())
                .addService(new CategoryServiceImpl())
                .build();
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                server.shutdown().awaitTermination(500, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }));

        server.awaitTermination();
    }
}