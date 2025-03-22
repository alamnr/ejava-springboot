package info.ejava.examples.app.config.auto.conditional;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class MongoDatabaseTypeCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String enabledString = System.getProperty("dbType");
        return enabledString != null && enabledString.equalsIgnoreCase("MONGODB");
    }

}
