import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class IDT {

    static ArrayList <Point> fullList = new ArrayList<Point>();
    static int THRESHOLD = 20;

    public static void main(String [] args) throws IOException {
        Scanner in = new Scanner(System.in);
        String modifiedFile = parseImageToBW(in.nextLine());
        recognizeBlocksHorizontal(modifiedFile);
        recognizeBlocksVertical(modifiedFile);

        System.out.println("Size of full list: " + fullList.size());
        makeLists();
        //doGrahams();
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
        //Fix up the edges
        for (int i = 0; i < img.getHeight(); i++) {
            newimg.setRGB(0, i, Color.white.getRGB());
        }
        for (int i = 0; i < img.getHeight(); i++) {
            newimg.setRGB(img.getWidth() - 1, i, Color.white.getRGB());
        }
        for (int i = 0; i < img.getWidth(); i++) {
            newimg.setRGB(i, 0, Color.white.getRGB());
        }
        for (int i = 0; i < img.getWidth(); i++) {
            newimg.setRGB(i, img.getHeight() - 1, Color.white.getRGB());
        }
        File file = new File("bwimg.png");
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
        BufferedImage newImg = ImageIO.read(new File(fileName)); //TODO CHANGE THIS PARAMETER
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
        BufferedImage modImg = ImageIO.read(new File(fileName)); //TODO CHANGE THIS PARAMETER
        System.out.println(starts.size());
        System.out.println(ends.size());

//        for (int i = 0; i < starts.size(); i++) {
//            modImg.setRGB((int)starts.get(i).getX(), (int)starts.get(i).getY(), Color.red.getRGB());
//            //            modImg.setRGB((int)ends.get(i).getX(), (int)ends.get(i).getY(), Color.red.getRGB());
//        }
//        for (int i = 0; i < ends.size(); i++) {
//            modImg.setRGB((int)ends.get(i).getX(), (int)ends.get(i).getY(), Color.red.getRGB());
//        }

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
        /*
        for (int i = 0; i < starts.size(); i++) {
            modImg.setRGB((int)starts.get(i).getX(), (int)starts.get(i).getY(), Color.red.getRGB());
            //            modImg.setRGB((int)ends.get(i).getX(), (int)ends.get(i).getY(), Color.red.getRGB());
        }
        for (int i = 0; i < ends.size(); i++) {
            modImg.setRGB((int)ends.get(i).getX(), (int)ends.get(i).getY(), Color.red.getRGB());
        }*/

        File file = new File("new.png");
        ImageIO.write(modImg,"png",file);
        fullList.addAll(starts);
        fullList.addAll(ends);
    }
    @SuppressWarnings("unchecked")
    private static void makeLists() throws IOException {
        Collections.sort(fullList);

        ArrayList <ArrayList<Point> > lists = new ArrayList<ArrayList<Point>>();
        for ( int i = 0; i < fullList.size(); i++ ) {
            Point curPoint = fullList.get(i);
            boolean placed = false;
            for ( int j = 0; j < lists.size(); j ++ ) {
                ArrayList<Point> curList = lists.get(j);
                for ( int k = 0; k < curList.size(); k ++ ) {
                    if ( curList.get(k).distance(curPoint) < THRESHOLD ) {
                        curList.add(curPoint);
                        placed = true;
                        break;
                    }
                    if ( placed )
                        break;
                }
            }
            if ( !placed ) {
                ArrayList<Point> newList = new ArrayList<Point>();
                newList.add(curPoint);
                lists.add(newList);
            }
        }
        BufferedImage im = ImageIO.read(new File("new.png"));
        List<Rectangle> rects = new ArrayList<Rectangle>();
        for ( int i = 0 ; i < lists.size(); i++) {
            ArrayList<Point> list = lists.get(i);
            Collections.sort(list);
            ConvexHull c = new ConvexHull(list);
            Polygon answer = c.grahamScan();
            Rectangle rect = answer.getBounds();
            rects.add(rect);
            //Graphics g = im.getGraphics();
            //g.setColor(Color.blue);
            //g.drawRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), 
            // (int)rect.getHeight());
            //        g.setColor(Color.green);
            //        g.drawPolygon(answer);

        }
        int i = 0;
        System.out.println(rects.size());
        while ( i < rects.size() - 1 )
        {
            for ( int j = 0; j < rects.size(); j++ ) {
                if ( rects.get(i).intersects( rects.get(j)) )
                {
                    Rectangle i2 = rects.remove(j);
                    Rectangle i1 = rects.remove(i);
                    rects.add(i, i2.union(i1));
                    break;
                }
            }
            i++;
        }
        System.out.println(rects.size());
        for ( i = 0; i < rects.size(); i++ ) {
            Rectangle rect = rects.get(i);
            Graphics g = im.getGraphics();
            g.setColor(Color.blue);
            g.drawRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), 
                    (int)rect.getHeight());
        }
        File file = new File("finalresult.png");
        ImageIO.write(im,"png",file);
    }

    @SuppressWarnings("unchecked")
    private static void doGrahams() throws IOException {
        Collections.sort(fullList);
        ConvexHull c = new ConvexHull(fullList);
        Polygon answer = c.grahamScan();
        Rectangle rect = answer.getBounds();
        BufferedImage i = ImageIO.read(new File("new.png"));
        Graphics g = i.getGraphics();
        g.setColor(Color.blue);
        g.drawRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), 
                (int)rect.getHeight());
        g.setColor(Color.green);
        g.drawPolygon(answer);
        File file = new File("finalresult.png");
        ImageIO.write(i,"png",file);
    }
} 