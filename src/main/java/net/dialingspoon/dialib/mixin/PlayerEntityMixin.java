package net.dialingspoon.dialib.mixin;

import net.dialingspoon.dialib.Interface.PlayerEntityInterface;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin extends LivingEntity implements PlayerEntityInterface {

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {super(entityType, world);}
	@Shadow
	public Iterable<ItemStack> getArmorItems() {return null;}
	@Shadow
	public ItemStack getEquippedStack(EquipmentSlot slot) {return null;}
	@Shadow
	public void equipStack(EquipmentSlot slot, ItemStack stack) {}
	@Shadow
	public Arm getMainArm() {return null;}
	private ArrayList<Integer> ticks = new ArrayList<>();
	private ArrayList<Vec3d> pointPos = new ArrayList<>();
	private ArrayList<Identifier> SpriteID = new ArrayList<>();
	@Inject(at = @At("HEAD"), method = "tick")
	void Tick(CallbackInfo callbackInfo) {
		ticks.replaceAll(integer -> integer - 1);
	}
	@Override
	public ArrayList<Integer> getTicks() { return ticks; }

	@Override
	public void addTicks(int index) { ticks.add(index); }

	@Override
	public ArrayList<Vec3d> getPointPos() { return pointPos; }

	@Override
	public void addPointPos(Vec3d pos){ pointPos.add(pos); }

	@Override
	public ArrayList<Identifier> getSpriteID() { return SpriteID; }

	@Override
	public void addSpriteID(Identifier ID){ SpriteID.add(ID); }
}