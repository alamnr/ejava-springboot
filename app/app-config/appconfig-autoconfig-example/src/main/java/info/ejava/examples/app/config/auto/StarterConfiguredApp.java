package info.ejava.examples.app.config.auto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

import info.ejava.examples.app.config.auto.conditional.DatabaseType;
import info.ejava.examples.app.config.auto.conditional.MongoDatabaseTypeCondition;
import info.ejava.examples.app.config.auto.conditional.MongoDbTypePropertyCondition;
import info.ejava.examples.app.config.auto.conditional.MongoDriverNotPresentsCondition;
import info.ejava.examples.app.config.auto.conditional.MongoDriverPresentsCondition;
import info.ejava.examples.app.config.auto.conditional.MysqlDatabaseTypeCondition;
import info.ejava.examples.app.config.auto.conditional.UserDAOBeanNotPresentsCondition;
import info.ejava.examples.app.config.auto.dao.JdbcUserDao;
import info.ejava.examples.app.config.auto.dao.MongoUserDao;
import info.ejava.examples.app.config.auto.dao.UserDao;
import info.ejava.examples.app.hello.Hello;
import info.ejava.examples.app.hello.stdout.StdOutHello;

@SpringBootApplication
public class StarterConfiguredApp {
    public static void main(String[] args) {
        SpringApplication.run(StarterConfiguredApp.class, args);
    }

    @Bean
    @ConditionalOnProperty(prefix = "hello", name = "quiet", havingValue = "true")
    public Hello quiteHello() {
        return new StdOutHello("hello.quiet property condition set, Application @Bean says hi");
    }

    //@Bean
    //@Conditional(MysqlDatabaseTypeCondition.class)
    //@Conditional(MongoDriverNotPresentsCondition.class)
    public UserDao getJdbcUserDao() {
        return new JdbcUserDao();
    }

    @Bean
    //@Conditional(MongoDatabaseTypeCondition.class)
    //@Conditional(MongoDriverPresentsCondition.class)
    //@Conditional(UserDAOBeanNotPresentsCondition.class)
    //@Conditional(MongoDbTypePropertyCondition.class)
    @DatabaseType("MONGO")
    public UserDao getMongoUserDao() {
        return new MongoUserDao();
    }
}
