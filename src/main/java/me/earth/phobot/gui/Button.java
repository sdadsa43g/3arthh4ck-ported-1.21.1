package me.earth.phobot.gui;

import me.earth.phobot.modules.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

public class Button {
    protected final MinecraftClient mc = MinecraftClient.getInstance();
    
    private final Module module;
    private final Frame frame;
    private int offsetY;
    private boolean binding;
    
    public Button(Module module, Frame frame, int offsetY) {
        this.module = module;
        this.frame = frame;
        this.offsetY = offsetY;
    }
    
    public void render(DrawContext context, int mouseX, int mouseY) {
        int y = frame.getY() + frame.getHeight() + offsetY;
        context.fill(frame.getX(), y, frame.getX() + frame.getWidth(), y + 14, module.isEnabled() ? 0xFF5555FF : 0xFF444444);
        
        String text = binding ? "Press a key..." : module.getName() + " [" + getKeyName(module.getBind()) + "]";
        context.drawTextWithShadow(mc.textRenderer, text, frame.getX() + 2, y + 3, 0xFFFFFF);
    }
    
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY)) {
            if (button == 0) {
                if (binding) {
                    binding = false;
                } else {
                    module.toggle();
                }
                return true;
            } else if (button == 1) {
                binding = true;
                return true;
            }
        }
        return false;
    }
    
    public void mouseReleased(double mouseX, double mouseY, int button) {}
    
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (binding) {
            if (keyCode != GLFW.GLFW_KEY_ESCAPE) {
                module.setBind(keyCode);
            }
            binding = false;
            return true;
        }
        return false;
    }
    
    private boolean isHovered(double mouseX, double mouseY) {
        int y = frame.getY() + frame.getHeight() + offsetY;
        return mouseX >= frame.getX() && mouseX <= frame.getX() + frame.getWidth() 
            && mouseY >= y && mouseY <= y + 14;
    }
    
    private String getKeyName(int key) {
        if (key == GLFW.GLFW_KEY_NONE) return "NONE";
        return GLFW.glfwGetKeyName(key, 0);
    }
}
