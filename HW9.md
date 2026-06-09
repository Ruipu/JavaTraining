# HW9

### What is Spring IoC

Spring IoC is called Inversion of Control. It is a design principle of the Spring Framework 

Spring Framework controls the creating and managing objects (called beans); sometimes the programmer just define the classes and dependencies. 

Rather than letting classes manage their own dependencies, Spring provides the required objects when the application starts. This makes the code more loosely coupled and easier to maintain.

### What is IoC Container

The IoC Container manages the lifecycle of Spring Beans. 

It creates beans, injects dependencies, configures them, and controls their destruction (when shut down)

The two main IoC containers in Spring are BeanFactory and ApplicationContext, with ApplicationContext being the most commonly used.

### What are the Advantages of IoC

One is loose coupling between classes. 

Since objects do not create their own dependencies, it is easier to replace implementations, perform testing, and maintain the code. 

IoC therefore allows for the reusing of the code.IoC reduces object creation logic, and makes applications easier to scale and manage.

### What is Dependency Injection (DI)

Dependency Injection is a technique used by Spring IoC to provide the required dependencies to a class. **IoC is the concept and DI is the real operation.**

A class does not have to create its own dependent objects. Spring injects them automatically. 

This allows classes to focus on business logic rather than object creation.

### Demo Code to Show Dependency Injection

```java
@Component
public class Engine {
    public void start() {
        System.out.println("Engine started");
    }
}

@Component
public class Car {

    private final Engine engine;

    @Autowired
    public Car(Engine engine) {
        this.engine = engine;
    }

    public void drive() {
        engine.start();
        System.out.println("Car is driving");
    }
}
```

In this example, Car depends on Engine. 

Instead of creating an Engine object using new Engine(), Spring automatically injects the Engine bean through the constructor.

### What are Different Types of Dependency Injection

Spring supports three types of Dependency Injection: **Constructor Injection, Setter Injection, and Field Injection.** 

Constructor Injection is highly recommended, and it passes dependencies through a constructor. 

Setter Injection uses setter methods to inject dependencies. 

Field Injection injects dependencies directly into class fields using @Autowired.

### What are the Pros and Cons of Each Type of Dependency Injection

**Constructor Injection is highly recommended.** Because it makes dependencies mandatory, improves immutability, and works well for unit testing. 

However, constructors may become lengthy when there are many dependencies.

**Setter Injection is useful when dependencies are optional.** 

It provides flexibility because dependencies can be changed after object creation. 

The disadvantage is that required dependencies may accidentally be left unset.

**Field Injection is simple and do not need much code.**

However, it makes unit testing difficult. It hides dependencies, and violates some object-oriented design principles. 

Thus, it is generally not recommended in modern Spring applications.

### @Component vs @Bean

**@Component is used on a class.** I own the class and I can annotate it directly.

Spring can automatically scan and discover and register it. 

It Includes stereotypes: `@Service`, `@Repository`, `@Controller`

**@Bean is used inside a @Configuration class to manually create and configure an object.** 

It is usually used on a method, and is used when I do not own the class or need fine-grained construction logic.

I explicitly instantiate and return the object. 

@Component is typically used for classes we own, while @Bean is commonly used for third-party classes that cannot be modified.

### What is @Configuration and @ComponentScan

**@Configuration** indicates/marks a class, which contains Spring Bean definitions and configuration settings. This is where I write the bean. 

Spring processes this class during startup and creates the beans defined within it. 

```java
@Configuration
public class AppConfig {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
```

**@ComponentScan** tells Spring where to search for classes annotated with stereotypes such as @Component, @Service, @Repository, and @Controller. 

Spring won't find your `@Component` classes and they just get ignored, if I do not have @ComponentScan.

```java
@ComponentScan(basePackages = "com.myapp")
public class AppConfig {
    // Spring will scan com.myapp and all its sub-packages
    // and auto-register any @Component it finds
}
```

### @Controller vs @RestController

**@Controller** is mainly used in traditional MVC applications that return view pages such as JSP or Thymeleaf templates. It also usually returns HTML page. 

It is mainly used in traditional web apps where the server renders the UI.

**@RestController** is a combination of @Controller and @ResponseBody. 

It is used in REST APIs where the client handles the UI.

It automatically returns JSON or XML data directly to the client. 

In modern REST APIs, @RestController is used frequently.

### @Controller vs @Service vs @Repository

@Controller handles HTTP requests and responses. 

It works just like the entry point of the application and communicates with the client. 

@Service contains business logic and coordinates application operations. Java programming usually is done here.

@Repository handles database access and data persistence. 

All the three form the common three-layer architecture: Controller Layer, Service Layer, and Repository Layer.

### Spring Bean Scope

Scope defines **how many instances** of a bean Spring creates and **how long** they live.

Common scopes include Singleton, Prototype, Request, Session, and Application. 

The scope determines whether Spring reuses the same object or creates new ones for different requests.

- **Singleton (default)**: One instance for the **entire application**. Every time you ask for the bean, you get the same object. It is used for stateless services, repositories, and most of your beans.

```java
@Component
@Scope("singleton")
public class UserService { ... }
```

- **Prototype:** A new instance every time you request the bean. It is used for: stateful objects where each caller needs their own copy.

```java
@Component
@Scope("prototype")
public class ReportGenerator { ... }
```

- **Request (Web only):** One instance **per HTTP request**. It is destroyed when the request ends.

```java
@Component
@Scope("request")
public class LoginRequest { ... }
```

- **Session (Web only):** One instance **per HTTP session**. It is destroyed when the session ends (Request is destroyed when the request ends).

```java
@Component
@Scope("session")
public class UserCart { ... }

```

### Singleton vs Prototype

Singleton scope creates only one bean instance for the entire Spring container. Every request for that bean returns the same object. Similar with our normal Singleton class.

```java
@Component
public class UserService {
    // same object shared across the entire app
}
@Autowired UserService a;
@Autowired UserService b;

a == b // true — same object
```

Prototype scope creates a new bean instance every time the bean is requested from the container. It is use when the class has state that is unique per caller.

```java
// BAD — singleton with state, shared across all users
@Component
public class ShoppingCart {
    private List<Item> items = new ArrayList<>(); // BUG: all users share this!
}

// GOOD — prototype with state, each caller gets their own
@Component
@Scope("prototype")
public class ShoppingCart {
    private List<Item> items = new ArrayList<>(); // each user gets their own
}
```

Singleton is useful for shared services, while Prototype is useful when each usage requires an independent object.

### Give 3 Use Cases for Singleton Bean Scope

Singleton scope is useful when the same object can be shared across the entire application.

Three cases are:

1. Service class, such as UserService of EmployeeService in my case. All uses will share the same business logic object, threrefore, I use the Singleton Bean Scope.
    
    ```java
    // 1. Service Class
    @Service  // @Service is @Component — singleton by default
    public class EmployeeService {
        
        private final EmployeeRepository employeeRepository;
        
        public EmployeeService(EmployeeRepository employeeRepository) {
            this.employeeRepository = employeeRepository;
        }
        
        public Employee getEmployee(Long id) {
            return employeeRepository.findById(id);
        }
    }
    ```
    
2. Repository classes, such as EmployeeRepository in my case. In Repository classes, the database access logic will not need multiple instances.

```java
// 2. Repository Class
@Repository  // singleton by default
public class EmployeeRepository {
    
    public Employee findById(Long id) {
        // database access logic
        // no state — same logic shared across all callers
    }
    
    public void save(Employee employee) {
        // save to database
    }
}
```

1. Configuration or utility classes. They provide shared functionality for the whole application.

```java
// 3. Utility Class
@Component  // singleton by default
public class EmailValidator {
    
    public boolean isValid(String email) {
        return email != null && email.contains("@");
    }
    
    public String format(String email) {
        return email.trim().toLowerCase();
    }
}
```

### Give 3 Use Cases for Prototype Bean Scope

I use Prototype when the bean holds state that is unique per caller/user/job.

1. Shoping Cart in E-commerce: In this case, each user needs their own cart with their own items.

```java
@Component
@Scope("prototype")
public class ShoppingCart {
    private List<Item> items = new ArrayList<>();
    
    public void addItem(Item item) { items.add(item); }
    public List<Item> getItems() { return items; }
}

```

1. Report Generator: In this case, each report generation job needs its own state — different data, different output.

```java
@Component
@Scope("prototype")
public class ReportGenerator {
    private List<String> lines = new ArrayList<>();
    private String reportTitle;
    
    public void addLine(String line) { lines.add(line); }
    public void setTitle(String title) { this.reportTitle = title; }
}

```

1. **Form / Request Data Holder: Here, every form submission carries different user input that shouldn't be shared.**

```java
@Component
@Scope("prototype")
public class RegistrationForm {
    private String username;
    private String email;
    private String password;
    
    // each form submission gets its own instance
    // no risk of one user's data leaking into another's
}
```

### Give 3 Use Cases for Request Bean Scope

Request scope is commonly used for request-specific data such as request metadata. 

It can also be used for validation objects that only exist during one HTTP request. 

Another use case is temporary user input processing where the data is only needed while handling a single request.

**Request Metadata Storage:** Each HTTP request gets its own `RequestInfo` object, so request information is not shared between users.

```java
@RequestScope
@Component
public class RequestInfo {

    private String requestId;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
@RestController
public class RequestController {

    @Autowired
    private RequestInfo requestInfo;

    @GetMapping("/request")
    public String getRequest() {

        requestInfo.setRequestId(UUID.randomUUID().toString());

        return "Request ID: " + requestInfo.getRequestId();
    }
}
```

**Form Validation Processing: Validation results only exist during the current request and are automatically removed after the request is completed.**

```java
@RequestScope
@Component
public class ValidationResult {

    private boolean valid;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
@RestController
public class ValidationController {

    @Autowired
    private ValidationResult validationResult;

    @PostMapping("/validate")
    public String validate(@RequestParam String email) {

        validationResult.setValid(email.contains("@"));

        if(validationResult.isValid()){
            return "Email is valid";
        }

        return "Email is invalid";
    }
}
```

**Checkout or Order Processing: Order information is stored temporarily while processing a checkout request and is destroyed after the request finishes.**

```java
@RequestScope
@Component
public class CheckoutContext {

    private double totalAmount;

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
@RestController
public class CheckoutController {

    @Autowired
    private CheckoutContext checkoutContext;

    @PostMapping("/checkout")
    public String checkout() {

        checkoutContext.setTotalAmount(99.99);

        return "Checkout completed. Total Amount = $"
                + checkoutContext.getTotalAmount();
    }
}
```

### Give 3 Use Cases for Session Bean Scope

Session scope is useful when data is **specific to a logged-in user** and needs to persist across multiple HTTP requests during their session.

- **Logged-in User Info**

Store the current user's details after login — available across all requests in their session.

```java
@Component
@Scope("session")
public class LoggedInUser {
    private String username;
    private String role;
    private String email;

    // getters and setters
    // stays alive as long as the user is logged in
}
```

- **Shopping Cart**

Items added across multiple pages should persist until checkout or logout.

```java
@Component
@Scope("session")
public class ShoppingCart {
    private List<Item> items = new ArrayList<>();

    public void addItem(Item item) { items.add(item); }
    public void removeItem(Item item) { items.remove(item); }
    public List<Item> getItems() { return items; }
}
```

- **User Preferences： Store settings the user chose (language, theme) — should apply across all pages during their visit.**

```java
@Component
@Scope("session")
public class UserPreferences {
    private String language = "English";
    private String theme = "light";

    public void setLanguage(String language) { this.language = language; }
    public void setTheme(String theme) { this.theme = theme; }
}
```

### Session vs Cookie

A Session stores data on the server side. 

A Cookie stores data in the user's browser (client side). 

Sessions are generally more secure because sensitive information remains on the server. 

Cookies are lightweight and useful for remembering user preferences, but users can view or modify them. 

In many web applications, cookies are used to store a session identifier, while the actual session data remains on the server.