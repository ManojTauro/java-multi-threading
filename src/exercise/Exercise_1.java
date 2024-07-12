package exercise;

import java.util.ArrayList;
import java.util.List;

public class Exercise_1 {
    public static void main(String[] args) {
        List<Runnable> tasks = new ArrayList<>();

        tasks.add(new Thread1());
        tasks.add(new Thread2());

        MultiExecutor ex = new MultiExecutor(tasks);

        ex.executeAll();
    }
}

class MultiExecutor {

    // Add any necessary member variables here
    List<Runnable> tasks;

    /*
     * @param tasks to executed concurrently
     */
    public MultiExecutor(List<Runnable> tasks) {
       this.tasks = tasks;
    }

    /**
     * Starts and executes all the tasks concurrently
     */
    public void executeAll() {
        List<Thread> threads = this.tasks.stream().map(Thread::new).toList();

        threads.forEach(Thread::start);
    }
}

class Thread1 implements Runnable {
    public void run() {
        System.out.println("Running task "+Thread.currentThread().getName());
    }
}

class Thread2 implements Runnable {
    public void run() {
        System.out.println("Running task "+Thread.currentThread().getName());
    }
}