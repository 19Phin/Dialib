package net.dialingspoon.dialib.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.dialingspoon.dialib.power.NoVibrationPower;
import net.minecraft.block.entity.SculkSensorBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.GameEventListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SculkSensorBlockEntity.class)
public class SculkSensorBlockEntityMixin {

    @Inject(method = "accepts(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/event/listener/GameEventListener;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/event/GameEvent;Lnet/minecraft/world/event/GameEvent$Emitter;)Z", at = @At("RETURN"), cancellable = true)
    private void injected(ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, GameEvent.Emitter emitter, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            cir.setReturnValue(!PowerHolderComponent.hasPower(emitter.sourceEntity(), NoVibrationPower.class));
        }
    }
}