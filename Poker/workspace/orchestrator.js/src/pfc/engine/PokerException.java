package pfc.engine;

/**
 * Created by fare on 30/09/14.
 */
public class PokerException extends RuntimeException {
    public PokerException(){
        super();
    }

    public PokerException(String msg, Throwable cause){
        super(msg,cause);
    }
}
