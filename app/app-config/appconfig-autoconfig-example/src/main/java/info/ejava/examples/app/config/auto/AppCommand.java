package info.ejava.examples.app.config.auto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import info.ejava.examples.app.config.auto.dao.UserDao;
import info.ejava.examples.app.hello.Hello;
import lombok.RequiredArgsConstructor;
@Component
@RequiredArgsConstructor
public class AppCommand implements CommandLineRunner {

    
    //private  final UserDao userDao;
    private final Hello greeter;

    // public AppCommand(@Nullable UserDao dao, Hello greeter){
    //     this.userDao = dao;
    //     this.greeter = greeter;
    // }
    @Override
    public void run(String... args) throws Exception {
        // if(userDao != null) {
        //     userDao.getAllUserNames().forEach(a->System.out.println(a));
        //     userDao.getAllUserNames().forEach(System.out::println);
        // }

        greeter.sayHello(" World");
        
    }

}
