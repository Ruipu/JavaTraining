package Homework;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Homework1 {
   static void main(String[] args) {
       System.out.println("Homework1 and partial of Homework2");
   }
}


/* Qa. List and Set
A: List and Set are both collections that store objects in a particular order.
But List allows duplicates while Set does not allow duplicates.
List follows the insertion order, but Set does not.
 */
class ListSet{
    public static void main(String[] args) {
        List<String> list = new ArrayList();
         Set<String> set = new HashSet<>();
         list.add("Mike");
         list.add("Tom");
         list.add("Jerry");
         list.add("Tom"); //duplicates
         set.add("Mike");
         set.add("Tom");
         set.add("Jerry");
         set.add("Tom"); // wrong
         System.out.println(list);
         System.out.println(set);
    }
}

/*Qb. LikedList and ArrayList
A: LinkedList maintains the insertion order, because it uses double-linked list as the internal structure.
ArrayList does not follow the insertion order. It uses dynamic array as the internal structure.
LinkedList is quick in insertion and deletion, but slow in accessing by index.
ArrayList is on the contrary; it is quick in searching but slow in insertion and deletion.
 */
class ListTrying{
    static void main(String[] args){
        List<String> list = new ArrayList<>();
        list.add("Mike");
        list.add("Tom");
        list.add("Jerry");
        list.add("Amy");
        System.out.println(list);

        List<String> list2 = new LinkedList<>();
        list2.add("Amy");
        list2.add("Mike");
        list2.add("Jerry");
        list2.add("Tom");
        System.out.println(list2); //Maintain the insertion order.
    }
}

/*
Qc. What is a Map Interface?
A: Map stores data in key-value pairs.
 */

class MapInterface{
    static void main(String[] args){
        Map<String, Integer> map = new HashMap<>();
        map.put("Mike", 20);
        map.put("Tom", 25);
        map.put("Jerry", 30);
        System.out.println(map);
    }
}

/*
Qd. How does HashMap work?
A: HashMap converts the key into an index in the internal buckets.
When storing or retrieving the data, HashMap uses the HashCode to location the correct buckets.
 */

/*
Qe. What is Hash Collision?
A: Hash Collision happens when multiple key use the same HashCode and location the same buckets.
HashMap solves it using linked list or tree structure. It takes time.
 */

/*
Qf. What is "Collections" used for?
A: Collections is a utility class.
it is used for performing utility operations on collection objects such as sorting, searching, reversing, shuffling, and finding maximum or minimum values.
It provides static methods for working with collections like List, Set, and Map.
 */

class CollectionsDemo{
    static void main(String[] args){
        List<String> list = new ArrayList<>();
        list.add("Apple");
        list.add("Banana");
        list.add("Orange");
        list.add("Grape");
        list.add("Pineapple");
        System.out.println(list);
        Collections.sort(list);
        System.out.println(list);
        Collections.reverse(list);
        System.out.println(list);
        Collections.shuffle(list);
        System.out.println(list);
        int frequency = Collections.frequency(list, "Apple");
        System.out.println(frequency);
    }
}

/*
Qg. What is an immutable class?
A: Immutable class usually contains the objects that cannot be changed.
It usually use private or final modifiers to restrict the fields.
It does not have the setter method.
 */
class ImmutableClass{
    private final String name;
    private final int age;
    public ImmutableClass(String name, int age){
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}

class ImmutableClassDemo{
    static void main(String[] args){
        ImmutableClass immutableClass = new ImmutableClass("Mike", 20);
        System.out.println(immutableClass.getName());
        System.out.println(immutableClass.getAge());
        // immutableClass.name = "Bob"; Cannot modify because of immutale class.
    }
}



/*
Qh. HashMap, HashTable and ConcurrentHashMap
A: All the three have Map Interface and they all store data by key-value pair.
But they differ in thread safe and performance speed.
HashMap is not thread safe as it does not have synchronization. But also, it performs fast.
HashTable is thread safe as the whole table is locked by synchronization. Therefore, its performance is slow.
ConcurrentHashMap is partially locked by synchronization and is thread safe. It performs faster than HashTable.
 */
class MapDemo{
    static void main(String[] args){
        Map<String, Integer> map = new HashMap<>();
        map.put("Mike", 20);
        map.put("Tom", 25);
        map.put("Jerry", 30);
        System.out.println(map);
    }
    static void main2(String[] args){
        Map<String, Integer> map = new ConcurrentHashMap<>();
        map.put("Mike", 20);
        map.put("Tom", 25);
        map.put("Jerry", 30);
        System.out.println(map);
    }
    static void main3(String[] args){
        Map<String, Integer> map = new Hashtable<>();
        map.put("Mike", 20);
        map.put("Tom", 25);
        map.put("Jerry", 30);
        System.out.println(map);
    }
}

/*
Qi. String, StringBuilder and StringBuffer.
A: String is immutable, but StringBuilder and String Buffer are mutable.
String is thread safe and it uses String pool. But it is slow for modificaiton.
StringBuilder is mutable, but it is not thread safe. Modification in StringBuilder is fast.
StringBuffer is mutable and thread-safe. Modification is slower than StringBuilder.
 */
class StringDemo{
    static void main(String[] args){
        String str = "Tom";
        System.out.println(str);
        str = str.concat(" Jerry"); //Cannot directly str.concat.
        System.out.println(str);
    }
    static void main2(String[] args){
        StringBuilder str = new StringBuilder("Tom");
        str.append(" Jerry");
        System.out.println(str);
    }
    static void main3(String[] args){
        StringBuffer str = new StringBuffer("Tom");
        str.append(" Jerry");
        System.out.println(str);
    }
    static void main4(String[] args){
        String str = "Hello";
        str = str.replace("H", "J");
        System.out.println(str);
    }

}
/* Qj. why we need to override the hashcode and equals method at the same time
A: To avoid the Hash Collision.
HashMap uses HashCode to location the buckets of objects, and uses equals() to compare the objects.
If we do not override HashCode, though two objects are the same, they are in different buckets.
This leads to the failure to retrieve the data.
 */

// Qk. Play around the common data structure apis (map, set, queue, list), write some practice codes
class DataStructurePractice{
    static void runMap() {
        //Map
        Map<String, Integer> map = new HashMap<>();
        map.put("Mike", 20);
        map.put("Tom", 25);
        map.put("Jerry", 30);
        System.out.println(map);
        System.out.println(map.get("Tom"));
        System.out.println(map.containsKey("Mike"));
        System.out.println(map.containsValue(30));
        System.out.println(map.containsValue(70));
        map.remove("Tom");
        System.out.println(map);
    }
    static void runSet() {
        //Set
        Set<String> set = new HashSet<>();
        set.add("Mike");
        set.add("Tom");
        set.add("Jerry");
        System.out.println(set);
        System.out.println(set.contains("Tom"));
        System.out.println(set.size());
        System.out.println(set.isEmpty());
    }

    static void runQueue() {
        //Queue
        Queue<String> queue = new LinkedList<>();
        queue.add("Mike");
        queue.add("Tom");
        queue.add("Jerry");
        System.out.println(queue);
        System.out.println(queue.peek());
        System.out.println(queue.poll());
    }
    static void runList() {
        //List
        List<String> list = new ArrayList<>();
        list.add("Mike");
        list.add("Tom");
        list.add("Jerry");
        list.add("Gabe");
        list.add("Nick");
        System.out.println(list);
        System.out.println(list.get(4));
        System.out.println(list.size());
        System.out.println(list.isEmpty());
    }
    static void runLinkedList() {
          List<String> list2 = new LinkedList<>();
          list2.add("Mike");
          list2.add("Tom");
          list2.add("Jerry");
          list2.add("Gabe");
          list2.add("Nick");
          System.out.println(list2);
          System.out.println(list2.contains("Gabe"));
          System.out.println(list2.size());
          System.out.println(list2.isEmpty());
     }
     //run it one by one.
     static void main(String[] args) {
          //runMap();
          runSet();
          //runQueue();
          //runList();
          //runLinkedList();
      }
}

/*
Homework 2
Qa. String, StringBuilder and StringBuffer.
Already answered in Homework 1 i.
 */

/*
Qb. Comparator vs. Comparable, when to use which one?
Comparable is used for default/natural sorting inside the class
It uses compareTo(),
Comparator is used for custom sorting outside the class using compare().
 */

/*
Qc. Override and Overload?
A: Override means a subclass implements a new implementation of the method from the parent class.
Override is at the compile time.
Overload means the methods have the same signature but different parameters. Overload is at run time.
 */
class OverloadOverrideDemo{
   static class Boss{
       public String Name(){
           return "EmployeeName";
       }
   }
   static class Employee2 extends Boss{
       @Override
       public String Name(){
           return "Tom";
       }
   }
   // Overload.
   static class Calculation{
       public Integer calculate(Integer a, Integer b){
           return a + b;
       }
       public Integer calculate(Integer a, Integer b, Integer c){
           return a * b * c;
       }
       static void main(String[] args){
       Calculation calc = new Calculation();
       System.out.println(calc.calculate(1, 2));
       System.out.println(calc.calculate(1, 2, 3));
       }
   }
}

/*
Qe. Java 8 basic data types
A: byte, int, short, long, float, char, boolean, double
 */

/*
Qf Primitive Type and Reference Type.
A: Primitive types are basic data types: byte, int, short, long, float, char, boolean, double.
Reference types are objects: String, ArrayList, HashMap, and other customized classes.
Reference types point to the objects stored in heap memory.
 */


