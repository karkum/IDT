import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * Main class for our submission. Performs the major steps of our solution. Our
 * algorithm follows these steps: 1. Convert the image to black and white 2.
 * Recognize blocks of black pixels going both horizontally and vertically 3.
 * Make rectangles using these blocks
 * 
 * @author Karthik Kumar, Mark Nachazel, Mike O'Briene
 */
public class IDT {

    /* This list will hold the points after recognizing blocks. */
    static ArrayList<Point> fullList = new ArrayList<Point>();
    static int THRESHOLD = 5;
    static final int WHITECOUNT = 4;

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        int num_args = args.length;
        boolean visualFlag = true;
        String srcFilename = "";
        String destFileName = "";
        if (num_args == 1 && args[0] == "-h") {
            displayHelp();
        }
        if (num_args == 3) {
            srcFilename = args[0];
            if (args[1].equals("-v")) {
                destFileName = args[2];
                visualFlag = true;
            } else if (args[1].equals("-f")) {
                destFileName = args[2];
                visualFlag = false;
            } else {
                displayHelp();
                return;
            }
        } else {
            displayHelp();
            return;
        }
        System.out.println("Reading file: " + srcFilename + "...DONE");
        System.out.print("Converting file to black and white...");
        String modifiedFile = parseImageToBW(srcFilename);
        System.out.println("DONE");

        System.out.print("Recognizing blocks of black pixels horizonally...");
        recognizeBlocksHorizontal(modifiedFile);
        System.out.println("DONE");

        System.out.print("Recognizing blocks of black pixels vertically...");
        recognizeBlocksVertical(modifiedFile);
        System.out.println("DONE");

        System.out.print("Grouping blocks of black pixels into rectangles...");
        ArrayList<Rectangle> groups = makeLists(srcFilename);
        System.out.println("DONE");
        
        System.out.print("Removing irrelvant small rectangles...");
        removeStatic(groups);
        System.out.println("DONE");

        System.out.print("Merging rectangles...");
        List<Rectangle> rects = mergeRectangles(groups);
        System.out.println("DONE");

        System.out.print("Outputting result to file " + destFileName + "...");
        doOutput(modifiedFile, destFileName, rects, visualFlag);
        System.out.println("DONE");

        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out
                .println("Automated Recognition and Bounding of Text successfully completed!");
        System.out.println("Computation took approximately: " + estimatedTime / 1000 + " seconds.");

    }

    private static void displayHelp() {
        System.out
                .println("Usage: <source image> -v <output image file name> -t <output text file name>");
        System.out.println("-----------------------------");
        System.out.println("-v file: The complete path to the file to process");
        System.out
                .println("-t: Output result to a text file. Format: (x, y) Width Height");
        System.out.println("-h: Displays the help");
        System.exit(0);
    }

    /**
     * Takes in a png file to read. Returns a modified png file that does some
     * edge detection. Converts the given image to black and white by checking
     * the color of the pixels around a given point.
     * 
     * @param fileName
     *            The file to read
     * @return String the name of the modified file.
     * @throws IOException
     */
    private static String parseImageToBW(String fileName) throws IOException {
        BufferedImage img = ImageIO.read(new File(fileName));
        BufferedImage newimg = new BufferedImage(img.getWidth(),
                img.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int i = 1; i < img.getHeight() - 1; i++) {
            for (int j = 1; j < img.getWidth() - 1; j++) {
                int rgb = img.getRGB(j, i);
                int next = img.getRGB(j, i + 1);
                int prev = img.getRGB(j, i - 1);
                int above = img.getRGB(j + 1, i);
                int below = img.getRGB(j - 1, i);
                if ((Math.abs(rgb - next) > 5 && Math.abs(rgb - prev) > 5))
                    newimg.setRGB(j, i, Color.black.getRGB());
                else if (Math.abs(rgb - above) > 5 && Math.abs(rgb - below) > 5)
                    newimg.setRGB(j, i, Color.black.getRGB());
                else
                    newimg.setRGB(j, i, Color.white.getRGB());
            }
        }
        // Fix up the edges
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
        file.deleteOnExit();
        ImageIO.write(newimg, "png", file);
        return file.getName();
    }

    /**
     * Parses a modified image to recognize blocks of text. It looks for a black
     * pixel, and look around it to find a string of white pixels, which
     * indicates the end of a block.
     * 
     * @param fileName
     *            the file to parse
     * @throws IOException
     */
    private static void recognizeBlocksHorizontal(String fileName)
            throws IOException {
        BufferedImage newImg = ImageIO.read(new File(fileName));
        List<Point> starts = new ArrayList<Point>();
        List<Point> ends = new ArrayList<Point>();
        int whiteCount = 0;
        for (int j = 0; j < newImg.getHeight(); j++) {
            boolean flag = true;
            for (int i = 0; i < newImg.getWidth(); i++) {

                int currRGB = newImg.getRGB(i, j);
                if (currRGB != Color.white.getRGB()) {
                    whiteCount = 0;
                    if (flag) {
                        starts.add(new Point(i, j));
                        flag = false;
                    }
                } else {
                    whiteCount++;
                }
                if (whiteCount == WHITECOUNT) {
                    if (!flag)
                        ends.add(new Point(i - WHITECOUNT, j));
                    whiteCount = 0;
                    flag = true;
                }
            }
            whiteCount = 0;
        }
        fullList.addAll(starts);
        fullList.addAll(ends);
    }

    /**
     * Parses a modified image to recognize blocks of text. It looks for a black
     * pixel, and look around it to find a string of white pixels, which
     * indicates the end of a block.
     * 
     * @param fileName
     * @throws IOException
     */
    private static void recognizeBlocksVertical(String fileName)
            throws IOException {
        BufferedImage newImg = ImageIO.read(new File(fileName));
        List<Point> starts = new ArrayList<Point>();
        List<Point> ends = new ArrayList<Point>();
        int whiteCount = 0;
        for (int i = 0; i < newImg.getWidth(); i++) {
            boolean flag = true; // use this flag to see if w e
            for (int j = 0; j < newImg.getHeight(); j++) {

                int currRGB = newImg.getRGB(i, j);
                if (currRGB != Color.white.getRGB()) {
                    whiteCount = 0;
                    if (flag) {
                        starts.add(new Point(i, j));
                        flag = false;
                    }
                } else {
                    whiteCount++;
                }
                if (whiteCount == WHITECOUNT) {
                    if (!flag)
                        ends.add(new Point(i, j - WHITECOUNT));
                    whiteCount = 0;
                    flag = true;
                }
            }
            whiteCount = 0;
        }
        fullList.addAll(starts);
        fullList.addAll(ends);
    }

    /**
     * This function goes through all the points in our list and groups points
     * into different lists based on their location in the image.
     * 
     * @param modifiedFile
     *            The file to read from
     * @return ArrayList<ArrayList<Point>> the list of list of points
     */
    @SuppressWarnings("unchecked")
    private static ArrayList<Rectangle> makeLists(String modifiedFile) {
        Collections.sort(fullList);
        ArrayList<Rectangle> lists = new ArrayList<Rectangle>();
        for (int i = 0; i < fullList.size(); i++) {
            Point curPoint = fullList.get(i);
            boolean placed = false;
            for (int j = 0; j < lists.size(); j++) {
                Rectangle r = lists.get(j);
                if ( distanceFromRect( curPoint, r) < THRESHOLD ) {
                    r.add( curPoint );
                    placed = true;
                    break;
                }
            }
            if (!placed) {
                Rectangle newRect = new Rectangle(curPoint);
                lists.add(newRect);
            }
        }
        return lists;
    }
    private static void removeStatic(ArrayList<Rectangle> rects) {
        int i = 0;
        while ( i < rects.size()) {
            if ( rects.get(i).width * rects.get(i).height < THRESHOLD )
                rects.remove(i);
            else
                i++;
        }
    }
    private static List<Rectangle> mergeRectangles(
            ArrayList<Rectangle> rects) {
        int i = 0;
        while (i < rects.size()) {
            boolean flag = false;
            int j = 0;
            while (j < rects.size()) {
                if (j == i) {
                    j++;
                    continue;
                } else if (rects.get(i).intersects(rects.get(j))) {
                    Rectangle r = rects.remove(j);
                    rects.set(i, rects.get(i).union(r));
                    flag = true;
                    continue;
                }
                j++;
            }
            if (!flag)
                i++;
        }
        return rects;
    }

    /**
     * Handles the output of the given data.
     * 
     * @param modifiedFile
     * @param destFileName
     * @param rects
     * @param visualFlag
     * @throws IOException
     */
    private static void doOutput(String modifiedFile, String destFileName,
            List<Rectangle> rects, boolean visualFlag) throws IOException {
        BufferedImage im = ImageIO.read(new File(modifiedFile));
        if (visualFlag) {
            for (int i = 0; i < rects.size(); i++) {
                Rectangle rect = rects.get(i);
                Graphics g = im.getGraphics();
                Graphics2D gr = (Graphics2D) g;
                gr.setStroke(new BasicStroke((float) 5.0));
                gr.setColor(Color.magenta);
                gr.drawRect((int) rect.getX(), (int) rect.getY(),
                        (int) rect.getWidth(), (int) rect.getHeight());
            }
            File file = new File(destFileName);
            ImageIO.write(im, "png", file);
        } else {
            BufferedWriter buf = new BufferedWriter(
                    new FileWriter(destFileName));
            for (int i = 0; i < rects.size(); i++) {
                Rectangle rect = rects.get(i);
                buf.write("(" + (int) rect.getX() + ", " + (int) rect.getY()
                        + ") " + (int) rect.getWidth() + " "
                        + (int) rect.getHeight() + "\n");
            }
            buf.close();
        }
    }
    public static double distanceFromRect( Point p, Rectangle r) {
        if ( r.contains( p ) )
            return 0.0;
        double hdistance = 0;
        double vdistance = 0;
        
        //check for hdistance
        if ( p.getX() > r.getMaxX() )
            hdistance = p.getX() - r.getMaxX();
        else if ( p.getX() < r.getMinX() )
            hdistance = r.getMinX() - p.getX();
        
      //check for vdistance
        if ( p.getY() > r.getMaxY() )
            vdistance = p.getY() - r.getMaxY();
        else if ( p.getY() < r.getMinY() )
            vdistance = r.getMinY() - p.getY();
            
        
        return Math.sqrt( hdistance * hdistance + vdistance * vdistance );
    }
}