package info.ejava.examples.app.config.beanfactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import info.ejava.examples.app.hello.Hello;

@Component
public class AppCommand implements CommandLineRunner {

    private final Hello greeter;

    // @Value("${app.audience:Default World}")
    // private String audience;

    private final String audience;

    private final int intVal;
    private final boolean boolVal;
    private final float floatVal;

    private final List<Integer> intList;
    private final Set<Integer> intSet;
    private final int[] intArray;
    

    private final Map<String, String> systemProperties;

    public AppCommand(Hello hello, @Value("${app.audience:Default World}") String audience,  @Value("${app.intVal:5}") int ival,
            @Value("${app.boolVal:false}") boolean bVal, @Value("${app.floatVal:0.1}") float fVal,
            @Value("${app.intList:}") List<Integer> iList, @Value("${app.intList:}") Set<Integer> iSet, @Value("${app.intList:}") int[] iArray,
            @Value("#{systemProperties}") Map<String,String> systemProperties){
        this.greeter = hello; 
        this.audience = audience;

        this.intVal = ival;
        this.boolVal = bVal;
        this.floatVal = fVal;

        this.intList = iList;
        this.intSet = iSet;
        this.intArray = iArray;

        this.systemProperties = systemProperties;
    }

    @Override
    public void run(String... args) throws Exception {
        greeter.sayHello(" World");
        greeter.sayHello(audience + " , " + intVal + " , " + boolVal + " , " + floatVal);
        greeter.sayHello(intList + " , " + intSet +" , " + intArray);
        greeter.sayHello(systemProperties.get("user.timezone"));
    }

}
