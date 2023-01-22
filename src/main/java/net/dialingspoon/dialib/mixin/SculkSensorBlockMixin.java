package net.dialingspoon.dialib.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.dialingspoon.dialib.power.NoVibrationPower;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SculkSensorBlock.class)
public class SculkSensorBlockMixin {

    @Redirect(method = "onSteppedOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isClient()Z"))
    private boolean injected(World instance, World world, BlockPos pos, BlockState state, Entity entity) {
        return instance.isClient() || PowerHolderComponent.hasPower(entity, NoVibrationPower.class);
    }
}