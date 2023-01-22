package net.dialingspoon.dialib.Interface;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

public interface PlayerEntityInterface {

    ArrayList<Integer> getTicks();

    void addTicks(int index);

    ArrayList<Vec3d> getPointPos();

    void addPointPos(Vec3d pos);

    ArrayList<Identifier> getSpriteID();

    void addSpriteID(Identifier ID);
}
