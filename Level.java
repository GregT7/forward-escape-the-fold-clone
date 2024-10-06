import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;

// implements the ActionListener for button clicking and KeyListener for spacebar hitting (uses the ability)
public class Level extends JPanel implements ActionListener, KeyListener {
    // all variables used in program
    protected final static int screenWidth = 525, screenHeight = 750;
    protected final static Random r = new Random();
    protected static ArrayList<ImageIcon> monsterImages= loadFileImages("../assets/Monsters/");
    protected static ArrayList<ImageIcon> bossImages = loadFileImages("../assets/Bosses/");
    protected static HashMap<String, ImageIcon> consumableImages = loadImagesHashMap("../assets/Consumables/");
    protected ArrayList<Card> cards = new ArrayList<Card>(); // current cards on board, does not include the player
    static protected Character player; // is static because it was inteded to eventually be used across multiple levels
    protected JProgressBar progressBar;
    protected JLabel progressBarLabel;
    protected JButton shop;
    protected static int levelNumber = 1;
    protected String[][] gameBoard;
    protected int boardRow, levelSize, currentRow = 0;
    protected boolean isComplete = false; // was added for future use
    protected JFrame frame; // frame defined in main, just so that events in this class can trigger the window to automatically
    // close

    // class constructor
    // sets up the board and it's core elements like the first cards on screen, the progress bar, the gameBoard, and
    // the panel itself
    public Level(int levelSize) {
        initializePanel(levelSize);
        initializeProgressBar();
        createBoard();
        populateBoard();
    }

    // sets up the panel
    protected void initializePanel(int levelSize) {
        this.setLayout(null);
        this.addKeyListener(this);
        this.setSize(screenWidth, screenHeight);
        this.setBackground(new Color(20,20,20));
        this.levelSize = levelSize;
    }

    // initializes the progress bar and adds to panel
    public void initializeProgressBar() {
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setBounds(Card.cardWidth + 2 * Card.xPadding, 685, Card.cardWidth, 25);
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("American Typewriter", Font.BOLD, 15));
        progressBar.setForeground(new Color(77, 20, 168));
        progressBar.setBackground(Color.BLACK);

        // creates a text label that sits above the progress bar to specify the progress bar's purpose
        progressBarLabel = new JLabel();
        progressBarLabel.setBounds(progressBar.getLocation().x, progressBar.getLocation().y-25, progressBar.getWidth(), 25);
        progressBarLabel.setText("Level Progression");
        progressBarLabel.setForeground(Color.WHITE);
        progressBarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        progressBarLabel.setVerticalAlignment(SwingConstants.BOTTOM);

        this.add(progressBar);
        this.add(progressBarLabel);
    }

    // update progress bar
    public void updateProgressBar() {
        int value = (int)(100*(double)currentRow / levelSize);
        progressBar.setValue(value);
    }

    // used in main to initialize the static player object that would have been used across multiple levels
    // didn't have enough time to implement multiple levels
    static public void initializePlayer(Character c) {
        player = new Character(c);
    }

    // accounts for all moving and interacting of the cards on the board alongside updating the board to populate
    // the next new row. The events being tracked are button clicks
    @Override
    public void actionPerformed(ActionEvent e) {
        // iterates through all current buttons on the board (ie in cards ArrayList) until it finds the right button
        // didn't make sense to have all cards be global variables as they are created and removed dynamically
        // so can't do e.getSource() == specificCard
        for (int i = 0; i < cards.size(); ++i) {
            // these booleans ensure that the selected card is in front of the player and not too many columns away
            // also ensures that the event triggered was a specific button click
            boolean isReachable = Math.abs(player.getRow() - cards.get(i).getRow()) == 1;
            isReachable = isReachable && Math.abs(player.getCol() - cards.get(i).getCol()) < 2;
            boolean isAhead = player.getRow() > cards.get(i).getRow();
            if (isReachable && isAhead && e.getSource() == cards.get(i)) {
                // updates currentRow and decides if isComplete is true, isComplete pushes for the game to finish once the cap row is
                // encountered
                if (++currentRow == levelSize) isComplete = true;
                player.move(cards.get(i)); // moves player to clicked card
                player.setRowCol(cards.get(i).getRow(), cards.get(i).getCol()); //updates player's row and col
                // determines what type of card was clicked and what to do (ie drink health potion, attack enemy)
                if (cards.get(i).type.equals("M") || cards.get(i).type.equals("B")) {
                    player.attack(cards.get(i));
                    if (player.getValue() <= 0) player.dies();
                     
                } else if (cards.get(i).type.equals("H")) {
                    player.drinkHealthPotion(cards.get(i));
                } else if (cards.get(i).type.equals("G")) {
                    player.collectGold(cards.get(i));
                } else if (cards.get(i).type.equals("HC")) {
                    player.increaseMaxHealth();
                } else if (cards.get(i).type.equals("MP")) {
                    player.drinkManaPotion(cards.get(i));
                }
                // updates the player's labels and progress bar
                player.updateLabel();
                updateProgressBar();
                this.removeCard(cards.get(i)); // removes card
                adjustBoard();
                // updates board until the end is met and there are no additional cards to populate the board with
                if (boardRow > -1) {
                    updateBoard();
                    --boardRow;
                }

                // if the player dies, display message and close the frame
                if (!player.isAlive) {
                    displayMessage("You've died! Game over! Relaunch program to play again...", 300);
                    frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                }

                // if the player reaches the end of the game, display message and close the frame
                if (isComplete) {
                    displayMessage("You've beaten the game!!!", 150);
                    frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                }
            }
            // update screen to ensure there are no bugs
            updateScreen();
        }
    }

    // used for displaying a dialog message for when you die or the game is completed prior to the frame being automatically closed
    static protected void displayMessage(String message, int width) {
        message = "<html><body><p style='" + width + ": 100px;'>" + message + "</p></body></html>";
        JOptionPane.showMessageDialog(null, message, "Instructions",JOptionPane.INFORMATION_MESSAGE);
    }

    // this method is used for using the ability
    // if the space bar is pressed and the player has enough mana, the ability will activate, dealing 4 damage points
    // to all visible enemies
    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_SPACE && player.getMana() >= player.getAbilityCost() && player.isAlive()) {
            player.useAbility(); // subtracts 4 mana points and updates the player's current mana
            player.updateLabel(); // updates the mana label
            // using int i = 0, ++i causes big problems when removing cards from the iterating array
            // iterates through all cards in cards ArrayList, searching for "M" and "B" typed cards (Monsters and Bosses)
            for (int i = cards.size() - 1; i >= 0; i--) {
                if (cards.get(i).getType().equals("M") || cards.get(i).getType().equals("B")) {
                    cards.get(i).reduceValue(player.getAbilityDamage());
                    cards.get(i).updateLabel();
                    // deletes cards with less than 1 hp and spawns a loot card in it's place
                    if (cards.get(i).getValue() < 1) {
                        generateNewCard(cards.get(i).getRow(), cards.get(i).getCol(), generateLootCardMarker());
                        this.removeCard(cards.get(i));
                    }
                }
            }
            updateScreen();
        }
    }
    

    // creates the String[][] gameBoard array that dictates what cards are put onto the board
    // creates the level's map
    protected void createBoard() {
        boardRow = levelSize - 4;
        String[][] board = new String[levelSize][3];
        for (int i = 1; i < levelSize; ++i) {
            board[i][0] = generateRandomCardMarker();
            board[i][1] = generateRandomCardMarker();
            board[i][2] = generateRandomCardMarker();
        }
        // the boss always spawns at the end with two empty spaces so you can't skip the boss
        board[0][0] = "X";
        board[0][1] = "B";
        board[0][2] = "X";
        gameBoard = board;
    }

    // used in creating the String[][] gameBoard array that stores the types of cards that need to be created and put onto the board
    // this method determines what one individual card's type will be
    protected String generateRandomCardMarker() {
        int rNum = r.nextInt(100) + 1;
        String cardType = "";
        if (rNum > 0 && rNum <= 50) {
            cardType = "M";
        } else if (rNum > 50 && rNum <=60) {
            cardType = "G";
        } else if (rNum > 60 && rNum <=85) {
            cardType = "H";
        } else if (rNum > 85 && rNum <=99) {
            cardType = "MP";
        } else if (rNum == 100) {
            cardType = "HC";
        } 
        return cardType;
    }

    // when the ability is used and kills enemies, a new loot card will spawn in it's place
    protected String generateLootCardMarker() {
        int rNum = r.nextInt(9) + 1;
        String cardType = "";
        if (rNum >= 1 && rNum < 4) cardType = "H";
        else if (rNum >= 4 && rNum < 7) cardType = "G";
        else if (rNum >= 7 && rNum < 10) cardType = "MP";
        return cardType;
    }

    // method called in the constructor to put the first cards on the board
    protected void populateBoard() {
        setUpPlayer();
        for (int i = gameBoard.length-3; i < gameBoard.length; ++i) {
            for (int j = 0; j < 3; ++j) {
                generateNewCard(i - gameBoard.length + 3, j, gameBoard[i][j]);
            }
        }
        updateScreen();
    }

    // creates a new card and sets it up so that it's label and itself is added to the panel
    protected void newCard(int row, int col, int value, String type, ImageIcon image) {
        Card card = new Card(row, col, value, type, image);
        card.addActionListener(this);
        this.add(card);
        this.add(card.getValueLabel());
        cards.add(card);
    }

    // sets up player so that all of it's labels are added to the panel with it's ActionListener
    protected void setUpPlayer() {
        player.addActionListener(this);
        this.add(player);
        this.add(player.getValueLabel());
        this.add(player.getGoldLabel());
        this.add(player.getManaLabel());
    }


    // this method is called during the populateBoard() method called inside the constructor
    // this puts all of the beginning cards on the board
    protected void generateNewCard(int row, int col, String type) {
        if (type.equals("M")) {
            newCard(row, col, r.nextInt(5) + 1 + (levelNumber-1),"M", monsterImages.get(r.nextInt(monsterImages.size())));
        } else if (type.equals("H")) {
            newCard(row, col, r.nextInt(5)+1 + (levelNumber-1),"H", consumableImages.get("Health_Potion"));
        } else if (type.equals("G")) {
            newCard(row, col, r.nextInt(5)+1 + (levelNumber-1),"G", consumableImages.get("Gold_Coins"));
        } else if (type.equals("HC")) {
            newCard(row, col, 1,"HC", consumableImages.get("Heart_Container"));
        } else if (type.equals("MP")) {
            newCard(row, col, r.nextInt(2)+1,"MP", consumableImages.get("Mana_Potion"));
        }
    }

    // after the player moves to the destination card, all of the cards on the board are moved and updated
    // the cards that are now on the same row are deleted
    protected void adjustBoard() {
        player.adjustCard();
        for (int i = cards.size() - 1; i >= 0; i--) {
            cards.get(i).adjustCard();
            if (cards.get(i).getRow() > 2) {
                removeCard(cards.get(i));
            }
        }
    }
    

    // Modifying or updating board
    // after the player moves and the board is adjusted with adjustBoard() called, the new row is populated using this
    // method. The type of card is determined by the String[][] gameBoard array that was populated in the constructor
    protected void updateBoard() {
        for (int i = 0; i <3; ++i) {
            if (gameBoard[boardRow][i].equals("M")) {
                newCard(0,i, r.nextInt(5)+1+ (levelNumber-1),"M", monsterImages.get(r.nextInt(monsterImages.size())));
            } else if (gameBoard[boardRow][i].equals("B")) {
                newCard(0,i, 9+ (levelNumber-1),"B", bossImages.get(r.nextInt(bossImages.size())));
            } else if (gameBoard[boardRow][i].equals("H")) {
                newCard(0,i, r.nextInt(5)+1+ (levelNumber-1),"H", consumableImages.get("Health_Potion"));
            } else if (gameBoard[boardRow][i].equals("G")) {
                newCard(0,i, r.nextInt(5)+1+ (levelNumber-1),"G", consumableImages.get("Gold_Coins"));
            } else if (gameBoard[boardRow][i].equals("HC")) {
                newCard(0,i, 1,"HC", consumableImages.get("Heart_Container"));
            } else if (gameBoard[boardRow][i].equals("MP")) {
                newCard(0,i, r.nextInt(2)+1,"MP", consumableImages.get("Mana_Potion"));
            }
        }
    }



    // removes the card from the panel, label from panel, and card from cards ArrayList
    protected void removeCard(Card card) {
        this.remove(card.getValueLabel());
        this.remove(card);
        cards.remove(card);
    }

    // this was just done to automatically close the window based off events that were occuring in this class (ie death or defeat boss)
    // didn't have time to research multithreading
    public void addFrame(JFrame frame) {
        this.frame = frame;
    }

    // sometimes the cards wouldn't accurately update after an action occured, typically the boss wouldn't update
    // after dying so this is done to prevent this
    public void updateScreen() {
        this.revalidate();
        this.repaint();
    }

    // Importing assets
    // this was done to store images where the specifics didn't matter, ie random images are selected for
    // the bosses and monsters with these mobs not having any specific features. This was done to add variety
    protected static ArrayList<ImageIcon> loadFileImages(String path) {
        ArrayList<ImageIcon> fileImages = new ArrayList<ImageIcon>();
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        for (File file: listOfFiles) {
            if (file.isFile()) {
                ImageIcon image = new ImageIcon(file.getAbsolutePath());
                fileImages.add(image);
            }
        }
        return fileImages;
    }

    // takes a path of a folder and loads all .png images as ImageIcons with the file name stored as the key and
    // the ImageIcon as the value. This was done specifically so I could access specific images all stored in one central
    // area without worrying about the order in which the images are stored in the folder
    protected static HashMap<String, ImageIcon> loadImagesHashMap(String path) {
        HashMap<String, ImageIcon> fileImages = new HashMap<>();
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        for (File file: listOfFiles) {
            if (file.isFile()) {
                ImageIcon image = new ImageIcon(file.getAbsolutePath());
                String itemName = file.getName().substring(0, file.getName().indexOf("."));
                fileImages.put(itemName, image);
            }
        }
        return fileImages;
    }


    // getters
    public static int getScreenHeight() {
        return screenHeight;
    }

    public static int getScreenWidth() {
        return screenWidth;
    }

    // inteded for implementing multiple levels but didn't have enough time
    public boolean getIsComplete() {
        return isComplete;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    // inherited abstract methods that I didn't use for KeyListener but needed to be overriden
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }
}
