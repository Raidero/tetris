/**
 * Created by Grzegorz on 2017-05-22.
 */
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
/**
 * Controller makes it possible to talk with the game
 */
public class Controller extends JFrame implements KeyListener {
    private Module module;
    /**
     * constructor links module with controller
     *
     * @param input link to module, needed for sending signals to module, that player pressed button
     */
    public Controller (Module input) {
        super("keylistener");
        module = input;
    }
    /**
     * this function calls some module function to send signal, which move we want to make, and tells module to make that
     * move. Based on button we press function decides what signal need to be send to module
     */
    public void keyPressed(KeyEvent event) {
        char c = event.getKeyChar();
        if (!module.isPaused()) {
            switch (c)
            {
                case 'a':
                    module.setDirection(Module.Direction.LEFT);
                    break;
                case 'd':
                    module.setDirection(Module.Direction.RIGHT);
                    break;
                case ' ':
                    module.setDirection(Module.Direction.SPACE);
                    break;
                case 's':
                    module.setDirection(Module.Direction.DOWN);
                    module.setPlayerActionTime();
                    break;
                case 'x':
                    module.setDirection(Module.Direction.MAXDOWN);
                    module.setPlayerActionTime();
                    break;
                default:
                    break;
            }
            module.playerMove();
            module.show();
        } else {
            if (c == 'r')
            {
                module.resetGame();
            }
        }

    }
    public void keyTyped(KeyEvent event) {
    }

    public void keyReleased(KeyEvent event) {
    }
}

