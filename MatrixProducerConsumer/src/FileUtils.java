import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.IntStream;

public class FileUtils {
    public static void saveMatrix(FileWriter writer, int[][] matrix) throws IOException {
//            for (int r = 0; r < size; r++) {
//                StringJoiner stringJoiner = new StringJoiner(", ");
//                for (int c = 0; c < size; c++) {
//                    stringJoiner.add(String.format("%.2f", matrix[r][c]));
//                }
//                writer.write(stringJoiner.toString());
//                writer.write('\n');
//            }
//            writer.write('\n');

        for (int[] row : matrix) {
            String rowString = IntStream.of(row)
                    .mapToObj(Integer::toString)
                    .reduce((a, b) -> a + "," + b)
                    .orElse("");

            writer.write(rowString);
            writer.write('\n');
        }

        writer.write('\n');
    }
}
