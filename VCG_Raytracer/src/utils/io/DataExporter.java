package utils.io;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class DataExporter {
    public static void exportImageToPng(BufferedImage image, String fileName){
        File outFile = new File(fileName);
        try {
            ImageIO.write(image, "png", outFile);
        } catch (Exception e) {
            System.err.println(e.getMessage()); // print any DataExporter errors to stderr.
        }
    }
}
