package Client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Client {
    public static void main(String[] args) throws IOException{

        String nom = "", ip = "", port = "";

        //Récupération de la sauvegarde des champs
        try{

            FileReader reader = new FileReader("./save.txt");
            BufferedReader bufferedReader = new BufferedReader(reader);

            nom = bufferedReader.readLine();
            ip = bufferedReader.readLine();
            port = bufferedReader.readLine();

            reader.close();

        }catch(IOException e){
            System.out.println("Pas de fichier de sauvegarde");
        }


        InterfaceClient ihm = new InterfaceClient(nom, ip, port);
        
        while(true){
            
            //Mis a jour des elements activer ou non
            ihm.updateConnexion(); 

        }

    }

    /**
     * Sauvegarde le nom, l'ip et le port donnés 
     * 
     * @param n nom a sauvegarder
     * @param i ip a sauvegarder
     * @param p port a sauvegarder
     */
    public static void saveDate(String n, String i, String p){

        try {

            FileWriter writer = new FileWriter("./save.txt");

            writer.write(n + "\n");
            writer.write(i + "\n");
            writer.write(p + "\n");

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
