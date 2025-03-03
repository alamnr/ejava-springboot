package info.ejava.examples.svc.content.quotes.generics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;

@Data
@NoArgsConstructor
@AllArgsConstructor
// Generic Class
public class Pair<K,V> {
    private K key;
    private V value;

    // Generic Method
    public<T,V> void printPair(T key, V value){
        System.out.println("Key : " + key + ", Value : "+ value);
    }

    public <T,V,P> String print3GenericMethod(T key, V value, P another){
        System.out.println("Key: "+key + ", Value: "+ value+", Another: "+another);
        return "genericMethodWith3GenericType";
    }
    
    public <T> T genericMethodHaving1TypeParameterReturnsType(Class<T> type){
        T result = null;
        return result;
    }

}
