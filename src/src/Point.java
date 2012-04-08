import java.awt.geom.Point2D;


public class Point implements Comparable {
    private double x, y;
    
    public Point (double a, double b) {
        x = a;
        y = b;
    }

    @Override
    public int compareTo(Object other) {
        Point that = (Point)other;
        if (this.y < that.y) return -1;
        if (this.y > that.y) return +1;
        if (this.x < that.x) return -1;
        if (this.x > that.x) return +1;
        return 0;
    }
    
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double distance(Point other) {
        Point2D.Double us = new Point2D.Double(x, y);
        Point2D.Double them = new Point2D.Double(other.getX(), other.getY());
        return us.distance(them);
    }
    
    
}
