package me.earth.phobot.gui;

import me.earth.phobot.modules.Module;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class Frame {
    private final Module.Category category;
    private int x, y;
    private final int width = 110;
    private final int height = 16;
    private boolean dragging;
    private int dragX, dragY;
    private final List<Button> buttons = new ArrayList<>();
    private boolean extended;
    
    public Frame(Module.Category category, int x, int y) {
        this.category = category;
        this.x = x;
        this.y = y;
        initButtons();
    }
    
    private void initButtons() {
        int offsetY = 0;
        for (Module module : Phobot.INSTANCE.getModuleManager().getModulesByCategory(category)) {
            buttons.add(new Button(module, this, offsetY));
            offsetY += 14;
        }
    }
    
    public void render(DrawContext context, int mouseX, int mouseY) {
        context.fill(x, y, x + width, y + height, 0xFF333333);
        context.drawTextWithShadow(mc.textRenderer, category.name(), x + 2, y + 4, 0xFFFFFF);
        
        if (extended) {
            for (Button button : buttons) {
                button.render(context, mouseX, mouseY);
            }
        }
    }
    
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY)) {
            if (button == 0) {
                dragging = true;
                dragX = (int) (mouseX - x);
                dragY = (int) (mouseY - y);
                return true;
            } else if (button == 1) {
                extended = !extended;
                return true;
            }
        }
        
        if (extended) {
            for (Button b : buttons) {
                if (b.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        for (Button button1 : buttons) {
            button1.mouseReleased(mouseX, mouseY, button);
        }
    }
    
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging) {
            x = (int) (mouseX - dragX);
            y = (int) (mouseY - dragY);
            return true;
        }
        return false;
    }
    
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (extended) {
            for (Button b : buttons) {
                if (b.keyPressed(keyCode, scanCode, modifiers)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean isHovered(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
    
    public Module.Category getCategory() { return category; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
