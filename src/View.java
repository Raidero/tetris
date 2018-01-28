/**
 * Created by Grzegorz on 2017-05-22.
 */
import javax.swing.*;
/**
 * class made to show all graphics
 */
public class View extends JFrame {
    /**
     * constructor, creates frame named Tetris, adds panel inside that frame, and one keylistener,
     * and set all that visible at the end
     * @param module links this class with Module class, which compute what View class will show us
     */
    public View(Module module) {
        super("Tetris");

        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new Component(module);

        add(panel);
        addKeyListener(new Controller(module));

        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
