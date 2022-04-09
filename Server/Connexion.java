package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import Exception.PseudoAlreadyExistException;

/**
 * Une Connexion est défini par son client et le pseudo du client
 * Elle ouvre une entrée et une sortie avec le client
 */
public class Connexion implements Runnable{
    
    private Socket client;
    private String pseudo;

    private BufferedReader fromClient;
    private PrintStream toClient;

    /**
     * Constructeur d'une connexion
     * 
     * @param client client qui se connecte
     * @throws PseudoAlreadyExistException
     */
    public Connexion(Socket client) throws PseudoAlreadyExistException{
            
        this.client = client;

        try{

            //Init entrée sortie
            fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
            toClient = new PrintStream(client.getOutputStream());

            //Envoie message de connexion au serveur
            toClient.println("Connecté au serveur !");

            //Récupération du pseudo
            this.pseudo = fromClient.readLine();

            for(Connexion c : Server.getClients()){
                if(c.getPseudo().equals(this.pseudo)){
                    this.toClient.println("ERROR - pseudo déjà existant !");
                    throw new PseudoAlreadyExistException();
                }
            }

            //Envoie de la nouvelle connexion au clients
            this.sendToClients("nc-" + pseudo);

        }catch(IOException e){
            try {
                client.close();
            }catch(IOException ex){}
        }

        new Thread(this).start();
    }

    @Override
    public void run() {

        String msg;

        try {

            //Récupération d'un message du client
            while((msg = fromClient.readLine()) != null){

                if(msg.startsWith("/mp ")){
                    this.sendMPTo(msg.substring(4));
                }else{

                    //pas de message envoyé
                    if(msg.length() == 0){
                        this.toClient.println("ERROR - Pas de message");
                    }else{
                        this.sendToClients("mg-" + this.pseudo + ": " + msg);
                    }

                }

            }

            //Fin de connexion
            this.sendToClients("dc-" + this.pseudo);
            Server.removeClient(this);
            Server.sendConnecteListe();
            this.client.close();

        } catch (IOException e) {
        }
        
    }

    /**
     * Envoie d'un message à tout les clients du serveurs
     * 
     * @param msg message à envoyer au clients
     */
    public void sendToClients(String msg){

        for(Connexion c: Server.getClients()){
            c.toClient.println(msg);
        }

    }

    /**
     * Envoie un message à un seul client
     * 
     * @param msgpseudo pseudo du client + message a envoyer
     */
    public void sendMPTo(String msgpseudo){

        String pseudo = msgpseudo.split(" ")[0];
        String msg = msgpseudo.substring(pseudo.length() + 1);

        //pas de message envoyé
        if(msg.length() == 0){
            this.toClient.println("ERROR - Pas de message");
            return;
        }

        for(Connexion c: Server.getClients()){
            if(c.getPseudo().equals(pseudo)){
                c.toClient.println("mp-" + this.pseudo + ": " + msg);
                this.toClient.println("su-" + pseudo + ": " + msg);
                return;
            }
        }
        
        this.toClient.println("ERROR - Le destinataire n'existe pas");

    }

    /**
     * Getter tu pseudo du client
     * 
     * @return pseudo du client
     */
    public String getPseudo(){
        return pseudo;
    }

}
