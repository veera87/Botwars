/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package botwars;

import java.awt.Point;

/**
 *
 * @author izaaz
 */
public class Utils {

    /**
     * 
     * @param oldPoint initial point
     * @param angle angle of motion in radians
     * @param distance distance to be moved
     * @return new point from the initial point in the angle of motion for the 
     * specified distance
     */
    static Point getNewPoint(Point oldPoint, int angle, int distance) {
        Point newPoint = new Point(oldPoint);
        newPoint.x += (int) (distance * Math.cos(Math.toRadians(angle)));
        newPoint.y += (int) (distance * Math.sin(Math.toRadians(angle)));
        return newPoint;
    }

    static boolean hasCollided(Point pt1, Point pt2, int radius1, int radius2) {
        double dist = Point.distance(pt1.x, pt1.y, pt2.x, pt2.y);
        if (dist <= (radius1 + radius2)) {
            return true;
        }

        return false;
    }

    
    static double distance(Point pt1, Point pt2) {
        return Point.distance(pt1.x, pt1.y, pt2.x, pt2.y);
    }

    /**
     * found code <a href="http://beradrian.wordpress.com/2009/03/23/calculating-the-angle-between-two-points-on-a-circle/">here</a>
     * Finds the angle between two points. The first point is treated as the center point
     * @param center - Point1
     * @param pt1 - Point 2
     * @return
     */
    static int angleBetweenTwoPoints(Point center, Point pt1) {
        Point p0 = new Point();
        double radius = Math.sqrt(Math.abs(pt1.x - center.x) * Math.abs(pt1.x - center.x) + Math.abs(pt1.y - center.y) * Math.abs(pt1.y - center.y));
        p0.x = (int) (center.x);
        p0.y = (int) (center.y - radius);
        return (int) (360 + 90 - (2 * Math.atan2(pt1.y - p0.y, pt1.x - p0.x)) * 180 / Math.PI);
    }
}
