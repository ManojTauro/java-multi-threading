package exercise;

import java.math.BigInteger;

public class Exercise_2 {
    public static void main(String[] args) throws InterruptedException {
        BigInteger base1 = new BigInteger("250");
        BigInteger power1 = new BigInteger("1000");
        BigInteger base2 = new BigInteger("10000");
        BigInteger power2 = new BigInteger("500");

        System.out.println("Result of the calculation is "+calculateResult(base1, power1, base2, power2));
    }

    public static BigInteger calculateResult(BigInteger base1, BigInteger power1, BigInteger base2, BigInteger power2)  throws InterruptedException {
        BigInteger result;
        PowerCalculatingThread t1 = new PowerCalculatingThread(base1, power1);
        PowerCalculatingThread t2 = new PowerCalculatingThread(base2, power2);

        t1.start();
        t2.start();

        t2.join();
        t1.join();

        result = t1.getResult().add(t2.getResult());
        /*
            Calculate result = ( base1 ^ power1 ) + (base2 ^ power2).
            Where each calculation in (..) is calculated on a different thread
        */
        return result;
    }

    private static class PowerCalculatingThread extends Thread {
        private BigInteger result = BigInteger.ONE;
        private final BigInteger base;
        private final BigInteger power;

        public PowerCalculatingThread(BigInteger base, BigInteger power) {
            this.base = base;
            this.power = power;
        }

        @Override
        public void run() {
            this.result = base.pow(power.intValue());
        }

        public BigInteger getResult() { return result; }
    }
}