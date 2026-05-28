package Homework;

public class Homework234 {
    static void main(String[] args){
        System.out.println("Homework2(Partial),3,4 and coding test");
    }
}

/*Homework2
Qd. JRE vs. JDK vs. JVM
A: JDK is Java Development Kit. It stores the toolkid for developers to write, compile and run the programs.
JVM stands for Java Virtual Machine. It is the place of running the Java programs. It convert the java file to the class file.
JVM makes Java program to "run anywhere". It contains the five memory parts, method area, heap, VM stack, PC register, and the native method stack.
JRE is Java Runtime Environment.
It contains everything needed to run a Java program, like the JVM and standard libraries.
But unlike JDK it has no compiler, so you can run Java programs but cannot write or compile new ones.
JDK>JVM>JRE
 */

/*Qg How does JVM work?
A: When we have the Java file, javac converts (compiles) the Java file to the class file.
Then the JVM loads the class file. Once loaded, the JVM runs the bytecode inside the Runtime Memory Are.
The Runtime Memory Area contains the Method Area (stores class blueprints and static fields),
the Heap (stores all objects created by new),
the VM Stack (stores method calls, local variables, and references),
the PC Register (tracks which instruction each thread is currently executing),
and the Native Method Stack (handles C/C++ native method calls).
Finally, the Execution Engine translates the bytecode into machine code that the operating system can actually run.
 */

/*
Qh. JVM memory data model.
JVM Runtime Memory Area contains five layers.
the Method Area: it stores the class and static fields.
the Heap: it stores the objects.
the VM Stack: it stores the method calls, local variables and reference.
the PC register: it tracks the instructions each thread is currently executing.
the Native Method Stack is just handles the old C/C++ native method calls.
 */

/*Qi. How does GC work?
Garbage Collection is Java's automatic memory management system that works entirely in the Heap.
The core idea is: if no reference points to an object anymore, that object is considered garbage and will be cleaned up.
GC works in three steps:
first it marks all objects that are still referenced,
then it sweeps away all unmarked objects,
and finally it compacts the remaining memory to fill the empty gaps.
 */

/*Qj. young/old/perm generation
The Young Generation is where all newly created objects start.
They are born in Eden Space, and if they survive a GC cycle they move to Survivor spaces (S0 and S1).
If an object survives enough GC cycles in the Young Generation, it gets promoted to the Old Generation
The Old Generation (also called Tenured Generation) stores long-living objects like cached data, static-like objects, or application-level objects that stick around for the lifetime of the application.
Finally, the Perm Generation (PermGen) sits outside the Heap and stores class metadata, static variables, and method bytecode, essentially everything in the Method Area.
However, PermGen was removed in Java 8 and replaced by Metaspace, which grows dynamically using native memory instead of a fixed JVM memory size, solving the common OutOfMemoryError: PermGen space problem that older Java versions frequently
 */

/*Qk.difference types of GC
The Serial GC uses a single thread to perform GC, freezing the entire application while it runs (called "Stop the World"), making it only suitable for small single-threaded applications.
The Parallel GC (also called Throughput GC) is similar but uses multiple threads to perform GC, still stopping the application but doing it faster — it was the default GC before Java 9 and is best for applications that prioritize overall throughput.
The CMS (Concurrent Mark Sweep) GC was designed to minimize pause times by doing most of its work concurrently alongside the application, but it was deprecated in Java 9 and removed in Java 14 due to high CPU usage and memory fragmentation issues.
The G1 (Garbage First) GC became the default from Java 9 onwards — it divides the Heap into small equal-sized regions instead of fixed generations, allowing it to predict and control pause times more efficiently, making it ideal for large heap applications.
The ZGC and Shenandoah GC are the most modern collectors designed for ultra-low pause times of under 10 milliseconds even on very large heaps, making them ideal for latency-sensitive applications.
 */

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
