# Homework 8

### **TCP 3-Way Handshake**

TCP uses a three-step process to establish a reliable connection between a client and a server. First, the client sends a SYN packet to the server, indicating that it wants to start a connection. Second, the server responds with a SYN-ACK packet, acknowledging the client’s request and indicating that it is ready to communicate. Finally, the client sends an ACK packet back to the server. After these three steps are completed, the connection is established and data transmission can begin.

```
SYN      =  Synchronize
             "I want to start a connection"
             client sends this FIRST

SYN-ACK  =  Synchronize + Acknowledge
             "I received your request AND I am ready"
             server sends this BACK

ACK      =  Acknowledge
             "I received your confirmation, let us begin!"
             client sends this LAST
```

---

### **TCP vs UDP**

TCP and UDP are both transport layer protocols, but they are designed for different purposes. 

TCP is connection-oriented, meaning it establishes a connection before transmitting data. 

It guarantees reliable delivery, packet ordering, and error recovery, making it suitable for applications such as HTTP, HTTPS, email, and file transfers.

UDP is connectionless and does not guarantee delivery or packet order. 

Because it has less overhead, it is generally faster than TCP. UDP is commonly used for online gaming, video streaming, voice calls, and DNS queries, where speed is often more important than perfect reliability.

**They are both at the transport layer. HTTP and HTTPS is at the application layer.**

---

### **What is Tomcat?**

Tomcat is a Java web server and servlet container developed by the Apache Software Foundation. 

It is responsible for receiving HTTP requests from clients, passing those requests to Java applications such as Spring Boot applications, and returning HTTP responses back to users.

In simple terms, Tomcat acts as the runtime environment that allows Java web applications to run and communicate with users over the internet.

---

### **What are the Basic Components of Tomcat?**

Tomcat consists of several core components that work together to process web requests. 

The **Server** is the top-level component that manages the entire Tomcat instance. 

Inside the server, a **Service** groups together one or more connectors and an engine.

A **Connector** listens for incoming requests on a specific port, such as port 8080. 

The **Engine** processes requests and determines which application should handle them. 

A **Host** represents a virtual host, such as localhost, while a **Context** represents an individual web application deployed on Tomcat.

Together, these components allow Tomcat to receive requests, locate the correct application, and generate responses.

---

### **What is a Web Server?**

A web server is software that accepts HTTP requests from clients and returns responses. The response may contain web pages, JSON data, images, files, or other resources requested by the user.

Examples of web servers include Apache HTTP Server, Nginx, and Tomcat. In modern web applications, the web server acts as the bridge between users and backend services.

---

### **What is 3-Tier Architecture?**

Three-tier architecture is a common software design pattern that separates an application into three layers: the Presentation Layer, the Business Layer, and the Data Layer.

The Presentation Layer is responsible for interacting with users through web pages or APIs. 

The Business Layer contains the application’s core logic, validation rules, and business operations. 

The Data Layer communicates with databases and handles data storage and retrieval.

This separation improves maintainability, scalability, and code organization because each layer has a clear responsibility and can evolve independently.

---

### **What is the OSI Model?**

The OSI Model is a conceptual framework that describes how data travels across a network. It divides network communication into seven layers, with each layer performing a specific responsibility.

The **Application Layer** interacts directly with user applications and protocols such as HTTP and FTP. 

The **Presentation Layer** handles data formatting, encryption, and compression. 

The **Session Layer** manages communication sessions between systems.

The **Transport Layer** is responsible for end-to-end communication and includes protocols such as TCP and UDP. 

The **Network Layer** handles routing and logical addressing through IP addresses. 

The **Data Link Layer** manages communication within the same network using MAC addresses and frame transmission. 

Finally, the **Physical Layer** is responsible for transmitting raw bits through physical media such as cables, fiber optics, or wireless signals.

For interviews, the most commonly discussed layers are the Application Layer (HTTP), Transport Layer (TCP/UDP), Network Layer (IP Address), and Data Link Layer (MAC Address), since these are directly related to web application development and network communication.