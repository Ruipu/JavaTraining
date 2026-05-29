package Homework;

public class Homework3 {
    static void main(String[] args){
        System.out.println("Homework3");
    }
}

// Homework 3.

/*Qa. Java modifier scope: public, private, protected, default scope
public members can be accessed from anywhere, such as the same class, packages, subclass, and different packages
private members can only be accessed within the same class.
protected members can be accessed by subclasses and classes in the same package.
Default access allows visibility only inside the same package. Other packages are forbidden.
No keyword is used for default access.
 */

/*Qb.What is the static scope?
A: static keywords can be added before a variable, a method, a code block, and the Inner class.
Static members belong to the class, rather than the object. In the JVM, the static stores in the Method Area.
For a static code block, it executes only once and can be called anywhere within the class even without instantiation.
We always use static to set up the logic initially, which is mostly for initialization.
 */

/*
Qc. how does classloader work?
A: Frist, javac will compile the java file to the class file.
And then, JVM will load the classfile to the Runtime Memory by classloader.
It has three steps:
Loading: find and load the bytecode into the Method Area.
Linking: verifying, preparing, and resolving the bytecode
Initialization: assigning static variables their actual values.

Java has three built-in class loaders in a hierarchy:
Bootstrap (core Java classes),
Extension (JDK extensions),
and Application (your own classes)
They follow the Parent Delegation Model, meaning they always ask the parent class loader first before loading a class themselves.

*/

/* Qd.Describe the difference between unchecked and checked exceptions in Java.
Checked exception is the compile time exception. It is caught at the compile time and will stop the program if I do not handle it.
Exampls of checked exception is `IOException`, when the file does not exist.
Unchecked exceptions, also called runtime exception, happen at the run time. It is unpredicted.
On the other hand, checked exception is predicted and should be handled at the compile time.
 */

/*Qe. final, finally, finalize.
A: final is the keyword that can be added before a variable, a class, or a method.
This helps to prevent changing the variable, override the methods and inherite the class. It uses much in Immutable class.
finally is used to handel exceptions. When we use try-catch to handle the exceptions, `finally part` shows whether the exception exists or not.
finalize() is a method called by the Garbage Collector before an object is destroyed.
It was used for cleanup operations, but it is deprecated in modern Java.
 */

/*Qf
Try-with-resources is a feature introduced in Java 7.
It automatically closes resources after use.
It is used with resources that implement the AutoCloseable interface.
 */

/*Qg What is Runtime Exception, Describe it with an example.
A Runtime Exception is an unchecked exception that occurs during program execution.
 The compiler does not require it to be handled using try-catch or throws.

Common examples include NullPointerException, ArithmeticException, ArrayIndexOutOfBoundsException, and NumberFormatException.
 */

//Runtime exception example.
class ExceptionEG{
    static String name = null;
    public static void main(String[] args) {
        System.out.println(name.length());
    }
}
/*Qh. What is the difference between NoClassDefFoundError and ClassNotFoundException in Java
ClassNotFoundException is a checked exception, means that I need to handle it during compile. It occurs when the JVM tries to load a class dynamically and cannot find it in the classpath.
NoClassDefFoundError occurs when a class was available during compilation but cannot be found at runtime.

 */

/*
Qk. What is Generics in Java? What are the advantages of using Generics?
A: Generics allow classes, interfaces, and methods to work with different data types while providing type safety.
They help detect type errors at compile time and reduce the need for explicit type casting.
Generics also improve code reusability and readability.
 */

/*Qi.
How does Generics work in Java? What is type erasure?
A: Java Generics work through a process called type erasure.
During compilation, generic type information is removed and replaced with Object or the bounded type.
Because of type erasure, generic type information is not available at runtime.
 */



/*
Qm. What is the difference between List<? extends T> and List<? super T>?
List<? extends T>: When you only need to read data from the list, because it accepts subclasses of T.
List<? super T>: When you need to add objects into the list, because it accepts superclasses of T.
 */

/*
Qo. What is OOP?
Object-Oriented Programming (OOP) is a programming paradigm based on objects and classes.
It is used to organize code by modeling real-world entities with data and behaviors.
OOP helps improve code reusability, scalability, and maintainability.
There are four main principles of OOP:
* Encapsulation
* Inheritance
* Polymorphism
* Abstraction
How do you engage in each of the following?
- Encapsulation
    Encapsulation means hiding the internal details of an object and controlling access to its data through methods.
    It uses modifiers like public, private, protected or default to restrict the access.

- Inheritance
    Inheritance in Java is a mechanism that allows one class to acquire the properties and behaviors of another class.
    Two inheritance mechanism abstraction class and interface. In Java, one class can only extend one class, but interface supports multiple inheritance of interfaces. This is the key difference in Inheritance.

- Polymorphism
    Polymorphism means one method or object can have different behaviors depending on the context or object type.
    It uses override and overload. Overload is inner class behavior or two methods with different signature. Override is in between the class methods have the same signature, but different logics. Overload is resolved in the compile time. Override is resolved at the run time.

- Abstraction
    Abstraction is to hide the detailed implementations behind the templates.
    We have two options, abstraction class and interface. (Going back to when we talk about inheritance).
 */
