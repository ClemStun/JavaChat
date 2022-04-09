package Client;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import Exception.PseudoAlreadyExistException;
import Graphics.PopUp;

/**
 * Gère la connexion avec le serveur
 */
public class ConnexionClient {

    private static Socket clientSocket;

    private static BufferedReader fromServer;
    private static PrintStream toServer;

    public ConnexionClient(String n, String ip, int port, JTextPane discuss, JList<String> connectes) throws IOException, PseudoAlreadyExistException{
            
        //Connexion au serveur
        clientSocket = new Socket(ip, port);

        //Initialisation E/S
        fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        toServer = new PrintStream(clientSocket.getOutputStream());

        //Si erreur de pseudo
        if(fromServer.readLine().startsWith("ERROR - pseudo")){
            throw new PseudoAlreadyExistException();
        }else{
            System.out.println("Connecté au serveur !");
        }

        //envoie du pseudo
        toServer.println(n);

        Thread waitReading = new Thread(new Runnable() {

            @Override
            public void run() {
                
                String msg;

                try {

                    //Attente d'un message
                    while((msg = fromServer.readLine()) != null){

                        //Défnition des différente couleurs
                        Style defaut = discuss.getStyle("default");

                        //Couleur de connexion
                        Style colorBlue = discuss.addStyle("colorBlue", defaut);
                        StyleConstants.setForeground(colorBlue, Color.BLUE);

                        //Couleur deconnexion
                        Style colorRed = discuss.addStyle("colorRed", defaut);
                        StyleConstants.setForeground(colorRed, Color.RED);
                        
                        //Couleur message privé
                        Style colorMP = discuss.addStyle("colorMP", defaut);
                        StyleConstants.setForeground(colorMP, new Color(50, 200, 125));
                        
                        StyledDocument doc = (StyledDocument) discuss.getDocument();
                        int l = doc.getLength();

                        //Date de reception du message
                        String date = new SimpleDateFormat("kk:mm:ss").format(new Date().getTime());

                        if(msg.startsWith("nc-")){ //Nouvel connexion

                            try {
								doc.insertString(l, "Nouvelle connexion : " + msg.substring(3) + "\n", colorBlue);
							} catch (BadLocationException e) {
							}

                        }else if(msg.startsWith("dc-")){ //Deconnexion

                            try {
								doc.insertString(l, "Deconnexion : " + msg.substring(3) + "\n", colorRed);
							} catch (BadLocationException e) {
							}

                        }else if(msg.startsWith("ls-")){//Liste des connectés

                            DefaultListModel<String> model = new DefaultListModel<>();
                            String[] lc = msg.substring(3).split(" ");

                            for(int i = 0; i < lc.length; i++){
                                model.addElement(lc[i]);
                            }

                            connectes.setModel(model);

                        }else if(msg.startsWith("mp-")){//Reception d'un messaage privé

                            try {
								doc.insertString(l, date + " De " + msg.substring(3) + "\n", colorMP);
							} catch (BadLocationException e) {
							}
                        
                        }else if(msg.startsWith("su-")){//succes de l'envoie de mp

                            try {
                                doc.insertString(l, date + " A " + msg.substring(3) + "\n", colorMP);
                            } catch (BadLocationException e) {
                            }
                            
                        }else if(msg.startsWith("ERROR -")){//Erreurs

                            new PopUp(msg.substring(7));

                        }else{//Message

                            try {
								doc.insertString(l, date + " " + msg.substring(3) + "\n", defaut);
							} catch (BadLocationException e) {
							}

                        }

                        

                    }
                }catch(IOException e) {
                    System.out.println("Déconnexion Serveur !");
                }

            }
            
        });

        waitReading.start();

    }

    /**
     * Fermeture de la connexion avec le serveur
     * 
     * @throws IOException
     */
    public static void connexionClose() throws IOException{

        if(clientSocket != null){
            toServer.close();
            clientSocket.close();
            System.out.println("Connexion fermé !");
        }
        
    }

    /**
     * Envoie d'un message au serveur
     * 
     * @param msg message a envoyer
     */
    public static void sendMessage(String msg){
        toServer.println(msg);
    }

}
