package yesman.epicfight.nightfall;

import yesman.epicfight.api.animation.AnimationManager.AnimationRegistryEvent;
import yesman.epicfight.api.event.EpicFightEventHooks;

/// Titik masuk addon Epic Fight Nightfall (senjata Crimson Moon). Dipanggil dari EpicFightFabric.
public final class NightfallMod {
	private NightfallMod() {}
	
	/// Memuat kelas-kelas holder SEKARANG supaya semua register() terjadi sebelum registerAll() dipanggil.
	/// (Bug tahap 1: NightfallSounds baru dimuat saat animasi dibangun, setelah registry terkunci -> crash di logo Mojang.)
	public static void bootstrap() {
		NightfallMobEffects.init();
		NightfallSkillDataKeys.init();
		NightfallSounds.init();
		NightfallItems.init();
		NightfallSkills.init();
	}
	
	/// Sebelum animasi dibangun.
	public static void registerAnimations(AnimationRegistryEvent event) {
		bootstrap();
		NightfallScytheAnimations.registerAnimations(event);
	}
	
	/// Saat init biasa.
	public static void init() {
		bootstrap();
		EpicFightEventHooks.Registry.WEAPON_CAPABILITY_PRESET.registerEvent(NightfallWeaponPresets::register);
	}
}
