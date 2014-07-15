package mattmc.mankini.utils;

import javax.swing.*;

public class GuiApp {

    //Note: Typically the main method will be in a
    //separate class. As this is a simple one class
    //example it's all in the one class.
    public static void main(String[] args) {

        new GuiApp();
    }

    public GuiApp()
    {
        JFrame guiFrame = new JFrame();

        //make sure the program exits when the frame closes
        guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        guiFrame.setTitle("MankiniBot");
        guiFrame.setSize(20,10);
        guiFrame.setResizable(false);

        //This will center the JFrame in the middle of the screen
        guiFrame.setLocationRelativeTo(null);

        guiFrame.setVisible(true);
    }

}