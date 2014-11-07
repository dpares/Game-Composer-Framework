package framework.engine;

/**
 * Created by fare on 30/09/14.
 */
public class FrameworkGameException extends RuntimeException {
    public FrameworkGameException(){
        super();
    }

    public FrameworkGameException(String msg, Throwable cause){
        super(msg,cause);
    }
}
