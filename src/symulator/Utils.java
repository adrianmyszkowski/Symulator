/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package symulator;

/**
 *
 * @author nooDy
 */
public class Utils {

    public static double Norm(double[] v) {
        return Math.sqrt(Math.pow(v[0], 2) + Math.pow(v[1], 2));
    }

    public static double[] Normalize(double[] v) {
        double vn = Norm(v);
        return new double[]{v[0] / vn, v[1] / vn};
    }

    public static double Scalar(double[] v, double[] u) {
        return v[0] * u[0] + v[1] * u[1];
    }
}
