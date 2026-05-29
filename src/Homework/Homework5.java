package Homework;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.*;

public class Homework5 {
    static void main(String[] args){
        System.out.println("Homework 5");
    }
}
/*Qa. how to create a thread( 4 ways, write code)
First, I extend the Thread class and override the run() method.
Second, I implement the Runnable interface and pass it to a Thread object.
Third, I use a lambda expression with Runnable (Java 8+).
Forth, I use Callable and FutureTask when a task needs to return a result
*/

//Example code of creating a thread.

class ThreadExamples {
    // 1. Extend Thread
    static class MyThread extends Thread {
        @Override
        public void run() {
            System.out.println("Thread Class");
        }
    }
    // 2. Implement Runnable
    static class MyRunnable implements Runnable {
        @Override
        public void run() {
            System.out.println("Runnable Interface");
        }
    }
    public static void main(String[] args) throws Exception {
        // Method 1
        MyThread t1 = new MyThread();
        t1.start();

        // Method 2
        Thread t2 = new Thread(new MyRunnable());
        t2.start();

        // Method 3 (Lambda)
        Thread t3 = new Thread(() ->
                System.out.println("Lambda Runnable"));
        t3.start();

        // Method 4 (Callable)
        Callable<String> callable =
                () -> "Callable Result";
        FutureTask<String> futureTask =
                new FutureTask<>(callable);
        Thread t4 = new Thread(futureTask);
        t4.start();
        System.out.println(futureTask.get());
    }
}
/* Qb. thread lifecycle, how does thread transfer from one state to another
A thread starts in the NEW state.
After calling start(), it enters RUNNABLE.
When the CPU schedules it, it begins running.
It may enter BLOCKED while waiting for a lock, WAITING when calling wait() or join(), or TIMED_WAITING when calling sleep().
After the run() method completes, the thread enters the TERMINATED state.
*/

/*Qc.how does thread pool work?
A Thread Pool is a collection of pre-created worker threads managed by the Executor Framework.
Instead of creating a new thread for every task, tasks are submitted to the thread pool, which assigns them to available worker threads.
When a task finishes, the thread is returned to the pool and reused for future tasks.
Using a thread pool improves performance, reduces thread creation overhead, and provides better resource management.
 */

/*Qd.what is the potential problem for the newCachedThreadPool and newFixedThreadPool and why

A: newCachedThreadPool() can create an unlimited number of threads.
If tasks arrive faster than they complete, the pool may create too many threads,
leading to excessive memory usage, CPU overhead, or even OutOfMemoryError.
newFixedThreadPool() uses a fixed number of threads, but its task queue is unbounded.
If tasks are submitted faster than they are processed, the queue can grow indefinitely and consume large amounts of memory.
 */

/*Qe. What is Feature?
Future is an interface in Java.
It represents the result of an asynchronous computation.
It allows a task to run in a separate thread and provides methods to check its status or retrieve the result later.
 */

/*Qf. what is CompletableFuture
A: CompletableFuture is an enhanced implementation of Future introduced in Java 8.
It supports asynchronous programming, task chaining, combining multiple tasks, and non-blocking execution.
 */

/*Qg.Future vs. CompletableFuture
Future provides basic support for asynchronous computation and retrieving results, but it has limited functionality and often requires blocking calls.
CompletableFuture extends Future and supports task chaining, combining multiple asynchronous operations, exception handling, and non-blocking execution.
 */

/*Qh. Lock vs. Synchronized
Both Lock and synchronized are used for thread synchronization and protecting shared resources from concurrent access.
The synchronized keyword is built into Java and automatically acquires and releases the monitor lock.
Lock (such as ReentrantLock) is more flexible and provides advanced features like tryLock(), lockInterruptibly(), and fairness policies.
 */

/*Qi. what is wait(), notify(), notifyAll(), join()
wait() puts the current thread into the WAITING state and releases the monitor lock.
notify() wakes one waiting thread, while notifyAll() wakes all waiting threads waiting on the same monitor.
join() causes one thread to wait until another thread finishes execution.
 */

//Qj, demo code

class CompletableFutureApiDemo {
    public static void main(String[] args) {
        // 1. runAsync(): no return value
        CompletableFuture<Void> runAsyncDemo =
                CompletableFuture.runAsync(() -> {
                    System.out.println("runAsync: task running");
                });
        runAsyncDemo.join();
        // 2. supplyAsync(): has return value
        CompletableFuture<String> supplyAsyncDemo =
                CompletableFuture.supplyAsync(() -> {
                    return "Hello";
                });
        System.out.println("supplyAsync: " + supplyAsyncDemo.join());
        // 3. thenApply(): transform result
        CompletableFuture<String> thenApplyDemo =
                CompletableFuture.supplyAsync(() -> "Java")
                        .thenApply(result -> result + " CompletableFuture");
        System.out.println("thenApply: " + thenApplyDemo.join());


        // 4. thenApplyAsync(): transform result asynchronously
        CompletableFuture<String> thenApplyAsyncDemo =
                CompletableFuture.supplyAsync(() -> "Async")
                        .thenApplyAsync(result -> result + " Processing");
        System.out.println("thenApplyAsync: " + thenApplyAsyncDemo.join());


        // 5. exceptionally(): handle exception and return fallback value
        CompletableFuture<String> exceptionallyDemo =
                CompletableFuture.supplyAsync(() -> {
                    int x = 10 / 0;
                    return "Success";
                }).exceptionally(ex -> {
                    return "Fallback value after exception";
                });
        System.out.println("exceptionally: " + exceptionallyDemo.join());


        // 6. handle(): handle both success and failure
        CompletableFuture<String> handleDemo =
                CompletableFuture.supplyAsync(() -> {
                    int x = 10 / 0;
                    return "Success";
                }).handle((result, ex) -> {
                    if (ex != null) {
                        return "Handled exception";
                    } else {
                        return result;
                    }
                });
        System.out.println("handle: " + handleDemo.join());


        // 7. thenCompose(): chain dependent async tasks
        CompletableFuture<String> thenComposeDemo =
                CompletableFuture.supplyAsync(() -> "User ID: 100")
                        .thenCompose(userId -> CompletableFuture.supplyAsync(() ->
                                userId + " -> User Name: Tom"
                        ));
        System.out.println("thenCompose: " + thenComposeDemo.join());


        // 8. thenCombine(): combine two independent async tasks
        CompletableFuture<String> future1 =
                CompletableFuture.supplyAsync(() -> "Hello");

        CompletableFuture<String> future2 =
                CompletableFuture.supplyAsync(() -> "World");

        CompletableFuture<String> thenCombineDemo =
                future1.thenCombine(future2, (a, b) -> a + " " + b);

        System.out.println("thenCombine: " + thenCombineDemo.join());


        // 9. allOf(): wait for all futures to finish
        CompletableFuture<String> task1 =
                CompletableFuture.supplyAsync(() -> "Task 1");

        CompletableFuture<String> task2 =
                CompletableFuture.supplyAsync(() -> "Task 2");

        CompletableFuture<String> task3 =
                CompletableFuture.supplyAsync(() -> "Task 3");

        CompletableFuture<Void> allOfDemo =
                CompletableFuture.allOf(task1, task2, task3);

        allOfDemo.join();

        System.out.println("allOf: "
                + task1.join() + ", "
                + task2.join() + ", "
                + task3.join());


        // 10. anyOf(): return when any one future finishes
        CompletableFuture<String> slowTask =
                CompletableFuture.supplyAsync(() -> {
                    sleep(2000);
                    return "Slow Task";
                });

        CompletableFuture<String> fastTask =
                CompletableFuture.supplyAsync(() -> {
                    sleep(500);
                    return "Fast Task";
                });

        CompletableFuture<Object> anyOfDemo =
                CompletableFuture.anyOf(slowTask, fastTask);

        System.out.println("anyOf: " + anyOfDemo.join());


        // 11. thenAccept(): consume result, no return value
        CompletableFuture<Void> thenAcceptDemo =
                CompletableFuture.supplyAsync(() -> "Data")
                        .thenAccept(result -> {
                            System.out.println("thenAccept: received " + result);
                        });

        thenAcceptDemo.join();


        // 12. thenRun(): run next task without using previous result
        CompletableFuture<Void> thenRunDemo =
                CompletableFuture.supplyAsync(() -> "Previous Result")
                        .thenRun(() -> {
                            System.out.println("thenRun: next task running");
                        });

        thenRunDemo.join();
    }

    private static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

