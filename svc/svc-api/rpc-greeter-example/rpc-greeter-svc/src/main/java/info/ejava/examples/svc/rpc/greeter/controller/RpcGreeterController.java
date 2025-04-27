package info.ejava.examples.svc.rpc.greeter.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("rpc/greeter")
public class RpcGreeterController {

    /*
     * This is an example of a method as simple as it gets
     * @return hi
     */

     @RequestMapping(path = "/sayHai", method = RequestMethod.GET)
     public String syHai() {
        return "hi";
     }

     @RequestMapping(path = "say/{greeting}", method =  RequestMethod.GET)
     public String sayGreeting(
                     @PathVariable("greeting") String greeting,
                     @RequestParam(value = "name", defaultValue = "you")String name) {
            
            return String.format("{} , {}", greeting,name);

     }

     
      /*
       * This method is an example of container returning an error when the  
       *  client does not supply a required query parameter
       * @param value
       */

       @RequestMapping(path = "boom", method = RequestMethod.GET)
       public String boom(@RequestParam(value = "value", required = true)String value) {
         return "worked ? ";
       }


       /*
        * This method shows how the controller method can have full controll over the response returned to the caller
        * @param name
        */

        @RequestMapping(path = "boys", method = RequestMethod.GET)
        public ResponseEntity<String> createBoy(@RequestParam("name")String name){

            try {
                  someMethodMayThrowException(name);

                  String url = ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString();
                  return ResponseEntity.ok().header(HttpHeaders.CONTENT_LOCATION, url)
                                       .body(String.format("Hello %s , how do you do ?",  name));

            } catch(IllegalArgumentException ex){
               return ResponseEntity.unprocessableEntity().body(ex.getMessage());
            }
        }

        private void someMethodMayThrowException(String name) {
          if("blue".equalsIgnoreCase(name)){
            throw new IllegalArgumentException("boy named blue");
          }
        }

      /*
       * This method is an example of offloadingdetailed response entity handling to an @ExceptionHandler
       * to keep the controller method clean 
       * @param name
       */


       @RequestMapping(path = "boys/throws",method = RequestMethod.GET)
       public ResponseEntity<String> createBoyThrows(@RequestParam("name") String name){

            someMethodMayThrowException(name);

            String url = ServletUriComponentsBuilder.fromCurrentRequest().replacePath("rpc/greeter/boys").build().toUriString();
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_LOCATION,url).body(String.format("Hello %s, how do you do ?",name));
       }
   
      /*
       * This is an example handler that will convert an exception to a 
       * ResponseEntity to return to the caller. It is supplied with in the controller
       * here as a first step example. Later example try to generelize the solution and create 
       * service wide advice
       * @param ex
       * @return ResponseEntity 
       */

      @ExceptionHandler(IllegalArgumentException.class)
      public ResponseEntity<String> handle(IllegalArgumentException ex){
         return ResponseEntity.unprocessableEntity().body(ex.getMessage());       
      }
}