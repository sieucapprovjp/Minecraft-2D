import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class PrintSizes {
    public static void main(String[] args) {
        File dir = new File(".");
        File[] files = dir.listFiles((d, name) -> name.endsWith(".png"));
        if (files != null) {
            for (File file : files) {
                try {
                    BufferedImage img = ImageIO.read(file);
                    System.out.println(file.getName() + ": " + img.getWidth() + "x" + img.getHeight());
                } catch (Exception e) {
                    System.out.println("Error reading " + file.getName());
                }
            }
        }
    }
}
