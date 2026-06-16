# Mock Interview June 15

### How to increase young generation size in heap

To increase the Young Generation size in the JVM heap, you have two main approaches. The first is setting an explicit size using `-Xmn<size>` (e.g., `-Xmn2g`), which locks both the initial and maximum size to a fixed value. For more flexibility, you can use `-XX:NewSize` and `-XX:MaxNewSize` separately, letting the JVM resize dynamically within your defined bounds. The second approach is ratio-based: `-XX:NewRatio=<value>` controls the Old-to-Young generation ratio, so lowering it (e.g., `-XX:NewRatio=1`) gives the Young Generation a larger share of the heap. You can also fine-tune the internal layout using `-XX:SurvivorRatio`, which adjusts how Eden and the two Survivor spaces (S0/S1) divide up the Young Generation — a lower value grows the Survivor spaces, helping retain short-lived objects longer before promotion. One important caveat: if you're using the G1 garbage collector, avoid hard-coding `-Xmn`, as it disables G1's dynamic sizing and can interfere with its pause-time optimization. Oracle's general rule of thumb is to keep the Young Generation between 25–33% of your total max heap (`-Xmx`).

### How does spring mvc work?

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

### How do you handle exception in java？

In Java, exceptions are split into two categories. **Checked exceptions** are enforced at compile time — the compiler forces you to either catch them with a `try-catch` block or declare them in the method signature using `throws`. Typical examples include `IOException` and `SQLException`, which represent recoverable situations the caller should be aware of. **Unchecked exceptions**, or runtime exceptions, are subclasses of `RuntimeException` such as `NullPointerException` or `IllegalArgumentException`. The compiler doesn't require you to handle them, but in practice you can explicitly `throw` them to signal invalid states or violated business rules — for example, throwing an `IllegalArgumentException` when a method receives bad input.

For resource management, **try-with-resources** is the recommended pattern since Java 7. Any object implementing `AutoCloseable` — such as file streams, database connections, or network sockets — can be declared inside the `try(...)` parentheses, and the JVM will automatically call `close()` on it when the block exits, whether normally or due to an exception. This eliminates the need for a `finally` block just to clean up, and prevents resource leaks that are easy to miss otherwise.

For **custom exceptions**, you extend either `Exception` or `RuntimeException` depending on how you want callers to interact with it. Extending `Exception` makes it checked, forcing callers to handle it explicitly — useful for domain-level errors like `InsufficientFundsException` where the caller should take deliberate action. Extending `RuntimeException` makes it unchecked, which suits programming errors or violations of internal contracts. You can also chain exceptions by passing the original cause into the constructor, which preserves the full error context when wrapping a low-level exception into a higher-level domain exception.

In a Spring Boot application, a clean pattern is to **centralize exception handling** rather than scattering `try-catch` blocks across the codebase. You can throw custom runtime exceptions freely from your service layer, then intercept them in one place. `@RestControllerAdvice` at the controller layer lets you define `@ExceptionHandler` methods that catch specific exception types and return structured error responses globally. For cross-cutting concerns beyond the controller layer — such as logging or handling exceptions in service or repository layers — you can use `@Aspect` with AOP to intercept method calls and handle exceptions in a centralized, non-intrusive way.

### What annotations we use to configure customized actuator

To create a custom Spring Boot Actuator endpoint, you use the `@Endpoint` annotation on a class to register it as a manageable endpoint exposed via both JMX and HTTP. Inside that class, you define operations using three method-level annotations that map to HTTP semantics: `@ReadOperation` maps to GET and is used for retrieving information, `@WriteOperation` maps to POST and is used for updating or triggering state changes, and `@DeleteOperation` maps to DELETE and is used for removing or resetting something. Together these annotations let you build a fully custom actuator endpoint without manually configuring any request mappings, and Spring Boot handles the exposure and routing automatically based on your `management.endpoints.web.exposure.include` configuration.

#### And how we actually use actuator?

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

### Can abstract class have no abstract method?

Yes, an abstract class can have no abstract methods. The purpose of marking a class as abstract is simply to **prevent it from being instantiated**, not to enforce the presence of abstract methods. It may contain only concrete methods, or even no methods at all.

As for interfaces, in Java 8 and beyond, interfaces can also have no abstract methods, since they now support `default` and `static` methods which have a body. An interface with no methods at all is called a **marker interface**, such as `Serializable`, which is used purely to signal something to the JVM or framework without defining any behavior.

So in both cases, the presence of abstract methods is optional. The key distinction is that neither an abstract class nor an interface can be instantiated directly, regardless of whether they contain abstract methods.

### How can you use optional?

`Optional` is a wrapper class introduced in Java 8 that is used to **represent a value that may or may not be present**, and its main purpose is to avoid `NullPointerException` and make null-handling more explicit and readable.

There are three main ways to create an Optional. `Optional.of(value)` is used when you are confident the value is **not null** — if you pass null it will throw a NullPointerException. `Optional.ofNullable(value)` is the safer alternative that accepts a value which **may be null** — if it is null, it simply returns an empty Optional instead of throwing an exception. `Optional.empty()` is used when you want to **explicitly represent the absence of a value**, which is commonly used as a return type to signal that no result was found.

To retrieve the value, you can use `orElse(defaultValue)`, which unwraps the Optional and returns the value inside, or falls back to the provided default if the Optional is empty.

In practice, the most common use case is as a **method return type**. For example, instead of a `findUserById()` method returning null when no user is found, it can return `Optional<User>`. This forces the caller to explicitly handle the case where the result may be absent, making the code much safer and more intentional compared to returning null and risking a NullPointerException somewhere down the line.

### What is functional interface?

A functional interface is an interface that has **exactly one abstract method**, and it is the foundation of functional programming in Java 8. The `@FunctionalInterface` annotation is optional but recommended as it tells the compiler to enforce the single abstract method rule.

There are four most common built-in functional interfaces. **Consumer** takes one input and returns no output — it is used when you want to perform an operation on a value without returning anything, such as printing it. **Supplier** takes no input and returns one output — it is used when you want to lazily generate or supply a value. **Predicate** takes one input and returns a boolean — it is used for conditional checks or filtering logic. **Function** takes one input and returns one output — it is the most general purpose one, used for transforming or mapping a value from one type to another.

Functional interfaces work hand in hand with **lambda expressions**, which provide a clean and concise way to implement them without writing a full anonymous class. For example, instead of writing a verbose anonymous class, you can simply write `i -> i % 2 != 0` to represent a Predicate that checks if a number is odd.

They are also heavily used in the **Stream API**. For instance, `filter()` takes a Predicate to keep only elements that match a condition, `map()` takes a Function to transform each element, `flatMap()` is similar to map but flattens nested structures into a single stream, and `distinct()` removes duplicate elements. Together, these allow you to write very expressive and readable data processing pipelines in a functional style.

### Why do you use post, instead of put？

The key difference between POST and PUT comes down to **idempotency** and **intent**.

**POST** is **not idempotent**, meaning if you send the same request multiple times, it will produce different results each time — for example, creating multiple duplicate records. This is why POST is used for **data insertion**, when you want to create a new resource and the server decides the resource's ID or location.

**PUT** is **idempotent**, meaning no matter how many times you send the same request, the result will always be the same — the resource will be in the same state. This is why PUT is used for **data update**, when you want to replace or update an existing resource at a known location.

So in practice, if I am building an endpoint to **create a new user**, I would use POST because every call should generate a new user with a new ID. But if I am building an endpoint to **update an existing user's information**, I would use PUT because calling it multiple times with the same data should always result in the same final state, with no unintended side effects.

In short, the choice between POST and PUT is not just a convention — it reflects the **semantic intent** of the operation and ensures the API behaves predictably and correctly.

### What is webflux? Have you used it in your project?

WebFlux is Spring’s reactive web framework. Traditional Spring MVC is based on the Servlet API and uses a thread-per-request model. Tomcat assigns one thread to each request, so it is a synchronous and blocking approach.

WebFlux uses the Reactor library and follows an asynchronous and non-blocking model. Instead of blocking a thread while waiting for a database or external service, it uses channels, event loops, and worker groups to process requests more efficiently.

In WebFlux, `Mono` represents a single object and `Flux` represents a group of objects. Controllers return `Mono<T>` or `Flux<T>` instead of regular objects.

The main advantage of WebFlux is high concurrency with fewer threads and better resource utilization. It is suitable for API gateways, streaming applications, and other high-traffic systems. One challenge is that reactive code is more difficult to debug and troubleshoot compared with traditional Spring MVC.

I used WebFlux in a project to build reactive REST APIs. We used `Mono` and `Flux` in the controller and service layers to process requests asynchronously and improve scalability.

Recently, Java 21 introduced Virtual Threads. Virtual Threads provide high concurrency while keeping the traditional programming model. Because of that, many teams are now evaluating Virtual Threads as an alternative to reactive programming in some scenarios.

### What is Hashmap?

HashMap is a data structure in Java that stores data in **key-value pairs**, and it uses a **hashing mechanism** internally to achieve fast lookup, insertion, and deletion at O(1) average time complexity.

Internally, HashMap works by first calling the **hashCode()** method on the key to calculate a hash value, and then uses that hash value to determine which **bucket** (index in an internal array) the entry should be stored in. When you try to retrieve a value, it does the same thing — it hashes the key to find the right bucket. However, two different keys can produce the same hash value, which is called a **collision**. When a collision occurs, Java stores multiple entries in the same bucket using a **linked list**, and from Java 8 onwards it converts to a **balanced tree** when the bucket size exceeds a threshold, to maintain performance.

This is where **hashCode() and equals()** become critical. When there are multiple entries in the same bucket, HashMap uses `equals()` to do a **cross comparison** to find the exact key that matches. This is why it is essential to **override both hashCode() and equals()** together when using a custom object as a key. If you only override equals() without overriding hashCode(), two objects that are logically equal may end up in different buckets, and HashMap will fail to find the correct value. The general contract is that if two objects are equal according to equals(), they **must** return the same hashCode().

In short, hashCode() is used to find the right bucket quickly, and equals() is used to find the exact entry within that bucket.

### What is @EnableAutoConfiguration?

`@EnableAutoConfiguration` is one of the three core annotations that make up `@SpringBootApplication`, alongside `@ComponentScan` and `@SpringBootConfiguration`. It is the annotation that tells Spring Boot to **automatically configure the application context** based on the dependencies present in the classpath, so developers do not have to manually write all the configuration themselves.

The auto configuration process works in three main steps. **First**, Spring Boot performs **class scanning** through `@ComponentScan`, which scans the current package and its sub-packages to detect and register all Spring-managed components such as `@Service`, `@Repository`, and `@Controller`.

**Second**, Spring Boot looks at the **pom.xml** to see what dependencies have been added. For example, if you add the Spring Data JPA dependency, Spring Boot knows you intend to use a database. Each starter dependency comes with pre-written `@Configuration` classes that define `@Bean` methods, which are the actual configurations that will be loaded into the application context.

**Third**, Spring Boot uses **conditional annotations** to decide whether a particular configuration should actually be applied. `@ConditionalOnClass` means the configuration will only be applied if a certain class is present on the classpath. `@ConditionalOnMissingBean` means the configuration will only be applied if the developer has **not already defined their own bean** of that type. This is what makes auto configuration smart and non-invasive — it backs off whenever you provide your own custom configuration, following the principle of **convention over configuration**.

In short, `@EnableAutoConfiguration` is what makes Spring Boot opinionated and developer-friendly, by wiring everything up automatically based on what is available on the classpath.