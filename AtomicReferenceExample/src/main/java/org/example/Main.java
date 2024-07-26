package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws InterruptedException {
//        StandardStack<Integer> stack = new StandardStack<>();
        LockFreeStack<Integer> stack = new LockFreeStack<>();
        Random random = new Random();

        IntStream.range(1, 100000).forEach(v -> stack.push(random.nextInt()));

        List<Thread> threads = new ArrayList<>();

        int pushThreads = 2;
        int popThreads = 2;

        for (int i = 0; i < pushThreads; i++) {
            Thread thread = new Thread(() -> {
                while (true) {
                    stack.push(random.nextInt());
                }
            });

            thread.setDaemon(true);
            threads.add(thread);
        }

        for (int i = 0; i < popThreads; i++) {
            Thread thread = new Thread(() -> {
                while (true) {
                    stack.pop();
                }
            });

            thread.setDaemon(true);
            threads.add(thread);
        }

        threads.forEach(Thread::start);

        Thread.sleep(10000);

        System.out.printf("%,d operations were performed in 10 seconds %n", stack.getCounter());
    }
}