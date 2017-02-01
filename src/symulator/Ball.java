/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package symulator;

import java.awt.Point;
import java.awt.geom.Point2D;

/**
 *
 * @author nooDy
 */
public class Ball {

    Point2D.Double Pos;//pozycja
    Point2D.Double Vel;//prędkość
    int Rad;//promień
    //double Mass//masa

    Ball() {
        Pos = new Point2D.Double(1, 1);
        Vel = new Point2D.Double(1, 1);
        Rad = 1;
    }

    Ball(Point2D.Double pos, int rad, Point2D.Double vel) {
        Pos = pos;
        Vel = vel;
        Rad = rad;
    }

    Ball(int windowwidth, int windowheight, int rad, Point2D.Double vel) {
        Pos = new Point2D.Double((int) Math.floor(Math.random() * (windowwidth - 2 * rad) + rad), (int) Math.floor(Math.random() * (windowheight - 2 * rad) + rad));
        Vel = vel;
        Rad = rad;
    }

    void Move() {
        Pos.setLocation(Pos.getX() + Vel.getX(), Pos.getY() + Vel.getY());
    }

    void Move(Point2D.Double v) {
        Pos.setLocation(Pos.getX() + v.getX(), Pos.getY() + v.getY());
    }

    void Move(double[] v) {
        Pos.setLocation(Pos.getX() + v[0], Pos.getY() + v[1]);
    }

    void rotateVel(double a, int i) {
        double x, y, n;
        Point2D.Double v, z;
        n = Utils.Norm(Vel);
        v = Utils.Normalize(Vel);
        x = Vel.getX() - i * (a + 1) * Vel.getY() / 20;
        y = Vel.getY() + i * (a + 1) * Vel.getX() / 20;
        z = Utils.Normalize(new Point2D.Double(x, y));
        Vel.setLocation(new Point2D.Double(n * z.getX(), n * z.getY()));
    }

    Point2D.Double DistanceV(Ball b) {
        return new Point2D.Double(Pos.getX() - b.Pos.getX(), Pos.getY() - b.Pos.getY());
    }

    Point2D.Double DistanceV(Point2D.Double b) {
        return new Point2D.Double(Pos.getX() - b.getX(), Pos.getY() - b.getY());
    }

    Point2D.Double DistanceV(Point b) {
        return new Point2D.Double(Pos.getX() - b.getX(), Pos.getY() - b.getY());
    }

    Point2D.Double DistanceV(double[] b) {
        return new Point2D.Double(Pos.getX() - b[0], Pos.getY() - b[1]);
    }

    int Distance(Ball b) {
        return Rad + b.Rad;
    }

    void CheckBorders(int w, int h) {
        if (Pos.getX() >= w - Rad) {
            Pos.setLocation(Pos.getX() - 2 * (Rad - (w - Pos.getX())), Pos.getY());
            Vel.setLocation(-Vel.getX(), Vel.getY());
        } else if (Pos.getX() <= Rad) {
            Pos.setLocation(Pos.getX() + 2 * (Rad - Pos.getX()), Pos.getY());
            Vel.setLocation(-Vel.getX(), Vel.getY());
        }
        if (Pos.getY() >= h - Rad) {
            Pos.setLocation(Pos.getX(), Pos.getY() - 2 * (Rad - (h - Pos.getY())));
            Vel.setLocation(Vel.getX(), -Vel.getY());
        } else if (Pos.getY() <= Rad) {
            Pos.setLocation(Pos.getX(), Pos.getY() + 2 * (Rad - Pos.getY()));
            Vel.setLocation(Vel.getX(), -Vel.getY());
        }
    }
}
