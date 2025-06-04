package com.example.service;

import cacib.shared.interfaces.task.TaskFlowDefinition;
import cacib.task.manager.CustomPluginManager;
import cacib.task.manager.TaskManager;
import cacib.task.manager.TaskManagerFlowLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class FlowConfig {

    @Value("${plugin.directory:plugins}")
    private String pluginsDir;

    //
    // ─────────────── Phase A ────────────────
    // Create the pluginManager bean but do NOT call init() here.
    // CustomPluginManager must not auto-init in its constructor.
    //
    @Bean
    public CustomPluginManager pluginManager() {
        // Make sure CustomPluginManager’s constructor simply sets up the folder path
        // and does NOT call loadPlugins()/startPlugins()/init() internally.
        return new CustomPluginManager(Paths.get(pluginsDir));
    }

    //
    // ─────────────── Phase B ────────────────
    // Spring will call TaskManagerFlowLoader(pluginManager) next, and then build TaskManager.
    //
    @Bean
    public TaskManagerFlowLoader flowLoader(CustomPluginManager pluginManager) {
        return new TaskManagerFlowLoader(pluginManager);
    }

    @Bean
    public TaskManager taskManager(TaskManagerFlowLoader loader,
                                   CustomPluginManager pluginManager) {
        List<? extends TaskFlowDefinition> flows = loader.loadFlows();
        return new TaskManager(new ArrayList<>(flows), pluginManager);
    }

    //
    // ─────────────── Phase C ────────────────
    // Now that TaskManager already exists in the context, we can safely initialize PF4J.
    // Any plugin controller that does “@Autowired TaskManager” will now succeed.
    //
    @Bean
    public ApplicationRunner deferPluginInit(CustomPluginManager pluginManager) {
        return args -> {
            // 1) load all plugin jars from the “pluginsDir” folder
//            pluginManager.loadPlugins();
//
//            // 2) start them
//            pluginManager.startPlugins();
//
            // 3) Start a watcher for the plugins directory
            startPluginDirectoryWatcher(pluginManager);
        };
    }

    private void startPluginDirectoryWatcher(CustomPluginManager pluginManager) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                Path path = Paths.get(pluginsDir);
                path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);

                while (true) {
                    WatchKey key = watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();

                        if (kind == StandardWatchEventKinds.OVERFLOW) {
                            continue;
                        }

                        // Log the event and reload plugins
                        System.out.println("Plugin directory change detected: " + event.context());
                        pluginManager.loadPlugins();
                        pluginManager.startPlugins();
                    }
                    key.reset();
                }
            } catch (IOException | InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Error watching plugin directory", e);
            }
        });
    }
}
