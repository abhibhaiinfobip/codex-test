// in your shared library
package cacib.plugin.http;

import org.pf4j.spring.SpringPlugin;
import org.pf4j.PluginWrapper;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class BaseControllerPlugin extends SpringPlugin {
    public BaseControllerPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        super.start();
        System.out.println("BaseControllerPlugin started with plugin ID: " + getWrapper().getPluginId());
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    protected ApplicationContext createApplicationContext() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.setClassLoader(getWrapper().getPluginClassLoader());
        applicationContext.register(SpringBootConfiguration.class);
        applicationContext.refresh();
        return applicationContext;
    }
}
