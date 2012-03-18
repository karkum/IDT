import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;


public class IDT {
	public static void main(String [] args) throws IOException {
		Scanner in = new Scanner(System.in);
		BufferedImage img = ImageIO.read(new File(in.nextLine()));
		Raster ras = img.getData();
		for (int i = 0; i < img.getHeight(); i++) {
			for (int j = 0; j < img.getWidth(); j++) {
				int rgb = img.getRGB(j, i);
				if (rgb == -1)
					System.out.print(0);
				else
					System.out.print(1);
			}
			System.out.println();
		}
	}
}
 	