package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import Exception.PseudoAlreadyExistException;
import Graphics.PopUp;

public class Server {
    
    static int port = 2001;

    static private List<Connexion> clientsListe;

    public static void main(String[] args){

        ServerSocket serveur; //Socket serveur
        clientsListe = new ArrayList<Connexion>(); //liste des clients connectés

        try{
            
            //demande du port
            String p = (String) JOptionPane.showInputDialog(new JFrame(), "Saississez le port du serveur :");
            int port = Integer.parseInt(p);

            serveur = new ServerSocket(port);

            while(true){

                try{

                    //Nouvelle connexion
                    clientsListe.add(new Connexion(serveur.accept()));

                    //Envoie de la liste des pseudos aux clients
                    sendConnecteListe();

                }catch(PseudoAlreadyExistException e){
                    System.out.println("Tentaive de connexion - le pseudo existait déjà sur le serveur !");
                }

                
            }

        }catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        }catch(NumberFormatException e){
            new PopUp("Port invalide - Arrêt du processus !");
            System.exit(1);
        }

    }

    /**
     * Envoie la liste des clients connecté a tout le monde  
     */
    public static void sendConnecteListe(){

        String cp = "";
        for(Connexion c: clientsListe){
            cp += c.getPseudo() + " ";
        }

        if(clientsListe.size() > 0){
            clientsListe.get(0).sendToClients("ls-" + cp);
        }

    }

    /**
     * @return la liste des connexions au serveur
     */
    public static List<Connexion> getClients(){
        return clientsListe;
    }

    /**
     * Supprime une connexion de la liste de connexion
     * 
     * @param c connexion a supprimer
     */
    public static void  removeClient(Connexion c){
        clientsListe.remove(c);
    }

}
