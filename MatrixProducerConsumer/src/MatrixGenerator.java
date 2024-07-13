import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.stream.IntStream;

public class MatrixGenerator {
    private static final String OUTPUT_FILE = "./out/matrices";
    private static final int size = 10;
    private static final int NUMBER_OF_MATRIX_PAIRS = 100000;
    private static Random random = new Random();

    public static void main(String[] args) throws IOException {
        File file = new File(OUTPUT_FILE);
        try (FileWriter writer = new FileWriter(file)) {
            createMatrices(writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createMatrices(FileWriter writer) throws IOException {
        for (int i = 0; i < NUMBER_OF_MATRIX_PAIRS; i++) {
            int[][] matrix = createMatrix();
            FileUtils.saveMatrix(writer, matrix);
        }
    }

    private static int[][] createMatrix() {
        return IntStream.range(0, size)
                .mapToObj(r -> createRow())
                .toArray(int[][]::new);

    }

    private static int[] createRow() {
        return random.ints(size, 0, 100).toArray();
    }
}
