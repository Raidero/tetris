/**
 * Created by Grzegorz on 2017-05-22.
 */

import java.awt.*;

/**
 * begins a program from here, creates Module, View, and starts running Tetris from function main
 */
public class Start {
    public static void main(String[] args) {
        Module module = new Module();
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                View view = new View (module);
                module.getView(view);
            }
        });
        //View view = new View(module);
        //module.getView(view);
        //view.runProgram();
        module.step();
        //view.dispose();
        //System.exit(0);
    }
}
