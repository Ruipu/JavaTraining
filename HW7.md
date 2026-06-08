# Homework 7

### **Write out the optimized Singleton Version and explain each line of code**

The optimized Singleton pattern is usually implemented using **Double-Checked Locking** with `volatile`.

```java
public class Singleton {

    private static volatile Singleton instance;

    private Singleton() {
    }

    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
```

**Explanation:**

- `private static volatile Singleton instance;`
    - Holds the only instance of the class.
    - `static` means it belongs to the class itself.
    - `volatile` prevents instruction reordering and ensures thread visibility.
- `private Singleton() {}`
    - Private constructor prevents external code from creating objects using `new`.
- `public static Singleton getInstance()`
    - Provides a global access point to get the instance.
- `if (instance == null)`
    - Avoids locking every time after the instance has already been created.
- `synchronized (Singleton.class)`
    - Only one thread can enter this block at a time.
- `if (instance == null)`
    - Second check prevents multiple threads from creating multiple objects.
- `instance = new Singleton();`
    - Creates the object only once.
- `return instance;`
    - Returns the singleton object.

---

### **What is Reflection?**

Reflection in Java is the ability of a program to **inspect and manipulate its own structure at runtime** — such as reading classes, fields, methods, and annotations without knowing them at compile time. It is the core mechanism that powers most Java frameworks like Spring, Hibernate, JUnit, and Jackson, allowing them to read developer code dynamically at startup without knowing it in advance.

With Reflection, Java can:

- Discover class information dynamically.
- Access private fields and methods.
- Create objects without directly calling constructors.
- Invoke methods during runtime.

**How Spring Uses Reflection**

Spring is a framework built long before any developer writes their code. When a Spring application starts up, Spring has absolutely no idea what classes, fields, or methods the developer wrote. So Spring uses Reflection to **scan all classes at runtime**, read their annotations, and wire the application automatically based on what it finds.

```java
// Developer writes this:
@RestController
public class UserController {

    @Autowired
    UserService userService;
}

// Spring internally does THIS via Reflection at startup:
Class<?> clazz = Class.forName("UserController");

// Spring finds @RestController → registers as HTTP handler
Annotation[] annotations = clazz.getDeclaredAnnotations();

// Spring finds @Autowired → injects UserService automatically
Field field = clazz.getDeclaredField("userService");
field.setAccessible(true);
field.set(controllerInstance, new UserService()); // inject!
```

```
Developer writes @Autowired
Spring scans → finds @Autowired via Reflection
Spring injects UserService automatically
Developer never writes: userService = new UserService()!

```

**Who uses reflection?**

```
Spring      →  reads @Autowired, @RestController
               wires entire application automatically

Hibernate   →  reads @Entity, @Column
               maps your class to database table

JUnit       →  discovers @Test methods
               runs them automatically without you calling them

Jackson     →  reads your class fields
               converts object ↔ JSON automatically
```

### **What are HTTP Status Codes?**

### **200 OK**

The request was successful and the server returned the expected result.

Example:

- GET employee by id
- Employee found successfully

---

### **201 Created**

The request was successful and a new resource was created.

Example:

- POST create employee
- New employee record created

---

### **202 Accepted**

The server received the request and accepted it, but the processing has not finished yet.

Example:

- File upload task submitted
- Background processing continues

---

### **204 No Content**

The request was successful, but there is nothing to return.

Example:

- DELETE employee
- Employee deleted successfully

---

### **307 Temporary Redirect**

The resource is temporarily located at another URL.

The client should use the new URL for now but continue using the original URL in future requests.

---

### **301 Moved Permanently**

The resource has permanently moved to a new URL.

Future requests should use the new URL.

---

### **400 Bad Request**

The request format is invalid.

Examples:

- Missing required field
- Invalid JSON
- Wrong parameter format

---

### **401 Unauthorized**

Authentication is required.

The user has not logged in or has an invalid token.

---

### **403 Forbidden**

The user is authenticated but does not have permission.

Example:

- Normal user tries to access admin APIs

---

### **404 Not Found**

The requested resource does not exist.

Example:

- Employee ID does not exist

---

### **500 Internal Server Error**

Something unexpected happened on the server side.

Examples:

- NullPointerException
- Database connection failure
- Application bug

---

### **What is HTTP?**

HTTP (HyperText Transfer Protocol) is a communication protocol used between clients and servers on the web.

When a browser or application sends a request, HTTP defines:

- How the request is sent
- How the response is returned
- What status code should be used

It follows a Request → Response model.

HTTP, or HyperText Transfer Protocol, is the foundation of communication on the web — it is a **request-response protocol** that defines how a client and a server communicate with each other over a network. When a user opens a browser and visits a website, the browser sends an HTTP request to the server, and the server responds with an HTTP response containing the requested data such as HTML, JSON, or images.

An HTTP request consists of several key components — the **method** which defines the action (`GET` to retrieve data, `POST` to create, `PUT` to update, `DELETE` to remove), the **URL** which identifies the resource, the **headers** which carry metadata like content type and authorization tokens, and the **body** which carries the actual data for methods like `POST` and `PUT`.

An HTTP response similarly consists of a **status code** that tells the client what happened — `200 OK` for success, `201 Created` when a resource is created, `400 Bad Request` for invalid input, `401 Unauthorized` when authentication is missing, `404 Not Found` when the resource does not exist, and `500 Internal Server Error` when something crashes on the server — along with **headers** and a **body** containing the returned data.

HTTP is also **stateless**, meaning the server remembers nothing between requests — every request must carry all the information the server needs, such as an authentication token in the header, which is why technologies like JWT tokens and sessions exist to maintain state across multiple requests.

In modern applications, **HTTPS** is used instead of plain HTTP, which is the same protocol but with **SSL/TLS encryption** layered on top, ensuring that data transferred between client and server is encrypted and cannot be intercepted by a third party.

### **What are GET, POST, PUT, DELETE, and PATCH?**

### **GET**

Used to retrieve data from the server.

### **POST**

Used to create a new resource.

### **PUT**

Used to completely update an existing resource.

### **DELETE**

Used to remove a resource.

### **PATCH**

Used to partially update a resource. Only updates the fields provided.

### **POST vs PATCH**

**POST**

- Usually creates a new resource.
- Multiple calls may create multiple records.

### **POST vs PUT**

**POST**

- Creates a new resource.
- Multiple calls usually create multiple records.
- Not idempotent.

**PUT**

- Replaces the entire resource.
- Repeated requests produce the same result.
- Idempotent.

### **What is Idempotent? Which HTTP Methods are Idempotent?**

An operation is idempotent if performing it multiple times produces the same result as performing it once.

Examples:

- Update salary to 10000 once → salary is 10000
- Update salary to 10000 ten times → still 10000

**Idempotent Methods**

- GET
- PUT
- DELETE
- HEAD
- OPTIONS

**Not Idempotent**

- POST

**PATCH**

- Usually considered not guaranteed to be idempotent because behavior depends on implementation.