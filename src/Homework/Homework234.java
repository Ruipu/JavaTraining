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

 */

/*Qj. young/old/perm generation

 */

/*Qk.difference types of GC


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

 */
