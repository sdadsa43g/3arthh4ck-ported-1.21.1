package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.event.impl.MoveEvent;
import me.alpha432.oyvey.event.impl.TickEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class HoleSnap extends Module {
    public Setting<Boolean> autoDisable = this.register(new Setting<>("AutoDisable", false));
    public Setting<Boolean> directional = this.register(new Setting<>("Directional", true));
    public Setting<Boolean> shift = this.register(new Setting<>("Shift", false));
    public Setting<Boolean> pauseStep = this.register(new Setting<>("PauseStep", false));
    public Setting<Float> height = this.register(new Setting<>("Height", 1.0f, 0.1f, 2.0f));
    public Setting<Float> timeout = this.register(new Setting<>("Timeout", 5.0f, 0.1f, 60.0f));
    public Setting<Float> range = this.register(new Setting<>("Range", 2.0f, 0.1f, 10.0f));
    public Setting<Float> speed = this.register(new Setting<>("Speed", 1.0f, 0.1f, 5.0f));
    public Setting<Float> pitch = this.register(new Setting<>("Pitch", 0.0f, -90.0f, 90.0f));
    
    private long lastHoleTime = -1L;
    private boolean walkingToHole = false;

    public HoleSnap() {
        super("HoleSnap", "Pushes you into holes as you go past them.", Category.MOVEMENT, false, false, false);
    }

    @Override
    public void onDisable() {
        lastHoleTime = -1L;
        walkingToHole = false;
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) return;
        
        // Check if player is sneaking or has special conditions
        if (mc.player.isSneaking()) return;
        if (mc.player.getPitch() < pitch.getValue() && pitch.getValue() != 0.0f) return;
        if (mc.player.isSpectator() || mc.player.isFallFlying()) return;
        
        BlockPos playerPos = mc.player.getBlockPos();
        Vec3d playerVec = mc.player.getPos();
        
        // Find nearby hole
        HoleData holeData = findNearbyHole(playerVec);
        
        if (holeData == null) {
            lastHoleTime = -1L;
            walkingToHole = false;
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        
        // If we found a hole and it's time to activate
        if (lastHoleTime == -1L || currentTime - lastHoleTime > timeout.getValue() * 1000) {
            lastHoleTime = currentTime;
            
            if (autoDisable.getValue()) {
                toggle();
                return;
            }
            
            walkingToHole = true;
        }
        
        // Don't move if within timeout
        if (currentTime - lastHoleTime <= timeout.getValue() * 1000) {
            return;
        }
    }

    @Override
    public void onMove(MoveEvent event) {
        if (mc.player == null || mc.world == null) return;
        if (isInvalidState()) return;
        
        // Check if player is already in a hole
        if (isInHole(mc.player.getBlockPos())) {
            if (autoDisable.getValue()) {
                toggle();
            }
            lastHoleTime = System.currentTimeMillis();
            walkingToHole = false;
            return;
        }
        
        if (!walkingToHole) return;
        
        HoleData holeData = findNearbyHole(mc.player.getPos());
        if (holeData == null) {
            walkingToHole = false;
            return;
        }
        
        // Calculate movement vector towards hole
        Vec3d target = holeData.target;
        Vec3d direction = target.subtract(mc.player.getPos()).normalize();
        double distance = mc.player.getPos().distanceTo(target);
        
        double moveSpeed = Math.min(getMovementSpeed() * speed.getValue(), distance);
        
        event.setMotionX(direction.x * moveSpeed);
        event.setMotionZ(direction.z * moveSpeed);
    }

    private boolean isInvalidState() {
        if (mc.player == null) return true;
        if (mc.player.isSneaking()) return true;
        if (mc.player.getPitch() < pitch.getValue() && pitch.getValue() != 0.0f) return true;
        if (mc.player.hasStatusEffect(StatusEffects.LEVITATION)) return true;
        return !walkingToHole;
    }

    private HoleData findNearbyHole(Vec3d pos) {
        Vec3d bestTarget = null;
        double bestDistance = Double.MAX_VALUE;
        
        int rangeInt = (int) Math.ceil(range.getValue());
        
        for (int x = -rangeInt; x <= rangeInt; x++) {
            for (int z = -rangeInt; z <= rangeInt; z++) {
                BlockPos checkPos = mc.player.getBlockPos().add(x, 0, z);
                
                if (checkPos.getSquaredDistance(pos) > range.getValue() * range.getValue()) {
                    continue;
                }
                
                if (isHole(checkPos)) {
                    Vec3d holeCenter = new Vec3d(
                        checkPos.getX() + 0.5,
                        MathHelper.clamp(pos.y, checkPos.getY(), checkPos.getY() + height.getValue()),
                        checkPos.getZ() + 0.5
                    );
                    
                    double dist = pos.distanceTo(holeCenter);
                    if (dist < bestDistance) {
                        // Check if path is clear
                        if (isPathClear(pos, holeCenter)) {
                            bestDistance = dist;
                            bestTarget = holeCenter;
                        }
                    }
                }
            }
        }
        
        if (bestTarget == null) return null;
        return new HoleData(bestTarget, bestDistance);
    }

    private boolean isHole(BlockPos pos) {
        if (mc.world == null) return false;
        
        // Check if the block below is solid
        if (!mc.world.getBlockState(pos.down()).getCollisionShape(mc.world, pos.down()).isEmpty()) {
            // Check if all four sides are solid
            boolean north = !mc.world.getBlockState(pos.north()).getCollisionShape(mc.world, pos.north()).isEmpty();
            boolean south = !mc.world.getBlockState(pos.south()).getCollisionShape(mc.world, pos.south()).isEmpty();
            boolean east = !mc.world.getBlockState(pos.east()).getCollisionShape(mc.world, pos.east()).isEmpty();
            boolean west = !mc.world.getBlockState(pos.west()).getCollisionShape(mc.world, pos.west()).isEmpty();
            
            // Check if the space above is empty
            boolean airAbove = mc.world.getBlockState(pos).getCollisionShape(mc.world, pos).isEmpty();
            boolean airAbove2 = mc.world.getBlockState(pos.up()).getCollisionShape(mc.world, pos.up()).isEmpty();
            
            return airAbove && airAbove2 && ((north && south && east && west) || // 4-hole
                   (north && south && east) || // 3-hole
                   (north && south && west) ||
                   (north && east && west) ||
                   (south && east && west) ||
                   (north && south) || // 2-hole
                   (east && west));
        }
        return false;
    }

    private boolean isInHole(BlockPos pos) {
        return isHole(pos);
    }

    private boolean isPathClear(Vec3d from, Vec3d to) {
        if (mc.world == null || mc.player == null) return false;
        
        RaycastContext context = new RaycastContext(
            from,
            to,
            RaycastContext.ShapeType.COLLIDER,
            RaycastContext.FluidHandling.NONE,
            mc.player
        );
        
        HitResult result = mc.world.raycast(context);
        return result.getType() == HitResult.Type.MISS;
    }

    private double getMovementSpeed() {
        if (mc.player == null) return 0.0;
        float f = mc.player.getMovementSpeed();
        return Math.sqrt(Math.pow(f, 2));
    }

    private static class HoleData {
        public final Vec3d target;
        public final double distance;

        public HoleData(Vec3d target, double distance) {
            this.target = target;
            this.distance = distance;
        }
    }
}
