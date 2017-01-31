package symulator;

import java.awt.*;
import java.awt.event.MouseEvent;
import javax.swing.*;
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
    // Kulki
    int j = 200; //iloĹ›Ä‡ kulek
    Ball[] Ball = new Ball[j];
    static int klatki = 30; // Liczba klatek/ramek na sekundÄ™

    // Konstruktor do tworzenia komponentĂłw
    public void generateBalls(int j) {
        for (int i = 0; i < j; i++) {
            Ball[i] = new Ball(SZEROKOSC_OKNA, WYSOKOSC_OKNA, (int) (Math.random() * 10 + 5), new double[]{Math.random() * (6) - 3, Math.random() * (6) - 3});
        }
    }

    public Symulator() {
        setPreferredSize(new Dimension(SZEROKOSC_OKNA, WYSOKOSC_OKNA));

        // obsĹ‚uga myszy
        addMouseMotionListener(new MouseInputAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseCoords = e.getPoint();
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mouseCoords = null;
                repaint();
            }
        }
        );

        Thread symulatorWatek;
        symulatorWatek = new Thread() {
            public void run() {
                generateBalls(j);
                double[] pd, pdp, term;
                while (true) {
                    //zmiany w ramce
                    //obliczanie pozycji kulek
                    for (int p = 0; p < j; p++) {
                        Ball[p].Move();
                    }
                    for (int i = 0; i < j; i++) {
                        for (int k = i + 1; k < j; k++) {
                            pd = Ball[i].DistanceV(Ball[k]);
                            double pdn = Utils.Norm(pd);
                            pd = Utils.Normalize(pd);
                            //zderzenia
                            if (pdn <= Ball[i].Distance(Ball[k])) {

                                //odpychamy od siebie kulki wzdłuż prostej łączącej obydwa środki
                                Ball[i].Move(new double[]{pd[0] * (Ball[k].Rad - pdn / 2), pd[1] * (Ball[k].Rad - pdn / 2)});
                                Ball[k].Move(new double[]{-pd[0] * (Ball[i].Rad - pdn / 2), -pd[1] * (Ball[i].Rad - pdn / 2)});
                                //nowa baza ortonormalna
                                pdp = new double[]{-pd[1], pd[0]};
                                //os rownolegla = pd
                                //os prostopadla = pdp
                                //przy zderzeniu zamieniamy składowe równoległe
                                term = Ball[i].Vel;
                                Ball[i].Vel = new double[]{
                                    pd[0] * Utils.Scalar(pd, Ball[k].Vel) + pdp[0] * Utils.Scalar(pdp, Ball[i].Vel),
                                    pd[1] * Utils.Scalar(pd, Ball[k].Vel) + pdp[1] * Utils.Scalar(pdp, Ball[i].Vel)
                                };
                                Ball[k].Vel = new double[]{
                                    pd[0] * Utils.Scalar(pd, term) + pdp[0] * Utils.Scalar(pdp, Ball[k].Vel),
                                    pd[1] * Utils.Scalar(pd, term) + pdp[1] * Utils.Scalar(pdp, Ball[k].Vel)
                                };
                                term = null;
                            }
                        }
                        //odbicia od krawędzi
                        Ball[i].CheckBorders(SZEROKOSC_OKNA, WYSOKOSC_OKNA);
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
            g.fillOval((int) (Ball[i].Pos[0] - Ball[i].Rad), (int) (Ball[i].Pos[1] - Ball[i].Rad), 2 * (int) Ball[i].Rad, 2 * (int) Ball[i].Rad);
        }
        g.setColor(Color.RED);
        for (int i = 0; i < j; i++) {
            g.drawLine((int) Ball[i].Pos[0], (int) Ball[i].Pos[1], (int) (Ball[i].Pos[0] + 10 * Ball[i].Vel[0]), (int) (Ball[i].Pos[1] + 10 * Ball[i].Vel[1]));
        }
        g.setColor(Color.YELLOW);
        for (int i = 0; i < j; i++) {
            g.drawLine((int) Ball[i].Pos[0], (int) Ball[i].Pos[1], (int) (Ball[i].Pos[0]), (int) (Ball[i].Pos[1] + 10 * Ball[i].Vel[1]));
            g.drawLine((int) Ball[i].Pos[0], (int) Ball[i].Pos[1], (int) (Ball[i].Pos[0] + 10 * Ball[i].Vel[0]), (int) (Ball[i].Pos[1]));
        }
        //kulka na myszy
//        g.setColor(Color.YELLOW);
//        g.fillOval((int) mouseCoords.getX() - 10, (int) mouseCoords.getY() - 10, 20, 20);
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
