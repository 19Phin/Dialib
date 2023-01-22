package net.dialingspoon.dialib.power.factory.action.bientity;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.dialingspoon.dialib.Interface.PlayerEntityInterface;
import net.dialingspoon.dialib.networking.ModMessages;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

public class PointTowards {
    public static void action(SerializableData.Instance data, Pair<Entity, Entity> entities) {
        if (entities.getLeft() instanceof ServerPlayerEntity player && entities.getLeft().getPos() != null){
            ((PlayerEntityInterface)player).addTicks(data.get("duration"));
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(data.get("duration"));
            buf.writeLongArray(new long[] {Double.doubleToLongBits(entities.getRight().getPos().x),Double.doubleToLongBits(entities.getRight().getPos().y), Double.doubleToLongBits(entities.getRight().getPos().z)});
            buf.writeIdentifier(data.getId("sprite_location"));
            ServerPlayNetworking.send(player, ModMessages.Render_ID, buf);
        }
    }

    public static ActionFactory<Pair<Entity, Entity>> getFactory() {
        return new ActionFactory<>(Apoli.identifier("point"),
                new SerializableData()
                        .add("duration", SerializableDataTypes.INT)
                        .add("sprite_location", SerializableDataTypes.IDENTIFIER, new Identifier("minecraft", "textures/particle/sculk_charge_3.png")),
                PointTowards::action
        );
    }
}
