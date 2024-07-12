package imageprocessing;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageProcessing {
    private static final String INPUT_FILE = "./resources/many-flowers.jpg";
    private static final String OUTPUT_FILE = "./out/many-flowers.jpg";

    public static void main(String[] args) throws IOException {
        BufferedImage orgImage = ImageIO.read(new File(INPUT_FILE));
        BufferedImage outImage = new BufferedImage(orgImage.getWidth(), orgImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        long start = System.currentTimeMillis();

//        recolorSingleThread(orgImage, outImage);

        recolorMultiThreaded(orgImage, outImage, 3);

        long end = System.currentTimeMillis();

        System.out.println("Duration "+(end - start));

        File outputImage = new File(OUTPUT_FILE);
        ImageIO.write(outImage, "jpg", outputImage);
    }

    private static void recolorMultiThreaded(BufferedImage orgImage, BufferedImage outImage, int numThreads) {
        List<Thread> workers = new ArrayList<>();
        int width = orgImage.getWidth();
        int height = orgImage.getHeight() / numThreads;

        for (int i = 0; i < numThreads; i++) {
            final int threadMultiplier = i;

            Thread thread = new Thread(() -> {
                int xOrigin = 0 ;
                int yOrigin = height * threadMultiplier;

                recolorImage(orgImage, outImage, xOrigin, yOrigin, width, height);
            });

            workers.add(thread);
        }

        workers.forEach(Thread::start);

        for (Thread worker: workers)
        {
            try {
                worker.join();
            } catch (InterruptedException ignored) {}
        }
    }

    private static void recolorSingleThread(BufferedImage orgImage, BufferedImage outImage) {
        recolorImage(orgImage, outImage, 0, 0, orgImage.getWidth(), orgImage.getHeight());
    }

    private static void recolorImage(BufferedImage orgImage, BufferedImage outImage, int left, int top, int w, int h) {
        for (int x = left; x < (left + w) && x < orgImage.getWidth(); x++) {
            for (int y = top; y < (top + h) && y < orgImage.getHeight(); y++) {
                recolorPixel(orgImage, outImage, x, y);
            }
        }
    }

    public static void recolorPixel(BufferedImage orgImage, BufferedImage outImage, int x, int y) {
        int rgb = orgImage.getRGB(x, y);

        int red = Utils.getRed(rgb);
        int green = Utils.getGreen(rgb);
        int blue = Utils.getBlue(rgb);

        int updatedRed = red;
        int updatedGreen = green;
        int updatedBlue = blue;

        if (Utils.isShadeOfGray(red, green, blue)) {
            updatedRed = Math.min(255, red + 10);
            updatedGreen = Math.max(0, green - 80);
            updatedBlue = Math.max(0, blue - 20);
        }

        int updatedRGB = Utils.getRGBFromColors(updatedRed, updatedGreen, updatedBlue);

        setRGB(outImage, x, y, updatedRGB);
    }

    private static void setRGB(BufferedImage outImage, int x, int y, int updatedRGB) {
        outImage.getRaster().setDataElements(x, y, outImage.getColorModel().getDataElements(updatedRGB, null));
    }
}
