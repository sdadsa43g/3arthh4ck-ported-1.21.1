package me.earth.phobot.modules.client;

import me.earth.phobot.Phobot;
import me.earth.phobot.modules.Module;
import me.earth.phobot.modules.Setting;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.DrawContext;
import java.awt.Color;

public class HUD extends Module {
    private final Setting<Boolean> moduleList = new Setting<>("Module List", true);
    private final Setting<Boolean> watermark = new Setting<>("Watermark", true);
    private final Setting<Integer> color = new Setting<>("Color", new Color(255, 0, 0).getRGB(), 0, 0xFFFFFF);
    
    public HUD() {
        super("HUD", Category.CLIENT, "Draws useful HUD elements");
    }
    
    @Override
    public void onEnable() {
        HudRenderCallback.EVENT.register(this::render);
    }
    
    @Override
    public void onDisable() {
        HudRenderCallback.EVENT.unregister(this::render);
    }
    
    private void render(DrawContext context, float tickDelta) {
        int y = 5;
        
        if (watermark.getValue()) {
            context.drawTextWithShadow(mc.textRenderer, "Phobot b1.0", 5, y, color.getValue());
            y += 10;
        }
        
        if (moduleList.getValue()) {
            for (Module module : Phobot.INSTANCE.getModuleManager().getModules()) {
                if (module.isEnabled()) {
                    String text = module.getName();
                    context.drawTextWithShadow(mc.textRenderer, text, 5, y, color.getValue());
                    y += 10;
                }
            }
        }
    }
}
