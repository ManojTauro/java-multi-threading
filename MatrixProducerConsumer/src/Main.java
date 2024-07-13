import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Main {
    private static final String INPUT = "./out/matrices";
    private static final String OUTPUT = "./out/matrix_multiplication_results.txt";
    private static final int size = 10;

    public static void main(String[] args) throws IOException {
        ThreadSafeQueue queue = new ThreadSafeQueue();
        File input = new File(INPUT);
        File output = new File(OUTPUT);

        MatrixReaderProducer producer = new MatrixReaderProducer(queue, new FileReader(input));
        MatrixMultiplierConsumer consumer = new MatrixMultiplierConsumer(queue, new FileWriter(output));

        producer.start();
        consumer.start();
    }

    private static class MatrixMultiplierConsumer extends Thread {
        private final ThreadSafeQueue queue;
        private final FileWriter writer;

        public MatrixMultiplierConsumer(ThreadSafeQueue queue, FileWriter writer) {
            this.queue = queue;
            this.writer = writer;
        }

        @Override
        public void run() {
            while (true) {
                MatrixPair pair = queue.remove();

                if (pair == null) {
                    System.out.println("No more matrices to read from the queue, consumer is terminating...");
                    break;
                }

                int[][] result = multiplyMatrices(pair.matrix1, pair.matrix2);

                try {
                    FileUtils.saveMatrix(writer, result);
                } catch (Exception ignored) {
                }
            }

            try {
                writer.flush();
                writer.close();
            } catch (Exception ignored) {
            }
        }

        private int[][] multiplyMatrices(int[][] m1, int[][] m2) {
            int[][] result = new int[size][size];
            for (int r = 0; r < size; r++) {
                for (int c = 0; c < size; c++) {
                    for (int k = 0; k < size; k++) {
                        result[r][c] += m1[r][k] * m2[k][c];
                    }
                }
            }
            return result;
        }
    }

    private static class MatrixReaderProducer extends Thread {
        private Scanner scanner;
        private ThreadSafeQueue queue;

        public MatrixReaderProducer(ThreadSafeQueue queue, FileReader reader) {
            this.queue = queue;
            this.scanner = new Scanner(reader);
        }

        @Override
        public void run() {
            while (true) {
                int[][] matrix1 = readMatrix();
                int[][] matrix2 = readMatrix();

                if (matrix1 == null || matrix2 == null) {
                    queue.terminate();
                    System.out.println("No more matrices to read. Producer Thread is terminating...");
                    return;
                }

                MatrixPair pair = new MatrixPair(matrix1, matrix2);
                queue.add(pair);
            }

        }

        private int[][] readMatrix() {
//            List<String> lines = Files.lines(Paths.get(INPUT)).toList();
//
//            return lines.stream()
//                    .map(line -> line.split(","))
//                    .map(row ->
//                            Arrays.stream(row).mapToInt(Integer::parseInt)
//                                    .toArray())
//                    .toArray(int[][]::new);

            int[][] matrix = new int[size][size];
            for (int r = 0; r < size; r++) {
                if (!scanner.hasNext()) {
                    return null;
                }
                String[] line = scanner.nextLine().split(",");
                for (int c = 0; c < size; c++) {
                    matrix[r][c] = Integer.parseInt(line[c]);
                }
            }
            scanner.nextLine();
            return matrix;
        }
    }

    private static class ThreadSafeQueue {
        private final Queue<MatrixPair> queue = new LinkedList<>();
        private boolean isEmpty = true;
        private boolean isTerminated = false;
        private static final int CAPACITY = 10;

        public synchronized void add(MatrixPair pair) {
            while (queue.size() == CAPACITY) {
                try {
                    wait();
                }
                catch (InterruptedException ignored){}
            }

            queue.add(pair);
            isEmpty = false;
            notify();
        }

        public synchronized MatrixPair remove() {
            while (isEmpty && !isTerminated) {
                try {
                    wait();
                } catch (Exception ignored) {}
            }

            if (queue.size() == 1) isEmpty = true;
            if (queue.isEmpty() && isTerminated) return null;

            System.out.println("Queue size "+queue.size());

            MatrixPair pair = queue.remove();

            if (queue.size() < CAPACITY) notifyAll();

            return pair;
        }

        public synchronized void terminate() {
            isTerminated = true;
            notifyAll();
        }
    }

    private static class MatrixPair {
        private final int[][] matrix1;
        private final int[][] matrix2;

        public MatrixPair(int[][] matrix1, int[][] matrix2) {
            this.matrix1 = matrix1;
            this.matrix2 = matrix2;
        }
    }
}