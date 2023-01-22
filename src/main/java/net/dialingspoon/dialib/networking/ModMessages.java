package net.dialingspoon.dialib.networking;

import net.dialingspoon.dialib.Dialib;
import net.dialingspoon.dialib.networking.packet.RenderS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

public class ModMessages {
    public static final Identifier Render_ID = new Identifier(Dialib.MOD_ID, "render");

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(Render_ID, RenderS2CPacket::receive);
    }
}