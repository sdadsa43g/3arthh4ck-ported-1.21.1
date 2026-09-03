package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.event.impl.RenderHandEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ViewModel extends Module {
    public Setting<Boolean> adjust = this.register(new Setting<>("Adjust", false));
    public Setting<Boolean> shadow = this.register(new Setting<>("Shadow", true));
    public Setting<Boolean> noSway = this.register(new Setting<>("NoSway", false));
    public Setting<Boolean> instantSwap = this.register(new Setting<>("InstantSwap", false));
    public Setting<Boolean> eating = this.register(new Setting<>("Eating", false));
    
    // Main hand settings
    public Setting<Boolean> mainHand = this.register(new Setting<>("MainHand", true));
    public Setting<Float> mainX = this.register(new Setting<>("MainX", 0.0f, -5.0f, 5.0f));
    public Setting<Float> mainY = this.register(new Setting<>("MainY", 0.0f, -5.0f, 5.0f));
    public Setting<Float> mainZ = this.register(new Setting<>("MainZ", 0.0f, -5.0f, 5.0f));
    public Setting<Float> mainScaleX = this.register(new Setting<>("MainScaleX", 1.0f, 0.1f, 3.0f));
    public Setting<Float> mainScaleY = this.register(new Setting<>("MainScaleY", 1.0f, 0.1f, 3.0f));
    public Setting<Float> mainScaleZ = this.register(new Setting<>("MainScaleZ", 1.0f, 0.1f, 3.0f));
    public Setting<Float> mainRotateX = this.register(new Setting<>("MainRotateX", 0.0f, -180.0f, 180.0f));
    public Setting<Float> mainRotateY = this.register(new Setting<>("MainRotateY", 0.0f, -180.0f, 180.0f));
    public Setting<Float> mainRotateZ = this.register(new Setting<>("MainRotateZ", 0.0f, -180.0f, 180.0f));
    
    // Off-hand settings
    public Setting<Boolean> offHand = this.register(new Setting<>("OffHand", false));
    public Setting<Float> offX = this.register(new Setting<>("OffX", 0.0f, -5.0f, 5.0f));
    public Setting<Float> offY = this.register(new Setting<>("OffY", 0.0f, -5.0f, 5.0f));
    public Setting<Float> offZ = this.register(new Setting<>("OffZ", 0.0f, -5.0f, 5.0f));
    public Setting<Float> offScaleX = this.register(new Setting<>("OffScaleX", 1.0f, 0.1f, 3.0f));
    public Setting<Float> offScaleY = this.register(new Setting<>("OffScaleY", 1.0f, 0.1f, 3.0f));
    public Setting<Float> offScaleZ = this.register(new Setting<>("OffScaleZ", 1.0f, 0.1f, 3.0f));
    public Setting<Float> offRotateX = this.register(new Setting<>("OffRotateX", 0.0f, -180.0f, 180.0f));
    public Setting<Float> offRotateY = this.register(new Setting<>("OffRotateY", 0.0f, -180.0f, 180.0f));
    public Setting<Float> offRotateZ = this.register(new Setting<>("OffRotateZ", 0.0f, -180.0f, 180.0f));
    
    // Misc settings
    public Setting<Boolean> misc = this.register(new Setting<>("Misc", false));
    public Setting<Float> eatMultiplier = this.register(new Setting<>("EatMultiplier", 1.0f, 0.1f, 5.0f));
    public Setting<Boolean> noTridentAnim = this.register(new Setting<>("NoTridentAnim", false));
    
    // Swing progress settings
    public Setting<Boolean> swingProgress = this.register(new Setting<>("SwingProgress", false));
    public Setting<Boolean> staticSwing = this.register(new Setting<>("StaticSwing", false));
    public Setting<Boolean> mainHandSwing = this.register(new Setting<>("MainHandSwing", true));
    public Setting<Float> mainSwingAmount = this.register(new Setting<>("MainSwingAmount", 0.0f, 0.0f, 1.0f));
    public Setting<Boolean> offHandSwing = this.register(new Setting<>("OffHandSwing", false));
    public Setting<Float> offSwingAmount = this.register(new Setting<>("OffSwingAmount", 0.0f, 0.0f, 1.0f));
    
    // FOV settings
    public Setting<Boolean> viewModelFov = this.register(new Setting<>("ViewModelFOV", false));
    public Setting<Integer> fovAmount = this.register(new Setting<>("FOVAmount", 90, 30, 150));

    public ViewModel() {
        super("ViewModel", "Transforms your 1st person view model.", Category.RENDER, false, false, false);
    }

    public float getSwingProgress(Hand hand, float progress) {
        if (!isOn() || !swingProgress.getValue()) {
            return progress;
        }
        
        if (hand == Hand.MAIN_HAND && mainHandSwing.getValue()) {
            return staticSwing.getValue() ? mainSwingAmount.getValue() : Math.max(progress, mainSwingAmount.getValue());
        }
        
        if (hand == Hand.OFF_HAND && offHandSwing.getValue()) {
            return staticSwing.getValue() ? offSwingAmount.getValue() : Math.max(progress, offSwingAmount.getValue());
        }
        
        return progress;
    }

    public void applyTransformations(Hand hand, Matrix4f matrix, Vector3f position, Quaternionf rotation, Vector3f scale) {
        if (!isOn()) return;
        
        boolean isMainHand = hand == Hand.MAIN_HAND;
        
        if (isMainHand && !mainHand.getValue()) return;
        if (!isMainHand && !offHand.getValue()) return;
        
        // Apply position offsets
        float x = isMainHand ? mainX.getValue() : offX.getValue();
        float y = isMainHand ? mainY.getValue() : offY.getValue();
        float z = isMainHand ? mainZ.getValue() : offZ.getValue();
        
        position.add(x, y, z);
        
        // Apply scale
        float scaleX = isMainHand ? mainScaleX.getValue() : offScaleX.getValue();
        float scaleY = isMainHand ? mainScaleY.getValue() : offScaleY.getValue();
        float scaleZ = isMainHand ? mainScaleZ.getValue() : offScaleZ.getValue();
        
        scale.mul(scaleX, scaleY, scaleZ);
        
        // Apply rotation
        float rotX = isMainHand ? mainRotateX.getValue() : offRotateX.getValue();
        float rotY = isMainHand ? mainRotateY.getValue() : offRotateY.getValue();
        float rotZ = isMainHand ? mainRotateZ.getValue() : offRotateZ.getValue();
        
        Quaternionf additionalRotation = new Quaternionf()
            .rotateXYZ(
                Math.toRadians(rotX),
                Math.toRadians(rotY),
                Math.toRadians(rotZ)
            );
        
        rotation.mul(additionalRotation);
    }

    public float getEatAnimationProgress(float progress) {
        if (!isOn() || !eating.getValue()) {
            return progress;
        }
        return progress * eatMultiplier.getValue();
    }

    public int getCustomFov(int originalFov) {
        if (!isOn() || !viewModelFov.getValue()) {
            return originalFov;
        }
        return fovAmount.getValue();
    }

    public boolean shouldRemoveSway() {
        return isOn() && noSway.getValue();
    }

    public boolean shouldRemoveTridentAnim() {
        return isOn() && noTridentAnim.getValue();
    }

    public boolean hasCustomShadow() {
        return isOn() && shadow.getValue();
    }
}
