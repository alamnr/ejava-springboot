package info.ejava.examples.app.config.auto.dao;

import java.util.Arrays;
import java.util.List;

public class JdbcUserDao implements UserDao {

    @Override
    public List<String> getAllUserNames() {
        System.out.println("************* Getting user name from RDBMS *****************");
        return Arrays.asList("Siva","Prasad","Reddy");
    }

}
