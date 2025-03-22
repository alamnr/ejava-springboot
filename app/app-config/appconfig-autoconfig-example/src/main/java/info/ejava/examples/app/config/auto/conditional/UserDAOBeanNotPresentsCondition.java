package info.ejava.examples.app.config.auto.conditional;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import info.ejava.examples.app.config.auto.dao.UserDao;

public class UserDAOBeanNotPresentsCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        UserDao userDao = context.getBeanFactory().getBean(UserDao.class);
        return userDao == null;
    }
    
}
