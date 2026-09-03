package me.earth.phobot.modules;

import me.earth.phobot.Phobot;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class Module {
    protected final MinecraftClient mc = MinecraftClient.getInstance();
    
    private final String name;
    private final String description;
    private final Category category;
    private boolean enabled;
    private int bind;
    
    public Module(String name, Category category, String description) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.enabled = false;
        this.bind = GLFW.GLFW_KEY_NONE;
    }
    
    public void toggle() {
        setEnabled(!enabled);
    }
    
    public void setEnabled(boolean enabled) {
        if (enabled != this.enabled) {
            this.enabled = enabled;
            if (enabled) {
                onEnable();
            } else {
                onDisable();
            }
            Phobot.INSTANCE.getModuleManager().saveModules();
        }
    }
    
    protected void onEnable() {}
    protected void onDisable() {}
    public void onTick() {}
    public void onRender() {}
    
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
    public boolean isEnabled() { return enabled; }
    public int getBind() { return bind; }
    public void setBind(int bind) { this.bind = bind; }
    
    public enum Category {
        COMBAT, MOVEMENT, RENDER, CLIENT, MISC
    }
}
