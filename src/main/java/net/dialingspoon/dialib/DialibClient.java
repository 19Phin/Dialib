package net.dialingspoon.dialib;

import net.dialingspoon.dialib.client.PointHudOverlay;
import net.dialingspoon.dialib.networking.ModMessages;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class DialibClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(new PointHudOverlay());
        ModMessages.registerS2CPackets();
    }
}
