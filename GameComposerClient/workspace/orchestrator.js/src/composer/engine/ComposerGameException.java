package composer.engine;

/**
 * Created by fare on 30/09/14.
 */
public class ComposerGameException extends RuntimeException {
    public ComposerGameException(){
        super();
    }

    public ComposerGameException(String msg, Throwable cause){
        super(msg,cause);
    }
}
