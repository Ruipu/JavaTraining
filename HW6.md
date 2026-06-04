# HW6

# **Client - Server Model**

The client-server model is a software architecture. 

A client sends requests and a server processes those requests and returns responses. 

The client is usually a web browser, mobile application, or desktop application. 

The server contains the business logic and data storage. 

This model allows users to access services without needing the application code installed on their own devices.

# **Application Service**

An application service is a software component that performs a specific business function within an application. 

For example, a Product Service may manage product information. A Payment Service may process payments. In a microservice architecture, each application service is deployed and maintained independently. 

These services communicate with each other through APIs, HTTP requests, or messaging systems such as message queues. This allows different services to work together while remaining loosely coupled and independently scalable.

# **HTTP Request / Response**

HTTP is a communication protocol used between clients and servers. An HTTP request is sent by the client to ask the server to perform an action, such as retrieving data or creating a new record. The server processes the request and returns an HTTP response, which contains a status code, response headers, and the requested data or result.

# **Horizontal Scaling vs Vertical Scaling**

Vertical scaling means increasing the resources of a single server, such as adding more CPU, memory (RAM), or storage. For example, a server with 4 CPU cores and 16GB RAM may be upgraded to 16 CPU cores and 64GB RAM. Storage can also be expanded by adding larger SSDs or hard drives to store more data.

Horizontal scaling means adding more servers or application instances and distributing the workload among them. An instance is a running copy of an application. For example, instead of running only one Product Service instance, a company may run multiple identical Product Service instances on different servers and use a load balancer to distribute incoming requests among them.

Vertical scaling is simpler because the application architecture remains unchanged. However, it has physical hardware limitations. A server can only support a limited number of CPU sockets, memory slots, and storage devices. Once those limits are reached, further upgrades become difficult or impossible.

Horizontal scaling provides better scalability and availability for large systems because additional instances can be added as traffic grows. However, it introduces extra complexity. Different instances may need to communicate through the network, synchronize data, and maintain consistency. Since network communication is inherently less reliable than communication within a single server, developers must handle issues such as latency, packet loss, network failures, and service interruptions. As a result, horizontal scaling often requires additional components such as load balancers, service discovery, and monitoring systems.

# **Load Balancer**

A load balancer is a component that distributes incoming requests across multiple servers or service instances. 

Its goal is to prevent any single server from becoming overloaded and to ensure that traffic is handled efficiently. Load balancing improves application performance, reliability, and availability.

# Microservice

A microservice architecture is a software design approach that splits a large application into multiple small, independent services. It is different from a monolithic architecture, where all business functions are packaged and deployed as a single application.

Each service is responsible for a specific business function, such as product management, payment processing, or shipment tracking.

These services can be developed, deployed, and scaled independently while communicating with each other through APIs, HTTP requests, or messaging systems such as message queues.

Compared with a monolithic architecture, microservices provide better scalability, fault tolerance, and deployment flexibility. However, they also introduce additional complexity because services must communicate and coordinate with each other over the network.

# Microfrontend

Microfrontend is the frontend equivalent of microservices. Just like our microservice in backend.

Instead of building the entire user interface as a single frontend application, the UI is divided into smaller independent frontend modules. 

Different teams can develop, deploy, and maintain different parts of the user interface separately while still providing a unified user experience. These frontend modules typically communicate with backend services through HTTP requests and REST APIs. In some cases, different frontend modules may also share data or communicate through browser events and shared application states.

**UI (User Interface)** is the part of an application that users directly interact with, such as web pages, buttons, forms, menus, and other visual elements displayed on the screen.

# Relational Database (SQL Database)

A relational database stores data in tables consisting of rows and columns. 

Relationships between tables are defined using keys and constraints. 

SQL is one example. SQL (Structured Query Language) is used to query and manipulate the data. 

Relational databases provide strong consistency and are commonly used for transactional systems.

Examples:

- MySQL
- PostgreSQL
- Oracle
- SQL Server

# Non-Relational Database (NoSQL Database)

A NoSQL database stores data in formats other than traditional tables. 

It stores data like, such as documents, key-value pairs, graphs, or column families. It is less formatted and messy. 

NoSQL databases are designed for scalability, flexibility, and handling large volumes of unstructured or semi-structured data.

Examples:

- MongoDB
- Cassandra
- Redis
- DynamoDB

# API Gateway

An API Gateway is the entry point of a microservice system. 

It receives requests from clients, routes them to the appropriate services, and returns responses.

It can also handle authentication, authorization, rate limiting, logging, and load balancing.

Using an API Gateway simplifies client communication because clients only need to interact with one endpoint instead of multiple services.

# Message Queue

A Message Queue is a communication mechanism that allows services to exchange information asynchronously. 

We do not have to directly calling another service and waiting for a response.

In message queue a service can send a message to a queue. Another service can process the message later, for example, after 3 mins. 

Message queues improve system reliability, scalability, and fault tolerance by reducing direct dependencies between services.

Examples:

- RabbitMQ
- Kafka
- AWS SQS
- Azure Service Bus

# Logging and Monitoring

## Logging

Logging is the process of recording application events, errors, warnings, and system activities. Logs help developers troubleshoot problems, understand application behavior, and investigate failures.

Common information recorded in logs includes:

- User actions
- API requests
- Error messages
- System events

## Monitoring

Monitoring is the process of continuously observing system performance and health. 

Monitoring tools collect metrics such as CPU usage, memory consumption, response time, and error rates.

Monitoring helps teams detect issues early and maintain system availability.

Common monitoring tools:

- Prometheus
- Grafana
- Datadog
- CloudWatch

# Deployment with AWS / Azure / GCP

Cloud deployment means hosting applications on cloud platforms rather than maintaining physical servers.

Major cloud providers include:

- AWS (Amazon Web Services)
- Microsoft Azure
- Google Cloud Platform (GCP)

Cloud platforms provide services such as:

- Virtual machines
- Databases
- Storage
- Networking
- Monitoring
- Security

Cloud deployment allows applications to scale easily, improve availability, and reduce infrastructure management effort.

# Security (Authentication and Authorization)

## Authentication

Authentication verifies who a user is.

Examples:

- Username and password
- Multi-factor authentication (MFA)
- OAuth login
- Single Sign-On (SSO)

Authentication answers the question:

"Who are you?"

## Authorization

Authorization determines what an authenticated user is allowed to do.

Examples:

- Admin can delete records.
- Customer can view their own orders.
- Guest can only browse products.

Authorization answers the question:

"What are you allowed to do?"

# Why Testing

Testing helps ensure that software works correctly and meets business requirements before being released to users.

Benefits of testing include:

- Detecting bugs early
- Improving software quality
- Reducing production failures
- Increasing confidence during deployments
- Ensuring new changes do not break existing functionality

Common types of testing include:

- Unit Testing
- Integration Testing
- System Testing
- End-to-End Testing
- Performance Testing

Testing is important because fixing bugs during development is much cheaper and easier than fixing them after deployment.

## Video is here! [Video](https://simon-java-training2026.s3.us-east-2.amazonaws.com/HW6.MP4)
