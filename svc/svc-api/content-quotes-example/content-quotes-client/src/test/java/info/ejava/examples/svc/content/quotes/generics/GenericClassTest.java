package info.ejava.examples.svc.content.quotes.generics;
import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;

public class GenericClassTest {

    /*
    public static void main(String[] args) {
        // Creating an instance of a generic class with Integer and String types
        Pair<Integer, String> intStringPair = new Pair<>(1,"One");
        System.out.println("Key: " + intStringPair.getKey() + ", Value: " + intStringPair.getValue());

        // Creating an instance of a generic class with String and Double types
        Pair<String,Double> stringDoublePair = new Pair<>("PI",3.14);
        System.out.println("Key: "+ stringDoublePair.getKey()+", Value: "+stringDoublePair.getValue());

    }
    */

    @Test
    void test_pair(){
        // given 
        Pair<Integer,String> intString = new Pair<>(1,"One");

        // then 
        then(intString.getKey()).isEqualTo(1);
        then(intString.getValue()).isEqualTo("One");

        // given
        Pair<String,Double> stringDouble = new Pair<>("PI",3.14);

        // then
        then(stringDouble.getKey()).isEqualTo("PI");
        then(stringDouble.getValue()).isEqualTo(3.14);

        intString.printPair(4, "Four");

        intString.print3GenericMethod(5, "Five", "Another Five");


    }
}
