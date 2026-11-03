package chachamala;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class GameAssets {
    private static Font pixelFont;
    private static final Map<String, BufferedImage> images = new HashMap<String, BufferedImage>();
    private static Clip bgmClip;
    public static boolean bgmEnabled = true;
    public static boolean sfxEnabled = true;

    public static void init() {
        String[] fontPaths = {
            "fonts/TA GBA monothai_MAX.ttf",
            "assets/fonts/TA GBA monothai_MAX.ttf",
            "TA GBA monothai_MAX.ttf"
        };
        for (int i = 0; i < fontPaths.length; i++) {
            try {
                File fontFile = new File(fontPaths[i]);
                if (fontFile.exists()) {
                    pixelFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
                    GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(pixelFont);
                    break;
                }
            } catch (Exception ignored) {}
        }
        if (pixelFont == null) {
            pixelFont = new Font("Monospaced", Font.BOLD, 14);
        }

        String[] imgNames = {
            "bg_sky", "bg_instore", "store", "bubble", "coin", "popup",
            "gas", "cash_reg", "owner",
            "cust_boy", "cust_girl", "cust_man", "cust_woman",
            "soup_mala", "soup_bone", "soup_tomato",
            "meat", "pork", "meatball", "mushroom", "veggie", "tofu"
        };

        for (int i = 0; i < imgNames.length; i++) {
            String name = imgNames[i];
            BufferedImage img = null;
            String[] tryPaths = {
                "images/" + name + ".png",
                "assets/images/" + name + ".png",
                name + ".png"
            };

            for (int p = 0; p < tryPaths.length; p++) {
                try {
                    File f = new File(tryPaths[p]);
                    if (f.exists()) {
                        img = ImageIO.read(f);
                        break;
                    }
                } catch (Exception ignored) {}
            }

            if (img == null) {
                try {
                    java.net.URL url = GameAssets.class.getResource("/images/" + name + ".png");
                    if (url == null) {
                        url = GameAssets.class.getResource("/assets/images/" + name + ".png");
                    }
                    if (url != null) {
                        img = ImageIO.read(url);
                    }
                } catch (Exception ignored) {}
            }

            if (img != null) {
                images.put(name, img);
            }
        }
    }

    public static Font getFont(float size) {
        if (pixelFont != null) {
            return pixelFont.deriveFont(Font.PLAIN, size);
        }
        return new Font("Monospaced", Font.BOLD, (int) size);
    }

    public static BufferedImage getImg(String key) {
        return images.get(key);
    }

    public static void playSound(final String fileName) {
        if (!sfxEnabled) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File f = new File("audio/" + fileName);
                    if (!f.exists()) {
                        f = new File("assets/audio/" + fileName);
                    }
                    if (f.exists()) {
                        AudioInputStream stream = AudioSystem.getAudioInputStream(f);
                        Clip clip = AudioSystem.getClip();
                        clip.open(stream);
                        clip.start();
                    }
                } catch (Exception ignored) {}
            }
        }).start();
    }

    public static void startBGM() {
        if (!bgmEnabled || bgmClip != null) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File f = new File("audio/bgm.wav");
                    if (!f.exists()) {
                        f = new File("assets/audio/bgm.wav");
                    }
                    if (f.exists()) {
                        AudioInputStream s = AudioSystem.getAudioInputStream(f);
                        bgmClip = AudioSystem.getClip();
                        bgmClip.open(s);
                        bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
                        bgmClip.start();
                    }
                } catch (Exception ignored) {}
            }
        }).start();
    }

    public static void stopBGM() {
        if (bgmClip != null) {
            bgmClip.stop();
            bgmClip.close();
            bgmClip = null;
        }
    }
}