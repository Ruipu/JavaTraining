package Homework;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Homework4 {
    static void main(String[] args){
        System.out.println("Homework4");
    }
}
//Homework 4
/*Qa. what is functional interface?
A Functional Interface is an interface that contains exactly one abstract method.
It is commonly used with Lambda Expressions and method references.
Examples include Runnable, Callable, Predicate, Function, Supplier, and Consumer.
 */

/*Qb. What is a default method?
A default method is a method in an interface that has an implementation.
It was introduced in Java 8 to allow new functionality to be added to interfaces without breaking existing implementations.
 */

/*Qc.what is the difference between Predicate, Supplier, Consumer, Function?
Predicate takes one input and returns a boolean value. It is commonly used for filtering conditions.
Supplier does not take any input and returns a value.
Consumer takes one input and performs an operation without returning a result.
Function takes one input and returns a transformed result. It is commonly used in Stream API map() operations.
 */

//Qd. Demo of Predicate, Supplier, Consumer, and Function.
class FunctionalInterfaceDemo {
    public static void main(String[] args) {
        // Predicate<T>
        Predicate<Integer> isAdult =
                age -> age >= 18;
        System.out.println(
                isAdult.test(20));
        // Supplier<T>
        Supplier<String> supplier =
                () -> "Hello Java";
        System.out.println(
                supplier.get());
        // Consumer<T>
        Consumer<String> consumer =
                name -> System.out.println(
                        "Hello " + name);
        consumer.accept("Tom");
        // Function<T,R>
        Function<String, Integer> function =
                str -> str.length();
        System.out.println(
                function.apply("Java"));
    }

}

/*Qe. What is method reference?
A Method Reference is a shorthand syntax introduced in Java 8.
It allows a method to be referenced using the :: operator instead of writing a Lambda Expression.
It improves code readability and is commonly used with functional interfaces and the Stream API.
 */

/*Qf. What is CompletableFuture?
CompletableFuture is a Java 8 API used for asynchronous and non-blocking programming.
It allows time-consuming tasks, such as database queries or API calls, to run in background threads while the main thread continues doing other work.
It also provides methods such as thenApply(), thenCompose(), and thenCombine() to process results when they become available.
 */

/*Qg default keyword and java default scope.
A default method is a method in an interface that includes an implementation and is declared using the default keyword.
It was introduced in Java 8 to allow new methods to be added to interfaces without breaking existing classes that already implement those interfaces.
Implementing classes can use the default implementation directly or override it with their own implementation.
This feature improves backward compatibility and makes interface evolution easier.
On the other hand, default scope (package-private access) refers to the access level applied when no access modifier is specified.
 */

//Qh. Coding exercise
class Student {
    private String name;
    private int age;
    private int score;
    private String gender;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Student(String name, int age, int score, String gender) {
        this.name = name;
        this.age = age;
        this.score = score;
        this.gender = gender;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return age == student.age && score == student.score && Objects.equals(name, student.name) && Objects.equals(gender, student.gender);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age, score, gender);
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", score=" + score +
                ", gender='" + gender + '\'' +
                '}';
    }
}

class StudentStreamDemo {
    public static void main(String[] args) {
        List<Student> list = new ArrayList<>();
        list.add(new Student("Alice", 20, 90, "Girl"));
        list.add(new Student("Amy", 21, 75, "Girl"));
        list.add(new Student("Bob", 20, 55, "Boy"));
        list.add(new Student("Alex", 22, 60, "Boy"));
        list.add(new Student("Tom", 21, 80, "Boy"));

        //names starting with A
        List<String> namesStartingWithA =
                list.stream()
                        .map(Student::getName)
                        .filter(name -> name.startsWith("A"))
                        .collect(Collectors.toList());
        System.out.println(namesStartingWithA);
        // sum of all scores
        int totalScore =
                list.stream()
                        .mapToInt(Student::getScore)
                        .sum();
        System.out.println(totalScore);
        // students whose score >= 60
        List<Student> passedStudents =
                list.stream()
                        .filter(student -> student.getScore() >= 60)
                        .collect(Collectors.toList());
        passedStudents.forEach(student ->
                System.out.println(student.getName()));
        // retrieve all students' names
        List<String> allNames =
                list.stream()
                        .map(Student::getName)
                        .collect(Collectors.toList());
        System.out.println(allNames);
        //  count frequency of each age
        Map<Integer, Long> ageFrequency =
                list.stream()
                        .collect(Collectors.groupingBy(
                                Student::getAge,
                                Collectors.counting()
                        ));
        System.out.println(ageFrequency);
        // count number of boys and girls using groupingBy
        Map<String, Long> genderCountByGrouping =
                list.stream()
                        .collect(Collectors.groupingBy(
                                Student::getGender,
                                Collectors.counting()
                        ));
        System.out.println(genderCountByGrouping);
        // count number of boys and girls using Collectors.toMap()
        Map<String, Integer> genderCountByToMap =
                list.stream()
                        .collect(Collectors.toMap(
                                Student::getGender,
                                student -> 1,
                                Integer::sum
                        ));
        System.out.println(genderCountByToMap);
    }
}

/*Qi.intermediate operation vs terminal operation
In the Stream API, intermediate operations transform or filter the stream and return another stream, allowing multiple operations to be chained together.
Terminal operations produce the final result or side effect and terminate the stream processing.
A stream can have multiple intermediate operations but only one terminal operation.
 */

/*Qk.Stream API, map() vs. flatmap()
map() is used to transform each element in a stream into another value, resulting in a one-to-one mapping.
flatMap() is used when each element may produce multiple values, and it flattens those values into a single stream.
In short, map() transforms elements, while flatMap() transforms and flattens nested structures.
 */

//Qj Coding: given a char array, use stream api to count the frequency of each char.

class CharFrequency {
    public static void main(String[] args) {
        char[] chars =
                {'a','b','a','c','b','a'};
        Map<Character, Long> result =
                new String(chars)
                        .chars()
                        .mapToObj(c -> (char) c)
                        .collect(Collectors.groupingBy(
                                Function.identity(),
                                Collectors.counting()
                        ));
        System.out.println(result);
    }
}