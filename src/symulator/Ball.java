/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package symulator;

import java.awt.*;

/**
 *
 * @author nooDy
 */
public class Ball {

    double[] Pos = new double[2];//pozycja
    double[] Vel = new double[2];//prędkość
    int Rad;//promień
    //double Mass//masa

    Ball() {
        Pos = new double[]{1, 1};
        Vel = new double[]{1, 1};
        Rad = 1;
    }

    Ball(double pos[], int rad, double vel[]) {
        Pos = pos;
        Vel = vel;
        Rad = rad;
    }

    Ball(int windowwidth, int windowheight, int rad, double vel[]) {
        Pos = new double[]{Math.floor(Math.random() * (windowwidth - 2 * rad)) + rad, Math.floor(Math.random() * (windowheight - 2 * rad)) + rad};
        Vel = vel;
        Rad = rad;
    }

    void Move() {
        Pos[0] += Vel[0];
        Pos[1] += Vel[1];
    }

    void Move(double[] v) {
        Pos[0] += v[0];
        Pos[1] += v[1];
    }

    void rotateVel(double a, int i) {
        double x, y, n;
        double[] v, z;
        n = Utils.Norm(Vel);
        v = Utils.Normalize(Vel);
        x = Vel[0] - i * (a + 1) * Vel[1];
        y = Vel[1] + i * (a + 1) * Vel[0];
        z = Utils.Normalize(new double[]{x, y});
        Vel = new double[]{n * z[0], n * z[1]};
    }

    double[] DistanceV(Ball b) {
        return new double[]{
            Pos[0] - b.Pos[0],
            Pos[1] - b.Pos[1]
        };
    }

    double[] DistanceV(double[] b) {
        return new double[]{
            Pos[0] - b[0],
            Pos[1] - b[1]
        };
    }

    int Distance(Ball b) {
        return Rad + b.Rad;
    }

    void CheckBorders(int w, int h) {
        if (Pos[0] >= w - Rad) {
            Pos[0] -= 2 * (Rad - (w - Pos[0]));
            Vel[0] *= -1;
        } else if (Pos[0] <= Rad) {
            Pos[0] += 2 * (Rad - Pos[0]);
            Vel[0] *= -1;
        }
        if (Pos[1] >= h - Rad) {
            Pos[1] -= 2 * (Rad - (h - Pos[1]));
            Vel[1] *= -1;
        } else if (Pos[1] <= Rad) {
            Pos[1] += 2 * (Rad - Pos[1]);
            Vel[1] *= -1;
        }
    }
}
