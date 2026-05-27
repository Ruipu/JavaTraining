import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
        System.out.println("This is a test");
    }
}


// Student class
class Student implements Comparable<Student> {
    String name;
    int age;
    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }
    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    // Comparable -> default sorting by age
    @Override
    public int compareTo(Student other){
        return this.age - other.age;
    }
    @Override
    public String toString(){
        return name + " : " + age;
    }
}
// Comparator -> custom sorting by name
class NameComparator implements Comparator<Student>{
    @Override
    public int compare(Student s1, Student s2){
        return s1.name.compareTo(s2.name);
    }
}
class Run {
    public static void main(String[] args){
        List<Student> students = new ArrayList<>();
        students.add(new Student("Tom", 22));
        students.add(new Student("Jerry", 18));
        students.add(new Student("Mike", 25));

        // Comparable sorting (default sorting by age)
        Collections.sort(students);

        System.out.println("Sort by age:");
        System.out.println(students);

        // Comparator sorting (custom sorting by name)
        Collections.sort(students, new NameComparator());

        System.out.println("Sort by name:");
        System.out.println(students);
    }
}