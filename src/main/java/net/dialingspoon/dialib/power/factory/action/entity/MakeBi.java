package net.dialingspoon.dialib.power.factory.action.entity;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class MakeBi {
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
        int limit = data.get("limit");
        ArrayList<Entity> entities = new ArrayList<>(world.getEntitiesByType(PASSTHROUGH_FILTER, condition));
        BiConsumer<Vec3d, List<? extends Entity>> sorter = EntitySelectorReader.NEAREST;
        sorter.accept(entity.getPos(), entities);
        if (limit == 0 || limit >= entities.size()) {
            for (Entity target:entities) {
                ((Consumer<Pair<Entity, Entity>>) data.get("bientity_action")).accept(new Pair<>(entity, target));
            }
        }else {
            for (int i = 0; i < limit; i++) {
                ((Consumer<Pair<Entity, Entity>>) data.get("bientity_action")).accept(new Pair<>(entity, entities.get(i)));
            }
        }
    }

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(Apoli.identifier("make_bi"),
                new SerializableData()
                        .add("condition",ApoliDataTypes.ENTITY_CONDITION)
                        .add("bientity_action",ApoliDataTypes.BIENTITY_ACTION)
                        .add("limit", SerializableDataTypes.INT, 0),
                MakeBi::action
        );
    }
}
