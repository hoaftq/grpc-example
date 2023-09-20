Run flowing commands in git bash (openssl comes with git)

1. Generate a self-signed CA
    openssl req -x509 -new -newkey rsa:2048 -nodes -keyout ca.key -out ca.pem -config ca-openssl.cnf -days 3650 -extensions v3_req  
    *(Leave every field as defaul)*

2. Generate service1 cerficate

    - Generate a RSA private key  
    openssl genrsa -out service1.key.rsa 2048

    - Convert the key to PKCS8 format  
    openssl pkcs8 -topk8 -in service1.key.rsa -out service1.key -nocrypt

    - Generate a certificate signing request  
    openssl req -new -key service1.key -out service1.csr  
    *(Enter common name with 'localhost' if it will be running on localhost)*

    - Sign the cefificate signing request with the self-signed CA to generate a signed certificate
    openssl x509 -req -CA ca.pem -CAkey ca.key -CAcreateserial -in service1.csr -out service1.pem -days 3650

3. Generate service2 certificate

    - openssl genrsa -out service2.key.rsa 2048
    - openssl pkcs8 -topk8 -in service2.key.rsa -out service2.key -nocrypt
    - openssl req -new -key service2.key -out service2.csr  
    *(Enter common name with 'localhost' if it will be running on localhost)*
    - openssl x509 -req -CA ca.pem -CAkey ca.key -CAcreateserial -in service2.csr -out service2.pem -days 3650