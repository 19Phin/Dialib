package net.dialingspoon.dialib.power.factory.action.entity;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import static net.minecraft.entity.Entity.RemovalReason.DISCARDED;

public class BecomeEntity {
    private static final TypeFilter<Entity, ?> PASSTHROUGH_FILTER = new TypeFilter<Entity, Entity>() {
        public Entity downcast(Entity entity) {
            return entity;
        }

        public Class<? extends Entity> getBaseClass() {
            return Entity.class;
        }
    };
    public static void action(SerializableData.Instance data, Entity entity) {
        ServerWorld world = (ServerWorld)entity.getWorld();
        Predicate<Entity> condition = data.get("condition");
        ArrayList<Entity> entities = new ArrayList<>(world.getEntitiesByType(PASSTHROUGH_FILTER, condition));
        BiConsumer<Vec3d, List<? extends Entity>> sorter = EntitySelectorReader.NEAREST;
        sorter.accept(entity.getPos(), entities);

        if (!entities.isEmpty()) {
            Entity target = entities.get(0);
            Set<PlayerPositionLookS2CPacket.Flag> set = EnumSet.noneOf(PlayerPositionLookS2CPacket.Flag.class);
            set.add(PlayerPositionLookS2CPacket.Flag.X_ROT);
            set.add(PlayerPositionLookS2CPacket.Flag.Y_ROT);
            teleport(entity, (ServerWorld) target.world, target.getX(), target.getY(), target.getZ(), set, target.getYaw(), target.getPitch());
            target.remove(DISCARDED);
        }
    }

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(Apoli.identifier("become_entity"),
                new SerializableData()
                        .add("condition",ApoliDataTypes.ENTITY_CONDITION),
                BecomeEntity::action
        );
    }

    private static void teleport(Entity target, ServerWorld world, double x, double y, double z, Set<PlayerPositionLookS2CPacket.Flag> movementFlags, float yaw, float pitch) {
        BlockPos blockPos = new BlockPos(x, y, z);
        if (World.isValid(blockPos)) {
            float f = MathHelper.wrapDegrees(yaw);
            float g = MathHelper.wrapDegrees(pitch);
            if (target instanceof ServerPlayerEntity) {
                ChunkPos chunkPos = new ChunkPos(new BlockPos(x, y, z));
                world.getChunkManager().addTicket(ChunkTicketType.POST_TELEPORT, chunkPos, 1, target.getId());
                target.stopRiding();
                if (((ServerPlayerEntity)target).isSleeping()) {
                    ((ServerPlayerEntity)target).wakeUp(true, true);
                }

                if (world == target.world) {
                    ((ServerPlayerEntity)target).networkHandler.requestTeleport(x, y, z, f, g, movementFlags);
                } else {
                    ((ServerPlayerEntity)target).teleport(world, x, y, z, f, g);
                }

                target.setHeadYaw(f);
            } else {
                float h = MathHelper.clamp(g, -90.0F, 90.0F);
                if (world == target.world) {
                    target.refreshPositionAndAngles(x, y, z, f, h);
                    target.setHeadYaw(f);
                } else {
                    target.detach();
                    Entity entity = target;
                    target = target.getType().create(world);
                    if (target == null) {
                        return;
                    }

                    target.copyFrom(entity);
                    target.refreshPositionAndAngles(x, y, z, f, h);
                    target.setHeadYaw(f);
                    entity.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
                    world.onDimensionChanged(target);
                }
            }

            if (!(target instanceof LivingEntity) || !((LivingEntity)target).isFallFlying()) {
                target.setVelocity(target.getVelocity().multiply(1.0, 0.0, 1.0));
                target.setOnGround(true);
            }

            if (target instanceof PathAwareEntity) {
                ((PathAwareEntity)target).getNavigation().stop();
            }

        }
    }
}
