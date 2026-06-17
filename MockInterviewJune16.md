# Mock Interview June 16

### Override HashCode but do not override equals()?

A HashMap is the implement of Map interfaces and it stores the data in the key-value pair. 

When I put in a key, it first calls `hashCode()` to decide which bucket the key goes into, then uses `equals()` to compare against keys already in that bucket. 

So `hashCode()` picks the bucket, and `equals()` compares within that bucket. The key point is that `equals()` is only ever called on objects in the same bucket.

If I override `hashCode()` but not `equals()`, two content-identical objects get the same hash code and land in the same bucket. If I override `hashCode()` but not `equals()`, two objects with the same key will land in the same bucket, but the map still can't tell they're equal. **Without overriding `equals()`, comparing two objects checks whether they're the same instance, not whether they have the same content. Since they were created separately, they count as different keys, and the map stores both as duplicates.**

**If I override `equals()` but not `hashCode()`,** the opposite happens. The default `hashCode()` is based on identity, so **two content-identical objects get different hash codes and go to different buckets.** The map looks in one bucket, does not find the key, and never checks the other. My `equals()` never even runs, because the objects were never in the same bucket. The result is a failed lookup — I store an object and get null when searching with an equal one.

That is why the two must be overridden together. `hashCode()` gets the object to the right bucket, and `equals()` confirms the match inside it. Override only one and you break the chain: `hashCode()` alone causes duplicates, `equals()` alone causes failed lookups.

### How you write restapi in spring boot?

To build a RESTful API in Spring Boot, I start at the controller layer. 

I annotate the class with `@RestController`, which is really a shortcut for `@Controller` plus `@ResponseBody`, meaning every method returns data directly in the response body instead of a view. 

On the class I also put `@RequestMapping` to define a base path url, something like `v4/users`. When designing that URL I follow two rules: no verbs in the path, since the HTTP method already expresses the action, and I include a version like `v4` so the API can evolve without breaking existing clients.

Next I map each operation to an HTTP method following CRUD semantics: GET to read, POST to create, PUT to update, and DELETE to remove. I can write this as `@RequestMapping(method = RequestMethod.GET)`, but in practice I use the shorthand annotations `@GetMapping`, `@PostMapping`, `@PutMapping`, and `@DeleteMapping`. Each method can also define its own sub-URL on top of the base path.

Then I handle the incoming request. Data can come in different ways, and I pick the right one for each case. `@RequestHeader` reads header values, `@RequestBody` maps the JSON payload into a DTO object. 

For parameters of urls, I choose between `@RequestParam` and `@PathVariable`. I use `@PathVariable` when the value identifies a specific resource, like the id in `/users/5`, and `@RequestParam` for optional or filtering values, like `?status=active`.

**`@PathVariable` extracts a value from the URL path** itself and is used to identify one specific resource, like the `5` in `/users/5`.

**`@RequestParam`** extracts a value from the query string after the `?` and i**s used for optional conditions like filtering or sorting**, like `status=active` in `/users?status=active`.

**After binding the request, I validate it. I put `@Valid` on the `@RequestBody` parameter,** and define the actual rules as annotations on the DTO fields, such as `@NotNull`, `@Min`, `@Max`, and `@Email`. This keeps validation declarative and separate from business logic, and Spring automatically rejects invalid input before it reaches my service layer.

**For the response, I return a `ResponseEntity`, which lets me control both the payload and the HTTP status code rather than just returning raw data.** This matters because the status code communicates the outcome: 2xx for success, 4xx for client errors like bad input or unauthorized, and 5xx for server errors. Returning the correct code is a core part of good REST design. I can also apply cross-cutting concerns like logging or timing here through AOP, so they stay out of the controller logic.

**Finally, I centralize exception handling with `@RestControllerAdvice or @Aspect.`** Instead of writing try-catch in every controller, I define one global handler that catches exceptions, maps each to the appropriate HTTP status, and returns a consistent error response. This keeps my controllers clean and gives clients a predictable error format across the whole API.

### What is thread state?

### What is thread local?

**ThreadLocal is a mechanism in Java that gives each thread its own independent copy of a variable.** 

**Even though different threads use the same ThreadLocal object, each thread can only see and modify the value it stored itself. One thread's value is completely invisible to the others.**

**The way it actually works is that every thread object holds its own internal map. When I call `set()`, the value is stored in the current thread's own map, not in some shared central place. So there is no single shared value that gets copied around. Each thread simply keeps its own value on itself.**

**The main reason to use ThreadLocal is thread safety without locking.** Normally, if multiple threads share one variable, I need locks to avoid conflicts, and locks slow things down. ThreadLocal takes a different approach: instead of sharing, each thread gets its own value, so there is no competition and no need for locks.

A common use case is storing context for a single request, like the current logged-in user, so I can access it anywhere in the call chain without passing it around. Other typical uses are holding a database connection or a `SimpleDateFormat`, which is not thread safe on its own.

The one important thing to remember is that I must call `remove()` when I am done. Most servers use thread pools, so threads are reused. If I do not clean up, the next request that reuses the thread may read leftover data from the previous one, which causes data leaks and can even lead to memory leaks.

### What is CORS?

**CORS stands for Cross-Origin Resource Sharing. It is a security mechanism enforced by the browser that controls whether a web page is allowed to access resources from a different origin. An origin is defined by three things: protocol, domain, and port. If any one of them differs, the browser considers it a cross-origin request.**

A typical example is when my frontend and backend run separately during development. My Angular app runs on localhost:4200, and my Spring Boot backend runs on localhost:8080. The protocol and domain are the same, but the ports are different, so the browser treats this as cross-origin. When Angular tries to call the backend API, the browser blocks the response with a CORS error, even though the request actually reached the backend and the backend processed it. The browser holds the response back because the backend never explicitly said it allows that origin.

The reason browsers do this is security. Without it, a malicious site could silently use my logged-in session and cookies to call another site's API on my behalf. The same-origin policy and CORS exist to prevent that kind of attack.

**The fix is on the backend, not the frontend. The backend needs to tell the browser that it permits the requesting origin. In Spring Boot, I do this with the `@CrossOrigin` annotation, where I specify the allowed origin as `http://localhost:4200`.** This makes the backend include an `Access-Control-Allow-Origin` header in its response. Once the browser sees that the backend explicitly allows 4200, it lets the response through and the frontend gets its data.

A couple of important points. CORS is enforced only by the browser, so two backend servers calling each other directly are not affected. And the solution is always a server-side configuration, not a frontend change. I can put `@CrossOrigin` on a single controller, or set up a global configuration so all my endpoints consistently allow the origins I trust.

### Map vs. filter()

**Both `map` and `filter` are intermediate operations in the Java Stream API, but they do different jobs. `filter` is used to select elements, while `map` is used to transform elements.**

The clearest way to tell them apart is by the input each one takes. 

**`filter` takes a `Predicate`, which is a functional interface whose method returns a boolean.** For each element, the predicate answers yes or no, and only the elements that return true stay in the stream. So `filter` never changes the elements themselves, it just decides which ones to keep. The number of elements can go down, but each element stays the same.

**`map` takes a `Function`, which is a functional interface that takes one value and returns another, possibly of a different type.** 

It applies that function to every element and replaces it with the result. So `map` keeps the same number of elements, but each element gets transformed. For example, mapping a list of employees to their names turns Employee objects into String objects, but the count stays the same.

**Because both `Predicate` and `Function` are functional interfaces, in practice I pass them as lambda expressions.** For `filter` I write something like `e -> e.getAge() > 30`, which returns a boolean. For `map` I write something like `e -> e.getName()`, which returns a transformed value.

So in short: `filter` uses a `Predicate` to decide what to keep and changes the size of the stream, while `map` uses a `Function` to transform each element and keeps the size the same. They are often chained together, first filtering down to the elements I want, then mapping them into the shape I need.

### Thread lifecycle?

A thread starts in the **NEW** state when it is created but `start()` has not been called yet. 

Once `start()` is called it moves to **RUNNABLE**, meaning it is ready and waiting for the CPU scheduler to pick it up. 

When the CPU picks it up it enters **RUNNING** state and actually executes — from here it can go in three directions: 

if it tries to enter a `synchronized` block that another thread holds, it moves to **BLOCKED** until the lock is released; 

if `wait()` is called it moves to **WAITING** until another thread calls `notify()`; 

and if `sleep()` or `join()` is called it moves to **TIMED_WAITING** until the time expires. 

In all three cases — once the condition is resolved — the thread goes back to **RUNNABLE** and waits for the CPU to pick it up again. 

Finally when `run()` completes the thread moves to **TERMINATED** and its lifecycle ends.

### **Question: What is the difference between inject by type and inject by name in Spring Bean Injection?**

**Spring Bean injection is managed by the IoC container. When Spring performs dependency injection, it first resolves the dependency by type. If there is only one bean of that type in the container, Spring injects it automatically. This is called injection by type.**

**However, if multiple beans of the same type exist, Spring cannot determine which bean should be injected. In that case, Spring uses additional information such as `@Primary`, `@Qualifier`, or bean name matching to identify the correct bean. This is commonly referred to as injection by name.**

`@Primary` is placed on the bean definition and tells Spring which bean should be used as the default implementation when multiple candidates are available. `@Qualifier` is used at the injection point and allows developers to explicitly specify which bean should be injected.

In summary, Spring always starts by resolving dependencies by type. If multiple beans of the same type are found, it uses `@Primary`, `@Qualifier`, or bean name matching to select the correct bean. If Spring still cannot determine a unique bean, it throws a `NoUniqueBeanDefinitionException`.

### How to send request from angular to backend?

**To send a request from Angular to the backend, the first thing I need to handle is CORS.** 

My Angular app usually runs on localhost:4200 and my backend on localhost:8080. S**ince the ports differ, the browser treats this as cross-origin and will block the response.** So the prerequisite is that the backend allows my origin, for example with the **`@CrossOrigin("http://localhost:4200")` annotation in Spring Boot.** Without that, the request still reaches the backend, but the browser holds back the response.

Once CORS is handled, **I send the actual request using Angular's built-in HttpClient. I import HttpClientModule, inject HttpClient into my service, and then use methods like `http.get`, `http.post`, `http.put`, and `http.delete`, which map to the four CRUD operations on the backend.**

One important point is that Angular's HttpClient has no truly synchronous requests. **Every HTTP call is asynchronous by nature.** The reason is that a request travels over the network and may take hundreds of milliseconds or more. If it were synchronous, the whole browser UI would freeze while waiting, and the user could not interact with anything. So the request is fired off, the code continues, and the response is handled once it comes back.

**There are two ways I handle that asynchronous response. The default Angular way is with Observables. HttpClient returns an Observable, and I have to subscribe to it, and the callback runs when the response arrives. A key detail here is that just calling `http.get` does not actually send the request — it only fires once I subscribe.**

**The second way is async/await, which I use when I want the code to read more like synchronous logic instead of nested callbacks. I convert the Observable into a Promise, for example with `firstValueFrom`, and then use `await`. The `await` keyword pauses the function until the response comes back,** but it does not freeze the browser — it only suspends that one function while everything else keeps running. That is the whole benefit of being asynchronous.

**So to summarize: I first make sure the backend allows my origin through CORS, then I use HttpClient for the CRUD calls. Every request is asynchronous because a synchronous one would freeze the UI. I handle the response either with an Observable and subscribe, which is the Angular default, or by converting it to a Promise and using async/await for more readable code.**

### What is pattern matching?

**Pattern matching is a feature in Java that lets me test whether an object has a certain type or shape and, at the same time, extract its value into a variable, all in one step. The goal is to remove the repetitive, error-prone boilerplate that used to come with type checking.**

**The classic problem it solves is the old `instanceof` pattern.** **Before, I would first check whether an object was a certain type, and then, inside the block, I had to manually cast it to that type before I could use it.** 

That meant writing the type twice and doing an explicit cast every time, which was verbose and easy to get wrong. **With pattern matching for `instanceof`, the check and the cast happen together.** If the check passes, the object is automatically bound to a typed variable that I can use right away, so the manual cast disappears entirely.

**Java then extended this idea to the `switch` statement, which became a standard feature in Java 21. Instead of switching only on simple values, I can now switch on the type of an object. Each case tests for a specific type, and if it matches, the object is automatically bound to a variable of that type within that branch.** T

his makes handling several possible types much cleaner than writing a long chain of `if`–`else` with `instanceof` checks and casts.

The main benefits are readability and safety. The code expresses my intent more directly, there is far less boilerplate, and because the compiler handles the binding, I avoid the risk of an incorrect or unsafe cast. It also pairs well with features like sealed classes, where the compiler can check that I have covered all possible types. Overall, pattern matching makes type-based logic shorter, clearer, and less prone to mistakes.

### Spring boot actuator

To create a custom Spring Boot Actuator endpoint, you use the `@Endpoint` annotation on a class to register it as a manageable endpoint exposed via both JMX and HTTP. 

Inside that class, you define operations using three method-level annotations that map to HTTP semantics: 

`@ReadOperation` maps to GET and is used for retrieving information, 

`@WriteOperation` maps to POST and is used for updating or triggering state changes, and 

`@DeleteOperation` maps to DELETE and is used for removing or resetting something. 

Together these annotations let you build a fully custom actuator endpoint without manually configuring any request mappings, and Spring Boot handles the exposure and routing automatically based on your `management.endpoints.web.exposure.include` configuration.

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

### What is clone in Java?

**`clone()` is the method Java provides to make a copy of an object.** It creates a new object and copies the field values from the original, so I get a separate object that looks the same as the one I started with.

To make a class support cloning, I need to do two things. First, the class has to implement the `Cloneable` interface, which is just a marker interface with no methods. Second, I override the `clone()` method and make it public, since in the `Object` class it is protected. If I call `clone()` on a class that does not implement `Cloneable`, Java throws a `CloneNotSupportedException`.

**The most important part of this topic is the difference between a shallow copy and a deep copy. The default `clone()` performs a shallow copy.** 

*For primitive fields like int or double, a shallow copy works fine because it actually copies the value. The problem is with reference fields, such as a nested object or a List. A shallow copy only copies the reference, meaning the address, not the actual object it points to.* 

So the original and the copy end up sharing the exact same inner object. If I change that List through the copy, the original's List changes too, because they are literally the same object.

**A deep copy solves this. A deep copy also duplicates the inner reference objects, creating brand new ones, so the copy and the original become completely independent and never affect each other.** 

**To achieve a deep copy, I usually have to override `clone()` manually and clone the inner objects one by one, or use another approach like serialization or a copy constructor.**

**So to summarize: `clone()` copies an object and requires implementing `Cloneable` and overriding `clone()`.** 

**By default it is a shallow copy, where primitives are copied by value but reference fields just share the same inner object, so changes leak between the two. A deep copy duplicates those inner objects as well, making the two objects fully independent. The key thing to communicate is that distinction between shallow and deep copy.**