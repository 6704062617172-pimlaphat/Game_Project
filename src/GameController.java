package chachamala;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameController extends MouseAdapter {
    private final GameModel model;
    private final GamePanel panel;

    public GameController(GameModel model, GamePanel panel) {
        this.model = model;
        this.panel = panel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        handleClick(e.getX(), e.getY());
        panel.repaint();
    }

    private void handleClick(int x, int y) {
        if (model.getState() == GameState.MENU) {
            if (x >= 280 && x <= 400 && y >= 495 && y <= 540) {
                model.startDialogue();
            }
            if (x >= 420 && x <= 540 && y >= 495 && y <= 540) {
                model.setState(GameState.SETTINGS);
                GameAssets.playSound("coin.wav");
            }
        } else if (model.getState() == GameState.SETTINGS) {
            if (x >= 480 && x <= 580 && y >= 220 && y <= 255) {
                GameAssets.bgmEnabled = !GameAssets.bgmEnabled;
                if (GameAssets.bgmEnabled) GameAssets.startBGM();
                else GameAssets.stopBGM();
            }
            if (x >= 480 && x <= 580 && y >= 275 && y <= 310) {
                GameAssets.sfxEnabled = !GameAssets.sfxEnabled;
            }
            if (x >= 350 && x <= 470 && y >= 400 && y <= 445) {
                model.setState(GameState.MENU);
                GameAssets.playSound("coin.wav");
            }
        } else if (model.getState() == GameState.DIALOGUE) {
            if (model.getDialogueIndex() < 3) {
                model.advanceDialogue();
            } else {
                if (x >= 310 && x <= 510 && y >= 470 && y <= 520) {
                    model.prepareDayBrief();
                }
            }
        } else if (model.getState() == GameState.DAY_GOAL_BRIEF) {
            int popupY = 55;
            int btnW = 240;
            int btnH = 50;
            int btnX = (panel.getWidth() - btnW) / 2;
            int btnY = popupY + 375;
            if (x >= btnX && x <= btnX + btnW && y >= btnY && y <= btnY + btnH) {
                model.startNewDay();
            }
        } else if (model.getState() == GameState.PLAYING) {
            if (x >= 705 && x <= 790 && y >= 20 && y <= 55) {
                GameAssets.playSound("coin.wav");
                model.setState(GameState.MENU);
                return;
            }

            int[][] soupPos = {{460, 375}, {550, 375}, {505, 445}};
            for (int i = 0; i < 3; i++) {
                int sx = soupPos[i][0], sy = soupPos[i][1];
                if (Math.hypot(x - (sx + 44), y - (sy + 32)) <= 44) {
                    model.setPotOnStoveKey(GameModel.SOUP_KEYS[i]);
                    GameAssets.playSound("coin.wav");
                    return;
                }
            }

            int[][] ingPos = {
                {45, 375}, {118, 360}, {205, 375},
                {45, 460}, {125, 460}, {197, 448}
            };
            int[][] ingSizes = {
                {70, 74}, {88, 94}, {70, 74},
                {70, 74}, {70, 74}, {88, 94}
            };
            for (int i = 0; i < 6; i++) {
                int ix = ingPos[i][0], iy = ingPos[i][1];
                int iw = ingSizes[i][0], ih = ingSizes[i][1];
                if (x >= ix && x <= ix + iw && y >= iy && y <= iy + ih) {
                    if (model.getPotOnStoveKey() != null) {
                        model.addIngredient(GameModel.INGREDIENT_KEYS[i]);
                        GameAssets.playSound("coin.wav");
                    } else {
                        JOptionPane.showMessageDialog(panel, "Select a soup pot onto the stove first!", "No Pot", JOptionPane.WARNING_MESSAGE);
                    }
                    return;
                }
            }

            if (x >= 325 && x <= 445 && y >= 415 && y <= 505) {
                if (model.getPotOnStoveKey() != null && !model.getIngredientsInPot().isEmpty()) {
                    model.setCooked(true);
                    GameAssets.playSound("coin.wav");
                }
                return;
            }

            if (x >= 625 && x <= 760 && y >= 380 && y <= 530) {
                if (model.getPotOnStoveKey() == null || model.getIngredientsInPot().isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "Ingredients not ready!", "Notice", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                boolean success = model.serveCurrentOrder();
                if (success) {
                    GameAssets.playSound("success.wav");
                } else {
                    GameAssets.playSound("wrong.wav");
                }
            }
        } else if (model.getState() == GameState.SUMMARY) {
            int popupY = 55;
            int btnW = 200;
            int btnH = 45;
            int btnX = (panel.getWidth() - btnW) / 2;
            int btnY = popupY + 375;

            if (x >= btnX && x <= btnX + btnW && y >= btnY && y <= btnY + btnH) {
                if (model.getMoney() > -1000 && model.getDayEarnings() >= model.getDayTarget()) {
                    model.nextDay();
                } else {
                    model.restartFromDayOne();
                }
            }
        }
    }
}