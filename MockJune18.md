# Mock Interview June 18

### What spring boot version you used

I am currently using Spring Boot 3.x, which is the current major version of the framework. 

and I ‘ve used Spring Boot 2.0. before.  

**One of the most important changes in Spring Boot 3.0 is the migration from `javax` to `jakarta` namespace,** which happened because Oracle transferred Java EE to the Eclipse Foundation and renamed the entire package namespace. 

**This also introduced a minimum Java version requirement of Java 17, whereas Spring Boot 2.x only required Java 8.** 

Java 17 is an LTS (Long Term Support) version, meaning it receives long-term official maintenance and security updates, making it more stable for production use.

### How to write restapi in spring boot

I write the restful api in controller layer. 

Writing a RESTful API in Spring Boot starts with the `@RestController` annotation, which is a combination of `@Controller` and `@ResponseBody`, meaning it handles HTTP requests and automatically serializes the return value into JSON without needing to annotate each method separately. 

I first using `@RequestMapping` to define the global url (no verb and version.)

Each HTTP method is mapped using dedicated annotations such as `@GetMapping`, `@PostMapping`, `@PutMapping`, and `@DeleteMapping` placed directly on top of each method. When a method needs to accept a request body, `@RequestBody` is used as a parameter annotation to deserialize the incoming JSON into a Java object. 

The return type of each method should be `ResponseEntity`, not `ResponseBody`, which allows you to control both the response body and the HTTP status code, such as returning `ResponseEntity.ok()` for a successful request or `ResponseEntity.notFound()` for a missing resource. 

For input validation, `@Valid` is used on the `@RequestBody` parameter, combined with constraint annotations on the DTO fields such as `@NotNull`, `@Min`, and `@Max`, which ensures that invalid data is rejected before it even reaches the service layer.

### Diff recursion and iteration?

Recursion is an approach that breaks a problem down into smaller subproblems, where a function calls itself repeatedly until it reaches a base case that stops the recursive calls. 

Iteration on the other hand repeats a set of operations through a loop with an ending condition, without breaking the problem into subproblems.

**The key difference in practice is memory usage — recursion has high memory usage because each function call is pushed onto the call stack and stays there until the base case is reached.** 

**Iteration has low memory usage because it only maintains a single loop variable in memory.** 

This difference in memory usage becomes critical in production environments, because under high QPS, meaning high queries per second traffic patterns, using recursion can cause memory usage to surge dramatically as thousands of concurrent requests each create deep call stacks, which can ultimately lead to a stack overflow or even a deadlock situation where the system runs out of memory and threads start blocking each other. 

**For this reason, in high traffic production systems it is generally recommended to avoid recursion and use iteration instead,** converting any recursive logic into an iterative approach with an explicit stack data structure if needed, in order to keep memory usage low and stable under heavy load.

### What is fairlock

A fair lock is a thread synchronization mechanism in Java that guarantees threads acquire a lock in the exact order they requested it, **following a FIFO, or First In First Out, ordering.** 

**By default, Java locks are unfair,** meaning there is no guarantee which thread will acquire the lock next when it becomes available, which can lead to thread starvation where some threads wait indefinitely. 

With a fair lock, if three threads T1, T2, and T3 request a lock in that order, they will also release and reacquire it in that exact same order — T1 locks and unlocks first, then T2, then T3. 

**In Java, a fair lock is implemented using `ReentrantLock` by passing `true` to its constructor, written as `Lock lock = new ReentrantLock(true)`, where the `true` parameter enables fair ordering.** 

The tradeoff is that fair locks have lower throughput compared to unfair locks because of the overhead of maintaining the strict ordering queue, so they are typically used in scenarios where thread starvation is a concern and predictable ordering is more important than raw performance.

### What is sealed class

**A sealed class in Java is a class that restricts which other classes are allowed to extend or implement it, using the `permits` keyword to explicitly define the allowed subclasses.** 

This gives developers more control over the inheritance hierarchy, ensuring that the class can only be extended by a known and finite set of subclasses.

### Introduce what is Spring Framework

Spring Framework is a lightweight Java framework used to build enterprise applications. It provides many core features (module) such as IoC (Inversion of Control), Dependency Injection, AOP, transaction management, and integration with different technologies.

**IoC, or Inversion of Control, is a design principle in Spring where the control of creating and managing objects is transferred from the developer to the Spring container. + `loose coupling`**

**Developer not have have to create objects using new keywords.** 

**DI is the implementation of IoC + `definition`+ `bean types (@Scope)`** 

**Three ways of injection.**

Constructor injection is the most recommended injection method because **dependencies are required to be passed in at the time the object is created,** which means **the object cannot be instantiated without its dependencies,** following **the fail fast principle where problems are caught immediately at startup rather than at runtime.** 

**This also avoids NullPointerException** because the dependency is guaranteed to exist as soon as the object is created, unlike other injection methods where the dependency might be null if not properly injected. **Additionally, dependencies can be marked as final**, meaning they are immutable and cannot be changed after the object is created, which makes the code safer and more predictable. **Constructor injection is also the easiest to unit test because you can simply pass in a mock object directly through the constructor without needing any special framework or reflection.**

Field injection is the most **concise** way to inject dependencies as it only requires a single **`@Autowired` annotation on the field.** However it has several significant disadvantages — fields **cannot be marked as final meaning dependencies can be modified at any time,** it is very **difficult to unit test** because the fields are private and there is no constructor or setter to pass in a mock object, and it **hides the dependency relationships meaning you cannot tell what the class depends on just by looking at its constructor.**

Setter injection allows dependencies to be optional, meaning the object can still be created even without the dependency being provided, and it also allows dependencies to be dynamically replaced at runtime. However similar to field injection, **dependencies cannot be marked as final meaning they can be modified at any time,** and since the dependency is injected after the object is created there is no guarantee that it exists when the object is first used, **which can lead to NullPointerException** if the setter is never called before the dependency is accessed.

---

**AOP, or Aspect Oriented Programming, is a programming paradigm that allows you to separate cross-cutting concerns such as logging, security, and transaction management from the main business logic by applying them automatically across multiple points in the application. +两种实现办法**

In the early days of Spring Framework, applications were mainly configured using XML files. The Spring container managed objects through BeanFactory and ApplicationContext. BeanFactory is the basic IoC container, while ApplicationContext is a more advanced container and is more commonly used in modern applications. At that time, Spring development was not annotation-driven, and developers had to define most configurations in XML.

Later, Spring MVC introduced annotations such as @Controller, @RequestMapping, and @Autowired, which significantly reduced the amount of XML configuration and made development easier and more maintainable.

Today, Spring Boot is built on top of Spring Framework and follows an annotation-driven development style. Developers can use annotations such as @SpringBootApplication, @RestController, @Service, and @Repository to quickly build applications with minimal configuration. **Spring Boot also provides auto-configuration, starter dependencies, and embedded servers,** which greatly simplify application development. 

In summary, Spring Framework provides the foundation and core features, Spring MVC introduced annotation-based web development, and Spring Boot further simplified development through convention over configuration and auto-configuration.

### What is consumer?

A Consumer is a functional interface in Java that takes a single input parameter, performs an operation on it, and returns nothing, meaning its return type is void. 

Its main method is `accept()`, which executes the operation on the given input, and it also has an `andThen()` method which allows you to chain multiple Consumer operations together, executing them sequentially on the same input. 

It is commonly used in lambda expressions for operations like iterating through a list or performing a side effect on an object, such as printing or updating a value.

### how Spring IOC work , all annotations and injection and bean types

**Bean Type:**

Singleton creates only one instance of the bean that is shared across the entire Spring boot application and is the default scope in Spring. 

Prototype creates a new instance every single time the bean is requested, meaning there is no shared instance. 

Request creates a new instance for each individual HTTP request and is destroyed once the request is completed, making it specific to web applications. 

Session creates a new instance for each HTTP session and lives as long as the user's session is active, also specific to web applications. 

Application creates a single instance for the entire lifecycle of a ServletContext, meaning it is shared across all users and all sessions within the same **web application**, similar to Singleton but scoped to the web application context rather than the Spring application context.