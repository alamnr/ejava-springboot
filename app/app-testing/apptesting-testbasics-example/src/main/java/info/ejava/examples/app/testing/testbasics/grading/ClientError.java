package info.ejava.examples.app.testing.testbasics.grading;

public class ClientError extends Exception {

    public ClientError(String msg) {
        super(msg);
    }
    public ClientError(String msg, Exception ex) {
        super(msg, ex);
    }

    public static class BadRequest extends ClientError {
    
        public BadRequest(String msg) {
            super(msg);
            
        }
        public BadRequest(String msg, Exception ex) {
            super(msg, ex);
        }           

    }

    public static class NotFound extends ClientError {
        public NotFound(String msg) {
            super(msg);
        }
        public NotFound(String msg, Exception ex){
            super(msg, ex);
        }
    }
    
}
