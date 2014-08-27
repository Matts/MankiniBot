package mattmc.mankini.libs;

import mattmc.mankini.MankiniBot;

import javax.swing.*;

public class GuiApp {
    public GuiApp(String[] args){
        for(int i=0;i < args.length; i++){
            if(args[i].equalsIgnoreCase("nogui")){
                MankiniBot.runGUI=false;
            }
        }
        if(MankiniBot.runGUI){
            JFrame guiFrame = new JFrame();

            guiFrame.setDefaultCloseOperation(3);
            guiFrame.setTitle("MankiniBot " + MankiniBot.VERSION);
            guiFrame.setSize(50,10);
            guiFrame.setResizable(false);

            guiFrame.setLocationRelativeTo(null);

            guiFrame.setVisible(true);
        }
    }
}