package org.example;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class CategoryServiceImpl extends CategoryServiceGrpc.CategoryServiceImplBase {

    @Override
    public void getCategory(CategoryIdentifier request, StreamObserver<Category> responseObserver) {
        // Result in UNKNOWN error without description & cause
        throw new IllegalArgumentException("exception 1");
    }

    @Override
    public void getAllCategories(Blank request, StreamObserver<Category> responseObserver) {
        // Result in UNKNOWN error without description & cause
        responseObserver.onError(new IllegalArgumentException("exception 2"));
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<CategoryIdentifier> getCategories(StreamObserver<Category> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(CategoryIdentifier value) {
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onCompleted() {
                // Cause hasn't been passed to client
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("Couldn't find any categories")
                        .withCause(new RuntimeException("exception 3"))
                        .asRuntimeException());
            }
        };
    }
}
