package Client;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.text.DefaultCaret;

import Exception.PseudoAlreadyExistException;
import Graphics.Champs;
import Graphics.PopUp;

//import java.util.regex.*;

public class InterfaceClient extends JFrame{

    //Element de connexion
    private JPanel connexionPanel;
    private JPanel connexion;

    private JButton butConnexion;
    private JButton butDeconnexion;
    private Champs nom;
    private Champs ip;
    private Champs port;

    //Connectés
    private JPanel connectesPanel;
    private JLabel connectesLabel;
    private JList<String> connectes;

    //Discussion + Message
    JPanel chatPanel;

    private JLabel discussionLabel;
    private JTextPane discussion;
    
    private JLabel messageLabel;
    private JTextField message;

    private JButton butEnvoyer;

    public InterfaceClient(String n, String i, String p){

        //Affectation du theme
        try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

        //Laaout d ela fenetre
        setLayout(new BorderLayout(0, 0));
        
        //Element de connexion
        connexion = new JPanel();
        connexion.setLayout(new GridLayout(2, 2, 5, 5));

        butConnexion = new JButton("Connexion");
        butConnexion.setPreferredSize(new Dimension(150, 20));

        //Action du bouton de connexion
        butConnexion.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
                try {

					new ConnexionClient(getNom(), getIp(), Integer.parseInt(getPort()), discussion, connectes);

                    //Echange des boutons de connexion et deconnexion
                    connexion.remove(butConnexion);
                    connexion.add(butDeconnexion);

                    //disactivation des champs de connexion
                    nom.setTextEnabled(false);
                    ip.setTextEnabled(false);
                    port.setTextEnabled(false);

                    //activation du chat
                    chatPanel.setVisible(true);
                    connectesPanel.setVisible(true);

                    //Sauvegarde des information de connexion
                    Client.saveDate(getNom(), getIp(), getPort());

				} catch (NumberFormatException err) {
					new PopUp("Port invalide - Connexion interrompu");
				} catch (IOException err) {
					new PopUp("Connexion avec le serveur impossible !");
				} catch (PseudoAlreadyExistException err){
                    new PopUp("Ce pseudo existe déjà sur le serveur !");
                }

			} 

        });

        butDeconnexion = new JButton("Déconnexion");
        butDeconnexion.setPreferredSize(new Dimension(150, 20));

        //Action du bouton de deconnexion
        butDeconnexion.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

                try{
                    ConnexionClient.connexionClose();
                }catch(IOException err){}

                //Echange des boutons de connexion et deconnexion
                connexion.remove(butDeconnexion);
                connexion.add(butConnexion);

                //Activation des champs de connexion
                nom.setTextEnabled(true);
                ip.setTextEnabled(true);
                port.setTextEnabled(true);

                //Reset zone de message discussion et liste des connectés
                message.setText("");
                discussion.setText("");

                DefaultListModel<String> model = new DefaultListModel<>();
                connectes.setModel(model);

                //desactivation de la zone de chat
                chatPanel.setVisible(false);
                connectesPanel.setVisible(false);

			} 

        });

        //Initialisation des champs
        nom = new Champs("Nom", new Dimension(150, 20), n);
        ip = new Champs("IP", new Dimension(150, 20), i);
        port = new Champs("Port", new Dimension(150, 20), p);

        connexion.add(nom);
        connexion.add(ip);
        connexion.add(port);
        connexion.add(butConnexion);

        connexionPanel = new JPanel();
        connexionPanel.setLayout(new FlowLayout());
        connexionPanel.add(connexion);

        //Connectés
        connectesPanel = new JPanel();
        connectesLabel = new JLabel("Connectés");
        connectes = new JList<>();

        BoxLayout connectesLayout = new BoxLayout(connectesPanel, BoxLayout.Y_AXIS);
        connectesPanel.setLayout(connectesLayout);

        connectesPanel.add(connectesLabel);
        connectesPanel.add(connectes);
        connectesPanel.setPreferredSize(new Dimension(100, 600));

        //Discussion + Message

        //Discussion
        discussionLabel = new JLabel("Discussion");
        discussion = new JTextPane();
        discussion.setEditable(false);
        JScrollPane discussionScroll = new JScrollPane(discussion);
        
        //Autoscroll
        DefaultCaret caret = (DefaultCaret)discussion.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        //Message
        messageLabel = new JLabel("Message");
        message = new JTextField();
        message.setMaximumSize(new Dimension(10000, 20));

        //Envoyer
        butEnvoyer = new JButton("Envoyer");

        //Panel droit du chat
        chatPanel = new JPanel();
        BoxLayout chatLayout = new BoxLayout(chatPanel,BoxLayout.Y_AXIS); 
        chatPanel.setLayout(chatLayout);

        chatPanel.add(discussionLabel);
        chatPanel.add(discussionScroll);
        chatPanel.add(messageLabel);
        chatPanel.add(message);
        chatPanel.add(butEnvoyer);

        //Action du bouton envoyer
        butEnvoyer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
                ConnexionClient.sendMessage(message.getText());
                message.setText("");
			}

        });

        chatPanel.setVisible(false);
        connectesPanel.setVisible(false);

        add(connexionPanel, BorderLayout.NORTH);
        add(chatPanel, BorderLayout.CENTER);
        add(connectesPanel, BorderLayout.WEST);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try{
                    ConnexionClient.connexionClose();
                }catch(IOException err){
                    System.out.println("Pas de connexion - fermeture de la fenetre.");
                }
                System.exit(0);
            }
        });

        pack();
        setSize(400, 500);
        setVisible(true);

    }

    //Récupération des champs

    /**
     * @return Le text du nom
     */
    public String getNom(){
        return nom.getFieldText();
    }

    /**
     * @return Le text de l'ip
     */
    public String getIp(){
        return ip.getFieldText();
    }

    /**
     * @return Le text du port
     */
    public String getPort(){
        return port.getFieldText();
    }

    //Test de compatibilité des champs

    /**
     * @return Vrai si le champs nom a une String
     */
    public boolean hasNom(){
        Pattern p = Pattern.compile("\\w");
        return p.matcher(getNom()).find();
    }

    /**
     * @return Vrai si le champs ip  a possede une chaine de caractere sou sla forme n.n.n.n
     */
    public boolean hasIp(){
        Pattern p = Pattern.compile("[0-9]{1,3}[.][0-9]{1,3}[.][0-9]{1,3}[.][0-9]{1,3}");
        return p.matcher(getIp()).find();
    }

    /**
     * @return Vraai si le port est constitué de chiffre
     */
    public boolean hasPort(){
        Pattern p = Pattern.compile("^[0-9]+$");
        return p.matcher(getPort()).find();
    }

    /**
     * Met a jour l'activation du bouton de connexion 
     */
    public void updateConnexion(){
        if(hasIp() && hasNom() && hasPort()){
            butConnexion.setEnabled(true);
        }else{
            butConnexion.setEnabled(false);
        }
    }

}