package net.dialingspoon.dialib.power.factory.action.entity;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class AtProjectiles {
    private static final TypeFilter<Entity, ?> PASSTHROUGH_FILTER = new TypeFilter<Entity, Entity>() {
        public Entity downcast(Entity entity) {
            return entity;
        }

        public Class<? extends Entity> getBaseClass() {
            return Entity.class;
        }
    };
    public static void action(SerializableData.Instance data, Entity thisEntity) {
        ServerWorld world = (ServerWorld)thisEntity.getWorld();
        int limit = data.get("limit");
        ArrayList<Entity> entities = new ArrayList<>(world.getEntitiesByType(PASSTHROUGH_FILTER, (
            (entity) -> {
                if (entity instanceof ProjectileEntity thrown) {
                    return thrown.getOwner() == thisEntity;
                }
                if (entity instanceof ItemEntity item) {
                    return item.getThrower() == thisEntity.getUuid();
                }
                return false;
            })
        ));
        BiConsumer<Vec3d, List<? extends Entity>> sorter = EntitySelectorReader.NEAREST;
        sorter.accept(thisEntity.getPos(), entities);
        if (limit == 0 || limit >= entities.size()) {
            for (Entity target:entities) {
                ((ActionFactory<Entity>.Instance) data.get("entity_action")).accept(target);
            }
        }else {
            for (int i = 0; i < limit; i++) {
                ((ActionFactory<Entity>.Instance) data.get("entity_action")).accept(entities.get(i));
            }
        }
    }

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(Apoli.identifier("at_projectiles"),
            new SerializableData()
                .add("entity_action", ApoliDataTypes.ENTITY_ACTION)
                .add("limit", SerializableDataTypes.INT, 0),
            AtProjectiles::action
        );
    }
}