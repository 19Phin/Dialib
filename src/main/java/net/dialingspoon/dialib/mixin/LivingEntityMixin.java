package net.dialingspoon.dialib.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.dialingspoon.dialib.power.VibrationPower;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.event.listener.EntityGameEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.BiConsumer;

@Mixin(LivingEntity.class)
public class LivingEntityMixin extends Entity {

	public LivingEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}
	@Shadow
	protected void initDataTracker() {}
	@Shadow
	public void readCustomDataFromNbt(NbtCompound nbt) {}
	@Shadow
	public void writeCustomDataToNbt(NbtCompound nbt) {}

	@Override
	public void updateEventHandler(BiConsumer<EntityGameEventHandler<?>, ServerWorld> callback) {
		for (VibrationPower power : PowerHolderComponent.getPowers(this, VibrationPower.class)) {
			if (this.world instanceof ServerWorld) {
				power.updateEventHandler(callback);
			}
		}
	}
}