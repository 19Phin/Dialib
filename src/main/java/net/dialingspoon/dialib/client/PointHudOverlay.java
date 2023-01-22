package net.dialingspoon.dialib.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.dialingspoon.dialib.Dialib;
import net.dialingspoon.dialib.Interface.PlayerEntityInterface;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import java.util.ArrayList;


public class PointHudOverlay implements HudRenderCallback {
    private static final Identifier ICON = new Identifier(Dialib.MOD_ID,
            "textures/hud/icon.png");

    @Override
    public void onHudRender(MatrixStack matrixStack, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        ArrayList<Integer> ticks = ((PlayerEntityInterface) client.player).getTicks();
        if(!ticks.isEmpty()) for(int i = 0; i < ticks.size(); i++) {
            ArrayList<Vec3d> pos = (((PlayerEntityInterface) client.player).getPointPos());
            ArrayList<Identifier> ID = (((PlayerEntityInterface) client.player).getSpriteID());

            Vec3f point = new Vec3f(client.player.getEyePos().subtract(pos.get(i)));
            Vec2f vec2 = rotateAroundZero(new Vec2f(point.getX(), point.getZ()), client.player.renderYaw);
            point = new Vec3f(vec2.x, point.getY(), vec2.y);

            if (point.getZ() > 0) {
                vec2 = rotateAroundZero(new Vec2f(point.getZ(), point.getY()), client.player.renderPitch);
                point = new Vec3f(point.getX(), vec2.y, vec2.x);

                int x = client.getWindow().getScaledWidth() / 2;
                int y = client.getWindow().getScaledHeight() /2;

                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                RenderSystem.setShaderTexture(0, ID.get(i));
                DrawableHelper.drawTexture(matrixStack,
                        (x - 5) + (int) (point.getX() *180 / (float)client.options.getFov().getValue() * (1/client.player.getFovMultiplier()) *70 / point.getZ()),
                        (y - 6) - (int) (point.getY() *180 / (float)client.options.getFov().getValue() * (1/client.player.getFovMultiplier()) *70 / point.getZ()),
                        0, 0, 12, 12, 12, 12);
            }
            if (ticks.get(i) <= 0){
                ticks.remove(i);
                pos.remove(i);
                ID.remove(i);
                if (ticks.size() != i-1) i--;
            }
        }
    }
    public Vec2f rotateAroundZero(Vec2f point, float rotation)
    {
        float newX = (float)(Math.cos(Math.toRadians(rotation))*point.x + Math.sin(Math.toRadians(rotation))*point.y);
        float newY = (float)(Math.sin(Math.toRadians(rotation))*point.x - Math.cos(Math.toRadians(rotation))*point.y);
        return new Vec2f(newX, newY);
    }
}
