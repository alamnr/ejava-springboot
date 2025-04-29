package info.ejava.examples.svc.rpc.greeter;

import java.time.Instant;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ErrorMessage {
    
    private Date timestamp;
    private int status;
    private String error;
    private String path;


}
