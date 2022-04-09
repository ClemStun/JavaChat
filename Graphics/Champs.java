package Graphics;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.*;

/**
 * Un champs est composé d'un label et d'une zone de text alignés horizontalement
 */
public class Champs extends JPanel{
    
    private JLabel label;
    private JTextField field;

    /**
     * Constructeur d'un champs
     * 
     * @param l nom du label
     * @param d dimmension du chaamps
     */
    public Champs(String l, Dimension d, String def){

        label = new JLabel(l);
        field = new JTextField();

        field.setPreferredSize(d);
        field.setText(def);
        
        this.setLayout(new BorderLayout(10, 0));
        this.setPreferredSize(new Dimension((int)d.getWidth() + 30, (int)d.getHeight()));
        this.add(label, BorderLayout.WEST);
        this.add(field, BorderLayout.EAST);

    }

    /**
     * @return Le texte dans la zone de chamsp
     */
    public String getFieldText(){
        return this.field.getText();
    }

    /**
     * Désactive ou aactive le champs de text.
     * 
     * @param e Vrai pour activé Faux pour désactiver
     */
    public void setTextEnabled(boolean e){
        field.setEnabled(e);
    }

}
