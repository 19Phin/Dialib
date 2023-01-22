package net.dialingspoon.dialib;

import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.PowerFactorySupplier;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import net.dialingspoon.dialib.power.NoVibrationPower;
import net.dialingspoon.dialib.power.VibrationPower;
import net.dialingspoon.dialib.power.factory.action.bientity.PointTowards;
import net.dialingspoon.dialib.power.factory.action.entity.*;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dialib implements ModInitializer {
	public static final String MOD_ID = "dialib";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static Identifier identifier(String path) {
		return new Identifier(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		register(VibrationPower::createFactory);
		register(NoVibrationPower::createFactory);
		registerbi(PointTowards.getFactory());
		register(AtBlocksInRadius.getFactory());
		register(AtEntity.getFactory());
		register(MakeBi.getFactory());
		register(AtProjectiles.getFactory());
		register(BecomeEntity.getFactory());
	}
	private static void register(PowerFactory<?> powerFactory) {
		Registry.register(ApoliRegistries.POWER_FACTORY, powerFactory.getSerializerId(), powerFactory);
	}
	private static void registerbi(ActionFactory<Pair<Entity, Entity>> actionFactory) {
		Registry.register(ApoliRegistries.BIENTITY_ACTION, actionFactory.getSerializerId(), actionFactory);
	}
	private static void register(ActionFactory<Entity> actionFactory) {
		Registry.register(ApoliRegistries.ENTITY_ACTION, actionFactory.getSerializerId(), actionFactory);
	}
	private static void register(PowerFactorySupplier<?> factorySupplier) {
		register(factorySupplier.createFactory());
	}
}
