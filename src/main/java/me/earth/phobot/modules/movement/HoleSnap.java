package me.earth.phobot.modules.movement;

import me.earth.phobot.modules.Module;
import me.earth.phobot.modules.Setting;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class HoleSnap extends Module {
    private final Setting<Float> range = new Setting<>("Range", 3.0f, 0.5f, 10.0f);
    private final Setting<Float> speed = new Setting<>("Speed", 1.0f, 0.1f, 5.0f);
    
    public HoleSnap() {
        super("HoleSnap", Category.MOVEMENT, "Pushes you into nearby holes");
    }
    
    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        
        BlockPos playerPos = mc.player.getBlockPos();
        Vec3d velocity = mc.player.getVelocity();
        
        for (Direction direction : Direction.HORIZONTAL) {
            BlockPos holePos = playerPos.offset(direction);
            
            if (isHole(holePos)) {
                double dist = mc.player.squaredDistanceTo(holePos.getX() + 0.5, mc.player.getY(), holePos.getZ() + 0.5);
                
                if (dist <= range.getValue() * range.getValue()) {
                    double moveX = (holePos.getX() + 0.5 - mc.player.getX()) * speed.getValue();
                    double moveZ = (holePos.getZ() + 0.5 - mc.player.getZ()) * speed.getValue();
                    
                    mc.player.setVelocity(velocity.x + moveX, velocity.y, velocity.z + moveZ);
                    break;
                }
            }
        }
    }
    
    private boolean isHole(BlockPos pos) {
        if (mc.world == null) return false;
        
        BlockState state = mc.world.getBlockState(pos);
        BlockState above = mc.world.getBlockState(pos.up());
        
        return (state.getBlock() == Blocks.AIR || state.getBlock() == Blocks.CAVE_AIR) &&
               (above.getBlock() == Blocks.AIR || above.getBlock() == Blocks.CAVE_AIR) &&
               isObsidian(mc.world.getBlockState(pos.down())) &&
               isObsidian(mc.world.getBlockState(pos.offset(Direction.NORTH))) &&
               isObsidian(mc.world.getBlockState(pos.offset(Direction.SOUTH))) &&
               isObsidian(mc.world.getBlockState(pos.offset(Direction.EAST))) &&
               isObsidian(mc.world.getBlockState(pos.offset(Direction.WEST)));
    }
    
    private boolean isObsidian(BlockState state) {
        return state.getBlock() == Blocks.OBSIDIAN || 
               state.getBlock() == Blocks.BEDROCK ||
               state.getBlock() == Blocks.NETHERITE_BLOCK ||
               state.getBlock() == Blocks.ANCIENT_DEBRIS;
    }
}
