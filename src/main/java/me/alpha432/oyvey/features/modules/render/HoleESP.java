package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.event.impl.Render3DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.client.render.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HoleESP extends Module {
    public Setting<HoleMode> fillMode = this.register(new Setting<>("FillMode", HoleMode.SOLID));
    public Setting<Float> lineWidth = this.register(new Setting<>("LineWidth", 1.0f, 0.1f, 5.0f));
    public Setting<Float> height = this.register(new Setting<>("Height", 1.0f, 0.1f, 2.0f));
    public Setting<Integer> radius = this.register(new Setting<>("Radius", 16, 1, 64));
    public Setting<Boolean> hideOwn = this.register(new Setting<>("HideOwn", false));
    public Setting<Boolean> fade = this.register(new Setting<>("Fade", false));
    public Setting<Float> fadeRadius = this.register(new Setting<>("FadeRadius", 5.0f, 0.1f, 20.0f));
    
    // Safe hole settings
    public Setting<Boolean> safe = this.register(new Setting<>("Safe", true));
    public Setting<Color> safeFillColor = this.register(new Setting<>("SafeFill", new Color(0, 255, 0, 100)));
    public Setting<Color> safeOutlineColor = this.register(new Setting<>("SafeOutline", new Color(0, 255, 0, 255)));
    
    // Unsafe hole settings
    public Setting<Boolean> unsafe = this.register(new Setting<>("Unsafe", false));
    public Setting<Color> unsafeFillColor = this.register(new Setting<>("UnsafeFill", new Color(255, 0, 0, 100)));
    public Setting<Color> unsafeOutlineColor = this.register(new Setting<>("UnsafeOutline", new Color(255, 0, 0, 255)));
    
    // Trapped hole settings
    public Setting<Boolean> trapped = this.register(new Setting<>("Trapped", true));
    public Setting<Color> trappedFillColor = this.register(new Setting<>("TrappedFill", new Color(0, 0, 255, 100)));
    public Setting<Color> trappedOutlineColor = this.register(new Setting<>("TrappedOutline", new Color(0, 0, 255, 255)));

    public HoleESP() {
        super("HoleESP", "Highlights the spots that are safe from end crystals.", Category.RENDER, false, false, false);
    }

    @Override
    public String getDisplayInfo() {
        List<HoleData> holes = findHoles();
        return String.valueOf(holes.size());
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (mc.player == null || mc.world == null) return;
        
        Vec3d cameraPos = mc.gameRenderer.getCamera().getPos();
        double playerY = MathHelper.lerp(event.getTickDelta(), mc.player.prevY, mc.player.getY());
        Box playerBox = new Box(mc.player.getBlockPos());
        
        List<HoleData> holes = findHoles();
        
        for (HoleData hole : holes) {
            // Check if hole is within radius
            double distance = cameraPos.distanceTo(hole.pos.toCenterPos());
            if (distance > radius.getValue()) continue;
            
            // Check if hole should be rendered based on type
            Color fillColor;
            Color outlineColor;
            
            if (hole.trapped) {
                if (!trapped.getValue()) continue;
                fillColor = trappedFillColor.getValue();
                outlineColor = trappedOutlineColor.getValue();
            } else if (hole.safe) {
                if (!safe.getValue()) continue;
                fillColor = safeFillColor.getValue();
                outlineColor = safeOutlineColor.getValue();
            } else {
                if (!unsafe.getValue()) continue;
                fillColor = unsafeFillColor.getValue();
                outlineColor = unsafeOutlineColor.getValue();
            }
            
            // Check if hiding own hole
            boolean isOwnHole = hole.box.intersects(playerBox);
            if (isOwnHole && hideOwn.getValue()) {
                if (!fade.getValue()) continue;
            }
            
            // Apply fade effect
            if (fade.getValue() && (distance >= fadeRadius.getValue() || isOwnHole)) {
                float alpha = 1.0f - MathHelper.clamp((float) (distance - fadeRadius.getValue()) / (radius.getValue() - fadeRadius.getValue()), 0.0f, 1.0f);
                if (isOwnHole) {
                    alpha = (float) (playerY - Math.floor(playerY));
                }
                
                fillColor = new Color(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), 
                                      (int) (fillColor.getAlpha() * alpha));
                outlineColor = new Color(outlineColor.getRed(), outlineColor.getGreen(), outlineColor.getBlue(), 
                                         (int) (outlineColor.getAlpha() * alpha));
            }
            
            // Render hole
            Box renderBox = hole.box.withMaxY(hole.pos.getY() + height.getValue());
            
            switch (fillMode.getValue()) {
                case SOLID:
                    event.getMatrixStack().push();
                    renderSolidBox(event.getMatrixStack().peek().getPositionMatrix(), renderBox, fillColor);
                    event.getMatrixStack().pop();
                    break;
                case GRADIENT:
                    event.getMatrixStack().push();
                    renderGradientBox(event.getMatrixStack().peek().getPositionMatrix(), renderBox, fillColor);
                    event.getMatrixStack().pop();
                    break;
            }
            
            // Render outline
            event.getMatrixStack().push();
            renderBoxOutline(event.getMatrixStack().peek().getPositionMatrix(), renderBox, outlineColor, lineWidth.getValue());
            event.getMatrixStack().pop();
        }
    }

    private List<HoleData> findHoles() {
        List<HoleData> holes = new ArrayList<>();
        if (mc.player == null || mc.world == null) return holes;
        
        BlockPos playerPos = mc.player.getBlockPos();
        int range = radius.getValue();
        
        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                BlockPos pos = playerPos.add(x, 0, z);
                
                if (pos.getSquaredDistance(playerPos) > range * range) continue;
                
                HoleType type = checkHole(pos);
                if (type != null) {
                    boolean safe = type == HoleType.SAFE || type == HoleType.TRAPPED;
                    boolean trapped = type == HoleType.TRAPPED;
                    
                    Box box = new Box(pos);
                    holes.add(new HoleData(pos, box, safe, trapped));
                }
            }
        }
        
        return holes;
    }

    private HoleType checkHole(BlockPos pos) {
        if (mc.world == null) return null;
        
        // Check if block below is solid
        if (mc.world.getBlockState(pos.down()).getCollisionShape(mc.world, pos.down()).isEmpty()) {
            return null;
        }
        
        // Check if space above is empty
        if (!mc.world.getBlockState(pos).getCollisionShape(mc.world, pos).isEmpty()) return null;
        if (!mc.world.getBlockState(pos.up()).getCollisionShape(mc.world, pos.up()).isEmpty()) return null;
        
        // Check surrounding blocks
        boolean north = !mc.world.getBlockState(pos.north()).getCollisionShape(mc.world, pos.north()).isEmpty();
        boolean south = !mc.world.getBlockState(pos.south()).getCollisionShape(mc.world, pos.south()).isEmpty();
        boolean east = !mc.world.getBlockState(pos.east()).getCollisionShape(mc.world, pos.east()).isEmpty();
        boolean west = !mc.world.getBlockState(pos.west()).getCollisionShape(mc.world, pos.west()).isEmpty();
        
        // Check for trapped (ceiling)
        boolean hasCeiling = !mc.world.getBlockState(pos.up(2)).getCollisionShape(mc.world, pos.up(2)).isEmpty();
        
        // Determine hole type
        int sides = (north ? 1 : 0) + (south ? 1 : 0) + (east ? 1 : 0) + (west ? 1 : 0);
        
        if (sides >= 3) {
            if (hasCeiling) {
                return HoleType.TRAPPED;
            }
            return HoleType.SAFE;
        } else if (sides == 2) {
            // Check if it's a valid 2-block hole (opposite sides)
            if ((north && south) || (east && west)) {
                if (hasCeiling) {
                    return HoleType.TRAPPED;
                }
                return HoleType.SAFE;
            }
        }
        
        return HoleType.UNSAFE;
    }

    private void renderSolidBox(Matrix4f matrix, Box box, Color color) {
        // Implementation would use Tessellator for rendering
        // This is a simplified version
    }

    private void renderGradientBox(Matrix4f matrix, Box box, Color color) {
        // Implementation would use Tessellator for gradient rendering
    }

    private void renderBoxOutline(Matrix4f matrix, Box box, Color color, float lineWidth) {
        // Implementation would use BufferBuilder for line rendering
    }

    public enum HoleMode {
        NONE("None"),
        SOLID("Solid"),
        GRADIENT("Gradient");

        private final String name;

        HoleMode(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum HoleType {
        SAFE,
        UNSAFE,
        TRAPPED
    }

    private static class HoleData {
        public final BlockPos pos;
        public final Box box;
        public final boolean safe;
        public final boolean trapped;

        public HoleData(BlockPos pos, Box box, boolean safe, boolean trapped) {
            this.pos = pos;
            this.box = box;
            this.safe = safe;
            this.trapped = trapped;
        }
    }
}
