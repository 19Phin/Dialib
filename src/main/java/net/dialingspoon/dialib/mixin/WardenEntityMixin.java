package net.dialingspoon.dialib.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.dialingspoon.dialib.power.NoVibrationPower;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.WardenEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WardenEntity.class)
public class WardenEntityMixin {

    @Inject(method = "isValidTarget(Lnet/minecraft/entity/Entity;)Z", at = @At("RETURN"), cancellable = true)
    private void injected(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            cir.setReturnValue(!PowerHolderComponent.hasPower(entity, NoVibrationPower.class));
        }
    }
}