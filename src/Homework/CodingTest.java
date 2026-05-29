package Homework;

import java.util.*;

public class CodingTest {
    static void main(String[] args){
        System.out.println("Coding test");
    }
}



// Coding exercise: given a random character array, find the char with third highest frequence
//input: [a, b, b, c, c, c], output: [a]

class Frequency {
    public static void stringFreq(String str) {
        Map<Character, Integer> map = new HashMap<>();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (map.containsKey(c)) {
                map.put(c, map.get(c) + 1);
            } else {
                map.put(c, 1);
            }
        }
        List<Integer> freqList = new ArrayList<>(map.values());
        freqList.sort(Collections.reverseOrder()); //Put all the frequency into a list.
        int thirdHighest = freqList.get(2); // The third high with reverseOrder.

        for(Map.Entry<Character, Integer> j: map.entrySet()){
            if(j.getValue() == thirdHighest){
                System.out.println(j.getKey());
            }
        }
    }
    public static void main(String[] args){
        String string = "abbccc";
        stringFreq(string);
    }
}

// Coding exercies: reverse a string
//input: “abc”, output: “cba”

class Reverse{
    public static List<String> reverseOrder(String str){
        List<String> list = new ArrayList<>();
        for(int i=0; i<str.length(); i++){
            list.add(String.valueOf(str.charAt(i)));
        }
        return list.reversed();
    }
    static void main(String[] args){
        String string = "abc";
        System.out.println(reverseOrder(string));
    }
}

//Code practice: given an integer array and target, return all the pairs sum to the target, each element can only be used once
//input: [1, 2, 3, 4] target = 5, return [[1, 4],[2, 3]]

class PairSum {
    public static List<List<Integer>> findPairs(int[] nums, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Set<Integer> seen = new HashSet<>();
        Set<Integer> used = new HashSet<>();
        for (int num : nums) {
            int complement = target - num;
            if (seen.contains(complement)
                    && !used.contains(num)
                    && !used.contains(complement)) {
                result.add(Arrays.asList(complement, num));
                used.add(num);
                used.add(complement);
            }
            seen.add(num);
        }
        return result;
    }
    public static void main(String[] args) {
        int[] nums = {1, 2, 3, 4};
        int target = 5;
        List<List<Integer>> result = findPairs(nums, target);
        System.out.println(result);
    }
}