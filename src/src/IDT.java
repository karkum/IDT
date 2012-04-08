import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;


public class IDT {

    static ArrayList <Point> fullList = new ArrayList<Point>();
    static List<ArrayList<Point>> buckets;
    public static void main(String [] args) throws IOException {
        Scanner in = new Scanner(System.in);
        String modifiedFile = parseImageToBW(in.nextLine());
        recognizeBlocksHorizontal(modifiedFile);
        recognizeBlocksVertical(modifiedFile);
        System.out.println("Size of full list: " + fullList.size());
        createBoxes();
        System.out.println(buckets.size());
        //paintBuckets(modifiedFile);
        
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
                if ( (Math.abs(rgb - next) > 5 &&  Math.abs(rgb - prev) > 5 ))
                    newimg.setRGB(j, i, Color.black.getRGB());
                else if ( Math.abs(rgb - above) > 5 &&  Math.abs(rgb - below) > 5 ) 
                    newimg.setRGB(j, i, Color.black.getRGB());
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
    private static void recognizeBlocksHorizontal(String fileName) throws IOException {
        BufferedImage newImg = ImageIO.read(new File("good.png")); //TODO CHANGE THIS PARAMETER
        List <Point> starts = new ArrayList<Point>();
        List <Point> ends = new ArrayList<Point>();
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
                        starts.add(new Point(i, j));
                        flag = false;

                    }
                } else {
                    whiteCount++;
                }
                // This 5 is arbitrary, when we see whiteCount white spaces
                // move back 5 spaces and add it to our list
                if (whiteCount == 4) { 
                    if (!flag)
                        ends.add(new Point(i - 4, j));
                    whiteCount = 0;
                    flag = true;
                }
            }
            whiteCount = 0;
        }
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

        Point min = findIMin(starts);
        System.out.println("min: " + min.getX() + "   " + min.getY());
        modImg.setRGB((int)min.getX(), (int)min.getY(), Color.blue.getRGB());
        File file = new File("modfull.png");
        ImageIO.write(modImg,"png",file);
        fullList.addAll(starts);
        fullList.addAll(ends);
    }

    /**
     * Parses a modified image to recognize blocks of text. It looks for (or is supposed to look for)
     * a black pixel, and look around it to find a string of white pixels, which indicates the
     * end of a block.
     * @param fileName
     * @throws IOException
     */
    private static void recognizeBlocksVertical(String fileName) throws IOException {
        BufferedImage newImg = ImageIO.read(new File("modfull.png")); //TODO CHANGE THIS PARAMETER
        List <Point> starts = new ArrayList<Point>();
        List <Point> ends = new ArrayList<Point>();
        int whiteCount = 0;
        for (int i = 0; i < newImg.getWidth(); i++) {
            boolean flag = true; // use this flag to see if w e 
            for (int j = 0; j < newImg.getHeight(); j++) {

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
                        starts.add(new Point(i, j));
                        flag = false;

                    }
                } else {
                    whiteCount++;
                }
                // This 5 is arbitrary, when we see whiteCount white spaces
                // move back 5 spaces and add it to our list
                if (whiteCount == 4) { 
                    if (!flag)
                        ends.add(new Point(i, j - 4));
                    whiteCount = 0;
                    flag = true;
                }
            }
            whiteCount = 0;
        }
        //      BufferedImage modImg = new BufferedImage(newImg.getWidth(),newImg.getHeight(),BufferedImage.TYPE_INT_RGB );
        BufferedImage modImg = ImageIO.read(new File("modfull.png")); //TODO CHANGE THIS PARAMETER
        System.out.println(starts.size());
        System.out.println(ends.size());

        for (int i = 0; i < starts.size(); i++) {
            modImg.setRGB((int)starts.get(i).getX(), (int)starts.get(i).getY(), Color.red.getRGB());
            //            modImg.setRGB((int)ends.get(i).getX(), (int)ends.get(i).getY(), Color.red.getRGB());
        }
        for (int i = 0; i < ends.size(); i++) {
            modImg.setRGB((int)ends.get(i).getX(), (int)ends.get(i).getY(), Color.red.getRGB());
        }

        Point min = findIMin(starts);
        System.out.println("min: " + min.getX() + "   " + min.getY());
        modImg.setRGB((int)min.getX(), (int)min.getY(), Color.blue.getRGB());
        File file = new File("new.png");
        ImageIO.write(modImg,"png",file);
        fullList.addAll(starts);
        fullList.addAll(ends);
    }

    private static Point findIMin(List<Point> list) {
        double minI = list.get(0).getX();
        for (Point d : list) {
            if (d.getX() < minI && list.get(0).getY() == d.getY()) {
                minI = d.getX();
            }
        }
        double minJ = list.get(0).getY();
        for (Point d : list) {
            if (d.getY() < minJ && list.get(0).getX() == d.getX()) {
                minJ = d.getY();
            }
        }
        return new Point(minI, minJ);
    }
    private static void createBoxes( ) {
        int count = 0;
        Collections.sort(fullList);
        buckets = new ArrayList<ArrayList<Point>>();
        for( int i = 0; i < fullList.size(); i++ ) {
            Point curPoint = fullList.get(i);
//            System.out.println(curPoint.getX() + " " + curPoint.getY());
            boolean placed = false;
            for ( int j = 0; j < buckets.size(); j++ ) {
                for ( int k = buckets.get(j).size() - 1; k >= 0; k-- ){
                    if ( buckets.get(j).get(k).distance(curPoint) < 7.0 ) {
                        buckets.get(j).add(curPoint);
                        placed = true;
                    }
                }
            }
            if ( !placed ) {
//                System.out.println(count++);
//                System.out.println(curPoint.getX() + " " + curPoint.getY());
                ArrayList<Point> newList = new ArrayList<Point>();
                newList.add(curPoint);
                buckets.add(newList);
            }
        }
    }
    private static void paintBuckets( String fileName ) throws IOException {
        BufferedImage img = ImageIO.read(new File(fileName));
        for ( int i = 0; i < img.getWidth(); i++ ) {
            for ( int j = 0; j < img.getHeight(); j++ ) {
                
            }
        }
        
    }
} 