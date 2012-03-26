import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.imageio.ImageIO;


public class IDT {
	public static void main(String [] args) throws IOException {
		Scanner in = new Scanner(System.in);
		String modifiedFile = parseImageToBW(in.nextLine());
		recognizeBlocks(modifiedFile);
	}
	/**
	 * Takes in a png file to read. Returns a modified png file that does some edge detection.
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	private static String parseImageToBW(String fileName) throws IOException {
		BufferedImage img = ImageIO.read(new File(fileName));
		BufferedImage newimg = new BufferedImage(img.getWidth(),img.getHeight(),BufferedImage.TYPE_INT_RGB );
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
		return file.getName();
	}
	
	/**
	 * Parses a modified image to recognize blocks of text. It looks for (or is supposed to look for)
	 * a black pixel, and look around it to find a string of white pixels, which indicates the
	 * end of a block.
	 * @param fileName
	 * @throws IOException
	 */
	private static void recognizeBlocks(String fileName) throws IOException {
		BufferedImage newImg = ImageIO.read(new File("good.png")); //TODO CHANGE THIS PARAMETER
		PrintWriter p = new PrintWriter(new File("out.txt"));
		for (int i = 0; i < newImg.getWidth(); i++) {
			for (int j = 0; j < newImg.getHeight(); j++) {
				int currRGB = newImg.getRGB(i, j);
				if (currRGB != Color.white.getRGB()) {
					String s = "loc" + new Point2D.Double(i, j).toString() + " black\n";
					p.write(s);
				}
			}
			p.flush();
		}
		p.close();
	}
}