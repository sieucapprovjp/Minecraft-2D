import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Color;

public class PrintAscii {
    public static void main(String[] args) {
        String[] filesToTest = {"body2.png", "body_cp_2.png", "body_cp_6.png", "01.png"};
        for (String fileName : filesToTest) {
            File file = new File(fileName);
            if (!file.exists()) continue;
            try {
                BufferedImage img = ImageIO.read(file);
                System.out.println("--- " + fileName + " ---");
                for (int y = 0; y < img.getHeight(); y++) {
                    StringBuilder sb = new StringBuilder();
                    for (int x = 0; x < img.getWidth(); x++) {
                        int argb = img.getRGB(x, y);
                        int a = (argb >> 24) & 0xff;
                        if (a < 128) {
                            sb.append(" ");
                        } else {
                            sb.append("#");
                        }
                    }
                    System.out.println(sb.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
