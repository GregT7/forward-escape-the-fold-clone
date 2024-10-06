import java.awt.*;
import javax.swing.*;

// this class needed additional functionality not shared by other cards so inheritance is used here
public class Character extends Card {
    // value for character is current hp
    protected int gold = 0;
    protected int mana = 0;
    protected int abilityCost = 4;
    protected int abilityDamage = 4;
    protected int max_health;
    protected static ImageIcon characterIcon = new ImageIcon("../assets/Characters/Edward.png");
    protected JLabel goldLabel, manaLabel;
    protected boolean isAlive = true;

    // constructor for the player, has two additional labels for gold and mana
    public Character(int row, int col, int value, String type) {
        super(row, col, value, type, characterIcon);
        max_health = value;
        valueLabel.setText(labelString + value + " / " + max_health);
        createGoldLabel();
        createManaLabel();
    }

    // used for creating a deep copy of a character, this was intended to be used for maintaining one singular
    // player across multiple levels but didn't get far enough to implement multiple levels.
    // the idea was to create a character in main and use a static method to pass a copy of the starting character
    // to the Level and store the player as a static object to be shared across all of the levels
    public Character(Character c) {
        super(c.getRow(), c.getCol(), c.getValue(), c.getType(), characterIcon);
        max_health = value;
        valueLabel.setText(labelString + value + " / " + max_health);
        createGoldLabel();
        createManaLabel();
    }

    // overrides the card's updateLabel method so that the new gold and mana labels are also updated
    @Override
    public void updateLabel() {
        int width = cardWidth;
        if (value < max_health) width = (int)((double)value / max_health * cardWidth);
        
        valueLabel.setBounds(this.getLocation().x, this.getLocation().y - 20, width, 20);
        valueLabel.setText(labelString + value + " / " + max_health);
        goldLabel.setBounds(this.getLocation().x, this.getLocation().y + cardHeight, cardWidth/2, 20);
        goldLabel.setText("Gold: " + gold);
        manaLabel.setBounds(goldLabel.getLocation().x + cardWidth / 2, this.getLocation().y + cardHeight, cardWidth/2, 20);
        manaLabel.setText("Mana: " + mana + " / " + abilityCost);
    }

    // creates gold label and is called in the constructor for this class
    public void createGoldLabel() {
        goldLabel = new JLabel();
        goldLabel.setBounds(this.getLocation().x, this.getLocation().y + cardHeight, cardWidth/2, 20);
        goldLabel.setBackground(new Color(217, 160, 2));
        goldLabel.setOpaque(true);
        goldLabel.setText("Gold: " + gold);
        goldLabel.setForeground(Color.WHITE);
        goldLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    // creates mana label and is called in the constructor for this class
    // there is no cap on the amount of mana a player can collect, the / 4 just specifies the cost of the ability
    public void createManaLabel() {
        manaLabel = new JLabel();
        manaLabel.setBounds(goldLabel.getLocation().x + cardWidth / 2, this.getLocation().y + cardHeight, cardWidth/2, 20);
        manaLabel.setBackground(new Color(121, 5, 179));
        manaLabel.setOpaque(true);
        manaLabel.setText("Mana: " + mana + " / " + abilityCost);
        manaLabel.setForeground(Color.WHITE);
        manaLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    // subtracts the player's health from the monster's current hp
    protected void attack(Card monster) {
        value -= monster.getValue();
    }

    // adds health back to player but ensures that the health does not exceed the current maximum
    protected void drinkHealthPotion(Card potion) {
        value += potion.getValue();
        if (value > max_health) value = max_health;
    }

    // adds mana points to player
    protected void drinkManaPotion(Card potion) {
        mana += potion.getValue();
    }

    // adds gold to player
    protected void collectGold(Card gold) {
        this.gold += gold.getValue();
    }

    // increases max health, this occurs when the interacting card in Level class is a heart container or type "HC"
    public void increaseMaxHealth() {
        ++value;
        ++max_health;
    }

    // updates current mana cost
    public void useAbility() {
        mana -= abilityCost;
    }

    // flips the boolean for player's isAlive field, this was inteded for use in future implementation
    public void dies() {
        isAlive = false;
    }

    // getters
    protected JLabel getGoldLabel() {
        return goldLabel;
    }
    
    protected JLabel getManaLabel() {
        return manaLabel;
    }

    public int getMana() {
        return mana;
    }

    public int getGold() {
        return gold;
    }

    public int getAbilityCost() {
        return abilityCost;
    }

    public int getAbilityDamage() {
        return abilityDamage;
    }

    public boolean isAlive() {
        return isAlive;
    }
}
