package me.earth.phobot;

import me.earth.phobot.modules.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class Phobot implements ClientModInitializer {
    public static final Phobot INSTANCE = new Phobot();
    
    private ModuleManager moduleManager;
    private KeyBinding clickGuiBind;
    
    @Override
    public void onInitializeClient() {
        moduleManager = new ModuleManager();
        moduleManager.init();
        
        // Register modules
        registerModules();
        
        // Setup keybinds
        clickGuiBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.phobot.clickgui",
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            "category.phobot"
        ));
        
        // Tick events
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (clickGuiBind.wasPressed()) {
                client.setScreen(new me.earth.phobot.gui.ClickGui());
            }
            
            for (me.earth.phobot.modules.Module module : moduleManager.getModules()) {
                if (module.isEnabled()) {
                    module.onTick();
                }
            }
        });
        
        // Render events
        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            for (me.earth.phobot.modules.Module module : moduleManager.getModules()) {
                if (module.isEnabled()) {
                    module.onRender();
                }
            }
        });
    }
    
    private void registerModules() {
        // Register all modules here
        // moduleManager.register(new me.earth.phobot.modules.render.HoleESP());
        // moduleManager.register(new me.earth.phobot.modules.render.Chams());
        // moduleManager.register(new me.earth.phobot.modules.movement.HoleSnap());
        // moduleManager.register(new me.earth.phobot.modules.render.ViewModel());
        // moduleManager.register(new me.earth.phobot.modules.client.HUD());
    }
    
    public ModuleManager getModuleManager() {
        return moduleManager;
    }
}
