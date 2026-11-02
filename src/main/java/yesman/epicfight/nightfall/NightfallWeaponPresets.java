package yesman.epicfight.nightfall;

import java.util.function.Function;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.event.types.registry.WeaponCapabilityPresetRegistryEvent;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.WeaponCapability;

public final class NightfallWeaponPresets {
	private NightfallWeaponPresets() {}
	
	/// Tipe senjata "efn:scythe" (dipakai data/efn/capabilities/weapons/crimson_moon.json)
	public static final Function<Item, CapabilityItem.Builder<?>> SCYTHE = (item) -> WeaponCapability.builder()
		.category(CapabilityItem.WeaponCategories.LONGSWORD)
		.styleProvider((entitypatch) -> CapabilityItem.Styles.TWO_HAND)
		.hitSound(EpicFightSounds.BLADE_HIT.get())
		.collider(ColliderPreset.LONGSWORD)
		.canBePlacedOffhand(false)
		.reach(0.45F)
		.newStyleCombo(CapabilityItem.Styles.TWO_HAND,
			NightfallScytheAnimations.SCYTHE_AUTO1,
			NightfallScytheAnimations.SCYTHE_AUTO2,
			NightfallScytheAnimations.SCYTHE_AUTO3,
			NightfallScytheAnimations.SCYTHE_AUTO4,
			NightfallScytheAnimations.SCYTHE_AUTO5,
			NightfallScytheAnimations.SCYTHE_DASH,
			NightfallScytheAnimations.SCYTHE_AIR_SLASH)
		.innateSkill(CapabilityItem.Styles.TWO_HAND, (itemstack) -> NightfallSkills.SCYTHE_SKILL.get())
		.livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.BLOCK, NightfallScytheAnimations.SCYTHE_BLOCK)
		.livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.IDLE, NightfallScytheAnimations.SCYTHE_IDLE_COMBAT)
		.livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.WALK, NightfallScytheAnimations.SCYTHE_WALK_COMBAT)
		.livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.CHASE, NightfallScytheAnimations.SCYTHE_RUN_COMBAT)
		.livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.RUN, NightfallScytheAnimations.SCYTHE_RUN_COMBAT)
		.livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.KNEEL, NightfallScytheAnimations.SCYTHE_IDLE_COMBAT)
		.livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.SNEAK, NightfallScytheAnimations.SCYTHE_WALK_COMBAT);
	
	public static void register(WeaponCapabilityPresetRegistryEvent event) {
		event.getTypeEntry().put(ResourceLocation.fromNamespaceAndPath(NightfallRegistries.MODID, "scythe"), SCYTHE);
	}
}
