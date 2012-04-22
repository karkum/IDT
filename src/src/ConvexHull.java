import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Performs a Graham's scan to find the convex hull for a given list of points.
 * @author Godmar Back (gback.cs.vt.edu)
 */
public class ConvexHull {
    
    /* The given list of points */
    private List<Point> fullList;

    /* Represents the points as an array */
    private Point points[];

    /**
     * Constructor for a ConvexHull object. It accepts a list of points
     * and converts it to an array of points
     * @param list
     */
    public ConvexHull(List<Point> list) {
        fullList = list;
        points = new Point[fullList.size()];
        for (int i = 0; i < list.size(); i++) {
            points[i] = list.get(i);
        }
    }

    /**
     * Overloaded constructor for ConvexHull that takes in an array of points
     * @param points
     */
    public ConvexHull(Point[] points) {
        this.points = (Point[]) fullList.toArray();
    }

    /**
     * A class that represents a data type that can be used to perform a
     * Graham's scan
     */
    private class pData implements Comparable<pData> {
        Point point;
        double angle;
        long distance;

        public pData(Point p, double a, long d) {
            point = p;
            angle = a;
            distance = d;
        }

        public int compareTo(pData that) {
            int angleSmaller = Double.valueOf(this.angle).compareTo(that.angle);
            if (angleSmaller != 0)
                return angleSmaller;
            else
                return Long.valueOf(this.distance).compareTo(that.distance);
        }
    }

    public double angle(Point o, Point a) {
        return Math.atan2(a.getY() - o.getY(), a.getX() - o.getX());
    }

    public long distance2(Point a, Point b) {
        return (b.getX() - a.getX()) * (b.getX() - a.getX())
                + (b.getY() - a.getY()) * (b.getY() - a.getY());
    }

    public int ccw(Point p1, Point p2, Point p3) {
        return (int) ((p2.getX() - p1.getX()) * (p3.getY() - p1.getY()) - (p2
                .getY() - p1.getY()) * (p3.getX() - p1.getX()));
    }

    /**
     * Performs the graham scan algorithm that will return a polygon of
     * points that encompasses all the points given.
     * @return
     */
    public Polygon grahamScan() {
        int pNum = points.length;
        Point min = fullList.get(0); // list is already sorted

        ArrayList<pData> al = new ArrayList<pData>();

        for (Point p : points) {
            if (p == min)
                continue;

            double ang = angle(min, p);
            if (ang < 0)
                ang += Math.PI;

            al.add(new pData(p, ang, distance2(min, p)));
        }

        Collections.sort(al);

        Point stack[] = new Point[pNum + 1];
        int j = 2;
        for (int i = 0; i < pNum; i++) {
            if (points[i] == min)
                continue;
            stack[j] = al.get(j - 2).point;
            j++;
        }
        stack[0] = stack[pNum];
        stack[1] = min;

        int M = 2;
        for (int i = 3; i <= pNum; i++) {
            while (M > 0 && ccw(stack[M - 1], stack[M], stack[i]) <= 0)
                M--;
            M++;

            Point tmp = stack[i];
            stack[i] = stack[M];
            stack[M] = tmp;
        }

        int[] xPoints2 = new int[M];
        int[] yPoints2 = new int[M];

        for (int i = 0; i < M; i++) {
            xPoints2[i] = (int) stack[i + 1].getX();
            yPoints2[i] = (int) stack[i + 1].getY();
        }
        return new Polygon(xPoints2, yPoints2, M);
    }
}