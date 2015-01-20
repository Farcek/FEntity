/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mn.le.farcek.common.entity.ejb;

/**
 *
 * @author Farcek
 */
public class FServiceException extends Exception{

    public FServiceException() {
    }

    public FServiceException(String message) {
        super(message);
    }

    public FServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public FServiceException(Throwable cause) {
        super(cause);
    }

    public FServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
