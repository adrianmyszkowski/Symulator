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

    static int SZEROKOSC_OKNA = 400;
    static int WYSOKOSC_OKNA = 400;
    private Point mouseCoords = new Point();
    // Kulki
    int j = 20; //iloĹ›Ä‡ kulek
    //pozycja
    double[] kulkaX = new double[j];
    double[] kulkaY = new double[j];
    //wektor prÄ™dkosci
    double[] kulkaVX = new double[j];
    double[] kulkaVY = new double[j];

    double[] kulkaR = new double[j];//promieĹ„
    //double[] kulkaM = new double[j];//masa

    //zmienne pomocnicze
    double mpx, mpy, mp, mvi1, mvi2, mvk1, mvk2;
    double cek, ocek; // do sprawdzania energii kineetycznej
    static int klatki = 30; // Liczba klatek/ramek na sekundÄ™

    // Konstruktor do tworzenia komponentĂłw
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

        for (int i = 0; i < j; i++) {
            kulkaR[i] = Math.floor(Math.random() * 2 + 10); // losowanie promienia z przedziaĹ‚u 10 - 15;
            kulkaX[i] = Math.floor(Math.random() * (SZEROKOSC_OKNA - 2 * kulkaR[i])) + kulkaR[i]; // losowanie x od 0 do szerokosc_okna;
            kulkaY[i] = Math.floor(Math.random() * (WYSOKOSC_OKNA - 2 * kulkaR[i])) + kulkaR[i]; // losowanie y od 0 do wysokosc_okna;
            //kulkaM[i] = Math.random() * (0.3) + 0.8; // losowanie masy 0.8 - 1.1;

            // losowanie wektora prÄ™dkoĹ›ci
            kulkaVX[i] = Math.random() * (4) - 2;
            kulkaVY[i] = Math.random() * (4) - 2;
        }

        Thread symulatorWatek;
        symulatorWatek = new Thread() {
            public void run() {
                while (true) {
                    //zmiany w ramce
                    //obliczanie pozycji kulek
                    ocek = cek > 0 ? cek : 0;
                    cek = 0; //całkowita energia kinetyczna układu, powinna się nie zmieniać pomimo zderzeń
                    for (int p = 0; p < j; p++) {
                        kulkaX[p] += kulkaVX[p];
                        kulkaY[p] += kulkaVY[p];
                        cek += Math.sqrt(Math.pow(kulkaVX[p], 2) + Math.pow(kulkaVY[p], 2));
                    }
                    if (cek != ocek) {
                        System.out.println("CEK: " + cek);
                    }
                    for (int i = 0; i < j; i++) {
                        for (int k = i + 1; k < j; k++) {
                            mpx = kulkaX[i] - kulkaX[k];
                            mpy = kulkaY[i] - kulkaY[k];
                            mp = Math.sqrt(Math.pow(mpx, 2) + Math.pow(mpy, 2));
                            mpx /= mp;
                            mpy /= mp;
                            //zderzenia
                            if (mp <= kulkaR[i] + kulkaR[k]) {

                                //odpychamy od siebie kulki wzdłuż prostej łączącej obydwa środki
                                kulkaX[i] += mpx * (kulkaR[k] - mp / 2);
                                kulkaY[i] += mpy * (kulkaR[k] - mp / 2);
                                kulkaX[k] -= mpx * (kulkaR[i] - mp / 2);
                                kulkaY[k] -= mpy * (kulkaR[i] - mp / 2);
                                //nowa baza ortonormalna
                                //os rownolegla = [mpx, mpy]
                                //os prostopadla = [-mpy, mpx]
                                //zapiszmy kulkaVX i kulkaVY w nowej bazie
                                mvi1 = kulkaVX[i] * mpx + kulkaVY[i] * mpy;
                                mvi2 = kulkaVY[i] * mpx - kulkaVX[i] * mpy;
                                mvk1 = kulkaVX[k] * mpx + kulkaVY[k] * mpy;
                                mvk2 = kulkaVY[k] * mpx - kulkaVX[k] * mpy;
                                //przy zderzeniu zamieniamy składowe równoległe
                                kulkaVX[i] = mpx * mvk1 - mpy * mvi2;
                                kulkaVY[i] = mpy * mvk1 + mpx * mvi2;
                                kulkaVX[k] = mpx * mvi1 - mpy * mvk2;
                                kulkaVY[k] = mpy * mvi1 + mpx * mvk2;

                            }
                        }
                        //odbicia od krawędzi
                        if (kulkaX[i] >= SZEROKOSC_OKNA - kulkaR[i]) {
                            kulkaX[i] -= 2 * (kulkaR[i] - (SZEROKOSC_OKNA - kulkaX[i]));
                            kulkaVX[i] *= -1;
                        } else if (kulkaX[i] <= kulkaR[i]) {
                            kulkaX[i] += 2 * (kulkaR[i] - kulkaX[i]);
                            kulkaVX[i] *= -1;
                        }
                        if (kulkaY[i] >= WYSOKOSC_OKNA - kulkaR[i]) {
                            kulkaY[i] -= 2 * (kulkaR[i] - (SZEROKOSC_OKNA - kulkaY[i]));
                            kulkaVY[i] *= -1;
                        } else if (kulkaY[i] <= kulkaR[i]) {
                            kulkaY[i] += 2 * (kulkaR[i] - kulkaY[i]);
                            kulkaVY[i] *= -1;
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
            g.fillOval((int) (kulkaX[i] - kulkaR[i]), (int) (kulkaY[i] - kulkaR[i]), 2 * (int) kulkaR[i], 2 * (int) kulkaR[i]);
        }
        g.setColor(Color.RED);
        for (int i = 0; i < j; i++) {
            g.drawLine((int) kulkaX[i], (int) kulkaY[i], (int) (kulkaX[i] + 10 * kulkaVX[i]), (int) (kulkaY[i] + 10 * kulkaVY[i]));
        }
        g.setColor(Color.YELLOW);
        for (int i = 0; i < j; i++) {
            g.drawLine((int) kulkaX[i], (int) kulkaY[i], (int) (kulkaX[i] + 10 * kulkaVX[i]), (int) kulkaY[i]);
            g.drawLine((int) kulkaX[i], (int) kulkaY[i], (int) kulkaX[i], (int) (kulkaY[i] + 10 * kulkaVY[i]));
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
