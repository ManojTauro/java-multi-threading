package example;

import java.math.BigInteger;

public class Example_1 {
    public static void main(String[] args) {
        Thread blockingThread = new Thread(new BlockingTask());
//        blockingThread.start();
//        blockingThread.interrupt();

        Thread longRunningTask = new Thread(new LongRunningTask(new BigInteger("20000"), new BigInteger("1000000000")));
        longRunningTask.start();
        longRunningTask.interrupt();
    }

    private static class BlockingTask implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(50000000);
            } catch (InterruptedException e) {
                throw new RuntimeException("Thread got interrupted");
            }
        }
    }

    private static class LongRunningTask implements Runnable {
        private final BigInteger base;
        private final BigInteger power;

        public LongRunningTask(BigInteger base, BigInteger power) {
            this.base = base;
            this.power = power;
        }

        @Override
        public void run() {
            System.out.println(this.base + "^" + this.power + " = " + this.pow(this.base, this.power));
        }

        private BigInteger pow(BigInteger base, BigInteger power) {
            BigInteger result = BigInteger.ONE;

            for (BigInteger i = BigInteger.ZERO; i.compareTo(power) != 0; i = i.add(BigInteger.ONE)) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Interrupted earlier");
                    return BigInteger.ZERO;
                }
                result = result.multiply(base);
            }
            return result;
        }
    }
}
