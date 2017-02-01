/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package symulator;

import java.awt.geom.Point2D;

/**
 *
 * @author nooDy
 */
public class Utils {

    public static double Norm(Point2D.Double v) {
        return Math.sqrt(Math.pow(v.getX(), 2) + Math.pow(v.getY(), 2));
    }

    public static Point2D.Double Normalize(Point2D.Double v) {
        double vn = Norm(v);
        return new Point2D.Double(v.getX() / vn, v.getY() / vn);
    }

    public static double Scalar(Point2D.Double v, Point2D.Double u) {
        return v.getX() * u.getX() + v.getY() * u.getY();
    }
}
