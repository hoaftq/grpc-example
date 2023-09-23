This is a demonstration on using gRPC as a mechanism to communicate between services. There are 4 method types in this example: **simple**, **server streaming**, **client streaming**, **bidirectional streaming**.  
It also considers several aspects that we might encouter in a real solution, such as
- **Authentication with TSL**  
    Server:
    ```java
        var credentials = TlsServerCredentials.newBuilder()
                .keyManager(certChainFilePath, privateKeyFilePath)
                .trustManager(trustCertFilePath) // to use mutual SSL
                .build();

        var server = Grpc.newServerBuilderForPort(4000, credentials)
    ```
    Client:
    ```java
        var chanelCredentials = TlsChannelCredentials.newBuilder()
                .trustManager(trustedCertFilePath)
                .keyManager(certChainFilePath, privateKeyFilePath) // to use mutual SSL
                .build();

        var channel = Grpc.newChannelBuilderForAddress("localhost", 4000, chanelCredentials).build();
    ```
- **Error handling**  
    Server:
    ```java
        @Override
        public void getAllCategories(Blank request, StreamObserver<Category> responseObserver) {
            // Result in UNKNOWN error without description & cause
            responseObserver.onError(new IllegalArgumentException("exception 2"));
            responseObserver.onCompleted();
        }
    ```
    ```java
        @Override
        public void onCompleted() {
            // Cause hasn't been passed to client
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Couldn't find any categories")
                    .withCause(new RuntimeException("exception 3"))
                    .asRuntimeException());
        }
    ```

    Client:
    ```java
        @Override
        public void onError(Throwable t) {
            var status = Status.fromThrowable(t);
            System.out.println(status);
        }
    ```
- **Deadline or timeout**  
    Blocking stub:
    ```java
        var blockingStub = CategoryServiceGrpc.newBlockingStub(channel)
                .withDeadlineAfter(500, TimeUnit.MILLISECONDS);
    ```

    Asynchronous stub:
    ```java
        var stub = CategoryServiceGrpc.newStub(channel)
                .withDeadlineAfter(500, TimeUnit.MILLISECONDS);
    ```

Compare to REST:
- gRPC use binary message format (Protocol Buffers) so it's faster, especially when the message is large.
- REST only supports request/response but gRPC also supports streaming communication.
- gRPC helps to avoid the difficulty specifying the right VERBs in REST
- gRPC use HTTP/2, REST use HTTP/1.1
