package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.event.impl.Render3DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.client.render.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Chams extends Module {
    public Setting<Integer> range = this.register(new Setting<>("Range", 100, 1, 500));
    public Setting<Boolean> fade = this.register(new Setting<>("Fade", false));
    public Setting<Integer> fadeRadius = this.register(new Setting<>("FadeRadius", 10, 1, 100));
    public Setting<Float> lineWidth = this.register(new Setting<>("LineWidth", 1.0f, 0.1f, 5.0f));
    public Setting<Boolean> extraLayer = this.register(new Setting<>("ExtraLayer", false));
    public Setting<Boolean> model = this.register(new Setting<>("Model", true));
    public Setting<Boolean> xqz = this.register(new Setting<>("XQZ", false));
    public Setting<Integer> opacity = this.register(new Setting<>("Opacity", 255, 0, 255));
    
    // Shine settings
    public Setting<Boolean> shine = this.register(new Setting<>("Shine", false));
    public Setting<Float> shineSpeed = this.register(new Setting<>("ShineSpeed", 1.0f, 0.1f, 10.0f));
    public Setting<Float> shineStrength = this.register(new Setting<>("ShineStrength", 1.0f, 0.1f, 5.0f));
    
    // Pop settings
    public Setting<Boolean> pop = this.register(new Setting<>("Pop", false));
    public Setting<Boolean> death = this.register(new Setting<>("Death", false));
    public Setting<Float> popTime = this.register(new Setting<>("PopTime", 2.0f, 0.1f, 10.0f));
    public Setting<Float> popMotion = this.register(new Setting<>("PopMotion", 0.5f, 0.1f, 2.0f));
    
    // Colors
    public Setting<Boolean> customColors = this.register(new Setting<>("CustomColors", false));
    public Setting<Boolean> friendColors = this.register(new Setting<>("FriendColors", true));
    public Setting<Color> fillColor = this.register(new Setting<>("FillColor", new Color(189, 153, 255, 100)));
    public Setting<Color> outlineColor = this.register(new Setting<>("OutlineColor", new Color(189, 153, 255, 255)));
    
    // Entity type settings
    public Setting<Boolean> targets = this.register(new Setting<>("Targets", true));
    public Setting<Boolean> animals = this.register(new Setting<>("Animals", false));
    public Setting<Boolean> hostiles = this.register(new Setting<>("Hostiles", false));
    public Setting<Boolean> players = this.register(new Setting<>("Players", true));
    public Setting<Boolean> self = this.register(new Setting<>("Self", false));
    public Setting<Boolean> crystals = this.register(new Setting<>("Crystals", true));
    
    private final Map<PlayerEntity, Long> popMap = new HashMap<>();

    public Chams() {
        super("Chams", "Wallhack on entities.", Category.RENDER, false, false, false);
    }

    @Override
    public void onDisable() {
        popMap.clear();
    }

    public boolean shouldRender(Entity entity) {
        if (mc.player == null || mc.world == null) return false;
        
        // Check range
        double distance = mc.player.getPos().distanceTo(entity.getPos());
        if (distance > range.getValue()) return false;
        
        // Check if entity is in view
        Box box = entity.getBoundingBox();
        if (!isBoxVisible(box)) return false;
        
        // Check entity type
        if (entity instanceof PlayerEntity) {
            if (entity == mc.player) return self.getValue();
            return players.getValue();
        } else if (entity instanceof EndCrystalEntity) {
            return crystals.getValue();
        } else if (entity instanceof PassiveEntity) {
            return animals.getValue();
        } else if (entity instanceof Monster) {
            return hostiles.getValue();
        }
        
        return false;
    }

    private boolean isBoxVisible(Box box) {
        // Simple visibility check - could be improved with proper frustum culling
        return true;
    }

    public float getFadeAlpha(Entity entity) {
        if (!fade.getValue()) return 1.0f;
        
        double distance = mc.player.getPos().distanceTo(entity.getPos());
        float fadeStart = fadeRadius.getValue();
        float fadeEnd = range.getValue();
        
        if (distance <= fadeStart) return 1.0f;
        if (distance >= fadeEnd) return 0.0f;
        
        return 1.0f - MathHelper.clamp((float) (distance - fadeStart) / (fadeEnd - fadeStart), 0.0f, 1.0f);
    }

    public Color getEntityColor(Entity entity) {
        if (customColors.getValue()) {
            return fillColor.getValue();
        }
        
        // Default color based on entity type
        if (entity instanceof PlayerEntity) {
            return new Color(189, 153, 255, opacity.getValue());
        } else if (entity instanceof EndCrystalEntity) {
            return new Color(255, 100, 100, opacity.getValue());
        } else if (entity instanceof PassiveEntity) {
            return new Color(100, 255, 100, opacity.getValue());
        } else if (entity instanceof Monster) {
            return new Color(255, 100, 100, opacity.getValue());
        }
        
        return new Color(255, 255, 255, opacity.getValue());
    }

    public void onRenderEntity(Entity entity, Matrix4f matrix, VertexConsumer vertexConsumer, int light, int overlay) {
        if (!shouldRender(entity)) return;
        
        float alpha = getFadeAlpha(entity);
        if (alpha <= 0) return;
        
        Color color = getEntityColor(entity);
        
        // Apply chams effect
        if (xqz.getValue()) {
            // Enable X-ray mode
            RenderSystem.enableBlend();
            RenderSystem.disableDepthTest();
        }
        
        // Render with custom color
        float r = color.getRed() / 255.0f;
        float g = color.getGreen() / 255.0f;
        float b = color.getBlue() / 255.0f;
        
        RenderSystem.setShaderColor(r, g, b, alpha);
        
        if (model.getValue()) {
            // Render custom model
            // This would require more complex rendering code
        }
        
        // Restore state
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        if (xqz.getValue()) {
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
        }
    }

    public Map<PlayerEntity, Long> getPopMap() {
        return popMap;
    }
}
