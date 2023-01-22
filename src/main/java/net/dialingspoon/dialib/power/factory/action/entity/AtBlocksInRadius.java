package net.dialingspoon.dialib.power.factory.action.entity;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.util.Shape;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class AtBlocksInRadius {
    public static void action(SerializableData.Instance data, Entity entity) {
        Predicate<CachedBlockPosition> blockCondition = data.get("block_condition");
        final Consumer<Triple<World, BlockPos, Direction>> blockAction = data.get("block_action");
        for(BlockPos pos : Shape.getPositions(entity.getBlockPos(), data.get("shape"), data.getInt("radius"))) {
            if(blockCondition.test(new CachedBlockPosition(entity.world, pos, true))) {
                blockAction.accept(Triple.of(entity.world, pos, Direction.byId(1)));
            }
        }
    }

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(Apoli.identifier("at_blocks_in_radius"),
                new SerializableData()
                        .add("block_condition",ApoliDataTypes.BLOCK_CONDITION)
                        .add("block_action",ApoliDataTypes.BLOCK_ACTION)
                        .add("radius", SerializableDataTypes.INT)
                        .add("shape",SerializableDataType.enumValue(Shape.class), Shape.CUBE),
                AtBlocksInRadius::action
        );
    }
}
