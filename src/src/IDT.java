import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
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
                //                int topRight = img.getRGB(j + 1, i + 1);
                //                int topLeft = img.getRGB(j + 1, i -1);
                //                int botRight = img.getRGB(j - 1, i + 1);
                //                int botLeft = img.getRGB(j - 1, i -1);
                if ( (Math.abs(rgb - next) > 5 &&  Math.abs(rgb - prev) > 5 ))
                    newimg.setRGB(j, i, Color.black.getRGB());
                else if ( Math.abs(rgb - above) > 5 &&  Math.abs(rgb - below) > 5 ) 
                    newimg.setRGB(j, i, Color.black.getRGB());
                //                else if ( Math.abs(rgb - topRight) > 5 &&  Math.abs(rgb - botLeft) > 5 ) 
                //                    newimg.setRGB(j, i, Color.black.getRGB());
                //                else if ( Math.abs(rgb - topLeft) > 5 &&  Math.abs(rgb - botRight) > 5 ) 
                //                    newimg.setRGB(j, i, Color.black.getRGB());
                else
                    newimg.setRGB(j, i, Color.white.getRGB());
            }
        }
        File file = new File("newimg1.png");
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
        List <Point2D.Double> starts = new ArrayList<Point2D.Double>();
        List <Point2D.Double> ends = new ArrayList<Point2D.Double>();
        int whiteCount = 0;
        for (int j = 0; j < newImg.getHeight(); j++) {
            boolean flag = true; // use this flag to see if w e 
            for (int i = 0; i < newImg.getWidth(); i++) {

                int currRGB = newImg.getRGB(i, j);
                // If we come across a non white pixel
                // and this is the first one we have seen (flag is true)
                // then we add it to our starts list
                // Then, we keep looking at the pixels and when
                // we reach the limit of whiteCount, we add
                // that to our ends list
                if (currRGB != Color.white.getRGB()) {
                    whiteCount = 0;
                    if (flag) {
                        starts.add(new Point2D.Double(i, j));
                        flag = false;

                    }
                } else {
                    whiteCount++;
                }
                // This 5 is arbitrary, when we see whiteCount white spaces
                // move back 5 spaces and add it to our list
                if (whiteCount == 5) { 
                    if (!flag)
                        ends.add(new Point2D.Double(i - 5, j));
                    whiteCount = 0;
                    flag = true;
                }
            }
            whiteCount = 0;
            p.flush();
        }
        p.close();
        //	    BufferedImage modImg = new BufferedImage(newImg.getWidth(),newImg.getHeight(),BufferedImage.TYPE_INT_RGB );
        BufferedImage modImg = ImageIO.read(new File("good.png")); //TODO CHANGE THIS PARAMETER
        System.out.println(starts.size());
        System.out.println(ends.size());

        for (int i = 0; i < starts.size(); i++) {
            modImg.setRGB((int)starts.get(i).getX(), (int)starts.get(i).getY(), Color.red.getRGB());
            //            modImg.setRGB((int)ends.get(i).getX(), (int)ends.get(i).getY(), Color.red.getRGB());
        }
        for (int i = 0; i < ends.size(); i++) {
            modImg.setRGB((int)ends.get(i).getX(), (int)ends.get(i).getY(), Color.red.getRGB());
        }
        
        Point2D.Double min = findIMin(starts);
        System.out.println("min: " + min.getX() + "   " + min.getY());
        modImg.setRGB((int)min.getX(), (int)min.getY(), Color.blue.getRGB());
        File file = new File("modfull.png");
        ImageIO.write(modImg,"png",file);
    }

    private static Point2D.Double findIMin(List<Point2D.Double> list) {
        double minI = list.get(0).getX();
        for (Point2D.Double d : list) {
            if (d.getX() < minI && list.get(0).getY() == d.getY()) {
                minI = d.getX();
            }
        }
        double minJ = list.get(0).getY();
        for (Point2D.Double d : list) {
            if (d.getY() < minJ && list.get(0).getX() == d.getX()) {
                minJ = d.getY();
            }
        }
        return new Point2D.Double(minI, minJ);
    }
} 