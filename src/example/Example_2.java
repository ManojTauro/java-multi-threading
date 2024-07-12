package example;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Example_2 {
    public static void main(String[] args) throws InterruptedException {
        List<Long> numbers = Arrays.asList(10000000L, 23L, 3456L, 9876L, 67878L, 36790L);

        List<FactorialThread> factorialThreads = new ArrayList<>();

        numbers.forEach(num -> factorialThreads.add(new FactorialThread(num)));

        factorialThreads.forEach(Thread::start);

        System.out.println("Before join");

        for (Thread t: factorialThreads) {
            t.join(3000);
            t.interrupt();
        }

        System.out.println("After join");

        for (int i = 0; i < numbers.size(); i++) {
            FactorialThread thread = factorialThreads.get(i);
            if (thread.isFinished())
                System.out.println("Factorial of "+numbers.get(i)+" is "+thread.getResult());
            else
                System.out.println("Calculation for "+numbers.get(i)+" is still in progress");
        }
    }

    private static class FactorialThread extends Thread {
        private BigInteger result = BigInteger.ONE;
        private final long inputNUmber;
        private boolean isFinished = false;

        public FactorialThread(long inputNUmber) {
            this.inputNUmber = inputNUmber;
        }

        @Override
        public void run() {
            this.result = factorial();
            this.isFinished = true;
        }

        private BigInteger factorial() {
            for (long i = this.inputNUmber; i > 0; i--) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Taking too long, so got interrupted");
                    return BigInteger.ZERO;
                }
                result = result.multiply(new BigInteger(Long.toString(i)));
            }


            return result;
        }

        public boolean isFinished() {
            return isFinished;
        }

        public BigInteger getResult() {
            return result;
        }
    }
}
