package info.ejava.examples.app.config.auto.dao;

import java.util.Arrays;
import java.util.List;

public class MongoUserDao implements UserDao {

    @Override
    public List<String> getAllUserNames() {
        System.out.println("************** Genearting user name from Mongo DB ***************");
        return Arrays.asList("Siva","Prasad","Reddy");
    }

}
