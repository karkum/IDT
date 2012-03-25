import java.awt.Color;
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
		BufferedImage newimg = new BufferedImage(img.getWidth(),img.getHeight(),BufferedImage.TYPE_INT_RGB );
		Raster ras = img.getData();
		for (int i = 1; i < img.getHeight() - 1; i++) {
			for (int j = 1; j < img.getWidth() - 1; j++) {
				int rgb = img.getRGB(j, i);
				int next = img.getRGB(j, i+1);
				int prev = img.getRGB(j, i-1);
				int above = img.getRGB(j+1, i);
				int below = img.getRGB(j-1, i);
				if ( Math.abs(rgb - next) > 5 &&  Math.abs(rgb - prev) > 5 )
				    newimg.setRGB(j, i, Color.black.getRGB());
				else if ( Math.abs(rgb - above) > 5 &&  Math.abs(rgb - below) > 5 ) 
				    newimg.setRGB(j, i, Color.black.getRGB());
				else
				    newimg.setRGB(j, i, Color.white.getRGB());
			}
		}
		File file = new File("newimg.png");
		ImageIO.write(newimg,"png",file);
	}
}
 	