import java.awt.*;
import javax.swing.*;

// This class is the backbone of the program where all cards on the board are created here
// it is just an extension of the JButton class
public class Card extends JButton{
    final static protected int cardWidth = 130;
    final static protected int cardHeight = 130;
    final static protected int xPadding = 30;
    final static protected int yPadding = 30;
    protected int row, col, value;
    protected String type, labelString;
    protected JLabel valueLabel;
    protected ImageIcon image;

    // constructor for creating a card to put onto the board
    // card types:
    //             G - gold, H - health potion, P - player, M - monster, B - boss, MP - mana potion, HC - heart container
    public Card(int row, int col, int value, String type, ImageIcon image) {
        this.row = row;
        this.col = col;
        this.value = value;
        this.type = type;
        this.image = image;
        // I didn't use a LayoutManager and manually set the positions
        setBounds(xPadding + (cardWidth + xPadding) * col, yPadding + row * (cardHeight + yPadding), cardWidth, cardHeight);
        setFocusable(false);
        setBackground(new Color(0, 163, 255));
        createLabel();

        Image img = image.getImage();
        Image newImg = img.getScaledInstance(cardWidth, cardHeight, java.awt.Image.SCALE_SMOOTH);
        setIcon(new ImageIcon(newImg));
    }

    // move this card to the passed card's destination
    public void move(Card destination) {
        this.setLocation(destination.getLocation().x,destination.getLocation().y);
    }

    // updates the card's row position and updates it's position on the board based on the new row value
    // also updates the accompanying label position and string text used for displaying the value of the card
    protected void adjustCard() {
        row +=1;
        setCardPosition();
        updateLabel();
    }

    // creates label that is used to display the value, this follows the card and is right above it
    protected void createLabel() {
        valueLabel = new JLabel();
        valueLabel.setBounds(this.getLocation().x, this.getLocation().y - 20, cardWidth, 20);
        Color backgroundColor = labelColorAndText(); //
        valueLabel.setBackground(backgroundColor);
        valueLabel.setText(labelString + value);
        valueLabel.setOpaque(true);
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    // determines the color and text of the card's label (if the card is a gold card, then make the label yellow)
    protected Color labelColorAndText() {
        Color returnColor;
        if (type.equals("G")) {
            labelString = "Gold: ";
            returnColor = new Color(217, 160, 2);
        } else if (type.equals("M") || type.equals("B") || type.equals("P")) {
            labelString = "HP: ";
            returnColor = Color.RED;
        } else if (type.equals("H")) {
            labelString = "Heals: ";
            returnColor = new Color(255, 46, 95);
        } else if (type.equals("MP")) {
            labelString = "Mana: ";
            returnColor = new Color(177, 73, 230);
        } else if (type.equals("HC")) {
            labelString = "Heart Container: ";
            returnColor = new Color(240, 158, 176);
        } 
        // default label text and color if there is no matching type
        else {
            labelString = "Value: ";
            returnColor = Color.GRAY;
        }

        return returnColor;
    }

    // updates the label's location and text
    public void updateLabel() {
        valueLabel.setBounds(this.getLocation().x, this.getLocation().y - 20, cardWidth, 20);
        valueLabel.setText(labelString + value);
    }

    // used for the player's ability where all of the monster card's hp and damage is reduced
    public void reduceValue(int value) {
        this.value -= value;
    }

    // getters
    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public String getLabelString() {
        return labelString;
    }

    public JLabel getValueLabel() {
        return valueLabel;
    }

    public ImageIcon getImageIcon() {
        return image;
    }

    // setters
    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setRowCol(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setCardPosition() {
        setLocation(xPadding + (cardWidth + xPadding) * col, yPadding + row * (cardHeight + yPadding));
    }
}
