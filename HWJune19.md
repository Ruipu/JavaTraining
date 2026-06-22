# Homework

### What is the Strategy Pattern?

**The Strategy Pattern is a behavioral design pattern.** 

*A design pattern is not a keyword, a class, or a library. It is a reusable, language-independent way of organizing code to solve a recurring problem. You will never find a built-in type literally called `Strategy`; instead, you build the pattern yourself using ordinary interfaces and classes that you already know.*

**In concrete terms, the Strategy Pattern defines a family of interchangeable algorithms, puts each algorithm in its own class, and makes them swappable at runtime — all without forcing the code that *uses* the algorithm to change.** 

The object that needs the work done holds a reference to an interface, not to any specific implementation, so it can be handed any algorithm that satisfies that interface.

**Key characteristics:**

- It belongs to the **behavioral family of patterns** (patterns concerned with how objects interact and distribute responsibility), not the *creational* or *structural* families.
- It is a **design convention**, not a language feature — it is assembled from plain interfaces and classes.
- It favors **composition over inheritance**: behavior is plugged in via a held object, rather than baked in through subclassing.
- It lets you select an algorithm **at runtime**, and even change it while the program is running.

---

### The three roles

The pattern is built from exactly three parts:

- **Strategy (the interface)** — declares the method signature that every algorithm must provide (for example, `pay(amount)`). This is the contract.
- **Concrete Strategies (the implementations)** — one class per algorithm, each implementing the Strategy interface with its own logic. Because they share the interface, they are interchangeable.
- **Context (the user)** — the class that needs the work done. It holds a reference of the *interface* type and calls the method through it. The Context does not know, and does not care, which concrete strategy it currently holds; it only depends on the interface.

The Context **delegates** the actual work to whichever strategy it currently holds, and the concrete strategies **implement** the interface.

---

### What problem it solves

Without the pattern, code that supports several variants of an operation tends to collect a large conditional block (`if-else` or `switch`) that must be edited every time a new variant is added. That violates the **Open/Closed Principle** — code should be open for extension but closed for modification.

The Strategy Pattern fixes this:

- **Adding a new behavior** means writing one new class that implements the interface. The Context is never touched.
- **Removing the conditional** — the branching logic disappears; the right strategy is simply plugged in.
- **Isolation** — each algorithm lives in its own class, so it can be understood, tested, and changed on its own.

---

### When to use it, Example

- You have several variants of the same operation and want to choose one at runtime.
    - Payment methods: credit card / PayPal / crypto
    - Sorting algorithms: quicksort / mergesort
    - Compression: zip / gzip
    - Route planning: fastest / shortest / avoid tolls
    - Pricing or discount rules
- You see a growing `if-else` / `switch` that selects between behaviors.
- You want to swap an algorithm without modifying the class that uses it.

---

### Trade-offs

- Introduces **more classes** — every algorithm becomes its own type, which can feel heavy for just two simple cases.
- The client (or some configuring code) must **know the strategies exist** in order to pick and inject the right one.
- For trivial differences, a plain conditional may be simpler and clearer.

---

### Code example (Java)

```java
// 1. The Strategy interface — the contract every algorithm follows
interface PaymentStrategy {
    void pay(int amount);
}

// 2. Concrete Strategies — interchangeable implementations
class CreditCardPayment implements PaymentStrategy {
    public void pay(int amount) {
        System.out.println("Paid " + amount + " by credit card");
    }
}

class PayPalPayment implements PaymentStrategy {
    public void pay(int amount) {
        System.out.println("Paid " + amount + " via PayPal");
    }
}

class CryptoPayment implements PaymentStrategy {
    public void pay(int amount) {
        System.out.println("Paid " + amount + " in crypto");
    }
}

// 3. The Context — holds a Strategy reference and delegates to it
class ShoppingCart {
    private PaymentStrategy strategy;          // depends on the interface only

    public void setStrategy(PaymentStrategy strategy) {
        this.strategy = strategy;              // inject any concrete strategy
    }

    public void checkout(int amount) {
        strategy.pay(amount);                  // delegate — doesn't know which one
    }
}

// 4. Usage — choose and swap the algorithm at runtime
public class Demo {
    public static void main(String[] args) {
        ShoppingCart cart = new ShoppingCart();

        cart.setStrategy(new CreditCardPayment());
        cart.checkout(100);   // Paid 100 by credit card

        cart.setStrategy(new PayPalPayment());   // swap algorithm — cart code unchanged
        cart.checkout(50);    // Paid 50 via PayPal
    }
}
```

Adding a new payment method (e.g. `ApplePayPayment`) means writing one new class that implements `PaymentStrategy`. The `ShoppingCart` class never changes — that is the whole point of the pattern.

### Singleton: Concepts, Interview Answers, and Coding.

The Singleton Pattern is a **creational design pattern**. It is to guarantee that a class has **exactly one instance**, and to give the whole program one global access point to that instance.

---

**Key characteristics**

- Belongs to the *creational* family (patterns about how objects are created).
- The class itself controls its own instantiation — the constructor is made **private** so no one else can call `new`.
- Exposes a `static` method (commonly `getInstance()`) that returns the one shared instance.
- The instance is **cached** after first creation; every later call returns that same object.

---

**Lazy vs. Eager initialization**

- **Lazy** — the instance is created on the *first* call to `getInstance()`. Saves resources if it's never used, but the first call pays the creation cost.
- **Eager** — the instance is created when the class loads, before anyone asks. Simple and inherently thread-safe, but the object is built even if never used.

---

**The multithreading problem**

In a single thread, a simple `if (instance == null) instance = new X();` is enough. With multiple threads, two threads can pass the `null` check at the same time and each create an instance — breaking the "only one" guarantee. Solutions:

- **`synchronized` method** — correct but slow; *every* call acquires a lock, even though almost all calls just read an already-created instance.
- **Double-checked locking** — check `null` *outside* the lock (fast path), and again *inside* the lock (correctness). Requires the field to be `volatile` so other threads never see a half-constructed object due to instruction reordering.
    - **First `null` check (outside the lock) — for performance.** Once the instance has been created, every later call returns it right here without ever entering the lock, avoiding the cost of acquiring `synchronized` on the 99.9% of calls that only read an already-existing instance.
    - **Second `null` check (inside the lock) — for correctness.** If two threads both pass the first check before either creates the instance, this check stops the second thread from creating a duplicate: by the time it acquires the lock, `instance` is already set, so it skips creation.
- **Modern alternatives** — static holder idiom, or an `enum` singleton in Java; these are simpler and thread-safe by construction, so new code usually prefers them over hand-written double-checked locking.

---

**When to use itv**

- Database connection pools
- Global configuration objects
- Loggers
- A shared cache or an event bus

**Trade-offs**

- Introduces **global state**, which makes code harder to test and reason about.
- Hides dependencies — a class secretly reaching for `getInstance()` is harder to mock than one that receives the object explicitly.
- Often considered an *anti-pattern* when overused; dependency injection is frequently a cleaner alternative.

---

**Code (Java — thread-safe double-checked locking)**

```java
class Database {
    private static volatile Database instance;   // volatile is required

    private Database() { }                        // private — blocks `new` elsewhere

    public static Database getInstance() {
        if (instance == null) {                   // 1st check: no lock, fast path
            synchronized (Database.class) {
                if (instance == null) {           // 2nd check: inside the lock
                    instance = new Database();
                }
            }
        }
        return instance;                          // always the same object
    }
}
```