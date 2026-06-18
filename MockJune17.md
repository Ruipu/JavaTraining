# Mock Interview June 17

### Equals v.s. ==

***A reference is the memory address that a variable holds, which points to where an object lives in the heap.***

In Java both equals() and

**`==` are to** compare references for objects and values for primitives. 

When you use equals() and `==` on two objects, they checks whether both variables point to the exact same memory location.

For equals(), though in default it behaves same as `==`, most classes like `String`, `Integer`, and custom classes override it to compare the actual content of the objects.

### How to inject bean with same type?

**The Problem:** By default, Spring injects beans by type. If there are multiple beans implementing the same interface — for example, two `EmployeeRepository` implementations, one using JDBC and one using Hibernate — Spring can't decide which one to use and the application will fail to start.

### Oop 4 principles

- Encapsulation
    
    Encapsulation means hiding the internal details of an object and controlling access to its data through methods. It uses modifiers like public, private, protected or default to restrict the access. `Difference in access restriction`
    
- Inheritance
    
    Inheritance in Java is a mechanism that allows one class to acquire the properties and behaviors of another class. Two inheritance mechanism abstraction class and interface. In Java, one class can only extend one class, but interface supports multiple inheritance of interfaces. This is the key difference in Inheritance.
    
- Polymorphism
    
    Polymorphism means one method or object can have different behaviors depending on the context or object type. It uses override and overload. Overload is inner class behavior or two methods with different signature. Override is in between the class methods have the same signature, but different logics. Overload is resolved in the compile time. Override is resolved at the run time.
    
- Abstraction
    
    Abstraction is to hide the detailed implementations behind the templates. We have two options, abstraction class and interface. (Going back to when we talk about inheritance). 
    

### What is Executors library?

---

`Executors` is a utility class in the `java.util.concurrent` package **that acts as a factory for creating thread pools.** 

It provides several static methods that return pre-configured `ExecutorService` instances, allowing developers to manage concurrent tasks efficiently without manually creating and destroying threads. The reason we need it is that manually creating threads with `new Thread()` for every task is expensive and dangerous. Thread creation consumes system resources, and if too many tasks come in at once, the application can run out of memory or become unresponsive. A thread pool solves this by maintaining a fixed group of reusable threads — tasks are submitted to a queue, and idle threads pick them up for execution, then return to the pool instead of being destroyed.

**The `Executors` class offers four commonly used factory methods, each suited for a different scenario.** 

**`newFixedThreadPool(n)` creates a pool with a fixed number of threads, ideal when you have a stable, predictable workload.** 

**`newCachedThreadPool()` creates threads as needed and reuses idle ones, suitable for many short-lived asynchronous tasks but risky under heavy load because it has no upper limit on thread count.** 

`newSingleThreadExecutor()` uses only one worker thread, guaranteeing tasks are executed sequentially in submission order. 

And `newScheduledThreadPool(n)` supports delayed or periodic task execution, similar to a cron scheduler.

It's important to distinguish three related terms. `Executor` is the base interface with a single `execute()` method, `ExecutorService` extends `Executor` and adds lifecycle management methods like `submit()`, `shutdown()`, and `invokeAll()`, while `Executors` is the factory utility class that produces `ExecutorService` instances. Another key distinction is between `execute()` and `submit()` — the `execute()` method takes a `Runnable` and returns nothing, while `submit()` can accept either a `Runnable` or a `Callable` and returns a `Future`, which lets you retrieve the result or check the task's status later.

After using a thread pool, it must be shut down explicitly, otherwise the JVM won't exit because the pool's threads keep running in the background. Calling `shutdown()` performs a graceful shutdown that waits for all submitted tasks to finish, while `shutdownNow()` attempts to interrupt running tasks immediately. 

**Although `Executors` is convenient, in production code it's often recommended to instantiate `ThreadPoolExecutor` directly,** because the factory methods in `Executors` use default configurations that can be unsafe. 

**`newFixedThreadPool` uses an unbounded task queue which can lead to out-of-memory errors**, and 

**`newCachedThreadPool` has no upper limit on threads.** 

**Using `ThreadPoolExecutor` directly forces developers to explicitly set the core pool size, maximum pool size, queue capacity, and rejection policy, making the system more predictable and safer.**

---

> One-liner summary: "`Executors` is a factory utility class for creating thread pools in Java. It simplifies concurrent programming by providing pre-configured `ExecutorService` instances, but for production systems, configuring `ThreadPoolExecutor` directly gives better control and safety."
> 

### Java 21 features

Java 21, released in September 2023, is a Long-Term Support (LTS) version and one of the most significant Java releases in recent years, introducing several major features that modernize the language. 

- **The most impactful feature is Virtual Threads,** finalized in Java 21 under JEP 444. **Virtual threads are lightweight threads managed by the JVM rather than the operating system, allowing applications to handle millions of concurrent tasks with minimal memory overhead.** Unlike traditional platform threads, which map one-to-one with OS threads and are expensive to create, virtual threads make blocking operations like I/O cheap, fundamentally changing how we write concurrent Java code without needing reactive frameworks.
- Another major addition is **Pattern Matching for `switch`**, which became a standard feature in Java 21.
    
    **It allows developers to use patterns directly in `switch` statements and expressions, including type patterns and guarded patterns.** This makes code that handles multiple types or conditions much cleaner and safer, replacing long chains of `if-else instanceof` checks. 
    
- **Combined with Record Patterns, also finalized in Java 21, developers can deconstruct** record values directly inside `switch` cases or `instanceof` checks, making data-oriented programming much more concise.

Java 21 also introduces **Sequenced Collections**, a new set of interfaces (`SequencedCollection`, `SequencedSet`, `SequencedMap`) that provide a uniform way to access the first and last elements of any ordered collection, along with methods to reverse the order. This fills a long-standing gap in the Collections framework where operations like "get first" or "get last" required different syntax depending on the collection type.

**In addition, String Templates were introduced as a preview feature, providing a safer and more readable alternative to string concatenation and `String.format()` by embedding expressions directly inside template literals.** 

**Unnamed Patterns and Variables** (preview) let developers use `_` as a placeholder when a variable isn't needed, reducing boilerplate. **Scoped Values** (preview) offer a more efficient alternative to `ThreadLocal` for sharing immutable data across threads, especially powerful when combined with virtual threads. Finally, the **Generational ZGC** garbage collector improves performance for typical workloads by separating young and old generations, reducing pause times even further.

Overall, Java 21 is considered a milestone release because it brings true lightweight concurrency through virtual threads, modernizes pattern matching, and introduces several long-awaited usability improvements — all under LTS support, meaning it will receive updates for years to come.

### What is thread local?

**ThreadLocal is a mechanism in Java that gives each thread its own independent copy of a variable.** 

**Even though different threads use the same ThreadLocal object, each thread can only see and modify the value it stored itself. One thread's value is completely invisible to the others.**

**The way it actually works is that every thread object holds its own internal map. When I call `set()`, the value is stored in the current thread's own map, not in some shared central place. So there is no single shared value that gets copied around. Each thread simply keeps its own value on itself.**

**The main reason to use ThreadLocal is thread safety without locking.** Normally, if multiple threads share one variable, I need locks to avoid conflicts, and locks slow things down. ThreadLocal takes a different approach: instead of sharing, each thread gets its own value, so there is no competition and no need for locks.

A common use case is storing context for a single request, like the current logged-in user, so I can access it anywhere in the call chain without passing it around. Other typical uses are holding a database connection or a `SimpleDateFormat`, which is not thread safe on its own.

The one important thing to remember is that I must call `remove()` when I am done. Most servers use thread pools, so threads are reused. If I do not clean up, the next request that reuses the thread may read leftover data from the previous one, which causes data leaks and can even lead to memory leaks.

### Java versions?

---

**Java 8 (2014)** was a revolutionary release that introduced functional programming to Java. The key features include **Lambda Expressions**, which allow concise functional-style code by treating behavior as data; the **Stream API**, which enables declarative processing of collections through operations like filter, map, and reduce; **Functional Interfaces** like `Predicate`, `Function`, and `Consumer` that provide the foundation for lambdas; the **`Optional` class** to handle nullable values more safely and avoid `NullPointerException`; and a new **Date and Time API** under `java.time` that replaced the flawed legacy `Date` and `Calendar` classes. Java 8 is still one of the most widely used LTS versions in production today.

**Java 11 (2018)** was the first LTS release after Java 8 and focused on cleaning up the language and improving developer productivity. Key features include the **`var` keyword** for local variable type inference, which reduces boilerplate while keeping static typing; the new **HTTP Client API** under `java.net.http` that supports HTTP/2 and WebSocket natively; several useful **String methods** like `isBlank()`, `strip()`, `lines()`, and `repeat()`; the ability to **run single-file Java programs** directly without compilation; and the removal of older modules like Java EE and CORBA to slim down the JDK.

**Java 17 (2021)** is another major LTS release that finalized several modern language features. Highlights include **Sealed Classes**, which let developers restrict which classes can extend or implement a type, improving domain modeling; **Pattern Matching for `instanceof`**, which removes the need for explicit casting after type checks; **Records**, a concise way to declare immutable data carrier classes with auto-generated `equals`, `hashCode`, and `toString`; **Text Blocks** for clean multi-line strings using triple quotes; and **Switch Expressions**, which allow `switch` to return values and use arrow syntax for cleaner code. Java 17 is widely adopted in modern Spring Boot 3 applications.

**Java 21 (2023)** is the latest LTS release and a milestone for modern concurrency and language expressiveness. Its standout feature is **Virtual Threads**, which are lightweight threads managed by the JVM that allow applications to handle millions of concurrent tasks cheaply. Other major features include **Pattern Matching for `switch`**, which extends pattern matching to switch statements and expressions; **Record Patterns**, which let you deconstruct records directly inside patterns; **Sequenced Collections**, providing a uniform API for accessing the first and last elements of ordered collections; and preview features like **String Templates** and **Scoped Values** that point toward the future of Java development.

### How do you validate the input data?

---

In Spring Boot, input data is typically validated using the **Bean Validation API (JSR-380)** together with the `@Valid` annotation. 

We apply `@Valid` on the controller method parameter — usually a DTO annotated with `@RequestBody` — which tells Spring to trigger validation before the method executes. The actual validation rules are declared as annotations on the DTO fields: **`@NotNull`** ensures the field is not null, **`@NotBlank`** ensures a string is not empty or whitespace, **`@Email`** validates email format, **`@Size(min=, max=)`** restricts string length or collection size, and **`@Min` / `@Max`** enforce numeric ranges. If any constraint fails, Spring automatically throws a `MethodArgumentNotValidException`, which can be handled globally with a `@RestControllerAdvice` class to return clean, structured error responses to the client. This declarative approach keeps validation logic out of the controller and service layers, making the code cleaner and easier to maintain.

### Injection types

There are three common ways to inject beans in Spring: Constructor Injection, Setter Injection, and Field Injection.

Field Injection uses `@Autowired` directly on a field. It is simple and requires less code, but it makes unit testing more difficult and hides dependencies, so it is generally not recommended.

Setter Injection uses `@Autowired` on a setter method. It is useful when a dependency is optional or needs to be changed after object creation. However, it allows the bean state to be modified and does not guarantee that all required dependencies are provided.

Constructor Injection uses `@Autowired` on the constructor, or in newer Spring versions, the annotation can be omitted when there is only one constructor. It clearly defines all required dependencies, makes the bean immutable, **and is easier to test.** It also helps detect circular dependencies during startup. **It avoid the NullPointerException and fail fast.** 

In most projects, Constructor Injection is the preferred and most commonly used approach because it improves readability, maintainability, and testability.

### Have you used patch

---

Yes, I've used `PATCH` in REST APIs, and the key thing to understand is how it differs from `PUT`. **`PUT`** is used to **fully replace** a resource — the client sends the complete representation, and the server overwrites the existing one. **`PATCH`**, on the other hand, is used for **partial updates** — the client sends only the fields that need to change, leaving the rest untouched. For example, if I want to update only an employee's email, I'd use `PATCH` with a body containing just `{"email": "new@example.com"}`, instead of sending the entire employee object with `PUT`.

An important distinction is **idempotency**. `PUT` is idempotent, meaning calling it multiple times with the same request body always produces the same result — the resource ends up in the exact same state. `PATCH` is **not guaranteed to be idempotent**, because depending on how it's implemented (for example, "increment counter by 1" or "append to a list"), repeated calls can produce different results. However, most well-designed `PATCH` endpoints that simply update specific fields are idempotent in practice. In Spring Boot, I implement `PATCH` using the `@PatchMapping` annotation and typically accept a `Map<String, Object>` or a partial DTO, then update only the non-null fields on the entity before saving. This makes the API more efficient and avoids accidentally wiping out fields that the client didn't intend to change.

### Garbage Collector

GC stands for **Garbage Collector** and it works by starting from the **GC root** and scanning through all reachable objects. Any object that **cannot be reached** from the GC root is considered garbage and gets collected. GC mainly works on the **Heap** and runs on a **background thread** automatically. However GC does come with a **performance cost**, which is why it runs in the background rather than interrupting your main program constantly.

GC, or Garbage Collection, is the mechanism used by the JVM to automatically reclaim memory occupied by objects that are no longer needed.

The garbage collector starts from a set of GC Roots and traverses all reachable objects.

Any object that can be reached directly or indirectly from a GC Root is considered alive.

Objects that are no longer reachable from any GC Root are considered garbage and become eligible for collection.

Garbage Collection primarily operates on the Heap memory because most Java objects are allocated there.

Although GC helps automate memory management, it also introduces performance overhead because the JVM needs to spend time identifying reachable and unreachable objects and reclaiming memory.

To reduce the impact on application performance, garbage collection is typically performed by background GC threads.

**What are Young, Old, and Permanent generations in GC? (not much important)**

The **Young and Old generations** both hold objects in the Heap. New objects start in the **Young generation**, and after surviving a certain number of GC cycles they get promoted to the **Old generation**. Eventually, objects that survive many GC cycles are moved into the **Permanent generation** — these are considered **long living objects** that are harder to recycle but make sense to keep since they have proven they are needed. 

The Young and Old generations also **swap with each other** during the sweeping process, because memory is divided into smaller pieces and used in chunks rather than one entire block — this makes GC more efficient and avoids memory fragmentation in the JVM.

**Different types of GC?** 

**G1GC (Garbage First GC)** uses a **divide and conquer** approach — it divides the heap into smaller memory regions, marks them first, and then recycles them later. It is specifically designed for applications that run with a **large amount of RAM** in the JVM.

G1GC also has two modern follow-ups:

- **ZGC (Zero Garbage Collector)** — a more advanced GC introduced in newer Java versions.
    
    It is low latency.
    It is designed to keep pause times extremely short (usually under 10ms), even with very large heap sizes. 
    
    ZGC performs most of its work concurrently with application threads and uses techniques like colored pointers to track object states. 
    
    It is suitable for large-scale, latency-sensitive applications.
    
- **Shenandoah GC** — another low latency GC also introduced in newer Java versions.
    
    It focuses on minimizing pause times by performing concurrent marking and concurrent compaction. 
    
    Unlike older collectors, Shenandoah aims to reduce both pause duration and memory fragmentation. 
    
    It is commonly used in systems where consistent response time is more important than maximum throughput.
    

These different types of GC are closely related to **which Java version** you are running, as newer collectors were introduced in later versions of Java.