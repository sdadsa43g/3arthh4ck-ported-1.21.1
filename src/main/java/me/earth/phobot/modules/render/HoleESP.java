package me.earth.phobot.modules.render;

import me.earth.phobot.modules.Module;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import java.awt.Color;

public class HoleESP extends Module {
    private final Setting<Integer> radius = new Setting<>("Radius", 5, 1, 20);
    private final Setting<Color> safeColor = new Setting<>("Safe Color", new Color(0, 255, 0, 100));
    private final Setting<Color> unsafeColor = new Setting<>("Unsafe Color", new Color(255, 0, 0, 100));
    private final Setting<Float> lineWidth = new Setting<>("Line Width", 1.5f, 0.5f, 5.0f);
    
    public HoleESP() {
        super("HoleESP", Category.RENDER, "Highlights safe holes");
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
        
        BlockPos playerPos = mc.player.getBlockPos();
        int r = radius.getValue();
        
        for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {
                for (int y = -r; y <= r; y++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    
                    if (isHole(pos)) {
                        boolean isSafe = isSafeHole(pos);
                        Color color = isSafe ? safeColor.getValue() : unsafeColor.getValue();
                        
                        Box box = new Box(pos);
                        renderBox(context, box, color);
                    }
                }
            }
        }
    }
    
    private void renderBox(WorldRenderContext context, Box box, Color color) {
        // Simplified box rendering - would need proper implementation with buffers
    }
    
    private boolean isHole(BlockPos pos) {
        if (mc.world == null) return false;
        
        BlockState state = mc.world.getBlockState(pos);
        BlockState above = mc.world.getBlockState(pos.up());
        
        if ((state.getBlock() != Blocks.AIR && state.getBlock() != Blocks.CAVE_AIR) ||
            (above.getBlock() != Blocks.AIR && above.getBlock() != Blocks.CAVE_AIR)) {
            return false;
        }
        
        return isSolid(mc.world.getBlockState(pos.down())) &&
               isSolid(mc.world.getBlockState(pos.offset(Direction.NORTH))) &&
               isSolid(mc.world.getBlockState(pos.offset(Direction.SOUTH))) &&
               isSolid(mc.world.getBlockState(pos.offset(Direction.EAST))) &&
               isSolid(mc.world.getBlockState(pos.offset(Direction.WEST)));
    }
    
    private boolean isSafeHole(BlockPos pos) {
        if (mc.world == null) return false;
        
        return isObsidian(mc.world.getBlockState(pos.offset(Direction.NORTH))) &&
               isObsidian(mc.world.getBlockState(pos.offset(Direction.SOUTH))) &&
               isObsidian(mc.world.getBlockState(pos.offset(Direction.EAST))) &&
               isObsidian(mc.world.getBlockState(pos.offset(Direction.WEST)));
    }
    
    private boolean isSolid(BlockState state) {
        return state.isSolid();
    }
    
    private boolean isObsidian(BlockState state) {
        return state.getBlock() == Blocks.OBSIDIAN || 
               state.getBlock() == Blocks.BEDROCK ||
               state.getBlock() == Blocks.NETHERITE_BLOCK ||
               state.getBlock() == Blocks.ANCIENT_DEBRIS;
    }
}
