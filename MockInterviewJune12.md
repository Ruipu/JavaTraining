# Spring Boot Mock Interview (June 12)

### Introduce what is Spring Framework

`Ioc and AOP`

`Spring Spring MVC and Spring Boot`

`Bean Factory is not commonly used, what is Application Context`

Spring Framework is a lightweight Java framework used to build enterprise applications. It provides many core features such as IoC (Inversion of Control), Dependency Injection, AOP, transaction management, and integration with different technologies.

In the early days of Spring Framework, applications were mainly configured using XML files. The Spring container managed objects through BeanFactory and ApplicationContext. BeanFactory is the basic IoC container, while ApplicationContext is a more advanced container and is more commonly used in modern applications. At that time, Spring development was not annotation-driven, and developers had to define most configurations in XML.

Later, Spring MVC introduced annotations such as @Controller, @RequestMapping, and @Autowired, which significantly reduced the amount of XML configuration and made development easier and more maintainable.

Today, Spring Boot is built on top of Spring Framework and follows an annotation-driven development style. Developers can use annotations such as @SpringBootApplication, @RestController, @Service, and @Repository to quickly build applications with minimal configuration. Spring Boot also provides auto-configuration, starter dependencies, and embedded servers, which greatly simplify application development.

In summary, Spring Framework provides the foundation and core features, Spring MVC introduced annotation-based web development, and Spring Boot further simplified development through convention over configuration and auto-configuration.

### Spring boot version you used

One of the biggest differences between Spring Boot 2 and Spring Boot 3 is the migration from javax*. (java extension) packages to jakarta.* packages.

In Spring Boot 2 (Java 8 and 11), Java EE APIs use the javax namespace. For example, annotations such as @PostConstruct, @PreDestroy, and JPA-related classes are imported from javax packages.

In Spring Boot 3 (At least Java 17), Spring migrated to Jakarta EE, so all these packages were changed from javax.* to jakarta.*. For example, javax.persistence.Entity becomes jakarta.persistence.Entity, and javax.servlet becomes jakarta.servlet.

Another important difference is the Java version requirement. Spring Boot 2 supports older Java versions such as Java 8 and Java 11, while Spring Boot 3 requires Java 17 or above.

Spring Boot 3 also provides better performance, improved observability, native image support through GraalVM, and better support for modern cloud-native applications.

In summary, the most important change for developers is the migration from javax to jakarta, and the requirement to use Java 17 or higher.

#### How do you define profile

`@Profile(DEV)` on the top of class. Only when the configuration is not DEV can use. 

A Spring Profile is a mechanism that allows an application to load different configurations for different environments, such as Development, QA, UAT, and Production.

The main purpose of Profiles is to separate environment-specific configurations. Different environments may require different database URLs, credentials, logging levels, cache settings, or third-party service endpoints. Without Profiles, developers would need to manually modify configuration files before deployment.

Spring Boot supports profile-specific configuration files, such as application-dev.properties, application-qa.properties, and application-prod.properties. When a profile is activated, Spring Boot loads both the default configuration file and the profile-specific configuration file. If the same property exists in both files, the profile-specific configuration overrides the default configuration.

Profiles can be activated in several ways. The most common approach is through application.properties or application.yml using spring.profiles.active. Profiles can also be activated through command-line arguments or environment variables, which is commonly used in CI/CD pipelines and cloud deployments.

For example, if the active profile is QA, Spring Boot may connect to a QA database, while the PROD profile connects to a production database without requiring any code changes.

In addition to configuration files, Spring provides the @Profile annotation to control bean creation. For example, @Profile(“DEV”) means the bean will only be created when the DEV profile is active. @Profile(”!DEV”) means the bean will be created in all environments except DEV.

In summary, Spring Profiles allow applications to manage environment-specific configurations, avoid manual changes during deployment, and provide a flexible way to control both configuration loading and bean creation.

### What discovery service impl you used before

`@SpringBootApplication @EnableEureka`

In one of my Spring Boot microservice projects, I used Eureka as the service discovery implementation. 

Eureka is a service registry provided by Spring Cloud Netflix. In a microservice architecture, each service registers itself with the Eureka Server when it starts up. Other services can then discover and communicate with it using the service name instead of hard-coded IP addresses and ports.

For example, if an Order Service needs to call a Payment Service, it can query Eureka to locate the available Payment Service instances. This improves scalability and makes service communication more flexible.

To enable service registration, we typically use the Eureka Client dependency and configure the service to register with the Eureka Server. In older versions, we could use annotations such as @EnableEurekaClient together with @SpringBootApplication. After registration, Eureka continuously monitors service availability and updates the registry automatically.

Overall, Eureka helps manage service discovery in a distributed microservice environment and reduces the dependency on fixed network addresses.

### What is AOP

AOP stands for Aspect-Oriented Programming. It is used to separate cross-cutting concerns from the main business logic, making the code cleaner and easier to maintain.

Common use cases include logging, exception handling, security, auditing, and transaction management. Instead of writing the same code repeatedly in multiple methods, we can centralize the logic in one place using AOP.

In Spring Boot, I have used two common styles.

The first style is @RestControllerAdvice, which is commonly used as a global exception handler on the **controller layer**. It allows us to handle exceptions from multiple controllers in one place and return consistent error responses.

The second style is using @Aspect with Pointcuts and Advices. A Pointcut defines WHERE the aspect should be applied, and an Advice defines WHEN the logic should be executed, such as @Before, @After, @AfterReturning, @AfterThrowing, or @Around. This approach is commonly used for logging, monitoring, and performance tracking.

Overall, AOP helps reduce duplicated code and keeps business logic separate from supporting concerns.

### How to write spring boot to call from frontend to backend and save data to database

To allow a frontend application to call a Spring Boot backend and save data into a database, I usually follow a three-tier architecture consisting of Controller Layer, Service Layer, and DAO Layer.

The process starts from the client side, such as a web browser or frontend application. The frontend sends an HTTP request, typically a POST request containing JSON data.

The request first reaches the Controller Layer. In the controller, I design RESTful APIs using annotations such as @RestController, @RequestMapping, @PostMapping, @GetMapping, @PutMapping, and @DeleteMapping. The controller receives the request body, validates the input data, converts it into DTO objects if necessary, and delegates the request to the Service Layer.

The Service Layer contains the business logic. I usually define a **Service Interface and a Service Implementation class**. This layer performs validations, business rules, calculations, transformations, and orchestrates calls to other services if needed.

Next, the Service Layer calls the DAO Layer, which is responsible for data persistence. In Spring Boot, the DAO Layer is usually implemented through Spring Data JPA repositories. The repository interacts with relational databases through ORM technologies such as Hibernate and generates SQL statements automatically. Depending on the project, it can also interact with NoSQL databases such as MongoDB or DynamoDB.

After the data is successfully saved, the DAO Layer returns the result to the Service Layer. The Service Layer processes the result and returns a response object, such as a DTO or ResponseEntity, back to the Controller Layer.

The Controller then sends the HTTP response to the embedded web server, such as Tomcat. Finally, Tomcat returns the response to the web browser or frontend application.

In summary, the data flow is:

Client (Web Browser)
→ Controller Layer (RESTful API Endpoint)
→ Service Layer (Business Logic)
→ DAO Layer (Database Access via ORM)
→ Database
→ DAO Layer
→ Service Layer
→ Controller Layer
→ Tomcat Web Server
→ Web Browser

### Describe Spring MVC

Spring MVC is a web framework built on top of the Spring Framework. 

It follows the Model-View-Controller design pattern, which separates an application into three components: Model, View, and Controller.

The Model represents the application’s data and business objects. The View is responsible for displaying data to users. The Controller handles incoming HTTP requests and coordinates the processing of those requests.

In Spring MVC, the core component is the DispatcherServlet, which acts as the Front Controller. All incoming requests first go through the DispatcherServlet.

For example, when a client sends a request such as:

GET /employee/1

or

PUT /employee/1

the request is first received by the web server, such as Tomcat, and then forwarded to the DispatcherServlet.

The DispatcherServlet uses Handler Mapping to locate the appropriate Controller method. The Controller receives the request and delegates the processing to the Service Layer, where the business logic is executed.

The Service Layer may call the Repository or DAO Layer to retrieve or update data in a database. After processing is completed, the result is returned to the Controller.

The Controller then places the data into a Model object and returns either a View name for rendering or a ResponseEntity for REST APIs. Finally, the response is sent back through the DispatcherServlet, then Tomcat, and ultimately returned to the client.

In modern Spring Boot applications, Spring MVC is commonly used together with RESTful APIs, where controllers return JSON responses instead of rendering JSP pages.

In summary, Spring MVC is a framework that implements the Model-View-Controller pattern, uses DispatcherServlet as the Front Controller, and provides a structured way to process web requests and generate responses.

### How do you validate input data in spring boot

`@NotNull` `NotEmpty`

In Spring Boot, input validation is typically implemented in two steps.

The first step is to define validation rules on a DTO, Entity, or Model class using validation annotations. Common annotations include @NotNull, @NotEmpty, @Email, @Min, @Max, @Size, and @Pattern. These annotations define the constraints that incoming data must satisfy.

The second step is to enable validation in the Controller by using the @Valid annotation. When a request is received, Spring automatically validates the request body against the validation rules defined in the DTO or Entity class.

If the validation passes, the request continues to the Service Layer. If the validation fails, Spring throws a validation exception, which can be handled by a global exception handler using @RestControllerAdvice.

For example, an email field can be annotated with @Email, and a user’s age can be restricted using @Min and @Max. By combining validation annotations with @Valid, Spring Boot can automatically validate incoming request data before business logic is executed.

### How do you use Spring boot actuator?

Spring Boot Actuator is used to **monitor (status)**and manage applications in production environments. It provides many built-in endpoints that expose application health, metrics, beans, caches, environment information, and other runtime details.

To use Spring Boot Actuator, the first step is to add the actuator dependency to the project.

The second step is to expose the actuator endpoints through configuration. For example, in application-dev.properties, we can configure:

management.endpoints.web.exposure.include=*

This allows access to endpoints such as /actuator/health, /actuator/beans, /actuator/caches, /actuator/env, and /actuator/metrics.

The third step is to collect and persist application metrics. In a production environment, actuator metrics are commonly exported to a time-series database such as Prometheus.

The fourth step is to connect Prometheus to a visualization tool such as Grafana. Grafana reads metrics from Prometheus and provides dashboards for monitoring application performance, memory usage, CPU usage, request counts, response times, and other operational metrics.

In summary, the typical flow is:

Spring Boot Application
→ Spring Boot Actuator
→ Prometheus
→ Grafana

This architecture allows developers and operations teams to monitor application health and performance in real time.

### How does spring mvc work

When a client sends an HTTP request, such as:

GET /employee/1

or

PUT /employee/1

the request first reaches the web server, such as Tomcat.

Tomcat forwards the request to the DispatcherServlet, which is the Front Controller in Spring MVC. DispatcherServlet is responsible for receiving all incoming requests and coordinating the request processing flow.

The DispatcherServlet consults the Handler Mapping to identify the appropriate Controller method that should handle the request.

The Controller receives the request, extracts path variables, query parameters, or request bodies, and delegates the request to the Service Layer.

The Service Layer executes the business logic and may call the Repository or DAO Layer to access data from a database.

The DAO Layer interacts with relational databases through JPA and Hibernate or with NoSQL databases such as MongoDB and DynamoDB.

After the business processing is completed, the result is returned from the DAO Layer to the Service Layer and then back to the Controller.

The Controller creates a Model object or returns a ResponseEntity object. For traditional MVC applications, the Model is passed to a View such as JSP or Thymeleaf for rendering. For RESTful APIs in Spring Boot, the Controller usually returns JSON data directly.

The response is then sent back to the DispatcherServlet, forwarded to Tomcat, and finally returned to the client browser.

In summary, the request flow is:

Client
→ Tomcat
→ DispatcherServlet
→ Handler Mapping
→ Controller
→ Service Layer
→ Repository / DAO
→ Database
→ Repository / DAO
→ Service Layer
→ Controller
→ DispatcherServlet
→ Tomcat
→ Client

### What is controller how you use controller how you implement controller

A Controller is the top layer of a 3-tier architecture. 

Its main responsibility is to expose RESTful endpoints to the UI 

and handle HTTP requests and responses. 

In Spring Boot, I usually use the `@RestController` annotation to implement a controller. The frontend sends requests to the controller through RESTful APIs. 

Then, the controller receives the request, extracts the input parameters, calls the Service layer to execute the business logic, and returns the result back to the client in JSON format. 

When implementing a controller, I follow RESTful endpoint design principles, such as using GET for retrieving data, POST for creating data, PUT for updating data, and DELETE for deleting data. 

I also handle exceptions properly to provide meaningful error messages to the client. Instead of writing exception handling logic in every controller, I usually use `@RestControllerAdvice` to implement centralized exception handling and keep the controller clean and maintainable.

### What is Webflux? Have you used it in your project

WebFlux is Spring’s reactive web framework. Traditional Spring MVC is based on the Servlet API and uses a thread-per-request model. Tomcat assigns one thread to each request, so it is a synchronous and blocking approach.

WebFlux uses the Reactor library and follows an asynchronous and non-blocking model. Instead of blocking a thread while waiting for a database or external service, it uses channels, event loops, and worker groups to process requests more efficiently.

In WebFlux, `Mono` represents a single object and `Flux` represents a group of objects. Controllers return `Mono<T>` or `Flux<T>` instead of regular objects.

The main advantage of WebFlux is high concurrency with fewer threads and better resource utilization. It is suitable for API gateways, streaming applications, and other high-traffic systems. One challenge is that reactive code is more difficult to debug and troubleshoot compared with traditional Spring MVC.

I used WebFlux in a project to build reactive REST APIs. We used `Mono` and `Flux` in the controller and service layers to process requests asynchronously and improve scalability.

Recently, Java 21 introduced Virtual Threads. Virtual Threads provide high concurrency while keeping the traditional programming model. Because of that, many teams are now evaluating Virtual Threads as an alternative to reactive programming in some scenarios.

### How do you connect the database in springboot?

To connect a database in Spring Boot, I usually follow three steps.

First, I add the Spring Data JPA dependency and the database driver dependency, such as PostgreSQL or MySQL.

Second, I configure the database properties in the `application.properties` or `application.yml` file. The configuration includes the database URL, username, password, driver class, connection pool size, connection timeout, and other datasource settings. In some projects, I also use `@Configuration`, `@Bean`, and `@Value` annotations to load custom configuration values.

Third, I configure the datasource bean. If the application has multiple datasources, I use `@Primary` to specify the default datasource and `@Qualifier` to inject a specific datasource when needed.

After the datasource is configured, Spring Data JPA can create and manage database connections automatically. Then I create Repository interfaces by extending `JpaRepository`, and the application can perform CRUD operations on the database.

### How do you handle the global exception in Spring Boot?

In Spring Boot, I usually handle global exceptions using `@RestControllerAdvice`. Instead of writing try-catch blocks in every controller method, I create a centralized exception handling class to manage exceptions across the entire application. Inside this class, I use `@ExceptionHandler` to handle different types of exceptions, such as `ResourceNotFoundException`, `IllegalArgumentException`, and generic `Exception`. This approach keeps the controller layer clean and improves maintainability. I usually return a standardized error response containing an error message, timestamp, and HTTP status code. Under the hood, `@RestControllerAdvice` follows the AOP concept by intercepting exceptions thrown from controller methods and routing them to the appropriate exception handler.

Another way to implement AOP in Spring is using an `@Aspect` class together with annotations such as `@Pointcut`, `@Before`, `@After`, `@AfterReturning`, `@AfterThrowing`, and `@Around`. However, for global exception handling in REST APIs, `@RestControllerAdvice` and `@ExceptionHandler` are the most common and recommended solution.

### Spring boot annotation

Spring annotations can be grouped into several categories based on their purpose,

### **IoC Annotations**

Spring Boot is built on IoC (Inversion of Control). Instead of creating objects manually with the `new` keyword, Spring manages object creation, dependency injection, and bean lifecycle through the IoC container.

`@SpringBootApplication` is the main entry point of a Spring Boot application. It combines three important annotations: `@EnableAutoConfiguration`, `@Configuration`, and `@ComponentScan`.

`@EnableAutoConfiguration` automatically configures Spring components based on project dependencies, such as DataSource, Tomcat, Jackson, and Spring MVC.

`@Configuration` marks a class as a configuration class and is commonly used together with `@Bean` to register beans manually.

`@ComponentScan` tells Spring where to scan for beans. It automatically discovers classes annotated with `@Controller`, `@RestController`, `@Service`, `@Repository`, and `@Component`.

`@Controller` is used in traditional Spring MVC applications and usually returns a view name. `@RestController` is used for REST APIs. `@RestController` is used to define a REST controller. It tells Spring to register the class as a controller bean and return data directly as the HTTP response.

`@Service` represents the business logic layer. `@Repository` represents the persistence layer and is responsible for the database communication. `@Component` is a generic bean annotation. It is used to register a class as a bean for Spring management.

`@Bean` is used to manually register a bean inside a configuration class, especially for third-party classes that cannot be annotated directly.

`@Scope` defines the lifecycle of a bean. Common scopes include Singleton, Prototype, Request, Session, and Application. Singleton is the default and most commonly used scope.

---

### **Dependency Injection Annotations**

Spring supports Dependency Injection to reduce coupling between components.

`@Autowired` is used to inject dependencies automatically. By default, Spring resolves dependencies by type.

When multiple beans share the same type, `@Qualifier` can be used to specify which bean should be injected. Spring first resolves by type and then resolves by bean name.

---

### **AOP Annotations**

Spring AOP allows us to add cross-cutting concerns such as logging, exception handling, security, auditing, and performance monitoring without modifying business logic.

`@Aspect` defines an aspect class.

`@Pointcut` defines where the advice should be applied.

`@Before` executes before the target method.

`@After` executes after the target method completes regardless of the outcome.

`@AfterReturning` executes only when the method completes successfully.

`@AfterThrowing` executes only when an exception is thrown.

`@Around` surrounds the target method and provides the most control because it can execute logic before and after the method invocation.

For global exception handling, Spring commonly uses `@RestControllerAdvice` together with `@ExceptionHandler`. `@RestControllerAdvice` centralizes exception handling across all controllers, while `@ExceptionHandler` handles specific exception types and returns standardized error responses.

---

### **REST API Annotations**

Spring Boot provides several annotations for building RESTful APIs.

`@RequestMapping` defines the base URL mapping for a controller.

`@GetMapping` handles HTTP GET requests.

`@PostMapping` handles HTTP POST requests.

`@PutMapping` handles HTTP PUT requests.

`@DeleteMapping` handles HTTP DELETE requests.

`@RequestParam` extracts query parameters from the URL.

Example:

```java
@GetMapping("/users")
public User getUser(@RequestParam Long id)
```

`@PathVariable` extracts values from the URL path.

Example:

```java
@GetMapping("/users/{id}")
public User getUser(@PathVariable Long id)
```

`@RequestHeader` extracts values from HTTP headers.

`@RequestBody` converts the HTTP request body into a Java object.

`@ResponseBody` converts a Java object into JSON or XML and returns it to the client. In a `@RestController`, `@ResponseBody` is automatically applied to all methods.

---

### **Summary**

Spring annotations can be grouped into four major categories:

- **IoC**: `@SpringBootApplication`, `@EnableAutoConfiguration`, `@Configuration`, `@ComponentScan`, `@Controller`, `@RestController`, `@Service`, `@Repository`, `@Component`, `@Bean`, `@Scope`
- **DI**: `@Autowired`, `@Qualifier`
- **AOP**: `@Aspect`, `@Pointcut`, `@Before`, `@After`, `@AfterReturning`, `@AfterThrowing`, `@Around`, `@RestControllerAdvice`, `@ExceptionHandler`
- **REST API**: `@RequestMapping`, `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`, `@RequestParam`, `@PathVariable`, `@RequestHeader`, `@RequestBody`, `@ResponseBody`

### How Spring IOC work , all annotations and injection and bean types

### How many ways to inject bean in spring and which one we use most

There are three common ways to inject beans in Spring: Constructor Injection, Setter Injection, and Field Injection.

Field Injection uses `@Autowired` directly on a field. It is simple and requires less code, but it makes unit testing more difficult and hides dependencies, so it is generally not recommended.

Setter Injection uses `@Autowired` on a setter method. It is useful when a dependency is optional or needs to be changed after object creation. However, it allows the bean state to be modified and does not guarantee that all required dependencies are provided.

Constructor Injection uses `@Autowired` on the constructor, or in newer Spring versions, the annotation can be omitted when there is only one constructor. It clearly defines all required dependencies, makes the bean immutable, and is easier to test. It also helps detect circular dependencies during startup. It avoid the NullPointerException and fail fast. 

In most projects, Constructor Injection is the preferred and most commonly used approach because it improves readability, maintainability, and testability.

### **Question: What is the difference between inject by type and inject by name in Spring Bean Injection?**

Spring Bean injection is managed by the IoC container. When Spring performs dependency injection, it first resolves the dependency by type. If there is only one bean of that type in the container, Spring injects it automatically. This is called injection by type.

However, if multiple beans of the same type exist, Spring cannot determine which bean should be injected. In that case, Spring uses additional information such as `@Primary`, `@Qualifier`, or bean name matching to identify the correct bean. This is commonly referred to as injection by name.

`@Primary` is placed on the bean definition and tells Spring which bean should be used as the default implementation when multiple candidates are available. `@Qualifier` is used at the injection point and allows developers to explicitly specify which bean should be injected.

In summary, Spring always starts by resolving dependencies by type. If multiple beans of the same type are found, it uses `@Primary`, `@Qualifier`, or bean name matching to select the correct bean. If Spring still cannot determine a unique bean, it throws a `NoUniqueBeanDefinitionException`.

### Why constructor injection

Constructor Injection uses `@Autowired` on the constructor, or in newer Spring versions, the annotation can be omitted when there is only one constructor. It clearly defines all required dependencies, makes the bean immutable, and is easier to test. It also helps detect circular dependencies during startup. I t avoid the NullPointerException and fail fast. 

### **What Java version is required for Spring Boot 3?**

Spring Boot 3 requires Java 17 or higher. Java 17 is the minimum supported version because Spring Boot 3 migrated from `javax.*` packages to `jakarta.*` packages and adopted many modern Java features. Therefore, applications using Spring Boot 3 must run on Java 17 or later.

### **What is DispatcherServlet in Spring MVC?**

DispatcherServlet is the front controller of Spring MVC and one of the core components of the framework. 

DispatcherServlet is the front controller of Spring MVC. It uses Handler Mapping to route incoming HTTP requests to the appropriate controller endpoint based on the URL and HTTP method.

Based on the request URL and HTTP method, it routes the request through the Handler Mapping and Handler Execution Chain. 

During this process, interceptors in the handler chain may process the request before it reaches the controller. 

DispatcherServlet then identifies the appropriate controller endpoint, sends the request to that controller for processing, receives the result, and finally returns the response back to the client. 

In simple terms, DispatcherServlet is responsible for coordinating the entire request-response lifecycle and distributing HTTP requests to the proper controller based on routing information.