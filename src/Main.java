import javax.swing.*;
import java.awt.*;

public class Main{
    public static void main(String[] args) {
        // creates the game and sets things up for gameplay
        final int screenWidth = Level.getScreenWidth(), screenHeight = Level.getScreenHeight();
        JFrame frame = setUpFrame(screenWidth, screenHeight);
        int startingLevelSize = 40; // changes the number of cards needed to make it to the boss or the levelsize

        // This weirdness was done for future implementation purposes. I wanted to have multiple levels but
        // couldn't due to time restraints. The idea was to have all levels share the same static player object
        // so that it's data could be synced when jumping from one level to the next
        Character player = new Character(3,1, 14, "P");
        Level.initializePlayer(player);
        Level level = new Level(startingLevelSize);

        level.addFrame(frame);
        frame.addKeyListener(level);
        frame.setContentPane(level);
        printInstructions(); // prints player instructions

    }

    // This sets up a JFrame object and returns it in main
    public static JFrame setUpFrame(int screenWidth, int screenHeight) {
        JFrame frame = new JFrame();
        frame.pack();
        frame.setSize(screenWidth, screenHeight);

        // sets where the JFrame pops up on the screen when the code is started
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width / 2 - frame.getSize().width / 2, 30);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Forward: Escape The Fold");
        frame.setResizable(false);

        // decided not to use a layout manager
        frame.setLayout(null);
        ImageIcon image = new ImageIcon("../assets/Logo.png");
        frame.setIconImage(image.getImage());
        frame.getContentPane().setBackground(new Color(20,20,20));
        frame.setVisible(true);

        return frame;
    }

    //prints instructions
    private static void printInstructions() {
        // source: https://stackoverflow.com/questions/14011492/text-wrap-in-joptionpane
        String instructions = "Move your character onto forward facing cards to progress through the dungeon without dying.";
        instructions += " When moving your character onto enemy cards, the damage value on the card will be dealt to you.";
        instructions += " Collect heart potions to heal yourself, heart containers to increase";
        instructions += " maximum HP, and enough mana potions to use your ability. To use your ability, hit the spacebar";
        instructions += " when you have collected enough mana points to activate the ability. When activated, all visible";
        instructions += " enemies will have 4 damage points dealt to them. At the end of the dungeon, defeat the boss and";
        instructions += " beat the game!";
        instructions = "<html><body><p style='width: 500px;'>" + instructions + "</p></body></html>";

        JOptionPane.showMessageDialog(null, instructions, "Instructions",JOptionPane.INFORMATION_MESSAGE);
    }

}


