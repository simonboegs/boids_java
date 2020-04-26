import java.awt.*;
import javax.swing.*;

public class Window extends Canvas
{
    private int width, height;
    private String title;
    private static JFrame frame;
    private boolean running;
    public Window(int width, int height, String title)
    {
       this.width = width;
       this.height = height;
       this.title = title;
       createWindow();
       running = true;
    }
    public void createWindow()
    {
       frame = new JFrame(title);
       frame.setPreferredSize(new Dimension(width, height));
       frame.setMaximumSize(new Dimension(width, height));
       frame.setMinimumSize(new Dimension(width, height));
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //if u exit it stops the game
       frame.setResizable(false);
       frame.setLocationRelativeTo(null); //starts at middle of screen
       frame.setVisible(true);
    }
    public static JFrame getFrame()
    {
        return frame;
    }
    public int getW()
    {
        return width;
    }
    public int getH()
    {
        return height;
    }
    public boolean getRunning()
    {
        return running;
    }
    public static void update()
    {
        
    }
}