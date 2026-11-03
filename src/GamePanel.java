package chachamala;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class GamePanel extends JPanel {
    private final GameModel model;
    private final Timer loopTimer;

    public GamePanel(JFrame parentFrame) {
        this.model = new GameModel();
        setPreferredSize(new Dimension(820, 620));
        setFocusable(true);

        GameController controller = new GameController(model, this);
        addMouseListener(controller);

        loopTimer = new Timer(50, e -> {
            if (model.getState() == GameState.PLAYING) {
                model.updateTimer(50);
                repaint();
            }
        });
        loopTimer.start();
    }

    public GameModel getModel() {
        return model;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        if (model.getState() == GameState.MENU) {
            drawMenu(g2);
        } else if (model.getState() == GameState.SETTINGS) {
            drawSettings(g2);
        } else if (model.getState() == GameState.DIALOGUE) {
            drawDialogue(g2);
        } else if (model.getState() == GameState.DAY_GOAL_BRIEF) {
            drawDayBrief(g2);
        } else if (model.getState() == GameState.PLAYING) {
            drawGameplay(g2);
        } else if (model.getState() == GameState.SUMMARY) {
            drawSummary(g2);
        }
    }

    private void drawSkyBackground(Graphics2D g) {
        g.setColor(new Color(153, 204, 255));
        g.fillRect(0, 0, getWidth(), getHeight());

        BufferedImage bgSky = GameAssets.getImg("bg_sky");
        if (bgSky != null) {
            g.drawImage(bgSky, 0, 0, getWidth(), getHeight(), null);
        }
    }

    private void drawMenu(Graphics2D g) {
        drawSkyBackground(g);

        g.setFont(GameAssets.getFont(46f));
        g.setColor(new Color(180, 40, 30));
        String title = "CHA CHA MALA";
        FontMetrics fm = g.getFontMetrics();
        int titleX = (getWidth() - fm.stringWidth(title)) / 2;
        g.drawString(title, titleX, 75);

        BufferedImage store = GameAssets.getImg("store");
        if (store != null) {
            int storeW = 480;
            int storeH = 380;
            int storeX = (getWidth() - storeW) / 2;
            g.drawImage(store, storeX, 90, storeW, storeH, null);
        }

        drawPixelBtn(g, 280, 495, 120, 45, "START", new Color(230, 235, 245));
        drawPixelBtn(g, 420, 495, 120, 45, "SETTINGS", new Color(230, 235, 245));
    }

    private void drawSettings(Graphics2D g) {
        drawSkyBackground(g);

        g.setColor(new Color(90, 50, 30));
        g.fillRoundRect(180, 120, 460, 360, 20, 20);
        g.setColor(Color.WHITE);
        g.setFont(GameAssets.getFont(24f));
        g.drawString("SETTINGS", 340, 170);

        g.setFont(GameAssets.getFont(18f));
        g.drawString("Music (BGM):", 240, 245);
        drawPixelBtn(g, 480, 220, 100, 35, GameAssets.bgmEnabled ? "ON" : "OFF", GameAssets.bgmEnabled ? Color.GREEN : Color.RED);

        g.drawString("Effects (SFX):", 240, 300);
        drawPixelBtn(g, 480, 275, 100, 35, GameAssets.sfxEnabled ? "ON" : "OFF", GameAssets.sfxEnabled ? Color.GREEN : Color.RED);

        drawPixelBtn(g, 350, 400, 120, 45, "BACK", Color.WHITE);
    }

    private void drawDialogue(Graphics2D g) {
        drawSkyBackground(g);

        BufferedImage owner = GameAssets.getImg("owner");
        if (owner != null) {
            g.drawImage(owner, 100, 200, 150, 220, null);
        }

        int bx = 260, by = 180, bw = 500, bh = 240;
        g.setColor(new Color(60, 35, 20));
        g.fillRoundRect(bx, by, bw, bh, 15, 15);
        g.setColor(new Color(245, 230, 205));
        g.fillRoundRect(bx + 8, by + 8, bw - 16, bh - 16, 10, 10);

        g.setColor(new Color(160, 40, 30));
        g.setFont(GameAssets.getFont(20f));
        g.drawString("[ Shop Owner ]", bx + 25, by + 40);

        g.setColor(Color.DARK_GRAY);
        g.setFont(GameAssets.getFont(15f));
        drawWrappedText(g, model.dialogueTexts[model.getDialogueIndex()], bx + 25, by + 75, bw - 50);

        if (model.getDialogueIndex() < 3) {
            g.setColor(Color.BLUE);
            g.drawString("[ Click to continue > ]", bx + 290, by + 210);
        } else {
            drawPixelBtn(g, 310, 470, 200, 45, "NEXT >", new Color(245, 150, 150));
        }
    }

    private void drawDayBrief(Graphics2D g) {
        drawSkyBackground(g);

        int popupW = 480;
        int popupH = 460;
        int popupX = (getWidth() - popupW) / 2;
        int popupY = 55;

        BufferedImage popup = GameAssets.getImg("popup");
        if (popup != null) {
            g.drawImage(popup, popupX, popupY, popupW, popupH, null);
        }

        g.setFont(GameAssets.getFont(26f));
        g.setColor(new Color(110, 45, 25));
        String title = "DAY " + model.getDay() + " GOAL";
        FontMetrics fmTitle = g.getFontMetrics();
        g.drawString(title, (getWidth() - fmTitle.stringWidth(title)) / 2, popupY + 130);

        g.setFont(GameAssets.getFont(17f));
        g.setColor(Color.DARK_GRAY);
        FontMetrics fmText = g.getFontMetrics();

        String line1 = "Target Revenue : $" + model.getDayTarget();
        String line2 = "Patience Limit : 8 Seconds / Customer";
        String line3 = "Deficit Limit  : Max -$1000 (Bankrupt)";

        g.drawString(line1, (getWidth() - fmText.stringWidth(line1)) / 2, popupY + 205);
        g.drawString(line2, (getWidth() - fmText.stringWidth(line2)) / 2, popupY + 255);
        g.drawString(line3, (getWidth() - fmText.stringWidth(line3)) / 2, popupY + 305);

        int btnW = 240;
        int btnH = 50;
        int btnX = (getWidth() - btnW) / 2;
        int btnY = popupY + 375;
        drawPixelBtn(g, btnX, btnY, btnW, btnH, "START COOKING!", new Color(130, 210, 130));
    }

    private void drawGameplay(Graphics2D g) {
        int tableTopY = 387;

        BufferedImage bgIn = GameAssets.getImg("bg_instore");
        if (bgIn != null) {
            g.drawImage(bgIn, 0, 0, getWidth(), getHeight(), null);
        }

        BufferedImage cust = GameAssets.getImg(model.getCurrentCustomerKey());
        if (cust != null) {
            g.drawImage(cust, 305, tableTopY - 175, 155, 260, null);
        }

        if (bgIn != null) {
            int cropY = (int) (tableTopY * ((double) bgIn.getHeight() / getHeight()));
            int cropH = bgIn.getHeight() - cropY;
            g.drawImage(bgIn, 0, tableTopY, getWidth(), getHeight(), 0, cropY, bgIn.getWidth(), bgIn.getHeight(), null);
        }

        List<String> orderIngs = model.getOrderIngKeys();
        int trayCount = orderIngs.size();
        
        int bubbleW;
        int bubbleH = 120;
        int bubbleX;
        int bubbleY = 15;

        if (trayCount == 1) {
            bubbleW = 245;
            bubbleX = 360;
        } else if (trayCount == 2) {
            bubbleW = 320;
            bubbleX = 330;
        } else {
            bubbleW = 405;
            bubbleX = 295;
        }

        BufferedImage bubble = GameAssets.getImg("bubble");
        if (bubble != null) {
            g.drawImage(bubble, bubbleX, bubbleY, bubbleW, bubbleH, null);
        }

        int timerBarW = 135;
        int timerBarH = 10;
        int timerBarX = 315;
        int timerBarY = 155;
        g.setColor(Color.DARK_GRAY);
        g.fillRect(timerBarX, timerBarY, timerBarW, timerBarH);
        float ratio = (float) model.getCurrentPatienceMs() / model.getMaxPatienceMs();
        g.setColor(ratio > 0.4f ? new Color(50, 205, 50) : Color.RED);
        g.fillRect(timerBarX, timerBarY, (int) (timerBarW * Math.max(0, ratio)), timerBarH);
        g.setColor(Color.WHITE);
        g.drawRect(timerBarX, timerBarY, timerBarW, timerBarH);

        int centerY = bubbleY + 50;
        int startIngX = bubbleX + 38; 
        int itemGap = 84;            

        g.setFont(GameAssets.getFont(17f));
        g.setColor(Color.BLACK);
        FontMetrics fmText = g.getFontMetrics();

        for (int i = 0; i < trayCount; i++) {
            String ingKey = orderIngs.get(i);
            BufferedImage reqIng = GameAssets.getImg(ingKey);
            if (reqIng != null) {
                boolean isSpecialTray = ingKey.equals("pork") || ingKey.equals("tofu");
                int bw = isSpecialTray ? 60 : 50;
                int bh = isSpecialTray ? 64 : 54;
                int curX = startIngX + (i * itemGap);
                g.drawImage(reqIng, curX, centerY - (bh / 2), bw, bh, null);

                g.drawString("x 1", curX + bw + 4, centerY + (fmText.getAscent() / 2) - 2);
            }
        }

        BufferedImage reqSoup = GameAssets.getImg(model.getOrderSoupKey());
        if (reqSoup != null) {
            int soupX = startIngX + (trayCount * itemGap) + 4;
            int sw = 64;
            int sh = 52;
            g.drawImage(reqSoup, soupX, centerY - (sh / 2), sw, sh, null);
        }

        int moneyBoxX = 72;
        int moneyBoxY = 62;
        int moneyBoxW = 120;
        int moneyBoxH = 34;

        g.setFont(GameAssets.getFont(24f));
        g.setColor(model.getMoney() < 0 ? Color.RED : Color.WHITE);
        String moneyStr = "$ " + model.getMoney();
        FontMetrics fmMoney = g.getFontMetrics();
        int moneyTextX = moneyBoxX + (moneyBoxW - fmMoney.stringWidth(moneyStr)) / 2;
        int moneyTextY = moneyBoxY + ((moneyBoxH - fmMoney.getHeight()) / 2) + fmMoney.getAscent();
        g.drawString(moneyStr, moneyTextX, moneyTextY);

        g.setFont(GameAssets.getFont(14f));
        g.setColor(new Color(255, 255, 200));
        g.drawString("Goal: $" + model.getDayEarnings() + " / $" + model.getDayTarget(), 25, 125);

        drawPixelBtn(g, 705, 20, 85, 35, "MENU", new Color(245, 235, 220));

        int[][] ingPos = {
            {45, 375}, {118, 360}, {205, 375},
            {45, 460}, {125, 460}, {197, 448}
        };
        int[][] ingSizes = {
            {70, 74}, {88, 94}, {70, 74},
            {70, 74}, {70, 74}, {88, 94}
        };
        for (int i = 0; i < 6; i++) {
            BufferedImage img = GameAssets.getImg(GameModel.INGREDIENT_KEYS[i]);
            if (img != null) {
                g.drawImage(img, ingPos[i][0], ingPos[i][1], ingSizes[i][0], ingSizes[i][1], null);
            }
        }

        BufferedImage gas = GameAssets.getImg("gas");
        if (gas != null) {
            g.drawImage(gas, 325, 415, 120, 90, null);
        }

        if (model.getPotOnStoveKey() != null) {
            BufferedImage pImg = GameAssets.getImg(model.getPotOnStoveKey());
            if (pImg != null) {
                g.drawImage(pImg, 340, 400, 90, 70, null);
            }
            List<String> inPot = model.getIngredientsInPot();
            for (int k = 0; k < inPot.size(); k++) {
                BufferedImage item = GameAssets.getImg(inPot.get(k));
                if (item != null) {
                    int offsetX = 352 + ((k % 3) * 18);
                    int offsetY = 410 + ((k / 3) * 12);
                    g.drawImage(item, offsetX, offsetY, 22, 22, null);
                }
            }
        }

        int[][] soupPos = {{460, 375}, {550, 375}, {505, 445}};
        for (int i = 0; i < 3; i++) {
            BufferedImage sImg = GameAssets.getImg(GameModel.SOUP_KEYS[i]);
            if (sImg != null) {
                g.drawImage(sImg, soupPos[i][0], soupPos[i][1], 88, 65, null);
            }
        }

        BufferedImage cr = GameAssets.getImg("cash_reg");
        if (cr != null) {
            g.drawImage(cr, 625, 380, 135, 150, null);
        }
    }

    private void drawSummary(Graphics2D g) {
        drawSkyBackground(g);

        int popupW = 480;
        int popupH = 460;
        int popupX = (getWidth() - popupW) / 2;
        int popupY = 55;

        BufferedImage popup = GameAssets.getImg("popup");
        if (popup != null) {
            g.drawImage(popup, popupX, popupY, popupW, popupH, null);
        }

        g.setFont(GameAssets.getFont(24f));
        g.setColor(new Color(110, 45, 25));
        String title = (model.getMoney() <= -1000) ? "GAME OVER" : "DAILY SUMMARY";
        FontMetrics fmTitle = g.getFontMetrics();
        g.drawString(title, (getWidth() - fmTitle.stringWidth(title)) / 2, popupY + 130);

        g.setFont(GameAssets.getFont(16f));
        g.setColor(Color.DARK_GRAY);
        FontMetrics fmText = g.getFontMetrics();

        String line1 = "Day: " + model.getDay();
        String line2 = "Today's Earnings: $" + model.getDayEarnings();
        String line3 = "Customers Served: " + model.getCustomersServedToday();

        g.drawString(line1, (getWidth() - fmText.stringWidth(line1)) / 2, popupY + 175);
        g.drawString(line2, (getWidth() - fmText.stringWidth(line2)) / 2, popupY + 215);
        g.drawString(line3, (getWidth() - fmText.stringWidth(line3)) / 2, popupY + 255);

        g.setColor(model.getMoney() < 0 ? Color.RED : new Color(0, 120, 0));
        String line4 = "Total Balance: $" + model.getMoney();
        g.drawString(line4, (getWidth() - fmText.stringWidth(line4)) / 2, popupY + 295);

        int btnW = 200;
        int btnH = 45;
        int btnX = (getWidth() - btnW) / 2;
        int btnY = popupY + 375;

        if (model.getMoney() <= -1000) {
            g.setColor(Color.RED);
            String overMsg = "Bankrupt (-$1000)! Game Over.";
            g.drawString(overMsg, (getWidth() - fmText.stringWidth(overMsg)) / 2, popupY + 335);
            drawPixelBtn(g, btnX, btnY, btnW, btnH, "RESTART", new Color(240, 120, 120));
        } else if (model.getDayEarnings() >= model.getDayTarget()) {
            drawPixelBtn(g, btnX, btnY, btnW, btnH, "NEXT DAY", new Color(130, 210, 130));
        } else {
            drawPixelBtn(g, btnX, btnY, btnW, btnH, "RESTART", new Color(240, 120, 120));
        }
    }

    private void drawPixelBtn(Graphics2D g, int x, int y, int w, int h, String text, Color bg) {
        g.setColor(bg);
        g.fillRect(x, y, w, h);
        g.setColor(new Color(60, 40, 30));
        g.setStroke(new BasicStroke(3));
        g.drawRect(x, y, w, h);

        g.setFont(GameAssets.getFont(15f));
        FontMetrics fm = g.getFontMetrics();
        int tx = x + (w - fm.stringWidth(text)) / 2;
        int ty = y + ((h - fm.getHeight()) / 2) + fm.getAscent();
        g.drawString(text, tx, ty);
    }

    private void drawWrappedText(Graphics2D g, String text, int x, int y, int maxWidth) {
        FontMetrics fm = g.getFontMetrics();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int curY = y;
        for (int i = 0; i < words.length; i++) {
            if (fm.stringWidth(line.toString() + words[i] + " ") < maxWidth) {
                line.append(words[i]).append(" ");
            } else {
                g.drawString(line.toString(), x, curY);
                curY += fm.getHeight() + 4;
                line = new StringBuilder(words[i]).append(" ");
            }
        }
        if (line.length() > 0) {
            g.drawString(line.toString(), x, curY);
        }
    }
}