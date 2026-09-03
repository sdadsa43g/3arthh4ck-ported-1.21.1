package me.earth.phobot.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleManager {
    private final Map<String, Module> modules = new HashMap<>();
    private final List<Module> moduleList = new ArrayList<>();
    
    public void init() {
        // Register modules here
    }
    
    public void register(Module module) {
        modules.put(module.getName(), module);
        moduleList.add(module);
    }
    
    public <T extends Module> T get(Class<T> clazz) {
        for (Module module : moduleList) {
            if (clazz.isInstance(module)) {
                return clazz.cast(module);
            }
        }
        return null;
    }
    
    public Module getByName(String name) {
        return modules.get(name);
    }
    
    public List<Module> getModules() {
        return moduleList;
    }
    
    public List<Module> getModulesByCategory(Module.Category category) {
        List<Module> result = new ArrayList<>();
        for (Module module : moduleList) {
            if (module.getCategory() == category) {
                result.add(module);
            }
        }
        return result;
    }
    
    public void saveModules() {
        // Save module states to config
    }
    
    public void loadModules() {
        // Load module states from config
    }
}
