package symulator;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import javax.swing.*;
import java.util.List;
import javax.swing.event.MouseInputAdapter;

/**
 * Symulator paniki Projekt za czekoladki
 */
/**
 * v0.1 - 29.12 - Adrian szielet aplikacji (okienko, moĹĽliwoĹ›Ä‡ ustawienia
 * klatek/s) losowanie dowolnej iloĹ›ci kulek ktĂłre podÄ…ĹĽajÄ… za kursorem
 *
 * v0.1.5 - 3.01 - Adrian odbicia kulek - wersja z prostÄ… stycznÄ… (Ĺ›rednio
 * dziaĹ‚ajÄ…, czasem siÄ™ nakĹ‚adajÄ…) opcja dodatkowego podÄ…ĹĽania za
 * wskaĹşnikiem myszki
 *
 * 31.01 - Michał, simplified velocity vector to consist of 2 components instead
 * of normalized 2 + speed; new elastic collision model (mass not included);
 * fixed behavior when meeting borders; added visual indicators for velocity
 * vector
 *
 * problems: * kinetic energy is changing on collision
 */
public class Symulator extends JPanel {
    // Rozmiar okna

    static int SZEROKOSC_OKNA = 600;
    static int WYSOKOSC_OKNA = 600;
    private Point mouseCoords = new Point();
    private boolean mouseOnScreen = false;
    // Kulki
    int j = 100; //iloĹ›Ä‡ kulek
    List<Ball> Balls;
//    Ball[] Ball = new Ball[j];
    static int klatki = 60; // Liczba klatek/ramek na sekundÄ™

    // Konstruktor do tworzenia komponentĂłw
    public void generateBalls(int j) {
        while (j-- > 0) {
            Balls.add(new Ball(
                    SZEROKOSC_OKNA,
                    WYSOKOSC_OKNA,
                    (int) (Math.random() * 5 + 10),
                    new Point2D.Double(Math.random() * (2) - 1, Math.random() * (2) - 1)
            ));
        }
    }

    public void CheckCollision(int i, int k) {
        Point2D.Double pd, pdp, term;
        pd = Balls.get(i).DistanceV(Balls.get(k));
        double pdn = Utils.Norm(pd);
        pd = Utils.Normalize(pd);
        //zderzenia
        if (pdn < Balls.get(i).Distance(Balls.get(k))) {

            //odpychamy od siebie kulki wzdłuż prostej łączącej obydwa środki
            Balls.get(i).Move(new Point2D.Double(pd.getX() * (Balls.get(k).Rad - pdn / 2 + 1), pd.getY() * (Balls.get(k).Rad - pdn / 2 + 1)));
            Balls.get(k).Move(new Point2D.Double(-pd.getX() * (Balls.get(i).Rad - pdn / 2 + 1), -pd.getY() * (Balls.get(i).Rad - pdn / 2 + 1)));
//            //nowa baza ortonormalna
//            pdp = new double[]{-pd[1], pd[0]};
//            //os rownolegla do prostej przechadzacej przez srodki kul = pd
//            //os prostopadla = pdp
//            //przy zderzeniu zamieniamy składowe równoległe
//            term = Ball[i].Vel;
//            Ball[i].Vel = new double[]{
//                pd[0] * Utils.Scalar(pd, Ball[k].Vel) + pdp[0] * Utils.Scalar(pdp, Ball[i].Vel),
//                pd[1] * Utils.Scalar(pd, Ball[k].Vel) + pdp[1] * Utils.Scalar(pdp, Ball[i].Vel)
//            };
//            Ball[k].Vel = new double[]{
//                pd[0] * Utils.Scalar(pd, term) + pdp[0] * Utils.Scalar(pdp, Ball[k].Vel),
//                pd[1] * Utils.Scalar(pd, term) + pdp[1] * Utils.Scalar(pdp, Ball[k].Vel)
//            };
//            term = null;
        }
    }

    public Symulator() {
        setPreferredSize(new Dimension(SZEROKOSC_OKNA, WYSOKOSC_OKNA));

        // obsĹ‚uga myszy
        addMouseMotionListener(new MouseInputAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseCoords = e.getPoint();
                mouseOnScreen = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                //mouseCoords = null;
                mouseOnScreen = false;
                repaint();
            }
        }
        );

        Thread symulatorWatek;
        symulatorWatek = new Thread() {
            public void run() {
                generateBalls(j);
                while (true) {
                    for (Ball b : Balls) {
                        b.Move();
                    }
                    for (int i = 0; i < j; i++) {
                        for (int k = i + 1; k < j; k++) {
                            CheckCollision(i, k);
                        }
                        Balls.get(i).CheckBorders(SZEROKOSC_OKNA, WYSOKOSC_OKNA);
                        if (mouseOnScreen) {
                            Point2D.Double d, dp;
                            double a, b;
                            d = Utils.Normalize(Balls.get(i).DistanceV(mouseCoords));
                            dp = new Point2D.Double(-d.getY(), d.getX());
                            a = Utils.Scalar(d, Utils.Normalize(Balls.get(i).Vel));
                            b = Utils.Scalar(dp, Utils.Normalize(Balls.get(i).Vel));
                            Balls.get(i).rotateVel(a, b > 0 ? 1 : -1);
                        }
                    }

                    repaint();

                    try {
                        Thread.sleep(1000 / klatki);
                    } catch (InterruptedException ex) {
                    }
                }
            }
        };
        symulatorWatek.start();  // powrĂłt do run();
    }

    // Rysowanie |JPanel
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);    // super. - przywoĹ‚ujemy razem z tym co robiĹ‚a dotychczas, tutaj rysuje tĹ‚o

        // Rysowanie okna
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, SZEROKOSC_OKNA, WYSOKOSC_OKNA);
        g.setColor(Color.BLUE);
        // Rysowanie kulek
        for (int i = 0; i < j; i++) {
            g.drawOval(
                    (int) (Balls.get(i).Pos.getX() - Balls.get(i).Rad),
                    (int) (Balls.get(i).Pos.getY() - Balls.get(i).Rad),
                    2 * (int) Balls.get(i).Rad,
                    2 * (int) Balls.get(i).Rad
            );
        }
        g.setColor(Color.RED);
        for (int i = 0; i < j; i++) {
            g.drawLine(
                    (int) Balls.get(i).Pos.getX(),
                    (int) Balls.get(i).Pos.getY(),
                    (int) (Balls.get(i).Pos.getX() + 10 * Balls.get(i).Vel.getX()),
                    (int) (Balls.get(i).Pos.getY() + 10 * Balls.get(i).Vel.getY())
            );
        }
//        g.setColor(Color.ORANGE);
//        for (int i = 0; i < j; i++) {
//            g.drawLine((int) Ball[i].Pos[0], (int) Ball[i].Pos[1], (int) (Ball[i].Pos[0]), (int) (Ball[i].Pos[1] + 10 * Ball[i].Vel[1]));
//            g.drawLine((int) Ball[i].Pos[0], (int) Ball[i].Pos[1], (int) (Ball[i].Pos[0] + 10 * Ball[i].Vel[0]), (int) (Ball[i].Pos[1]));
//        }
        if (mouseOnScreen) {
//            g.setColor(Color.YELLOW);
//            g.fillOval((int) mouseCoords.getX() - 2, (int) mouseCoords.getY() - 2, 4, 4);
            g.setColor(Color.GREEN);
            for (int i = 0; i < j; i++) {
                Point2D.Double d;
                double a;
                d = Utils.Normalize(Balls.get(i).DistanceV(mouseCoords));
                a = Utils.Scalar(d, Utils.Normalize(Balls.get(i).Vel));
                g.drawLine(
                        (int) Balls.get(i).Pos.getX(),
                        (int) Balls.get(i).Pos.getY(),
                        (int) (Balls.get(i).Pos.getX() - 10 * (1 + a) * d.getX()),
                        (int) (Balls.get(i).Pos.getY() - 10 * (1 + a) * d.getY())
                );

            }

        }
    }

    // GĹ‚Ăłwny program
    public static void main(String[] args) {
        // Event Dispatcher Thread - wszystkie aktualizacje przesyĹ‚ane do programu muszÄ… siÄ™ tutaj znaleĹşÄ‡
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Tworzymy okno aplikacji (swing)
                JFrame okno = new JFrame("Symulator paniki");
                Symulator panel = new Symulator();
                okno.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                okno.setContentPane(panel);
                okno.pack();
                okno.setVisible(true);
            }
        });
    }
}
