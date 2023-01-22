package net.dialingspoon.dialib.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.dialingspoon.dialib.power.NoVibrationPower;
import net.minecraft.block.SculkShriekerBlock;
import net.minecraft.block.entity.SculkShriekerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SculkShriekerBlock.class)
public class SculkShriekerBlockMixin {

    @Redirect(method = "onSteppedOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/SculkShriekerBlockEntity;findResponsiblePlayerFromEntity(Lnet/minecraft/entity/Entity;)Lnet/minecraft/server/network/ServerPlayerEntity;"))
    private ServerPlayerEntity injected(Entity entity) {
        if(PowerHolderComponent.hasPower(entity, NoVibrationPower.class))
            return null;
        else return SculkShriekerBlockEntity.findResponsiblePlayerFromEntity(entity);
    }
}