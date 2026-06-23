# Youtube Video——Spring Cloud Eureka | Java Techie Summary

## **1. What Is Eureka?**

Eureka is a **service registry** originally provided by Netflix. In Spring Cloud, Eureka is integrated with the Spring framework and is commonly used in microservice architecture.

The main purpose of Eureka is to help microservices **register themselves** and allow other services to **discover them dynamically**. Instead of hardcoding the exact host and port of every service, each microservice registers with the Eureka Server, and other services can find it through its service name.

---

## **2. Why Do We Need Eureka?**

In microservice architecture, we usually split one large application into multiple independent services.

For example:

- Product Service
- Order Service
- Payment Service
- Shopping Service

Each service may run on a different **host and port.**

For example:

- Product Service: `localhost:8081`
- Order Service: `localhost:8082`
- Payment Service: `localhost:8848`

Without Eureka, if one service wants to call another service, it must know the exact URL, host, port, and endpoint path. This creates tight coupling between services.

For example, the client must know:

- Which host the service is running on
- Which port the service uses
- What endpoint path should be called

This is inconvenient and hard to maintain, especially when there are many services or multiple instances of the same service.

---

## **3. Traditional Service-to-Service Communication Problem**

In the traditional approach, if Shopping Service wants to call Payment Service, it may need to call a fixed URL like:

```java
http://localhost:8848/payment-provider/payNow/4000
```

This approach has several problems:

- The client must remember the exact host and port.
- The client is tightly coupled with the provider service.
- If the provider service changes its port or moves to another server, the client code or configuration must change.
- It becomes harder to manage when there are many microservices.

So the problem is not just calling another service. The real problem is that service locations are dynamic in a microservice system.

---

## **4. Eureka’s Solution**

Eureka solves this problem by acting as a **central service registry**.

Instead of calling a service by its exact host and port, each microservice registers itself with Eureka Server.

Then, when another service wants to call it, it can use the **service name** instead of the physical host and port.

For example, instead of using:

```java
http://localhost:8848/payment-provider/payNow/4000
```

The client can use:

```java
http://PAYMENT-SERVICE/payment-provider/payNow/4000
```

Here, `PAYMENT-SERVICE` is the service name registered in Eureka.

The client does not need to know where Payment Service is actually running. Eureka helps resolve the service name to the actual host and port.

---

## **5. Main Components in the Demo**

The tutorial builds three Spring Boot applications:

### **Eureka Server**

This is the service registry. Its job is to allow other microservices to register themselves. It does not contain business logic.

In the video, the Eureka Server is named something like:

```java
amazon-server
```

It runs on the default Eureka port:

```java
8761
```

---

### **Payment Service**

This is the provider service.

It exposes a simple API, such as:

```java
/payment-provider/payNow/{price}
```

The method returns a dummy response like:

```java
Payment with 4000 is successful
```

This service registers itself with Eureka Server using the service name:

```java
PAYMENT-SERVICE
```

---

### **Shopping Service**

This is the client service.

It receives a request from the user, then internally calls Payment Service.

Instead of directly calling Payment Service with host and port, it calls Payment Service through the service name registered in Eureka.

It uses `RestTemplate` to invoke the Payment Service.

---

## **6. How to Create Eureka Server**

To create a Eureka Server, the tutorial creates a new Spring Boot project and adds these dependencies:

- Spring Web
- Eureka Server
- DevTools

Then the main application class is annotated with:

```java
@EnableEurekaServer
```

This annotation tells Spring Boot that this application should behave as a Eureka Server.

The Eureka Server also needs configuration in `application.yml`.

The important configuration includes:

```yaml
server:
  port: 8761

spring:
  application:
    name: amazon-server

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
```

The key point is:

```yaml
register-with-eureka: false
```

This means the Eureka Server itself should not register as a normal client service.

After starting the application, the Eureka dashboard can be opened at:

```java
http://localhost:8761
```

At first, there are no registered instances. After microservices register themselves, they will appear in the Eureka dashboard.

---

## **7. How to Create Payment Service**

Payment Service is a normal Spring Boot microservice.

It needs these dependencies:

- Spring Web
- Eureka Discovery Client
- DevTools

The main application class is annotated with:

```java
@EnableEurekaClient
```

This annotation tells Spring Boot that this service should register itself with Eureka.

The service has a controller like:

```java
@RestController
@RequestMapping("/payment-provider")
public class PaymentController {

    @GetMapping("/payNow/{price}")
    public String payNow(@PathVariable int price) {
        return "Payment with " + price + " is successful";
    }
}
```

The `application.yml` file contains the Eureka client configuration:

```yaml
server:
  port: 8848

spring:
  application:
    name: PAYMENT-SERVICE

eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

The key ideas are:

```yaml
spring.application.name: PAYMENT-SERVICE
```

This is the service name registered in Eureka.

```yaml
defaultZone: http://localhost:8761/eureka/
```

This tells Payment Service where the Eureka Server is.

After Payment Service starts, it registers itself with Eureka Server. Then the Eureka dashboard will show `PAYMENT-SERVICE` as a registered instance.

---

## **8. How to Create Shopping Service**

Shopping Service is the client service.

It also needs these dependencies:

- Spring Web
- Eureka Discovery Client
- DevTools

The main application class is annotated with:

```java
@EnableEurekaClient
```

Shopping Service also registers itself with Eureka.

Its `application.yml` may look like this:

```yaml
server:
  port: 9999

spring:
  application:
    name: SHOPPING-SERVICE

eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

After it starts, the Eureka dashboard should show both:

```java
PAYMENT-SERVICE
SHOPPING-SERVICE
```

---

## **9. Using RestTemplate with Eureka**

Shopping Service uses `RestTemplate` to call Payment Service.

First, `RestTemplate` is registered as a bean:

```java
@Bean
@LoadBalanced
public RestTemplate restTemplate() {
    return new RestTemplate();
}
```

The important annotation is:

```java
@LoadBalanced
```

This allows `RestTemplate` to call a service by its Eureka service name instead of a fixed host and port.

Without `@LoadBalanced`, `RestTemplate` would not understand a URL like:

```java
http://PAYMENT-SERVICE/payment-provider/payNow/4000
```

With `@LoadBalanced`, Spring can resolve `PAYMENT-SERVICE` through Eureka.

---

## **10. Shopping Service Calling Payment Service**

Shopping Service exposes its own endpoint, such as:

```java
/amazon-payment/{price}
```

Inside this endpoint, it calls Payment Service.

Example logic:

```java
@RestController
public class ShoppingController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/amazon-payment/{price}")
    public String invokePaymentService(@PathVariable int price) {
        String url = "http://PAYMENT-SERVICE/payment-provider/payNow/" + price;
        return restTemplate.getForObject(url, String.class);
    }
}
```

Here, Shopping Service does not call Payment Service by using:

```java
http://localhost:8848/payment-provider/payNow/4000
```

Instead, it calls:

```java
http://PAYMENT-SERVICE/payment-provider/payNow/4000
```

`PAYMENT-SERVICE` is the name registered in Eureka.

---

## **11. Final Request Flow**

The final flow is:

### **Step 1**

The user calls Shopping Service:

```java
http://localhost:9999/amazon-payment/4000
```

### **Step 2**

Shopping Service receives the request.

### **Step 3**

Shopping Service uses `RestTemplate` to call Payment Service by service name:

```java
http://PAYMENT-SERVICE/payment-provider/payNow/4000
```

### **Step 4**

Eureka helps resolve `PAYMENT-SERVICE` to the actual host and port.

### **Step 5**

The request is forwarded to Payment Service.

### **Step 6**

Payment Service returns:

```java
Payment with 4000 is successful
```

### **Step 7**

Shopping Service returns that response back to the user.

---

## **12. What Eureka Actually Helps With**

Eureka helps remove hardcoded host and port information from service-to-service communication.

Without Eureka, the client must know:

```java
localhost:8848
```

With Eureka, the client only needs to know:

```java
PAYMENT-SERVICE
```

This makes the system more flexible.

If Payment Service moves to another port or another machine, the client does not need to change its calling logic. Payment Service only needs to register its new location with Eureka.

---

## **13. Important Annotations**

### **`@EnableEurekaServer`**

Used in the Eureka Server application.

It tells Spring Boot:

```java
This application is a Eureka Server.
```

---

### **`@EnableEurekaClient`**

Used in microservices such as Payment Service and Shopping Service.

It tells Spring Boot:

```java
This application should register with Eureka Server.
```

---

### **`@LoadBalanced`**

Used on `RestTemplate`.

It tells Spring Boot:

```java
This RestTemplate can call services by service name through Eureka.
```

Without this annotation, `RestTemplate` cannot resolve service names like `PAYMENT-SERVICE`.

---

## **14. Important Configuration**

### **Eureka Server Configuration**

```yaml
server:
  port: 8761

spring:
  application:
    name: amazon-server

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
```

This configuration means:

- The Eureka Server runs on port `8761`.
- Its application name is `amazon-server`.
- It does not register itself as a client.
- It does not fetch the service registry from another Eureka Server.

---

### **Payment Service Configuration**

```yaml
server:
  port: 8848

spring:
  application:
    name: PAYMENT-SERVICE

eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

This configuration means:

- Payment Service runs on port `8848`.
- Its registered service name is `PAYMENT-SERVICE`.
- It registers itself with Eureka Server.
- It knows the Eureka Server is at `localhost:8761`.

---

### **Shopping Service Configuration**

```yaml
server:
  port: 9999

spring:
  application:
    name: SHOPPING-SERVICE

eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

This configuration means:

- Shopping Service runs on port `9999`.
- Its registered service name is `SHOPPING-SERVICE`.
- It registers itself with Eureka Server.
- It can discover other services through Eureka.

---

## **15. Key Takeaway**

Spring Cloud Eureka is used for **service registration and service discovery** in microservice architecture.

In a microservice system, services should not depend on hardcoded host and port values. Instead, each service registers itself with Eureka Server, and other services call it by service name.

In this tutorial, the final architecture is:

```java
Eureka Server
Payment Service
Shopping Service
```

Payment Service registers itself as:

```java
PAYMENT-SERVICE
```

Shopping Service calls Payment Service through:

```java
http://PAYMENT-SERVICE/payment-provider/payNow/{price}
```

Eureka resolves the service name to the real host and port.

So the main purpose of Eureka is to make service-to-service communication more flexible, dynamic, and easier to manage in a microservice architecture.