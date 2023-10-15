package byow.Core;

public class BYOWException extends RuntimeException{
    BYOWException(){super();}

    BYOWException(String msg)
    {
        super(msg);
    }
}
