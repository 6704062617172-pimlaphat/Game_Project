package chachamala;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameModel {
    public static final String[] CUSTOMER_KEYS = {"cust_boy", "cust_girl", "cust_man", "cust_woman"};
    public static final String[] SOUP_KEYS = {"soup_mala", "soup_bone", "soup_tomato"};
    public static final String[] INGREDIENT_KEYS = {"meat", "pork", "meatball", "mushroom", "veggie", "tofu"};

    public final String[] dialogueTexts = {
        "Hey! I am the owner of Cha Cha Mala. My back is aching, so you are taking over the business!",
        "Check the order bubble above the customer. Select the right soup pot to put on the stove, then add ingredients.",
        "When cooked, click the cash register to serve and receive cash. Don't make wrong dishes!",
        "Are you ready to open the restaurant for Day 1?"
    };

    private GameState state = GameState.MENU;
    private int money = 0;
    private int day = 1;
    private int dayTarget = 750;
    private int dayEarnings = 0;
    private int customersServedToday = 0;
    private int dialogueIndex = 0;

    private String currentCustomerKey;
    private String orderSoupKey;
    private final List<String> orderIngKeys = new ArrayList<String>();

    private String potOnStoveKey = null;
    private final List<String> ingredientsInPot = new ArrayList<String>();
    private boolean isCooked = false;

    private final int MAX_PATIENCE_MS = 8000;
    private int currentPatienceMs = 8000;

    public void startDialogue() {
        dialogueIndex = 0;
        state = GameState.DIALOGUE;
        GameAssets.playSound("coin.wav");
    }

    public void advanceDialogue() {
        if (dialogueIndex < 3) {
            dialogueIndex++;
            GameAssets.playSound("coin.wav");
        }
    }

    public void prepareDayBrief() {
        dayTarget = 500 + (day * 250);
        dayEarnings = 0;
        customersServedToday = 0;
        state = GameState.DAY_GOAL_BRIEF;
    }

    public void startNewDay() {
        state = GameState.PLAYING;
        spawnNextCustomer();
    }

    public void spawnNextCustomer() {
        resetStove();
        Random rand = new Random();
        currentCustomerKey = CUSTOMER_KEYS[rand.nextInt(CUSTOMER_KEYS.length)];
        orderSoupKey = SOUP_KEYS[rand.nextInt(SOUP_KEYS.length)];

        orderIngKeys.clear();
        int maxTrays = 1;
        if (day == 2) {
            maxTrays = 1 + rand.nextInt(2);
        } else if (day >= 3) {
            maxTrays = 1 + rand.nextInt(3);
        }

        while (orderIngKeys.size() < maxTrays) {
            String selectedIng = INGREDIENT_KEYS[rand.nextInt(INGREDIENT_KEYS.length)];
            if (!orderIngKeys.contains(selectedIng)) {
                orderIngKeys.add(selectedIng);
            }
        }

        currentPatienceMs = MAX_PATIENCE_MS;
    }

    public void resetStove() {
        potOnStoveKey = null;
        ingredientsInPot.clear();
        isCooked = false;
    }

    public void addIngredient(String ingKey) {
        ingredientsInPot.add(ingKey);
    }

    public void updateTimer(int elapsedMs) {
        if (state == GameState.PLAYING) {
            currentPatienceMs -= elapsedMs;
            if (currentPatienceMs <= 0) {
                money -= 200;
                dayEarnings -= 200;
                GameAssets.playSound("wrong.wav");
                customersServedToday++;

                if (money <= -1000) {
                    state = GameState.SUMMARY;
                } else {
                    spawnNextCustomer();
                }
            }
        }
    }

    public boolean serveCurrentOrder() {
        boolean correctSoup = potOnStoveKey != null && potOnStoveKey.equals(orderSoupKey);
        
        boolean correctIng = true;
        for (String req : orderIngKeys) {
            if (!ingredientsInPot.contains(req)) {
                correctIng = false;
                break;
            }
        }

        if (correctSoup && correctIng) {
            int earn = orderIngKeys.size() * 125;
            money += earn;
            dayEarnings += earn;
        } else {
            money -= 300;
            dayEarnings -= 300;
        }

        customersServedToday++;

        if (money <= -1000 || dayEarnings >= dayTarget) {
            state = GameState.SUMMARY;
        } else {
            spawnNextCustomer();
        }
        return correctSoup && correctIng;
    }

    public void nextDay() {
        day++;
        prepareDayBrief();
    }

    public void restartFromDayOne() {
        money = 0;
        day = 1;
        prepareDayBrief();
    }

    public GameState getState() { return state; }
    public void setState(GameState state) { this.state = state; }
    public int getMoney() { return money; }
    public int getDay() { return day; }
    public int getDayTarget() { return dayTarget; }
    public int getDayEarnings() { return dayEarnings; }
    public int getCustomersServedToday() { return customersServedToday; }
    public int getDialogueIndex() { return dialogueIndex; }
    public String getCurrentCustomerKey() { return currentCustomerKey; }
    public String getOrderSoupKey() { return orderSoupKey; }
    public List<String> getOrderIngKeys() { return orderIngKeys; }
    public String getPotOnStoveKey() { return potOnStoveKey; }
    public void setPotOnStoveKey(String key) { this.potOnStoveKey = key; }
    public List<String> getIngredientsInPot() { return ingredientsInPot; }
    public boolean isCooked() { return isCooked; }
    public void setCooked(boolean cooked) { isCooked = cooked; }
    public int getCurrentPatienceMs() { return currentPatienceMs; }
    public int getMaxPatienceMs() { return MAX_PATIENCE_MS; }
}