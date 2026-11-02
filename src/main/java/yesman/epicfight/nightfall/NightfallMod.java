package yesman.epicfight.nightfall;

import yesman.epicfight.api.animation.AnimationManager.AnimationRegistryEvent;
import yesman.epicfight.api.event.EpicFightEventHooks;

/// Titik masuk addon Epic Fight Nightfall (senjata Crimson Moon). Dipanggil dari EpicFightFabric.
public final class NightfallMod {
	private NightfallMod() {}
	
	/// Sebelum animasi dibangun.
	public static void registerAnimations(AnimationRegistryEvent event) {
		NightfallScytheAnimations.registerAnimations(event);
	}
	
	/// Saat init biasa (sebelum registry di-bake).
	public static void init() {
		EpicFightEventHooks.Registry.WEAPON_CAPABILITY_PRESET.registerEvent(NightfallWeaponPresets::register);
	}
}
