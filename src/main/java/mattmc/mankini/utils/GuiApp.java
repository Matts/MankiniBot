package mattmc.mankini.utils;

import javax.swing.*;

public class GuiApp {
    public static void main(String[] args) {

        new GuiApp();
    }

    public GuiApp()
    {
        JFrame guiFrame = new JFrame();

        guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        guiFrame.setTitle("MankiniBot");
        guiFrame.setSize(20,10);
        guiFrame.setResizable(false);

        guiFrame.setLocationRelativeTo(null);

        guiFrame.setVisible(true);
    }

}