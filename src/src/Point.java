import java.awt.geom.Point2D;

/**
 * This class represents a Point in a picture. It simply provides
 * a way to sort points. It also allows us to compute the distance
 * between two points.
 */
@SuppressWarnings("rawtypes")
public class Point implements Comparable {
    
    /* The x and y coordinates of this point*/
    private int x, y;
    
    /**
     * Constructor. Creates a point object given the x and y coordinates. 
     * @param x the x point
     * @param y the y point 
     */
    public Point (int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Compare to function. Compares this point to an other. 
     * 
     * @param other The other point to compare with this one
     * @return > 0 if this point is lower/to the right of the other point
     * @return < 0 if this point is not <above predicate>
     * @return = 0 if this point is equal to the other point
     */
    @Override
    public int compareTo(Object other) {
        Point that = (Point)other;
        if (this.y > that.y) return +1;
        if (this.x < that.x) return -1;
        if (this.x > that.x) return +1;
        return 0;
    }
    
    /**
     * @return x Return the x point
     */
    public int getX() {
        return x;
    }
    
    /**
     * @return y Return the y point
     */
    public int getY() {
        return y;
    }
    
    /**
     * Returns the distance from this point to the other.
     * @param other the other point to get the distance from
     * @return the distance between this and the other point.
     */
    public int distance(Point other) {
        Point2D.Double us = new Point2D.Double(x, y);
        Point2D.Double them = new Point2D.Double(other.getX(), other.getY());
        return (int) us.distance(them);
    }
}