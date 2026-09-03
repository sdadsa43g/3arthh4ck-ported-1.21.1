package me.earth.phobot.modules.render;

import me.earth.phobot.modules.Module;
import me.earth.phobot.modules.Setting;
import net.minecraft.util.Hand;

public class ViewModel extends Module {
    // Main hand settings
    private final Setting<Float> mainX = new Setting<>("Main X", 0.0f, -5.0f, 5.0f);
    private final Setting<Float> mainY = new Setting<>("Main Y", 0.0f, -5.0f, 5.0f);
    private final Setting<Float> mainZ = new Setting<>("Main Z", 0.0f, -5.0f, 5.0f);
    private final Setting<Float> mainScaleX = new Setting<>("Main Scale X", 1.0f, 0.1f, 5.0f);
    private final Setting<Float> mainScaleY = new Setting<>("Main Scale Y", 1.0f, 0.1f, 5.0f);
    private final Setting<Float> mainScaleZ = new Setting<>("Main Scale Z", 1.0f, 0.1f, 5.0f);
    private final Setting<Float> mainRotateX = new Setting<>("Main Rotate X", 0.0f, -180.0f, 180.0f);
    private final Setting<Float> mainRotateY = new Setting<>("Main Rotate Y", 0.0f, -180.0f, 180.0f);
    private final Setting<Float> mainRotateZ = new Setting<>("Main Rotate Z", 0.0f, -180.0f, 180.0f);
    
    // Off hand settings
    private final Setting<Boolean> offHand = new Setting<>("Off Hand", false);
    private final Setting<Float> offX = new Setting<>("Off X", 0.0f, -5.0f, 5.0f);
    private final Setting<Float> offY = new Setting<>("Off Y", 0.0f, -5.0f, 5.0f);
    private final Setting<Float> offZ = new Setting<>("Off Z", 0.0f, -5.0f, 5.0f);
    private final Setting<Float> offScaleX = new Setting<>("Off Scale X", 1.0f, 0.1f, 5.0f);
    private final Setting<Float> offScaleY = new Setting<>("Off Scale Y", 1.0f, 0.1f, 5.0f);
    private final Setting<Float> offScaleZ = new Setting<>("Off Scale Z", 1.0f, 0.1f, 5.0f);
    private final Setting<Float> offRotateX = new Setting<>("Off Rotate X", 0.0f, -180.0f, 180.0f);
    private final Setting<Float> offRotateY = new Setting<>("Off Rotate Y", 0.0f, -180.0f, 180.0f);
    private final Setting<Float> offRotateZ = new Setting<>("Off Rotate Z", 0.0f, -180.0f, 180.0f);
    
    // Misc settings
    private final Setting<Boolean> eating = new Setting<>("Eating", true);
    private final Setting<Boolean> noSway = new Setting<>("No Sway", false);
    private final Setting<Boolean> instantSwap = new Setting<>("Instant Swap", false);
    
    public ViewModel() {
        super("ViewModel", Category.RENDER, "Transforms your 1st person view model");
    }
    
    public float getMainX(float original) { return mainX.getValue(); }
    public float getMainY(float original) { return mainY.getValue(); }
    public float getMainZ(float original) { return mainZ.getValue(); }
    public float getMainScaleX(float original) { return mainScaleX.getValue(); }
    public float getMainScaleY(float original) { return mainScaleY.getValue(); }
    public float getMainScaleZ(float original) { return mainScaleZ.getValue(); }
    public float getMainRotateX(float original) { return mainRotateX.getValue(); }
    public float getMainRotateY(float original) { return mainRotateY.getValue(); }
    public float getMainRotateZ(float original) { return mainRotateZ.getValue(); }
    
    public float getOffX(float original) { return offX.getValue(); }
    public float getOffY(float original) { return offY.getValue(); }
    public float getOffZ(float original) { return offZ.getValue(); }
    public float getOffScaleX(float original) { return offScaleX.getValue(); }
    public float getOffScaleY(float original) { return offScaleY.getValue(); }
    public float getOffScaleZ(float original) { return offScaleZ.getValue(); }
    public float getOffRotateX(float original) { return offRotateX.getValue(); }
    public float getOffRotateY(float original) { return offRotateY.getValue(); }
    public float getOffRotateZ(float original) { return offRotateZ.getValue(); }
    
    public boolean shouldModifyEating() { return eating.getValue(); }
    public boolean shouldDisableSway() { return noSway.getValue(); }
    public boolean shouldInstantSwap() { return instantSwap.getValue(); }
    public boolean shouldModifyOffHand() { return offHand.getValue(); }
}
