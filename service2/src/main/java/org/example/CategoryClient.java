package org.example;

import io.grpc.Channel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;

public class CategoryClient {

    private final Channel channel;

    public CategoryClient(Channel channel) {
        this.channel = channel;
    }

    public void executeWithBlockingStub() {
        System.out.println("1. Execute blocking stub");
        var blockingStub = CategoryServiceGrpc.newBlockingStub(channel);
        List<Category> categories = new ArrayList<>();
        try {
            blockingStub.getAllCategories(Blank.newBuilder().build()).forEachRemaining(categories::add);
        } catch (StatusRuntimeException e) {
            System.out.println(e.getStatus());
        }
    }

    public void executeWithStub() throws InterruptedException {
        System.out.println("2. Execute stub");
        var stub = CategoryServiceGrpc.newStub(channel);
        stub.getCategory(CategoryIdentifier.newBuilder().setId("customer1").build(), new StreamObserver<>() {

            @Override
            public void onNext(Category value) {
            }

            @Override
            public void onError(Throwable t) {
                var status = Status.fromThrowable(t);
                System.out.println(status);
            }

            @Override
            public void onCompleted() {
            }
        });

        stub.getAllCategories(Blank.newBuilder().build(), new StreamObserver<>() {
            @Override
            public void onNext(Category value) {
            }

            @Override
            public void onError(Throwable t) {
                var status = Status.fromThrowable(t);
                System.out.println(status);
            }

            @Override
            public void onCompleted() {
            }
        });

        var requestObserver = stub.getCategories(new StreamObserver<>() {
            @Override
            public void onNext(Category value) {
            }

            @Override
            public void onError(Throwable t) {
                var status = Status.fromThrowable(t);
                System.out.println(status);
            }

            @Override
            public void onCompleted() {

            }
        });
        requestObserver.onCompleted();

        Thread.sleep(5000);
    }
}
