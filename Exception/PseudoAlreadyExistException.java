package Exception;

/**
 * Si le pseudo existe déjà sur le serveur cette erreur est renvoyé
 */
public class PseudoAlreadyExistException extends Exception{

    public PseudoAlreadyExistException(){
        new Exception("Ce pseudo existe déjà sur le serveur !");
    }
    
}
