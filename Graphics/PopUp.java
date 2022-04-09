package Graphics;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Lance un popup
 */
public class PopUp {
    
    /**
     * Constructeur d'un PopUp
     * 
     * @param msg message a afficher sur le PopUp
     */
    public PopUp(String msg){

        JFrame frame = new JFrame();
        JOptionPane.showMessageDialog(frame, msg);

    }

}
