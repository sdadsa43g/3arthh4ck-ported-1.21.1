package me.earth.phobot.modules.render;

import me.earth.phobot.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import java.awt.Color;

public class Chams extends Module {
    private final Setting<Integer> range = new Setting<>("Range", 50, 1, 200);
    private final Setting<Color> fillColor = new Setting<>("Fill Color", new Color(255, 0, 0, 100));
    private final Setting<Color> outlineColor = new Setting<>("Outline Color", new Color(255, 0, 0, 255));
    private final Setting<Boolean> players = new Setting<>("Players", true);
    private final Setting<Boolean> mobs = new Setting<>("Mobs", false);
    private final Setting<Boolean> animals = new Setting<>("Animals", false);
    
    public Chams() {
        super("Chams", Category.RENDER, "Wallhack on entities");
    }
    
    @Override
    public void onEnable() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(this::render);
    }
    
    @Override
    public void onDisable() {
        WorldRenderEvents.AFTER_TRANSLUCENT.unregister(this::render);
    }
    
    private void render(WorldRenderContext context) {
        if (mc.player == null || mc.world == null) return;
        
        for (Entity entity : mc.world.getEntities()) {
            if (entity == mc.player) continue;
            if (mc.player.squaredDistanceTo(entity) > range.getValue() * range.getValue()) continue;
            
            if (!isValidTarget(entity)) continue;
            
            // Render chams effect
            renderEntityChams(context, entity);
        }
    }
    
    private boolean isValidTarget(Entity entity) {
        if (entity instanceof PlayerEntity && players.getValue()) return true;
        // Add more checks for mobs and animals
        return false;
    }
    
    private void renderEntityChams(WorldRenderContext context, Entity entity) {
        // Simplified - would need proper rendering implementation
    }
}
