package cacib.task.manager;

import org.pf4j.RuntimeMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.nio.file.Paths;

@Configuration
public class PluginManagerConfig {

    @Bean
    @Profile({"dev", "default"})
    public CustomPluginManager devPluginManager() {
        return new CustomPluginManager(Paths.get("plugins")) {
            @Override
            public RuntimeMode getRuntimeMode() {
                return RuntimeMode.DEVELOPMENT;
            }
        };
    }

    @Bean
    @Profile("prod")
    public CustomPluginManager prodPluginManager() {
        return new CustomPluginManager(Paths.get("plugins")) {
            @Override
            public RuntimeMode getRuntimeMode() {
                return RuntimeMode.DEPLOYMENT;
            }
        };
    }
}

