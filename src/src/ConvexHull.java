import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConvexHull
{
    private List<Point> fullList;
    Point points[];
    public ConvexHull(List <Point> l) {
        fullList = l;
        points = new Point[fullList.size()];
        for (int i = 0; i < l.size(); i++) {
            points[i] = l.get(i);
        }

    }
   
    
    ConvexHull(Point [] points)
    {
        this.points = (Point[]) fullList.toArray();
    }
    
    private class pData implements Comparable<pData>
    {
        Point point;
        double angle;
        long distance;
        
        public pData(Point p, double a, long d)
        {
            point = p;
            angle = a;
            distance = d;
        }
        
        public int compareTo(pData that)
        {
            int angleSmaller = Double.valueOf(this.angle).compareTo(that.angle);
            if (angleSmaller != 0)
                return angleSmaller;
            else
                return Long.valueOf(this.distance).compareTo(that.distance);
        }
    }
    
    public double angle(Point o, Point a)
    {
        return Math.atan2(a.y - o.y, a.x - o.x);
    }
    
    public long distance2(Point a, Point b)
    {
        return (b.x - a.x) * (b.x - a.x) + (b.y - a.y) * (b.y - a.y);
    }
    
    public int ccw(Point p1, Point p2, Point p3)
    {
        return (int) ((p2.x - p1.x) * (p3.y - p1.y) - (p2.y - p1.y) * (p3.x - p1.x));
    }
    
    public Polygon grahamScan()
    {
        int pNum = points.length;
        Point min = fullList.get(0); //list is already sorted
//        Point min = Collections.min(fullList, new Comparator<Point>() {
//            public int compare(Point p1, Point p2) {
//                int y = Integer.valueOf(p1.y).compareTo(p2.y);
//                return y != 0 ? y : Integer.valueOf(p1.x).compareTo(p2.x);
//            }
//        });
        
        ArrayList<pData> al = new ArrayList<pData>();
        
        for (Point p : points)
        {
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
        for (int i = 0; i < pNum; i++)
        {
            if (points[i] == min)
                continue;
            stack[j] = al.get(j-2).point;
            j++;
        }
        stack[0] = stack[pNum];
        stack[1] = min;
        
        int M = 2;
        for (int i = 3; i <= pNum; i++)
        {
            while (M >0 && ccw(stack[M-1], stack[M], stack[i]) <= 0)
                M--;
            M++;
            
            Point tmp = stack[i];
            stack[i] = stack[M];
            stack[M] = tmp;
        }
        
        int [] xPoints2 = new int[M];
        int [] yPoints2 = new int[M];
        
        for (int i = 0; i < M; i++)
        {
            xPoints2[i] = (int) stack[i+1].x;
            yPoints2[i] = (int) stack[i+1].y;
        }
        return new Polygon(xPoints2, yPoints2, M);
    }
}