package helper.webservices;
import jakarta.enterprise.context.ApplicationScoped;
import org.kie.kogito.process.impl.DefaultWorkItemHandlerConfig;

@ApplicationScoped
public class CustomWorkItemHandlerConfig extends DefaultWorkItemHandlerConfig {
 
    {
        register("CustomTask", new CustomTaskWorkItemHandler());
    }
}