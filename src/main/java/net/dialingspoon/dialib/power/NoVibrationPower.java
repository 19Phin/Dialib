package net.dialingspoon.dialib.power;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.LivingEntity;

public class NoVibrationPower extends Power{
    public NoVibrationPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }

    public static PowerFactory createFactory() {
        return new PowerFactory<>(Apoli.identifier("no_vibration"),
            new SerializableData(),
            data ->
                (type, player) -> new NoVibrationPower(type, player))
        .allowCondition();
    }
}