package info.ejava.examples.svc.rpc.greeter;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

import jakarta.websocket.server.PathParam;

public interface GreeterApi {
    
    @GetExchange("/sayHai")
    String sayHai();

    @GetExchange("/say/{greeting}")
    ResponseEntity<String> sayGreeting(@PathVariable(value = "greeting",required = true)String greeting,
                                        @RequestParam(value = "name", required = false)String name );

    @GetExchange("/boom")
    ResponseEntity<String> boom(); // this will fail with no param, defined separately to allow no value

    @GetExchange("/boom")
    ResponseEntity<String> boom(@RequestParam(value = "value",required = true)String value); // this will work when given a value

    @GetExchange("/boys")
    ResponseEntity<String> createBoy(@RequestParam("name")String name); // no exception with param except name  = blue

    @GetExchange("/boys")
    ResponseEntity<String> createBoy(); // without param throws exception
}
