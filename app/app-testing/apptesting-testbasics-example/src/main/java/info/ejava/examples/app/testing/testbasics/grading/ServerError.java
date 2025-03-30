package info.ejava.examples.app.testing.testbasics.grading;

public class ServerError extends RuntimeException {

    public ServerError(String errorMessage) {
        super(errorMessage);
    }

    public  ServerError(String errMsg, Exception ex) {
        super(errMsg, ex);
    }

    public static class InternalFailure extends RuntimeException {
        public InternalFailure(String msg) {
            super(msg);
        }
        public InternalFailure(String msg, Exception ex) {
            super(msg, ex);
        }
    }
    
}
