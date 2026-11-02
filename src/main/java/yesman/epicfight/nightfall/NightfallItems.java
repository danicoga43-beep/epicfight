package yesman.epicfight.nightfall;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.Unbreakable;
import yesman.epicfight.platform.fabric.shim.registries.DeferredHolder;

public final class NightfallItems {
	private NightfallItems() {}
	
	public static final DeferredHolder<Item, ScytheItem> CRIMSON_MOON = NightfallRegistries.ITEMS.register("crimson_moon", () ->
		new ScytheItem(Tiers.NETHERITE, new Item.Properties()
			.rarity(Rarity.EPIC)
			.fireResistant()
			.component(DataComponents.UNBREAKABLE, new Unbreakable(false))
			.attributes(ScytheItem.createScytheAttributes(Tiers.NETHERITE)))
	);
}
