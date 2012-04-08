import java.awt.geom.Point2D;


@SuppressWarnings("rawtypes")
public class Point implements Comparable {
    int x, y;
    
    public Point (int a, int b) {
        x = a;
        y = b;
    }

    @Override
    public int compareTo(Object other) {
        Point that = (Point)other;
        if (this.y > that.y) return +1;
        if (this.x < that.x) return -1;
        if (this.x > that.x) return +1;
        return 0;
    }
    
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int distance(Point other) {
        Point2D.Double us = new Point2D.Double(x, y);
        Point2D.Double them = new Point2D.Double(other.getX(), other.getY());
        return (int) us.distance(them);
    }
    
    
}
