package net.dialingspoon.dialib.networking.packet;

import net.dialingspoon.dialib.Interface.PlayerEntityInterface;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

public class RenderS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender){
        if (client.player != null) {
            ((PlayerEntityInterface) client.player).addTicks(buf.readInt());
            long[] longs = buf.readLongArray();
            ((PlayerEntityInterface) client.player).addPointPos(new Vec3d(Double.longBitsToDouble(longs[0]), Double.longBitsToDouble(longs[1]), Double.longBitsToDouble(longs[2])));
            ((PlayerEntityInterface) client.player).addSpriteID(buf.readIdentifier());
        }
    }
}
