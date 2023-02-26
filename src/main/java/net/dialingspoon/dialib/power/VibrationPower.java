package net.dialingspoon.dialib.power;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.EntityPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.EntityGameEventHandler;
import net.minecraft.world.event.listener.GameEventListener;
import net.minecraft.world.event.listener.VibrationListener;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class VibrationPower extends Power implements VibrationListener.Callback {
    final int radius;
    final boolean detectBlocks;
    final boolean detectEntities;
    final Consumer<Triple<World, BlockPos, Direction>> blockAction;
    final Consumer<Entity> entityAction;
    final Consumer<Pair<Entity, Entity>> bientityAction;
    final Predicate<Pair<Entity, Entity>> bientityCondition;
    final String commandAtVibration;
    final int vibrationCooldown;
    private int currentCooldown = 0;
    private final EntityGameEventHandler<VibrationListener> gameEventHandler;

    public VibrationPower(PowerType<?> type, LivingEntity entity, int radius, boolean detectBlocks, boolean detectEntities, Consumer<Triple<World, BlockPos, Direction>> blockAction, Consumer<Entity> entityAction, Consumer<Pair<Entity, Entity>>bientityAction, Predicate<Pair<Entity, Entity>>bientityCondition, String commandAtVibration, int vibrationCooldown) {
        super(type, entity);
        this.radius = radius;
        this.detectBlocks = detectBlocks;
        this.detectEntities = detectEntities;
        this.blockAction = blockAction;
        this.entityAction = entityAction;
        this.bientityAction = bientityAction;
        this.bientityCondition = bientityCondition;
        this.commandAtVibration = commandAtVibration;
        this.vibrationCooldown = vibrationCooldown;
        gameEventHandler = new EntityGameEventHandler(new VibrationListener(new EntityPositionSource(this.entity, this.entity.getStandingEyeHeight()), radius, this));
        this.setTicking(false);
    }

    public void executeAction(Triple triple, Entity target) {
        currentCooldown = vibrationCooldown;
        if(entity == null || bientityCondition == null || bientityCondition.test(new Pair<>(entity, target))) {
            if (entityAction != null) {
                entityAction.accept(entity);
            }
            if(target !=null && detectEntities){
                if (bientityAction != null) {
                    bientityAction.accept(new Pair<>(entity, target));
                }
                if (commandAtVibration != null) {
                    executeCommandAtVibration(entity, target.getPos(), commandAtVibration);
                }
            }else if(triple != null && detectBlocks) {
                if (blockAction != null) {
                    blockAction.accept(triple);
                }
                if (commandAtVibration != null) {
                    executeCommandAtVibration(entity, new Vec3d(((BlockPos) triple.getMiddle()).getX(), ((BlockPos) triple.getMiddle()).getY(), ((BlockPos) triple.getMiddle()).getZ()), commandAtVibration);
                }
            }
        }
    }

    public void tick() {
        if(currentCooldown > 0) {
            currentCooldown--;
        }
        if (this.entity.world instanceof ServerWorld serverWorld) {
            gameEventHandler.getListener().tick(serverWorld);
        }
    }

    public void updateEventHandler(BiConsumer<EntityGameEventHandler<?>, ServerWorld> callback) {
        if (this.entity.world instanceof ServerWorld serverWorld) {
            callback.accept(this.gameEventHandler, serverWorld);
        }
    }

    private static void executeCommandAtVibration(Entity entity, Vec3d hitPosition, String command) {
        MinecraftServer server = entity.world.getServer();
        if(server != null) {
            boolean validOutput = !(entity instanceof ServerPlayerEntity) || ((ServerPlayerEntity)entity).networkHandler != null;
            ServerCommandSource source = new ServerCommandSource(
                    Apoli.config.executeCommand.showOutput && validOutput ? entity : CommandOutput.DUMMY,
                    hitPosition,
                    entity.getRotationClient(),
                    entity.world instanceof ServerWorld ? (ServerWorld)entity.world : null,
                    Apoli.config.executeCommand.permissionLevel,
                    entity.getName().getString(),
                    entity.getDisplayName(),
                    entity.world.getServer(),
                    entity);
            server.getCommandManager().executeWithPrefix(source, command);
        }
    }

    public static PowerFactory createFactory() {
        return new PowerFactory<>(Apoli.identifier("vibration"),
            new SerializableData()
                .add("radius", SerializableDataTypes.INT, 16)
                .add("detect_blocks", SerializableDataTypes.BOOLEAN, true)
                .add("detect_entities", SerializableDataTypes.BOOLEAN, true)
                .add("block_action", ApoliDataTypes.BLOCK_ACTION, null)
                .add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
                .add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null)
                .add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
                .add("command_at_vibration", SerializableDataTypes.STRING, null)
                .add("vibration_cooldown", SerializableDataTypes.INT, 40),
            data ->
                (type, player) -> new VibrationPower(type, player,
                        data.get("radius"),
                        data.get("detect_blocks"),
                        data.get("detect_entities"),
                        data.get("block_action"),
                        data.get("entity_action"),
                        data.get("bientity_action"),
                        data.get("bientity_condition"),
                        data.get("command_at_vibration"),
                        data.get("vibration_cooldown")))
        .allowCondition();
    }

    public boolean isValidTarget(@Nullable Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            return this.entity.world == livingEntity.world && EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(livingEntity) && !this.entity.isTeammate(livingEntity) && livingEntity.getType() != EntityType.ARMOR_STAND && livingEntity.getType() != EntityType.WARDEN && !PowerHolderComponent.hasPower(entity, VibrationPower.class) && !livingEntity.isInvulnerable() && !livingEntity.isDead() && this.entity.world.getWorldBorder().contains(livingEntity.getBoundingBox()) && entity != this.entity;
        }
        return false;
    }

    @Override
    public boolean accepts(ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, GameEvent.Emitter emitter) {
        if (!this.entity.isDead() && world.getWorldBorder().contains(pos) && !this.entity.isRemoved() && this.entity.world == world && currentCooldown <= 0) {
            Entity sourceEntity = emitter.sourceEntity();
            if (sourceEntity instanceof LivingEntity livingEntity) {
                return isValidTarget(livingEntity);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void accept(ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity entity, @Nullable Entity sourceEntity, float distance) {
        if (sourceEntity != null) {
            if (this.entity.isInRange(sourceEntity, radius * 2)) {
                executeAction(null, sourceEntity);
            } else {
                executeAction(null, entity);
            }
        } else {
            if (entity != null) {
                executeAction(null, entity);
            } else {
                executeAction(Triple.of(world, pos, Direction.byId(1)), null);
            }
        }
    }
}