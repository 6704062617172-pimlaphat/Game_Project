package chachamala;

import javax.swing.*;

public class ChaChaMala extends JFrame {

    public ChaChaMala() {
        GameAssets.init();
        GameAssets.startBGM();

        setTitle("Cha Cha Mala");
        setSize(820, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        GamePanel panel = new GamePanel(this);
        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ChaChaMala().setVisible(true);
            }
        });
    }
}