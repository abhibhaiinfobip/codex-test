package cacib.task.manager;

import org.pf4j.RuntimeMode;
import org.pf4j.spring.SpringPluginManager;

import java.nio.file.Path;


public class CustomPluginManager extends SpringPluginManager {

    public CustomPluginManager(Path pluginsRootPath) {
        super(pluginsRootPath);
        initialize();
    }

    @Override
    public RuntimeMode getRuntimeMode() {
        return RuntimeMode.DEVELOPMENT;
    }

}
